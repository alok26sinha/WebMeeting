package controller.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import model.Person;
import dao.PersonAdminDao;

@SuppressWarnings("serial")
public class UserPreferencesAction extends BaseAdminLesdAction {

    public Person person;
    public String personName;
    public String personEmail;
    public String personTimeZone = "Australia/NSW";
    public String personReminderPeriod;
    
    public List<String> timeZones;
    public List<String> reminderPeriods;

    private PersonAdminDao personDao;

    public UserPreferencesAction() {
        timeZones = Arrays.asList(TimeZone.getAvailableIDs());
        Collections.sort(timeZones);
        reminderPeriods = new ArrayList<String>();
        reminderPeriods.add("(Disable reminders)");
        reminderPeriods.add("24");
        reminderPeriods.add("48");
        reminderPeriods.add("72");
    }

    @Override
    public String list() {
        return null;
    }

    @Override
    public String edit() {
        person = personDao.load(id);
        personEmail = person.email;
        personName = person.name;
        personTimeZone = person.userTimeZone;
        if( person.reminderPeriodDays > -1)
        	personReminderPeriod = "" + ( person.reminderPeriodDays * 24);
        else
        	personReminderPeriod = "(Disable reminders)";
        return EDIT;
    }

    @Override
    public String save() {
        person = personDao.load(id);
        person.userTimeZone = personTimeZone;
        
        person.reminderPeriodDays = getReminderPeriodInDays();
        personDao.save(person);
        personDao.flush();
        return redirect("Dashboard.action");
    }

    private int getReminderPeriodInDays() {
    	
    	if("(Disable reminders)".equals(personReminderPeriod))
    		return -1;

        if( !reminderPeriods.contains(personReminderPeriod)){
        	personReminderPeriod = reminderPeriods.get(1);
        }
        
        int reminderInHours = Integer.parseInt(personReminderPeriod);
        
        int reminderInDays = reminderInHours / 24;
        
        return reminderInDays;
	}

	@Override
    public String delete() {
        return null;
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

	public String getPersonReminderPeriod() {
		return personReminderPeriod;
	}

	public void setPersonReminderPeriod(String personReminderPeriod) {
		this.personReminderPeriod = personReminderPeriod;
	}

	public List<String> getTimeZones() {
		return timeZones;
	}

	public void setTimeZones(List<String> timeZones) {
		this.timeZones = timeZones;
	}

	public List<String> getReminderPeriods() {
		return reminderPeriods;
	}

	public void setReminderPeriods(List<String> reminderPeriods) {
		this.reminderPeriods = reminderPeriods;
	}
    
    

}
