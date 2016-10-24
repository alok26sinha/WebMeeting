package dao;

import java.util.List;

import model.ForesightTraction;
import model.Meeting;
import model.Person;
import model.ShiftMeeting;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class ForesightTractionDao extends BaseDaoNew<ForesightTraction, Long> {

	public ForesightTractionDao() {
		super(ForesightTraction.class);
	}
	
	@SuppressWarnings("unchecked")
    public List<ForesightTraction> getAll(ShiftMeeting meeting) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("shiftMeeting", meeting));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
    public List<ForesightTraction> getForUser(Person personResponsible) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("personResponsible", personResponsible));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
    public void deleteAll(Meeting meeting) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("shiftMeeting", meeting));
//		criteria.addOrder(Order.asc("id"));
		deleteAll(criteria.list());
	}
}
