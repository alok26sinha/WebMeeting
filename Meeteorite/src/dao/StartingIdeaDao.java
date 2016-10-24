package dao;

import java.util.List;

import model.Meeting;
import model.Person;
import model.ShiftMeeting;
import model.StartingIdea;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class StartingIdeaDao extends BaseDaoNew<StartingIdea, Long> {

	public StartingIdeaDao() {
		super(StartingIdea.class);
	}

	@SuppressWarnings("unchecked")
    public void deleteAll(Meeting meeting) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("shiftMeeting", meeting));
		criteria.addOrder(Order.asc("id"));
		deleteAll(criteria.list());
	}

	@SuppressWarnings("unchecked")
    public List<StartingIdea> getMyStartingIdeas(ShiftMeeting meeting,
			Person person) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("shiftMeeting", meeting));
		criteria.add(Restrictions.eq("contributor", person));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	public List<StartingIdea> getAll(Person person) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("contributor", person));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

}
