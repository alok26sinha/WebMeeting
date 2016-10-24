package dao;

import hibernate.NoRecordsFoundException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import model.Guest;
import model.Meeting;
import model.Person;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import service.SubscriptionService;

@Repository
public class GuestDao extends BaseDaoNew<Guest, Long> {

	@Resource
	private SubscriptionService subscriptionService;
	@Resource
	private PersonDao personDao;

	public GuestDao() {
		super(Guest.class);
	}

	@SuppressWarnings("unchecked")
	public List<Guest> getAttendingGuests(Meeting meeting) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("meeting", meeting));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	public List<Guest> getNeedInvitationSent(Meeting meeting) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("meeting", meeting));
		criteria.add(Restrictions.eq("invitationStatus", Guest.INVITATION_SEND_INVITATION));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}
	
	
	public List<Person> getNotAttening(Meeting meeting) {
		List<Guest> guests = getAttendingGuests(meeting);
		List<Person> attendingGuests = new ArrayList<Person>();

		// Put together the list of guests attending
		for (Guest guest : guests)
			attendingGuests.add(guest.person);

		List<Person> allGuests = subscriptionService.getAllFullScriptions(meeting.company);

		List<Person> notAttendingGuests = new ArrayList<Person>();

		// Look through the list of all people to find those not attending
		for (Person guest : allGuests)
			if (!attendingGuests.contains(guest)
					&& !guest.equals(meeting.organiser))
				notAttendingGuests.add(guest);

		return notAttendingGuests;
	}

	@SuppressWarnings("unchecked")
	public void deleteAll(Meeting meeting) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("meeting", meeting));
		deleteAll(criteria.list());
	}

	public Guest get(Meeting meeting, Person person) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("meeting", meeting));
		criteria.add(Restrictions.eq("person", person));
		@SuppressWarnings("unchecked")
		List<Guest> guests = criteria.list();

		if (guests.size() == 0)
			throw new NoRecordsFoundException("Could not find a guest for:"
					+ meeting + "  " + person);
		// Delete the others
		else if (guests.size() > 1)
			for (int i = 1; i < guests.size(); i++)
				delete(guests.get(i));

		return guests.get(0);

	}

	public List<Guest> getAll(Person person) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("person", person));
		return criteria.list();
	}

	public boolean isPersonAGuestOnMeeting(Long meetingId, Long personId) {
		Query query = getSession()
				.createSQLQuery(
						"SELECT count(id) FROM GUEST WHERE meeting_id = ? AND person_id = ?");
		query.setLong(0, meetingId);
		query.setLong(1, personId);

		List<?> results = query.list();
		BigInteger count = (BigInteger) results.get(0);

		if (count.intValue() > 0)
			return true;
		else
			return false;
	}

}
