package dao;

import java.util.List;

import model.AgendaItem;
import model.Meeting;
import model.Person;
import model.PrivateNotes;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class PrivateNotesDao extends BaseDaoNew<PrivateNotes, Long> {

	public PrivateNotesDao() {
		
		super(PrivateNotes.class);
	}
	
	public PrivateNotes getAgendaReviewNotes(Person person, Meeting meeting){
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("person",person));
		criteria.add(Restrictions.eq("meeting",meeting));
		criteria.add(Restrictions.isNotNull("agendaReviewNotes"));
		return firstOrNull(criteria);		
	}
	
	public PrivateNotes getSummaryCloseNotes(Person person, Meeting meeting){
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("person",person));
		criteria.add(Restrictions.eq("meeting",meeting));
		criteria.add(Restrictions.isNotNull("summaryCloseNotes"));
		return firstOrNull(criteria);
	}
	
	public PrivateNotes getAgendaItemNotes(Person person, AgendaItem agendaItem){
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("person",person));
		criteria.add(Restrictions.eq("agendaItem",agendaItem));
		criteria.add(Restrictions.isNotNull("agendaItemNotes"));
		return firstOrNull(criteria);
	}
	
	public List<PrivateNotes> getAll(Meeting meeting) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("meeting",meeting));
		return criteria.list();
	}
	
	public void deleteAllFor(Meeting meeting) {
		deleteAll(getAll(meeting));
	}
	
	public void deleteAllFor(AgendaItem agendaItem) {
		deleteAll(getAll(agendaItem));
	}
	
	private List<PrivateNotes> getAll(AgendaItem agendaItem) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("agendaItem",agendaItem));
		return criteria.list();
	}

	private PrivateNotes firstOrNull(Criteria criteria) {
		List<PrivateNotes> notes = criteria.list();
		if(notes!=null && !notes.isEmpty()){
			return notes.get(0);
		}
		else{
			return null;
		}
	}

}
