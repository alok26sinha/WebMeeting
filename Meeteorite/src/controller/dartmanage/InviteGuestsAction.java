package controller.dartmanage;

import java.util.List;

import javax.annotation.Resource;

import model.DartMeeting;
import model.Guest;
import model.Person;
import security.SecurityService;
import service.GuestService;
import service.PersonService;
import type.StringUtils;
import controller.support.BaseAction;
import dao.DartMeetingDao;
import dao.GuestDao;
import dao.PersonDao;
import dao.RecordNotFoundException;
import dao.SecurityDao;

@SuppressWarnings("serial")
public class InviteGuestsAction extends BaseAction {
	
	
	public Long id;
	public DartMeeting meeting;
	public List<Guest> attendingGuests;
	public List<Person> notAttending;
	public String newPersonName;
	public String newPersonEmail;
	public Long deleteId;
	public Long existingPerson;
	public String submitButton;

	private DartMeetingDao dartMeetingDao;
	private GuestDao guestDao;
	private PersonDao personDao;
	private SecurityDao securityDao;
	@Resource
	private SecurityService securityService;
	@Resource
	private PersonService personService;
	@Resource 
	private GuestService guestService;

	@Override
	public String execute() {
		meeting = dartMeetingDao.load(id);
		attendingGuests = guestDao.getAttendingGuests(meeting);
		notAttending = guestDao.getNotAttening(meeting);		
		return SUCCESS;
	}

	public String addGuest() {
		if (!StringUtils.isEmpty(newPersonName)) {
			if (!StringUtils.isEmpty(newPersonEmail) && StringUtils.isEmail(newPersonEmail.trim()))
				addNewPersonAndAddAsGuest();
			else
				addActionError("Email address cannot be empty and/or correctly formatted");
		} else {
			addSelectedPersonAsGuest();
		}
		guestDao.flush();
		if ("SaveAndContinue".equals(submitButton) || (submitButton != null && submitButton.startsWith("Save"))) {
			return saveAndContinue();
		} else if ("Back".equals(submitButton) || (submitButton != null && submitButton.endsWith("Back"))) {
			return redirect("MeetingDetails.action?id=" + id);
		}
		return execute();
	}

	private void addSelectedPersonAsGuest() {
		if (existingPerson != null && !Long.valueOf(-1).equals(existingPerson)) {
			Person person = personDao.load(existingPerson);
			addPersonAsGuest(person);
		}
	}

	private void addNewPersonAndAddAsGuest() {
		// Check this email address does not exist
		if (emailAddressExists()) {
			Person existingPerson = securityDao.getPersonForEmail(newPersonEmail);
			addPersonAsGuest(existingPerson);
		} else {
			Person newPerson = personService.createPerson(newPersonName, newPersonEmail,null);
			newPersonName = null;
			newPersonEmail = null;
		}
	}

	private void addPersonAsGuest(Person newPerson) {
		guestService.addGuest(newPerson, meeting);
		guestDao.flush();
	}

	private boolean emailAddressExists() {
		try {
			securityDao.getPersonForEmail(newPersonEmail);
			return true;
		} catch (RecordNotFoundException e) {
			return false;
		}
	}

	public String delete() {
		guestDao.delete(deleteId);
		guestDao.flush();
		return execute();
	}

	public String saveAndContinue() {
		return redirect("BuildAgenda.action?id=" + id);
	}

	// Getters and setters
	public void setDartMeetingDao(DartMeetingDao dartMeetingDao) {
		this.dartMeetingDao = dartMeetingDao;
	}

	public void setGuestDao(GuestDao guestDao) {
		this.guestDao = guestDao;
	}

	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}

	public void setSecurityDao(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DartMeeting getMeeting() {
		return meeting;
	}

	public void setMeeting(DartMeeting meeting) {
		this.meeting = meeting;
	}

	public List<Guest> getAttendingGuests() {
		return attendingGuests;
	}

	public void setAttendingGuests(List<Guest> attendingGuests) {
		this.attendingGuests = attendingGuests;
	}

	public List<Person> getNotAttending() {
		return notAttending;
	}

	public void setNotAttending(List<Person> notAttending) {
		this.notAttending = notAttending;
	}

	public String getNewPersonName() {
		return newPersonName;
	}

	public void setNewPersonName(String newPersonName) {
		this.newPersonName = newPersonName;
	}

	public String getNewPersonEmail() {
		return newPersonEmail;
	}

	public void setNewPersonEmail(String newPersonEmail) {
		this.newPersonEmail = newPersonEmail;
	}

	public Long getDeleteId() {
		return deleteId;
	}

	public void setDeleteId(Long deleteId) {
		this.deleteId = deleteId;
	}

	public Long getExistingPerson() {
		return existingPerson;
	}

	public void setExistingPerson(Long existingPerson) {
		this.existingPerson = existingPerson;
	}

	public String getSubmitButton() {
		return submitButton;
	}

	public void setSubmitButton(String submitButton) {
		this.submitButton = submitButton;
	}
	
}


