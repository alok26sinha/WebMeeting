package service;

import java.util.List;

import javax.annotation.Resource;

import model.AgendaItem;
import model.DartItem;
import model.Guest;
import model.Meeting;
import model.Person;
import model.StartingIdea;
import model.Traction;

import org.springframework.stereotype.Component;

import security.LocalSecurityContext;
import security.SecurityService;
import spring.LocalApplicationContext;
import type.StringUtils;
import dao.AgendaItemDao;
import dao.DartItemDao;
import dao.EventDao;
import dao.GuestDao;
import dao.MeetingDao;
import dao.PersonDao;
import dao.StartingIdeaDao;
import dao.SubscriptionDao;
import dao.TractionDao;

@Component
public class PersonService {

	@Resource
	private MeetingDao meetingDao;
	@Resource
	private SubscriptionDao subDao;
	@Resource
	private GuestDao guestDao;
	@Resource
	private DartItemDao dartItemDao;
	@Resource
	private StartingIdeaDao startIdeaDao;
	@Resource
	private TractionDao tractionDao;
	@Resource
	private AgendaItemDao agendaItemDao;
	@Resource
	private EventDao eventDao;
	@Resource 
	private PersonDao personDao;
	@Resource
	private EventService eventService;
	@Resource
	private SecurityService securityService;

	public void transferRecords(Person from, Person to) {

		subDao.deleteAll(subDao.getSubscripitions(from));
		
		eventDao.deleteAll(from);

		List<Meeting> organiser = meetingDao.whereOrganiser(from);
		for (Meeting meeting : organiser){
			meeting.organiser = to;
		}

		List<Guest> guests = guestDao.getAll(from);
		for(Guest guest: guests){
			//Prevent multiple invites
			if(alreadyInvited(to,guest.meeting))
				guestDao.delete(guest);
			else //Modify
				guest.person = to;
		}

		List<DartItem> dartItems = dartItemDao.getAll(from);
		for(DartItem dartItem:dartItems){
			dartItem.responsiblePerson = to;
		}
		
		List<StartingIdea> startIdeas = startIdeaDao.getAll(from);
		for(StartingIdea st: startIdeas){
			st.contributor = to;
		}
		
		List<Traction> tractions = tractionDao.getAll(from);
		for(Traction traction: tractions){
			traction.personResponsible = to;
		}
		
		List<AgendaItem> agendaItems = agendaItemDao.getAllContributor(from);
		for(AgendaItem item: agendaItems){
			item.contributor = to;
		}
		
		agendaItems = agendaItemDao.getAllOwner(from);
		for(AgendaItem item: agendaItems){
			item.itemOwner = to;
		}
				
	}

	private boolean alreadyInvited(Person to, Meeting meeting) {
		return guestDao.isPersonAGuestOnMeeting(meeting.getId(), to.getId());
	}
	
	public Person createPerson(String email, String name, String userTimeZone ) {
		email = email.trim().toLowerCase();
		Person person = new Person();
		person.administrator = false;
		person.email = email;
		person.name = name;
		
		if( !StringUtils.isEmpty(userTimeZone))
			person.userTimeZone = userTimeZone;
		else
			//Set as the same time zone as the current user
			person.userTimeZone = LocalSecurityContext.get().getUser().userTimeZone;
			
		person.acceptTermsAndConditions = false;
		person.reminderPeriodDays = 1;
		person = personDao.save(person);
		eventService.logEvent(person, "Add New User");
		securityService.sendLinkEmail(person.email);
		return person;
	}

}
