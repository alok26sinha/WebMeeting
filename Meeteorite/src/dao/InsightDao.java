package dao;

import model.Insight;
import model.Meeting;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class InsightDao extends BaseDaoNew<Insight, Long> {

	public InsightDao() {
		super(Insight.class);
	}

	@SuppressWarnings("unchecked")
    public void deleteAll(Meeting meeting) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("shiftMeeting", meeting));
		deleteAll(criteria.list());
	}

}
