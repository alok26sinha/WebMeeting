package dao;

import java.util.List;

import model.Hindsight;
import model.Meeting;
import model.ShiftMeeting;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class HindsightDao extends BaseDaoNew<Hindsight, Long> {

	public HindsightDao() {
		super(Hindsight.class);
	}

	@SuppressWarnings("unchecked")
    public List<Hindsight> getAll(ShiftMeeting meeting) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("shiftMeeting", meeting));
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
