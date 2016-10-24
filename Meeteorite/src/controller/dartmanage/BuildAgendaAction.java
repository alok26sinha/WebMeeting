package controller.dartmanage;

import java.util.ArrayList;
import java.util.List;

import model.AgendaItem;
import model.DartMeeting;
import type.MinuteDuration;
import controller.support.BaseAction;
import dao.AgendaItemDao;
import dao.DartMeetingDao;
import dao.PersonDao;

@SuppressWarnings("serial")
public class BuildAgendaAction extends BaseAction {
	public Long id;
	public DartMeeting meeting;
	public String newAgendaItemDescription;
	public String newSubAgendaItem;
	public MinuteDuration newAgendaItemDurationInMinutes;
	public Long deleteId;
	public List<MinuteDuration> durationOptions;
	public MinuteDuration totalTime;
	public int remainingTime;
	public MinuteDuration agendaTime;
	public String submitButton;
	public Long editedId;
	public String caller;
	public String inputValid;
	public Long itemId;
	public Long itemOwnerId;
	public String itemDescription;

	protected AgendaItemDao agendaItemDao;
	protected DartMeetingDao dartMeetingDao;
	private PersonDao personDao;

	@Override
	public String execute() {
		meeting = dartMeetingDao.load(id);

		durationOptions = new ArrayList<MinuteDuration>();
		durationOptions.add(MinuteDuration.create(2));
		durationOptions.add(MinuteDuration.create(5));
		durationOptions.add(MinuteDuration.create(10));
		durationOptions.add(MinuteDuration.create(15));
		durationOptions.add(MinuteDuration.create(20));
		durationOptions.add(MinuteDuration.create(25));
		durationOptions.add(MinuteDuration.create(30));
		durationOptions.add(MinuteDuration.create(35));
		durationOptions.add(MinuteDuration.create(40));
		durationOptions.add(MinuteDuration.create(45));
		durationOptions.add(MinuteDuration.create(50));
		durationOptions.add(MinuteDuration.create(55));
		durationOptions.add(MinuteDuration.create(60));
		durationOptions.add(MinuteDuration.create(90));
		durationOptions.add(MinuteDuration.create(120));

		totalTime = MinuteDuration.create(meeting.durationInMinutes);
		
		//Allow 5 mins for start  and end of meeting
		agendaTime = MinuteDuration.create(5);
				
		for (AgendaItem item : meeting.agendaItems) {
			agendaTime = agendaTime.add(item.durationInMinutes);
		}

		remainingTime = (int) (totalTime.getMinutes() - agendaTime.getMinutes());

		if ("SaveAndContinue".equals(submitButton) || (submitButton != null && submitButton.startsWith("Save"))) {
			if (inputValid != null && "true".equals(inputValid)) {
				return redirect("MeetingSummary.action?id=" + id);
			}
		} else if ("Back".equals(submitButton) || (submitButton != null && submitButton.endsWith("Back"))) {
			return redirect("InviteGuests.action?id=" + id);
		}
		
		//Default value
		if( newAgendaItemDurationInMinutes == null) {
			newAgendaItemDurationInMinutes = MinuteDuration.create(15);
		}
		if (itemOwnerId == null) {
			itemOwnerId = meeting.organiser.getId();
		}
		
		newAgendaItemDescription = meeting.newAgendaItemDescription;
		newSubAgendaItem = meeting.newSubAgendaItem;
		
		return SUCCESS;
	}


	public String delete() {
		if (deleteId != null) {
			agendaItemDao.delete(deleteId);
			agendaItemDao.flush();
			meeting = dartMeetingDao.load(id);
			for (int i = 0; i < meeting.agendaItems.size(); i++) {
				meeting.agendaItems.get(i).number = i + 1;
			}
			dartMeetingDao.save(meeting);
		}
		return execute();
	}
	
	public String saveAgendaItemDescription(){
		AgendaItem item = agendaItemDao.load(id);
		item.description = itemDescription;
		return NONE;
	}
	
	public String saveSubAgendaItem(){
		AgendaItem item = agendaItemDao.load(id);
		item.subAgendaItem = itemDescription;
		return NONE;
	}
	
	public String saveNewAgendaItemDescription(){
		DartMeeting meeting = dartMeetingDao.load(id);
		meeting.newAgendaItemDescription = newAgendaItemDescription;
		return NONE;
	}
	
	public String saveNewSubAgendaItem(){
		DartMeeting meeting = dartMeetingDao.load(id);
		meeting.newSubAgendaItem = newSubAgendaItem;
		return NONE;
	}
	
