package dao;

import java.util.List;

import model.AgendaItem;
import model.DartItem;
import model.Person;
import model.Status;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import type.UsefulDate;

@Repository
public class DartItemDao extends BaseDaoNew<DartItem, Long> {
	public DartItemDao() {
		super(DartItem.class);
	}

	@SuppressWarnings("unchecked")
	public List<DartItem> getAllForAgendaItem(AgendaItem agendaItem) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("agendaItem", agendaItem));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	public void deleteAllFor(AgendaItem agendaItem) {
		deleteAll(getAllForAgendaItem(agendaItem));
	}

	@SuppressWarnings("unchecked")
	public List<DartItem> getAll(Person responsiblePerson) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("responsiblePerson", responsiblePerson));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	public List<DartItem> getReminders(Person person, UsefulDate startDate,
			UsefulDate endDate) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("responsiblePerson", person));
		/*criteria.add(Restrictions.and(Restrictions.ge("timing", startDate),
				Restrictions.le("timing", endDate)));*/
		criteria.add(Restrictions.or(Restrictions.isNull("reminderSent"),
				Restrictions.eq("reminderSent", false)));
		//criteria.add(Restrictions.eq("status", Status.OPEN));
		criteria.addOrder(Order.asc("timing"));
		return criteria.list();
	}
}
