package dao;

import java.util.List;

import model.Meeting;
import model.PermanentFile;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class PermanentFileDao extends BaseDaoNew<PermanentFile, Long> {
	
	public PermanentFileDao() {
		super(PermanentFile.class);
	}

	public List<PermanentFile> getAll(Meeting meeting) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("meeting", meeting));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}

	public void deleteAll(Meeting meeting) {
		deleteAll(getAll(meeting));
	}
	
}
