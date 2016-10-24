package dao;

import java.util.List;

import model.Event;
import model.Person;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class EventDao extends BaseDaoNew<Event, Long> {

	public EventDao() {
		super(Event.class);
	}

	public void deleteAll(Person person) {
		deleteAll(getAllByDateDesc(person));
	}

	@SuppressWarnings("unchecked")
    public List<Event> getAllByDateDesc(Person person) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("person", person));
		criteria.addOrder(Order.desc("eventDateTime"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
    public List<Event> getAllByDateDesc() {
		Criteria criteria = getCriteria();
		criteria.addOrder(Order.desc("eventDateTime"));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public Event getEvent(Person person, String eventName) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("person", person));
		criteria.add(Restrictions.eq("name", eventName));
		criteria.addOrder(Order.desc("eventDateTime"));
		List<Event> events = criteria.list();
		
		if( events.size() == 0 )
			return null;
		else
			return events.get(0);
	}

}