	public String adjustDuration(){
		execute();
		
		meeting.durationInMinutes = (int)agendaTime.getMinutes();
		dartMeetingDao.flush();
		
		return execute();
	}
	
	public String edit() {
		return redirect("AgendaItemEdit.action?id=" + id + "&detailId=" + editedId + "&caller=" + caller);
	}
	
	public String moveUp () {
		meeting = dartMeetingDao.load(id);
		int itemIndex = findMovedItemById();
		if (itemIndex > 0) {
			// clicked element position in range [1..agendaItems.size()[
			switchItems(itemIndex, itemIndex - 1);
		}
		return execute();
	}

	public String moveDown () {
		meeting = dartMeetingDao.load(id);
		int itemIndex = findMovedItemById();
		if (itemIndex >= 0 && itemIndex < meeting.agendaItems.size() - 1) {
			// clicked element position in range [0..agendaItems.size() - 1[
			switchItems(itemIndex + 1, itemIndex);
		}
		return execute();
	}
	
	private void switchItems(int indexFrom, int indexTo) {
		meeting.agendaItems.get(indexFrom).number--;
		meeting.agendaItems.get(indexTo).number++;
		agendaItemDao.save(meeting.agendaItems.get(indexFrom));
		agendaItemDao.save(meeting.agendaItems.get(indexTo));
		agendaItemDao.flush();
		dartMeetingDao.evict(meeting);
	}
	
	private int findMovedItemById() {
		for (int i = 0; i < meeting.agendaItems.size(); i++) {
			if (meeting.agendaItems.get(i).getId().longValue() == itemId.longValue()) {
				return i;
			}
		}
		return -1;
	}

	// Getters and setters
	public void setAgendaItemDao(AgendaItemDao agendaItemDao) {
		this.agendaItemDao = agendaItemDao;
	}

	public void setDartMeetingDao(DartMeetingDao dartMeetingDao) {
		this.dartMeetingDao = dartMeetingDao;
	}
	
	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
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


	public String getNewAgendaItemDescription() {
		return newAgendaItemDescription;
	}


	public void setNewAgendaItemDescription(String newAgendaItemDescription) {
		this.newAgendaItemDescription = newAgendaItemDescription;
	}


	public String getNewSubAgendaItem() {
		return newSubAgendaItem;
	}


	public void setNewSubAgendaItem(String newSubAgendaItem) {
		this.newSubAgendaItem = newSubAgendaItem;
	}


	public MinuteDuration getNewAgendaItemDurationInMinutes() {
		return newAgendaItemDurationInMinutes;
	}


	public void setNewAgendaItemDurationInMinutes(
			MinuteDuration newAgendaItemDurationInMinutes) {
		this.newAgendaItemDurationInMinutes = newAgendaItemDurationInMinutes;
	}


	public Long getDeleteId() {
		return deleteId;
	}


	public void setDeleteId(Long deleteId) {
		this.deleteId = deleteId;
	}


	public List<MinuteDuration> getDurationOptions() {
		return durationOptions;
	}


	public void setDurationOptions(List<MinuteDuration> durationOptions) {
		this.durationOptions = durationOptions;
	}


	public MinuteDuration getTotalTime() {
		return totalTime;
	}


	public void setTotalTime(MinuteDuration totalTime) {
		this.totalTime = totalTime;
	}


	public int getRemainingTime() {
		return remainingTime;
	}


	public void setRemainingTime(int remainingTime) {
		this.remainingTime = remainingTime;
	}


	public MinuteDuration getAgendaTime() {
		return agendaTime;
	}


	public void setAgendaTime(MinuteDuration agendaTime) {
		this.agendaTime = agendaTime;
	}


	public String getSubmitButton() {
		return submitButton;
	}


	public void setSubmitButton(String submitButton) {
		this.submitButton = submitButton;
	}


	public Long getEditedId() {
		return editedId;
	}


	public void setEditedId(Long editedId) {
		this.editedId = editedId;
	}


	public String getCaller() {
		return caller;
	}


	public void setCaller(String caller) {
		this.caller = caller;
	}


	public String getInputValid() {
		return inputValid;
	}


	public void setInputValid(String inputValid) {
		this.inputValid = inputValid;
	}


	public Long getItemId() {
		return itemId;
	}


	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}


	public Long getItemOwnerId() {
		return itemOwnerId;
	}


	public void setItemOwnerId(Long itemOwnerId) {
		this.itemOwnerId = itemOwnerId;
	}


	public String getItemDescription() {
		return itemDescription;
	}


	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}
	
	
}
