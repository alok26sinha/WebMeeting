package controller.support;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.Resource;

import model.Company;
import model.DartMeeting;
import model.Guest;
import model.Meeting;
import model.Person;
import security.NoAuthenticationRequired;
import service.GuestService;
import service.SubscriptionService;
import type.TimeZone;
import type.UsefulDateTime;
import dao.CompanyDao;
import dao.DartMeetingDao;
import dao.GuestDao;
import dao.MeetingDao;
import dao.PersonDao;
import dao.SecurityDao;

@SuppressWarnings("serial")
@NoAuthenticationRequired
public class MeetingAction extends BaseAction {

	public String organiser;
	public String attendees;
	public String startUtc;
	public String endUtc;
	public String meetingName;
	public String location;
	public Meeting meeting;

	@Resource
	private MeetingDao meetingDao;
	@Resource
	private PersonDao personDao;
	@Resource
	private DartMeetingDao dartMeetingDao;
	@Resource
	private GuestService guestService;
	@Resource
	private GuestDao guestDao;
	@Resource
	private CompanyDao companyDao;
	@Resource
	private SecurityDao securityDao;
	@Resource
	private SubscriptionService subscriptionService;

	public String execute() {
		meeting = new DartMeeting();

		meeting.organiser = getOrganiser();
		meeting.startDateTime = getStartDateTime();
		meeting.durationInMinutes = calculateDurationInMinutes();
		meeting.complete = false;
		meeting.company = calculateCompany();
		meeting = dartMeetingDao.save((DartMeeting) meeting);
		meeting.name = meetingName;
		meeting.location = location;

		Guest guest = guestService.addGuest(meeting.organiser, meeting);
		guest.status = Guest.ACCEPT_STATUS;
		guestDao.save(guest);

		for (String attendeeEmail : getAttendeesEmail()) {
			Person attendee = securityDao.getPersonForEmail(attendeeEmail);
			guestService.addGuest(attendee, meeting);
		}

		return SUCCESS;
	}

	private List<String> getAttendeesEmail() {
		List<String> attendeeList = new ArrayList<String>();
		if (attendees != null && !"".equals(attendees)) {
			java.util.StringTokenizer st = new StringTokenizer(attendees,",");
			while(st.hasMoreTokens())
				attendeeList.add(st.nextToken());
		} 

		return attendeeList;
	}

	public String edit() {
		return EDIT;
	}

	private Company calculateCompany() {
		return subscriptionService.getDefaultCompany(getOrganiser());
	}

	private int calculateDurationInMinutes() {
		// TODO Auto-generated method stub
		return 60;
	}

	private UsefulDateTime getStartDateTime() {
		return UsefulDateTime.create(startUtc,
				"yyyy-MM-dd'T'HH:mm:ss'.0000000Z'", TimeZone.UTC);
	}

	private Person getOrganiser() {
		return securityDao.getPersonForEmail(organiser);
	}

}
