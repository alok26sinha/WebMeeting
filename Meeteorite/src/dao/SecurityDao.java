package dao;

import java.util.List;

import model.Person;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import security.NotAuthenticatedException;

@Repository
public class SecurityDao extends BaseDaoNew<Person, Long> {
	protected final Log log = LogFactory.getLog(getClass());

	public SecurityDao() {
		super(Person.class);

	}

	public Person authenticate(String token) {
		String queryString = "from " + Person.class.getName()
				+ " as person where person.userToken = ?";
		Object[] values = new Object[] { token };
		List<Person> list = find(queryString, values);
		if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new NotAuthenticatedException(
					"Did not find distinct user with token:" + token);
		}
	}

	@SuppressWarnings("unchecked")
	public Person authenticate(String email, String encryptedPassword) {
		Criteria criteria = getCriteria();
		criteria.add(Restrictions.eq("email", email));
		criteria.add(Restrictions.eq("encryptedPassword", encryptedPassword));
		List<Person> list = criteria.list();
		if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new NotAuthenticatedException(
					"Did not find distinct user with email:" + email
							+ " encryptedPassword:" + encryptedPassword);
		}
	}


	public Person getPersonForPasswordToken(String passwordToken) {
		String queryString = "from " + Person.class.getName()
				+ " as person where person.passwordToken = ?";
		Object[] values = new Object[] { passwordToken };
		List<Person> list = find(queryString, values);
		if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new RecordNotFoundException(
					"Did not find distinct user with passwordToken:"
							+ passwordToken);
		}
	}

	public Person getPersonForEmailAndPassword(String email,
			String encryptedPassword) {
		String queryString = "from "
				+ Person.class.getName()
				+ " as person where person.email = ? and person.encryptedPassword = ?";
		Object[] values = new Object[] { email, encryptedPassword };
		List<Person> list = find(queryString, values);
		if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new RecordNotFoundException(
					"Did not find distinct user with email:" + email
							+ " and encrypted password:" + encryptedPassword);
		}
	}

	public Person getPersonForUserToken(String userToken) {
		String queryString = "from " + Person.class.getName()
				+ " as person where person.userToken = ?";
		Object[] values = new Object[] { userToken };
		List<Person> list = find(queryString, values);
		if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new RecordNotFoundException(
					"Did not find distinct user with userToken:" + userToken);
		}
	}

	public Person getPersonForEmail(String email) {
		String queryString = "from " + Person.class.getName()
				+ " as person where person.email = ?";
		Object[] values = new Object[] { email };
		List<Person> list = find(queryString, values);

		if (list.size() == 1) {
			return list.get(0);
		} else {
			String error = "Did not find distinct user with email:" + email
					+ " listSize:" + list.size();

			for (Person person : list) {
				error += "\n" + person;
			}

			throw new RecordNotFoundException(error);
		}
	}



}
