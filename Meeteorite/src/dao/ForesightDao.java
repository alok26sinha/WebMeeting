package dao;

import java.util.List;

import model.Foresight;
import model.ShiftMeeting;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class ForesightDao extends BaseDaoNew<Foresight, Long> {

	public ForesightDao() {
		super(Foresight.class);
	}

	public void deleteAll(ShiftMeeting meeting) {
		deleteAll(getAll(meeting));
	}

	@SuppressWarnings("unchecked")
    public List<Foresight> getAll(ShiftMeeting meeting) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("shiftMeeting", meeting));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

}
