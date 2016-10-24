package service.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import model.DartItem;
import model.DartMeeting;
import model.Guest;
import model.Meeting;
import model.Person;
import model.ShiftMeeting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import security.SecurityService;
import service.EventService;
import service.GuestService;
import service.PersonService;
import service.SubscriptionService;
import servlet.RESTAuthFilter;

import common.UncheckedException;

import controller.dartmanage.PersonComparator;
import dao.DartItemDao;
import dao.DartMeetingDao;
import dao.GuestDao;
import dao.MeetingDao;
import dao.PersonDao;
import dao.RecordNotFoundException;
import dao.SecurityDao;
import dao.ShiftMeetingDao;

@Service("meeting.guests")
@Path("/guests")
public class MeetingGuestsService {

	public static final class EmailDTO {

		public String label;
		public String value;

		public EmailDTO(String value, String label) {
			this.label = label;
			this.value = value;
			System.out.println(label + " - " + value);
		}

	}

	private static final Logger LOG = LoggerFactory
			.getLogger(RESTAuthFilter.class);

	@Resource
	private DartMeetingDao dartMeetingDao;

	@Resource
	private ShiftMeetingDao shiftMeetingDao;

	@Resource
	private MeetingDao meetingDao;

	@Resource
	private DartItemDao dartItemDao;

	@Resource
	private GuestDao guestDao;

	@Resource
	private PersonDao personDao;

	@Resource
	private SecurityDao securityDao;

	@Resource
	SecurityService securityService;

	@Resource
	private SubscriptionService subscriptionService;

	@Resource
	private EventService eventService;

	@Resource
	private PersonService personService;
	@Resource
	private GuestService guestService;

	@GET
	@Path("email-lookup/{type}")
	@Produces("application/json")
	public EmailDTO[] lookupEmails(@QueryParam("term") String pattern,
			@PathParam("type") String type) {
		List<Person> persons = personDao.findByPartNameOrEmail(pattern);
		List<EmailDTO> result = new ArrayList<MeetingGuestsService.EmailDTO>(
				persons.size());
		for (Person person : persons) {
			if ("email".equals(type)) {
				result.add(new EmailDTO(person.email, person.name));
			} else {
				result.add(new EmailDTO(person.name, person.email));
			}
		}
		return result.toArray(new EmailDTO[result.size()]);
	}

	@PUT
	@Path("dart/{meetingId}/{itemId}/{guestId}")
	public void setResponsiblePerson(@PathParam("meetingId") Long meetingId,
			@PathParam("itemId") Long itemId, @PathParam("guestId") Long guestId) {
		try {
			Person person = personDao.load(guestId);
			DartItem dartItem = dartItemDao.load(itemId);
			if (dartItem.agendaItem.dartMeeting.getId().longValue() != meetingId
					.longValue()) {
				throw new UncheckedException("incorrect meeting ID");
			}
			dartItem.responsiblePerson = person;
			dartItemDao.save(dartItem);
			dartItemDao.flush();
		} catch (Exception e) {
			// just for testing
			e.printStackTrace();
		}
	}

	@PUT
	@Path("dart/invite")
	@Produces("application/json")
	public GuestDTO[] inviteDartGuest(@FormParam("meetingId") Long meetingId,
			@FormParam("personId") Long personId) {

		DartMeeting meeting = dartMeetingDao.load(meetingId);
		return inviteCommon(personId, meeting);
	}

	private GuestDTO[] inviteCommon(Long personId, Meeting meeting) {
		LOG.info("Inviting Guest");

		Person person = personDao.load(personId);
		return addInvitedPerson(meeting, person);
	}

	@PUT
	@Path("shift/invite")
	@Produces("application/json")
	public GuestDTO[] inviteShiftGuest(@FormParam("meetingId") Long meetingId,
			@FormParam("personId") Long personId) {

		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		return inviteCommon(personId, meeting);
	}

	@PUT
	@Path("dart/invite-new")
	@Produces("application/json")
	public GuestDTO[] inviteNewDartGuest(
			@FormParam("meetingId") Long meetingId,
			@FormParam("personName") String personName,
			@FormParam("personEmail") String personEmail) {
		DartMeeting meeting = dartMeetingDao.load(meetingId);
		return inviteNewCommon(personName, personEmail, meeting);
	}

	@PUT
	@Path("shift/invite-new")
	@Produces("application/json")
	public GuestDTO[] inviteNewShiftGuest(
			@FormParam("meetingId") Long meetingId,
			@FormParam("personName") String personName,
			@FormParam("personEmail") String personEmail) {
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		return inviteNewCommon(personName, personEmail, meeting);
	}

	private GuestDTO[] inviteNewCommon(String personName, String personEmail,
			Meeting meeting) {
		Person person;
		try {
			person = securityDao.getPersonForEmail(personEmail);

		} catch (RecordNotFoundException e) {
			person = personService.createPerson(personEmail, personName, null);
			personDao.flush();
		}
		subscriptionService.addAsGuestIfNotASubscriber(person, meeting.company);
		return addInvitedPerson(meeting, person);
	}

	@GET
	@Path("invited/{meetingId}")
	@Produces("application/json")
	public GuestDTO[] invitedGuests(@PathParam("meetingId") Long meetingId) {
		Meeting meeting = meetingDao.load(meetingId);
		GuestDTO[] guests = guestsToResult(meeting);
		return guests;
	}

	@GET
	@Path("not-invited/{meetingId}")
	@Produces("application/json")
	public Person[] nonAttendingGuests(@PathParam("meetingId") Long meetingId) {
		Meeting meeting = meetingDao.load(meetingId);
		Person[] notAttending = guestDao.getNotAttening(meeting).toArray(
				new Person[0]);
		Arrays.sort(notAttending, new PersonComparator());
		return notAttending;
	}

	@DELETE
	@Path("revoke/{meetingId}/{guestId}")
	@Produces("application/json")
	public GuestDTO[] retireGuest(@PathParam("meetingId") Long meetingId,
			@PathParam("guestId") Long guestId) {
		Guest guest = guestDao.load(guestId);
		guestDao.delete(guest);
		guestDao.flush();
		Meeting meeting = meetingDao.load(meetingId);
		GuestDTO[] guests = guestsToResult(meeting);
		return guests;
	}

	private GuestDTO[] guestsToResult(Meeting meeting) {
		GuestDTO[] guests = new GuestDTO[meeting.guests.size()];
		for (int i = 0; i < meeting.guests.size(); i++) {
			guests[i] = new GuestDTO();
			final Guest dbGuest = meeting.guests.get(i);
			guests[i].setId(dbGuest.getId());
			guests[i].person = new Person();
			guests[i].person.name = dbGuest.person.name;
			guests[i].person.email = dbGuest.person.email;
			guests[i].person.userToken = dbGuest.getStatusResponse();
			guests[i].isOrganizer = dbGuest.person.getId().longValue() == meeting.organiser
					.getId().longValue();
		}
		return guests;
	}

	private GuestDTO[] addInvitedPerson(Meeting meeting, Person person) {
		if (!isPersonInvited(meeting, person)) {
			Guest guest = guestService.addGuest(person,  meeting);
			guestDao.flush();
			meeting.guests.add(guest);
			// dartMeetingDao.save(meeting);
			// dartMeetingDao.flush();
		}
		GuestDTO[] guests = guestsToResult(meeting);
		return guests;
	}

	private boolean isPersonInvited(Meeting meeting, Person person) {
		for (Guest guest : meeting.guests) {
			if (guest.person.name.equals(person.name)) {
				return true;
			}
		}
		return false;
	}

}
