package controller.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Resource;

import model.Company;
import model.Person;
import model.Subscription;
import security.SecurityService;
import service.EventService;
import service.PersonService;
import service.SubscriptionService;
import type.NullSafeComparator;
import dao.CompanyDao;
import dao.EventDao;
import dao.PersonAdminDao;
import dao.SubscriptionDao;

@SuppressWarnings("serial")
public class PersonAction extends BaseAdminLesdAction {

	public Person person;
	public List<Person> persons;
	public Long companyId;
	public String personName;
	public String personEmail;
	public String personTimeZone;
	public String sendPassword;
	public List<Subscription> subscriptions;
	public List<Company> unsubscribedCompanies;
	public List<Person> otherPeople;
	public int type;
	public Long transferToId;

	public List<String> timeZones;

	private PersonAdminDao personDao;
	@Resource
	private SecurityService securityService;
	@Resource
	private SubscriptionService subscriptionService;
	@Resource
	private SubscriptionDao subscriptionDao;
	@Resource
	private CompanyDao companyDao;
	@Resource
	private PersonService personService;
	@Resource
	private EventService eventService;

	public PersonAction() {
		timeZones = Arrays.asList(TimeZone.getAvailableIDs());
		Collections.sort(timeZones);
	}

	@Override
	public String list() {
		checkCurrentUserIsAdmin();
		persons = personDao.getAll();
		Collections.sort(persons, new PersonNameComparitor());
		return LIST;
	}

	@Override
	public String edit() {
		checkCurrentUserIsAdmin();
		person = personDao.load(id);
		personEmail = person.email;
		personName = person.name;
		personTimeZone = person.userTimeZone;

		subscriptions = subscriptionDao.getSubscripitions(person);

		unsubscribedCompanies = subscriptionService
				.getUnsubscribedCompanies(person);

		otherPeople = personDao.getAll();
		otherPeople.remove(person);

		return EDIT;
	}

	@Override
	public String save() {
		checkCurrentUserIsAdmin();
		if (isValid(false)) {
			person = personDao.load(id);
			
			//isValid will check for personEmail == null
			personEmail = personEmail.toLowerCase().trim();
			person.email = personEmail;
			person.name = personName;
			person.userTimeZone = personTimeZone;
			personDao.save(person);
			cleanFields();

			if (sendPassword != null && sendPassword.contains("Send")) {
				securityService.sendLinkEmail(person.email);
			}

			return list();
		}
		return EDIT;
	}

	@Override
	public String delete() {
		checkCurrentUserIsAdmin();
		personDao.delete(id);
		return list();
	}

	public String addNew() {
		checkCurrentUserIsAdmin();
		if (isValid(true)) {
			//isValid will check for personEmail == null
			personEmail = personEmail.toLowerCase().trim();
			personService.createPerson( personEmail, personName, personTimeZone);
			cleanFields();
		}
		return list();
	}

	public String deleteSubscription() {
		Subscription subscription = subscriptionDao.load(id);

		Person person = subscription.person;
		id = person.getId();

		subscriptionDao.delete(subscription.getId());
		subscriptionDao.flush();

		return edit();
	}

	public String addSubscription() {
		person = personDao.load(id);
		Company company = companyDao.load(companyId);

		Subscription sub = new Subscription();
		sub.company = company;
		sub.person = person;
		sub.type = type;
		subscriptionDao.save(sub);
		subscriptionDao.flush();

		return edit();
	}

	public String deleteDuplicate() {

		if (transferToId.longValue() != -1l) {
			Person personToDelete = personDao.load(id);
			Person personToTransferTo = personDao.load(transferToId);

			personService.transferRecords(personToDelete, personToTransferTo);

			id = transferToId;
			personDao.delete(personToDelete);
			personDao.flush();

			return edit();
		}
		else{
			return edit();
		}
	}

	private boolean isValid(boolean uniqueEmail) {
		if ((personName == null || "".equals(personName.trim()))
				|| (personEmail == null || "".equals(personEmail.trim()))) {
			addActionError("Both person name and email must be specified.");
			return false;
		}
		if (!personDao.isEmailUnknown(personEmail, id, uniqueEmail)) {
			addActionError("A person with this email address already exists.");
			return false;
		}
		return true;
	}

	private void cleanFields() {
		personEmail = "";
		personName = "";
	}

	public void setPersonDao(PersonAdminDao personDao) {
		this.personDao = personDao;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getPersonEmail() {
		return personEmail;
	}

	public void setPersonEmail(String personEmail) {
		this.personEmail = personEmail;
	}

	public String getPersonTimeZone() {
		return personTimeZone;
	}

	public void setPersonTimeZone(String personTimeZone) {
		this.personTimeZone = personTimeZone;
	}

	public String getSendPassword() {
		return sendPassword;
	}

	public void setSendPassword(String sendPassword) {
		this.sendPassword = sendPassword;
	}

	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public List<Company> getUnsubscribedCompanies() {
		return unsubscribedCompanies;
	}

	public void setUnsubscribedCompanies(List<Company> unsubscribedCompanies) {
		this.unsubscribedCompanies = unsubscribedCompanies;
	}

	public List<Person> getOtherPeople() {
		return otherPeople;
	}

	public void setOtherPeople(List<Person> otherPeople) {
		this.otherPeople = otherPeople;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Long getTransferToId() {
		return transferToId;
	}

	public void setTransferToId(Long transferToId) {
		this.transferToId = transferToId;
	}

	public List<String> getTimeZones() {
		return timeZones;
	}

	public void setTimeZones(List<String> timeZones) {
		this.timeZones = timeZones;
	}
	
	

}

class PersonNameComparitor implements Comparator<Person> {

	@Override
	public int compare(Person a, Person b) {
		if (a == null && b == null)
			return 0;
		else if (a != null && b == null)
			return 1;
		else if (a == null && b != null)
			return -1;
		else {
			int nameCompare = NullSafeComparator.compare(a.name, b.name);

			if (nameCompare != 0)
				return nameCompare;
			else
				return NullSafeComparator.compare(a.getId(), b.getId());
		}

	}
	
	
	
}
