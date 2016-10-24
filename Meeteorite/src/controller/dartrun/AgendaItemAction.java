package controller.dartrun;

import java.util.List;

import model.AgendaItem;
import model.DartItem;
import model.DartMeeting;
import model.Guest;
import model.Person;
import model.PrivateNotes;
import model.Status;
import service.DartTimeMeasurementService;
import type.StringUtils;
import type.UsefulDate;
import type.UsefulDateTime;
import controller.support.BaseAction;
import dao.AgendaItemDao;
import dao.DartItemDao;
import dao.DartMeetingDao;
import dao.GuestDao;
import dao.PersonDao;
import dao.PrivateNotesDao;

@SuppressWarnings("serial")
public class AgendaItemAction extends BaseAction {
	public Long id;
	public Long upperFormId;
	public Long deleteId;
	public int item;
	public int upperItem;
	public DartMeeting meeting;
	public List<DartItem> dartItems;
	public List<Guest> guests;
	public AgendaItem agendaItem = new AgendaItem();
	public String buttonText = "NEXT AGENDA ITEM";
	public float timeRemaining;

	public String newAction;
	public Long newResponsiblePersonId;
	public String newTiming;
	public String discussionPoint;
	public String submitButton;
	public String parkedThoughts;
	public String actionText;

	public Long editId;
	public DartItem dartItem;
	public String caller;
	public String privateNotes;

	private DartItemDao dartItemDao;
	private AgendaItemDao agendaItemDao;
	private DartMeetingDao dartMeetingDao;
	private GuestDao guestDao;
	private PersonDao personDao;
	private DartTimeMeasurementService timeService;
	private PrivateNotesDao privateNotesDao;

	@Override
	public String execute() {
		meeting = dartMeetingDao.load(id);
		if (meeting.actualStartDateTime == null) {
			meeting.actualStartDateTime = UsefulDateTime.now();
			dartMeetingDao.save(meeting);
			dartMeetingDao.flush();
		}
		agendaItem = meeting.agendaItems.get(item);

		discussionPoint = agendaItem.discussionPoint;
		dartItems = dartItemDao.getAllForAgendaItem(agendaItem);
		guests = guestDao.getAttendingGuests(meeting);
		/*
		 * if (item == agendaItems.size()) { buttonText =
		 * "END OF ITEMS, VIEW SUMMARY"; }
		 */

		newTiming = UsefulDate.today().getSaasuFormat();
		timeRemaining = timeService.getTimeRemaining(meeting, item);

		upperFormId = id;
		upperItem = item;
		newAction = agendaItem.newAction;
		
		PrivateNotes pn = privateNotesDao.getAgendaItemNotes(getSecurityContext().getUser(), agendaItem);
		if(pn!=null){
			privateNotes = pn.agendaItemNotes;
		}

		return SUCCESS;
	}

	public String addNew() {
		meeting = dartMeetingDao.load(id);
		agendaItem = meeting.agendaItems.get(item);

		agendaItem.discussionPoint = discussionPoint;
		agendaItemDao.flush();

		if (!StringUtils.isEmpty(newAction) && newResponsiblePersonId != null
				&& newTiming != null) {
			DartItem dartItem = new DartItem();
			dartItem.action = newAction;

			Person person = personDao.load(newResponsiblePersonId);
			dartItem.responsiblePerson = person;

			dartItem.timing = UsefulDate.createSaasuFormat(newTiming);

			dartItem.agendaItem = agendaItem;
			dartItem.status = Status.OPEN;

			dartItemDao.save(dartItem);
			dartItemDao.flush();

			newAction = null;
			newResponsiblePersonId = null;
			newTiming = null;
		}

		return execute();
	}

	public String delete() {
		if (deleteId != null) {
			dartItemDao.delete(deleteId);
			dartItemDao.flush();
		}

		return execute();
	}

	public String nextItem() {
		if (id == null) {
			id = upperFormId;
			item = upperItem;
		}
		meeting = dartMeetingDao.load(id);

		if ("NextItem".equals(submitButton)
				|| (submitButton != null && submitButton.startsWith("Next"))) {
			if (item == (meeting.agendaItems.size() - 1)) {
				return redirect("SummaryClose.action?id=" + id);
			} else {
				item++;
				return redirect("AgendaItem.action?id=" + id + "&item=" + item);
			}
		} else if ("PrevItem".equals(submitButton)
				|| (submitButton != null && submitButton.endsWith("Back"))) {
			if (item == 0) {
				return redirect("AgendaRecap.action?id=" + id);
			} else {
				item--;
				return redirect("AgendaItem.action?id=" + id + "&item=" + item);
			}
		} else {
			return redirectDashboard();
		}
	}

	public String edit() {
		meeting = dartMeetingDao.load(id);
		guests = guestDao.getAttendingGuests(meeting);
		dartItem = dartItemDao.load(editId);
		newResponsiblePersonId = dartItem.responsiblePerson.getId();
		newTiming = dartItem.timing.getSaasuFormat();
		newAction = dartItem.action;
		return EDIT;
	}

