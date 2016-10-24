package dao;

import java.util.List;

import model.AgendaItem;
import model.DartMeeting;
import model.Meeting;
import model.Person;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class AgendaItemDao extends BaseDaoNew<AgendaItem, Long> {

	public AgendaItemDao() {
		super(AgendaItem.class);
	}

	public int getMaximumNumber(DartMeeting meeting) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("dartMeeting", meeting));
		criteria.setProjection(Projections.projectionList().add(
				Projections.max("number")));

		Object o = criteria.uniqueResult();
		return o == null ? 0 : ((Number) o).intValue();
	}
	
	public List<AgendaItem> getAll(Meeting meeting) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("dartMeeting", meeting));
		return criteria.list();
	}
	
	public void deleteAll(Meeting meeting) {
		deleteAll(getAll(meeting));
	}
	
	public List<AgendaItem> getAllContributor(Person person){
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("contributor", person));
		return criteria.list();
	}

	public List<AgendaItem> getAllOwner(Person person){
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("itemOwner", person));
		return criteria.list();
	}
}
