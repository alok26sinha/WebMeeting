package service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import model.Company;
import model.Person;
import model.Subscription;

import org.springframework.stereotype.Component;

import common.UncheckedException;

import dao.CompanyDao;
import dao.SubscriptionDao;

@Component
public class SubscriptionService {


	@Resource
	private SubscriptionDao subscriptionDao;
	@Resource
	private CompanyDao companyDao;
	
	public Company getDefaultCompany(Person organiser) {
		List<Company> companies =  subscriptionDao.getAllFullUserCompanies(organiser);
		
		if( companies.size() > 0)
			return companies.get(0);
		else
			throw new UncheckedException("User:" + organiser + " does not have a full subscription with any company and cannot create a new meeting");
		
	}

	public List<Company> getUnsubscribedCompanies(Person person) {
		List<Company> companies = companyDao.getAll();
		List<Subscription> subscriptions =  subscriptionDao.getSubscripitions(person);
		
		for(Subscription sub: subscriptions){
			companies.remove(sub.company);
		}
		return companies;
	}
	
	public boolean hasASubscription(Person person){
		List<Subscription> subscriptions =  subscriptionDao.getSubscripitions(person);
		if( subscriptions.size() > 0)
			return true;
		else
			return false;
	}
	
	public boolean hasAFullSubscription(Person person){
		List<Company> companies =  subscriptionDao.getAllFullUserCompanies(person);
		if( companies.size() > 0)
			return true;
		else
			return false;
	}
	
	public List<Person> getAllFullScriptions(Company company){
		List<Person> people = new ArrayList<Person>();
		List<Subscription> subscriptions = subscriptionDao.getFullSubscriptions(company);
		for(Subscription sub: subscriptions)
			people.add(sub.person);
		return people;
	}
	
	public void addAsGuestIfNotASubscriber(Person person, Company company){
		List<Subscription> subscriptions = subscriptionDao.getSubscripitions(person, company);
		
		if(subscriptions.size() == 0){
			Subscription sub = new Subscription();
			sub.person = person;
			sub.company = company;
			sub.type = 2; //guest
			subscriptionDao.save(sub);
		}
	}
	
	
}
