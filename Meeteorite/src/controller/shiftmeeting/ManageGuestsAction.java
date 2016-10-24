package controller.shiftmeeting;

import java.util.List;

import javax.annotation.Resource;

import model.Guest;
import model.Person;
import model.ShiftMeeting;
import security.SecurityService;
import service.GuestService;
import service.PersonService;
import type.StringUtils;
import controller.support.BaseAction;
import dao.GuestDao;
import dao.PersonDao;
import dao.RecordNotFoundException;
import dao.SecurityDao;
import dao.ShiftMeetingDao;


@SuppressWarnings("serial")
public class ManageGuestsAction extends BaseAction {
	public Long id;
	public ShiftMeeting meeting;
	public List<Guest> attendingGuests;
	public List<Person> notAttending;
	public String newPersonName;
	public String newPersonEmail;
	public Long deleteId;
	public Long existingPerson;
	public String submitButton;

	private ShiftMeetingDao shiftMeetingDao;
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
		meeting = shiftMeetingDao.load(id);
		attendingGuests = guestDao.getAttendingGuests(meeting);
		notAttending = guestDao.getNotAttening(meeting);
		return SUCCESS;
	}

	public String addGuest() {
		if (!StringUtils.isEmpty(newPersonName)) {
			if (!StringUtils.isEmpty(newPersonEmail))
				addNewPersonAndAddAsGuest();
			else
				addActionError("Email address cannot be empty");
		} else {
			addSelectedPersonAsGuest();
		}
		guestDao.flush();
		
		if ("StartIdeas".equals(submitButton)) {
			return redirect("MeetingStartIdeas.action?id=" + id);
		} else if ("MeetingDetails".equals(submitButton)) {
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
		if (!emailAddressExists()) {
			Person newPerson = personService.createPerson(newPersonName, newPersonEmail,null);
			addPersonAsGuest(newPerson);
			newPersonName = null;
			newPersonEmail = null;
		} else {
			addActionError("A person with this email address already exists.");
		}
	}

	private void addPersonAsGuest(Person newPerson) {
		meeting = shiftMeetingDao.load(id);
		guestService.addGuest(newPerson, meeting);
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
		return redirect("MeetingStartIdeas.action?id=" + id);
	}

	// Getters and setters
	public void setShiftMeetingDao(ShiftMeetingDao shiftMeetingDao) {
		this.shiftMeetingDao = shiftMeetingDao;
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

	public ShiftMeeting getMeeting() {
		return meeting;
	}

	public void setMeeting(ShiftMeeting meeting) {
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
