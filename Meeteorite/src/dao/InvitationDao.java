package dao;

import hibernate.NoRecordsFoundException;

import java.util.List;
import java.util.UUID;

import model.Guest;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class InvitationDao extends BaseDaoNew<Guest, Long> {
	public InvitationDao() {
		super(Guest.class);
	}
	
    public Guest loadInvitation(UUID invitationId) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("invitationId", invitationId));
	    @SuppressWarnings("unchecked")
		List<Guest> list = criteria.list();
		if (list.size() == 1) {
			return list.get(0);
		} else {
			String errorMessage = "Could not invitation id:" + invitationId;
			RuntimeException exception = new NoRecordsFoundException(errorMessage);
			throw exception;
		}
	}
}
