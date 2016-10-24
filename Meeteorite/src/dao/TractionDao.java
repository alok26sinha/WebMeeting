package dao;

import java.util.List;

import model.Person;
import model.ShiftMeeting;
import model.Status;
import model.Traction;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import type.UsefulDate;

@Repository
public class TractionDao extends BaseDaoNew<Traction, Long> {

	public TractionDao() {
		super(Traction.class);
	}

	@SuppressWarnings("unchecked")
	public List<Traction> getAll(Person personResponsible) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("personResponsible", personResponsible));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Traction> getAllOpenAndInProgress(Person personResponsible) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("personResponsible", personResponsible));
		criteria.add(Restrictions.eq("status", Status.OPEN));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Traction> getAll(ShiftMeeting meeting, Person assignedTo) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("personResponsible", assignedTo));
		criteria.add(Restrictions.eq("shiftMeeting", meeting));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	public void deleteAll(ShiftMeeting shiftMeeting) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("shiftMeeting", shiftMeeting));
		deleteAll(criteria.list());
	}

	public List<Traction> getReminders(Person person, UsefulDate startDate,
			UsefulDate endDate) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("personResponsible", person));
		criteria.add(Restrictions.and(
				Restrictions.ge("dueDate", startDate.getSqlDate()),
				Restrictions.le("dueDate", endDate.getSqlDate())));
		criteria.add(Restrictions.or(Restrictions.isNull("reminderSent"),
				Restrictions.eq("reminderSent", false)));
		criteria.add(Restrictions.eq("status", Status.OPEN));
		criteria.addOrder(Order.asc("dueDate"));
		return criteria.list();
	}

}
