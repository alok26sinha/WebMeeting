package dao;

import java.util.List;

import model.Person;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class PersonDao extends BaseDaoNew<Person, Long> {

    public PersonDao() {
        super(Person.class);
    }

    public List<Person> findByPartNameOrEmail(String pattern) {
        if (pattern == null || "".equals(pattern)) {
            return getAll();
        } else {
            return find(Restrictions.or(
                    Restrictions.like("name", pattern, MatchMode.ANYWHERE), 
                    Restrictions.like("email", pattern, MatchMode.ANYWHERE)));
        }
    }

	@SuppressWarnings("unchecked")
	public List<Person> getAllWithNoLastReminder() {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.isNull("lastReminderSent"));
		return criteria.list();
	}

}
