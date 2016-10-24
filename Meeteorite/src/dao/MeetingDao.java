package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import model.Guest;
import model.Meeting;
import model.Person;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class MeetingDao extends BaseDaoNew<Meeting, Long> {

	@Resource
	private GuestDao guestDao;
//	private TractionDao tractionDao;
	@Resource
	private StartingIdeaDao startingIdeaDao;
	@Resource
	private HindsightDao hindsightDao;
	@Resource
	private InsightDao insightDao;
	@Resource
	private ForesightTractionDao foresightTractionDao;
	@Resource
	private StartingIdeaTractionDao startingIdeaTractionDao;
	@Resource
	private AgendaItemDao agendaItemDao;

	public MeetingDao() {
		super(Meeting.class);
	}

	@SuppressWarnings("unchecked")
    public List<Meeting> drafts(Person user) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("invitationSend", false));
		criteria.createCriteria("guests")
		.add(Restrictions.eq("person", user));
		criteria.addOrder(Order.asc("startDateTime"));
		return criteria.list();
	}

	
	public List<Meeting> pendingDecision(Person person) {
		return ofStatus(person, Guest.NO_RESPONSE_STATUS, true);
	}

	public List<Meeting> declined(Person person) {
		return ofStatus(person, Guest.DECLINE_STATUS, false);
	}

	public List<Meeting> attending(Person person) {
		return ofStatus(person, Guest.ACCEPT_STATUS, true);
	}
	
	@SuppressWarnings("unchecked")
    private List<Meeting> ofStatus(Person person, int status, boolean ascending) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("invitationSend", true));
		criteria.add(Restrictions.eq("complete", false));
		criteria.createCriteria("guests")
				.add(Restrictions.eq("person", person))
				.add(Restrictions.eq("status", status));
		
		if( ascending)
			criteria.addOrder(Order.asc("startDateTime"));
		else
			criteria.addOrder(Order.desc("startDateTime"));
		
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
    public List<Meeting> past(Person user){
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("complete", true));
		criteria.createCriteria("guests")
		.add(Restrictions.eq("person", user));
		criteria.addOrder(Order.desc("startDateTime"));
		return criteria.list();
		
	}
	
	/*
	public List<Meeting> attendingCurrent(Person person) {
		List<Meeting> attending = attending(person);
		List<Meeting> current = new ArrayList<Meeting>();
		
		UsefulDateTime now = UsefulDateTime.now();
		for (Meeting meeting:attending) {
			if (meeting.getEndDateTime().isAfter(now)) {
				current.add(meeting);
			}
		}
		
		return current;
	}
	
	public List<Meeting> attendingPast(Person person) {
		List<Meeting> attending = attending(person);
		List<Meeting> past = new ArrayList<Meeting>();
		
		UsefulDateTime now = UsefulDateTime.now();
		for (Meeting meeting:attending) {
			if (now.isAfter(meeting.getEndDateTime())) {
				past.add(meeting);
			}
		}
		
		return past;
	}
	*/

	@SuppressWarnings({ "unchecked" })
    public List<Meeting> whereOrganiser(Person person) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("organiser", person));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Meeting> onInviteListAndComplete(Person person) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("complete", true));
		criteria.createCriteria("guests")
				.add(Restrictions.eq("person", person));
		criteria.addOrder(Order.asc("startDateTime"));
		return criteria.list();
	}

	public void deleteAllMeetingData(Meeting meeting) {
		// Delete all related classes
		guestDao.deleteAll(meeting);
		foresightTractionDao.deleteAll(meeting);
		startingIdeaTractionDao.deleteAll(meeting);
		startingIdeaDao.deleteAll(meeting);
		insightDao.deleteAll(meeting);
		hindsightDao.deleteAll(meeting);
		agendaItemDao.deleteAll(meeting);
		
		// Finally delete the meeting
		delete(meeting);

	}

	public Map<Person, List<Meeting>> findMeetingsWithoutReports() {
		Map<Person, List<Meeting>> result = new HashMap<Person, List<Meeting>>();
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("complete", true));
		criteria.add(Restrictions.eq("followupReportSent", false));
		@SuppressWarnings("unchecked")
		List<Meeting> meetings = criteria.list();
		for (Meeting meeting : meetings) {
			List<Meeting> missed = result.get(meeting.organiser);
			if (missed == null) {
				missed = new ArrayList<Meeting>();
				result.put(meeting.organiser, missed);
			}
			missed.add(meeting);
		}
		return result;
	}
	
}
