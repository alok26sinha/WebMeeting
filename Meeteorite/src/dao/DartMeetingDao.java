package dao;

import java.util.ArrayList;
import java.util.List;

import model.DartMeeting;
import model.Guest;
import model.Meeting;
import model.Person;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class DartMeetingDao extends BaseDaoNew<DartMeeting, Long> {

	private GuestDao guestDao;

	private StartingIdeaDao startingIdeaDao;
	private HindsightDao hindsightDao;
	private InsightDao insightDao;
	private ForesightTractionDao foresightTractionDao;
	private StartingIdeaTractionDao startingIdeaTractionDao;
	private AgendaItemDao agendaItemDao;

	public DartMeetingDao() {
		super(DartMeeting.class);
	}

	public List<DartMeeting> pendingDecision(Person person) {
		return ofStatus(person, Guest.NO_RESPONSE_STATUS);
	}

	public List<DartMeeting> declined(Person person) {
		return ofStatus(person, Guest.DECLINE_STATUS);
	}

	@SuppressWarnings("unchecked")
    private List<DartMeeting> ofStatus(Person person, int status) {
		Criteria criteria = getCriteria();
		criteria.createCriteria("guests")
				.add(Restrictions.eq("person", person))
				.add(Restrictions.eq("status", status));
		criteria.addOrder(Order.asc("startDateTime"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
    public List<DartMeeting> attending(Person person) {
		Criteria criteria = getCriteria();
		criteria.createCriteria("guests")
				.add(Restrictions.eq("person", person))
				.add(Restrictions.eq("status", Guest.ACCEPT_STATUS));
		criteria.addOrder(Order.asc("startDateTime"));
		return criteria.list();
		/*
		 * List<Meeting> guestAttendence = criteria.list();
		 * 
		 * List<Meeting> organiserAttendence = whereOrganiser(person);
		 * 
		 * List<Meeting> allAttendence = new ArrayList<Meeting>();
		 * allAttendence.addAll(guestAttendence);
		 * allAttendence.addAll(organiserAttendence);
		 * 
		 * return allAttendence;
		 */
	}

	public List<DartMeeting> attendingCurrent(Person person) {
		List<DartMeeting> attending = attending(person);
		List<DartMeeting> current = new ArrayList<DartMeeting>();

		for (DartMeeting meeting : attending) {
			if (meeting.complete == false) {
				current.add(meeting);
			}
		}

		return current;
	}

	public List<DartMeeting> attendingPast(Person person) {
		List<DartMeeting> attending = attending(person);
		List<DartMeeting> past = new ArrayList<DartMeeting>();

		for (DartMeeting meeting : attending) {
			if (meeting.complete == true) {
				past.add(meeting);
			}
		}

		return past;
	}

	public void deleteAllMeetingData(DartMeeting meeting) {
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

	public void setGuestDao(GuestDao guestDao) {
		this.guestDao = guestDao;
	}

	public void setStartingIdeaDao(StartingIdeaDao startingIdeaDao) {
		this.startingIdeaDao = startingIdeaDao;
	}

	public void setHindsightDao(HindsightDao hindsightDao) {
		this.hindsightDao = hindsightDao;
	}

	public void setInsightDao(InsightDao insightDao) {
		this.insightDao = insightDao;
	}

	public void setForesightTractionDao(
			ForesightTractionDao foresightTractionDao) {
		this.foresightTractionDao = foresightTractionDao;
	}

	public void setStartingIdeaTractionDao(
			StartingIdeaTractionDao startingIdeaTractionDao) {
		this.startingIdeaTractionDao = startingIdeaTractionDao;
	}

	public void setAgendaItemDao(AgendaItemDao agendaItemDao) {
		this.agendaItemDao = agendaItemDao;
	}
}
