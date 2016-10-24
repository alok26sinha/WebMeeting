package dao;

import java.util.ArrayList;
import java.util.List;

import model.Company;
import model.Person;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class PersonAdminDao extends BaseDaoNew<Person, Long> {

	// private static final Log log = LogFactory.getLog(PersonAdminDao.class);

	public PersonAdminDao() {
		super(Person.class);
	}

	
	public boolean isEmailUnknown(String personEmail, Long id, boolean uniqueEmail) {
		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("email", personEmail));
		if (!uniqueEmail) {
			criterions.add(Restrictions.ne("id", id));
		}
		return find(criterions.toArray(new Criterion[criterions.size()])).isEmpty();
	}

}