	public String save() {
		dartItem = dartItemDao.load(editId);
		dartItem.action = newAction;

		Person person = personDao.load(newResponsiblePersonId);
		dartItem.responsiblePerson = person;

		dartItem.timing = UsefulDate.createSaasuFormat(newTiming);

		dartItemDao.save(dartItem);
		dartItemDao.flush();
		if ("AgendaItem".equals(caller)) {
			return execute();
		} else {
			return redirect("SummaryClose.action?id=" + id);
		}
	}
	
	public String saveParkedThoughts(){
		AgendaItem agendaItem = agendaItemDao.load(id);
		
		agendaItem.parkedThoughts = parkedThoughts;
		
		return NONE;
	}
	
	public String savePrivateNotes() {
		
		AgendaItem agendaItem = agendaItemDao.load(id);
		Person person = getSecurityContext().getUser();		
		
		PrivateNotes notes = privateNotesDao.getAgendaItemNotes(person, agendaItem);
		if(notes==null){
			notes = new PrivateNotes();
			notes.person = person;
			notes.agendaItem = agendaItem;
		}		
		
		notes.agendaItemNotes = privateNotes;
		privateNotesDao.save(notes);		
		return NONE;
	}
	
	public String saveDiscussionPoints(){
		AgendaItem agendaItem = agendaItemDao.load(id);
		
		agendaItem.discussionPoint = discussionPoint;
		
		return NONE;
	}
	
	public String saveAction () {
	    DartItem dartItem = dartItemDao.load(id);
	    dartItem.action = actionText;
	    
	    return NONE;
	}

	public String saveNewAction(){
		AgendaItem agendaItem = agendaItemDao.load(id);
		
		agendaItem.newAction = newAction;
		
		return NONE;
	}

	// Getters and setters
	public void setDartItemDao(DartItemDao dartItemDao) {
		this.dartItemDao = dartItemDao;
	}

	public void setAgendaItemDao(AgendaItemDao agendaItemDao) {
		this.agendaItemDao = agendaItemDao;
	}

	public void setDartMeetingDao(DartMeetingDao dartMeetingDao) {
		this.dartMeetingDao = dartMeetingDao;
	}

	public void setGuestDao(GuestDao guestDao) {
		this.guestDao = guestDao;
	}

	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}

	public void setTimeService(DartTimeMeasurementService timeService) {
		this.timeService = timeService;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUpperFormId() {
		return upperFormId;
	}

	public void setUpperFormId(Long upperFormId) {
		this.upperFormId = upperFormId;
	}

	public Long getDeleteId() {
		return deleteId;
	}

	public void setDeleteId(Long deleteId) {
		this.deleteId = deleteId;
	}

	public int getItem() {
		return item;
	}

	public void setItem(int item) {
		this.item = item;
	}

	public int getUpperItem() {
		return upperItem;
	}

	public void setUpperItem(int upperItem) {
		this.upperItem = upperItem;
	}

	public DartMeeting getMeeting() {
		return meeting;
	}

	public void setMeeting(DartMeeting meeting) {
		this.meeting = meeting;
	}

	public List<DartItem> getDartItems() {
		return dartItems;
	}

	public void setDartItems(List<DartItem> dartItems) {
		this.dartItems = dartItems;
	}

	public List<Guest> getGuests() {
		return guests;
	}

	public void setGuests(List<Guest> guests) {
		this.guests = guests;
	}

	public AgendaItem getAgendaItem() {
		return agendaItem;
	}

	public void setAgendaItem(AgendaItem agendaItem) {
		this.agendaItem = agendaItem;
	}

	public String getButtonText() {
		return buttonText;
	}

	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}

	public float getTimeRemaining() {
		return timeRemaining;
	}

	public void setTimeRemaining(float timeRemaining) {
		this.timeRemaining = timeRemaining;
	}

	public String getNewAction() {
		return newAction;
	}

	public void setNewAction(String newAction) {
		this.newAction = newAction;
	}

	public Long getNewResponsiblePersonId() {
		return newResponsiblePersonId;
	}

	public void setNewResponsiblePersonId(Long newResponsiblePersonId) {
		this.newResponsiblePersonId = newResponsiblePersonId;
	}

	public String getNewTiming() {
		return newTiming;
	}

	public void setNewTiming(String newTiming) {
		this.newTiming = newTiming;
	}

	public String getDiscussionPoint() {
		return discussionPoint;
	}

	public void setDiscussionPoint(String discussionPoint) {
		this.discussionPoint = discussionPoint;
	}

	public String getSubmitButton() {
		return submitButton;
	}

	public void setSubmitButton(String submitButton) {
		this.submitButton = submitButton;
	}

	public String getParkedThoughts() {
		return parkedThoughts;
	}

	public void setParkedThoughts(String parkedThoughts) {
		this.parkedThoughts = parkedThoughts;
	}

	public String getActionText() {
		return actionText;
	}

	public void setActionText(String actionText) {
		this.actionText = actionText;
	}

	public Long getEditId() {
		return editId;
	}

	public void setEditId(Long editId) {
		this.editId = editId;
	}

	public DartItem getDartItem() {
		return dartItem;
	}

	public void setDartItem(DartItem dartItem) {
		this.dartItem = dartItem;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public void setPrivateNotesDao(PrivateNotesDao privateNotesDao) {
		this.privateNotesDao = privateNotesDao;
	}
	
	

}
