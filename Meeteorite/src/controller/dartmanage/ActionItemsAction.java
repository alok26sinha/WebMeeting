package controller.dartmanage;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import model.AgendaItem;
import model.DartItem;
import model.DartMeeting;
import model.Meeting;
import model.Person;
import model.Status;
import controller.support.BaseAction;
import dao.DartItemDao;
import dao.MeetingDao;

@SuppressWarnings("serial")
public class ActionItemsAction extends BaseAction {
	public List<Meeting> invitedMeetings;
	public Long dartItemId;
	public Long id;
	public Person currentUser;

	@Resource
	private MeetingDao meetingDao;
	@Resource
	private DartItemDao dartItemDao;


	public String execute() {
	    currentUser = securityContext.getUser();
		invitedMeetings = meetingDao
				.onInviteListAndComplete(getSecurityContext().getUser());

		return SUCCESS;
	}

	public List<DartItem> getDartItemsFor(DartMeeting meeting) {
		List<DartItem> dartItems = new ArrayList<DartItem>();

		List<AgendaItem> agendaItems = meeting.agendaItems;

		for (AgendaItem agendaItem : agendaItems) {
			dartItems.addAll(agendaItem.dartItems);
		}

		return dartItems;
	}

	public String startProgress() {
//		setDartItemStatus(Status.IN_PROGRESS);
		return SUCCESS;
	}

	public String close() {
		setDartItemStatus(Status.CLOSED);
		return SUCCESS;
	}

	private void setDartItemStatus(int status) {
		execute();
		DartItem dartItem = dartItemDao.load(dartItemId);

		dartItem.status = status;
	}

	// Getters and setters

	public List<Meeting> getInvitedMeetings() {
		return invitedMeetings;
	}

	public void setInvitedMeetings(List<Meeting> invitedMeetings) {
		this.invitedMeetings = invitedMeetings;
	}
	
	
	public Long getDartItemId() {
		return dartItemId;
	}

	public void setDartItemId(Long dartItemId) {
		this.dartItemId = dartItemId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Person getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(Person currentUser) {
		this.currentUser = currentUser;
	}
	

	
}
