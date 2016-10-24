package dao;

import java.util.ArrayList;
import java.util.List;

import model.Guest;
import model.Person;
import model.ShiftMeeting;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class ShiftMeetingDao extends BaseDaoNew<ShiftMeeting, Long> {

	private GuestDao guestDao;
//	private TractionDao tractionDao;
	private StartingIdeaDao startingIdeaDao;
	private HindsightDao hindsightDao;
	private InsightDao insightDao;
	private ForesightTractionDao foresightTractionDao;
	private StartingIdeaTractionDao startingIdeaTractionDao;

	public ShiftMeetingDao() {
		super(ShiftMeeting.class);
	}

	public List<ShiftMeeting> pendingDecision(Person person) {
		return ofStatus(person, Guest.NO_RESPONSE_STATUS);
	}

	public List<ShiftMeeting> declined(Person person) {
		return ofStatus(person, Guest.DECLINE_STATUS);
	}

	public List<ShiftMeeting> attending(Person person) {
		return ofStatus(person, Guest.ACCEPT_STATUS);
	}

	@SuppressWarnings("unchecked")
    private List<ShiftMeeting> ofStatus(Person person, int status) {
		Criteria criteria = getCriteria();
		criteria.createCriteria("guests")
				.add(Restrictions.eq("person", person))
				.add(Restrictions.eq("status", status));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	public List<ShiftMeeting> attendingCurrent(Person person) {
		List<ShiftMeeting> attending = attending(person);
		List<ShiftMeeting> current = new ArrayList<ShiftMeeting>();

		for (ShiftMeeting meeting : attending) {
			if (meeting.complete == false) {
				current.add(meeting);
			}
		}

		return current;
	}

	public List<ShiftMeeting> attendingPast(Person person) {
		List<ShiftMeeting> attending = attending(person);
		List<ShiftMeeting> past = new ArrayList<ShiftMeeting>();

		for (ShiftMeeting meeting : attending) {
			if (meeting.complete == true) {
				past.add(meeting);
			}
		}

		return past;
	}

	public void deleteAllMeetingData(ShiftMeeting meeting) {
		// Delete all related classes
		guestDao.deleteAll(meeting);
		foresightTractionDao.deleteAll(meeting);
		startingIdeaTractionDao.deleteAll(meeting);
		startingIdeaDao.deleteAll(meeting);
		insightDao.deleteAll(meeting);
		hindsightDao.deleteAll(meeting);

		// Finally delete the meeting
		delete(meeting);

	}

	public void setGuestDao(GuestDao guestDao) {
		this.guestDao = guestDao;
	}

//	public void setTractionDao(TractionDao tractionDao) {
//		this.tractionDao = tractionDao;
//	}
//
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
	
	public void setStartingIdeaTractionDao (StartingIdeaTractionDao startingIdeaTractionDao) {
		this.startingIdeaTractionDao = startingIdeaTractionDao;
	}
}
