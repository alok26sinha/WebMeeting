package dao;

import java.util.ArrayList;
import java.util.List;

import model.Company;
import model.Person;
import model.Subscription;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class SubscriptionDao extends BaseDaoNew<Subscription, Long> {

	public SubscriptionDao() {
		super(Subscription.class);
	}


	public List<Company> getAllFullUserCompanies(Person person) {
		Criteria criteria = getCriteria();

		criteria.add(Restrictions.eq("person", person));
		criteria.add(Restrictions.eq("type", 1)); // Full user

		List<Subscription> subscriptionsWhereFullUser = criteria.list();

		List<Company> companies = new ArrayList<Company>();
		
		for(Subscription sub:subscriptionsWhereFullUser )
			companies.add(sub.company);
		
		return companies;
	}

	public List<Subscription> getSubscripitions(Company company) {
		Criteria criteria = getCriteria();

		criteria.add(Restrictions.eq("company", company));
		criteria.createAlias("person", "persont");
		criteria.addOrder(Order.asc("persont.name"));
		
		return criteria.list();
	}
	
	public List<Subscription> getFullSubscriptions(Company company) {
		Criteria criteria = getCriteria();

		criteria.add(Restrictions.eq("company", company));
		criteria.add(Restrictions.eq("type", 1));
		criteria.createAlias("person", "persont");
		criteria.addOrder(Order.asc("persont.name"));
		
		return criteria.list();
	}
	public List<Subscription> getSubscripitions(Person person) {
		Criteria criteria = getCriteria();

		criteria.add(Restrictions.eq("person", person));
		criteria.createAlias("company", "companyt");
		criteria.addOrder(Order.asc("companyt.name"));
		
		return criteria.list();
	}


	public List<Subscription> getSubscripitions(Person person, Company company) {
		Criteria criteria = getCriteria();

		criteria.add(Restrictions.eq("person", person));
		criteria.add(Restrictions.eq("company", company));
		criteria.addOrder(Order.asc("type"));
		
		return criteria.list();
	}
	
}
