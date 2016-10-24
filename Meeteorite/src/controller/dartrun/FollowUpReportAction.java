package controller.dartrun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.AgendaItem;
import model.DartItem;
import model.Guest;
import model.Person;
import model.PrivateNotes;
import model.Status;
import dao.DartItemDao;
import dao.GuestDao;

@SuppressWarnings("serial")
public class FollowUpReportAction extends SummaryCloseAction {
	public String startDate;
	public String startTime;
	public String endDate;
	public String endTime;
	public Long dartItemId;
	public List<Guest> attendingGuests;
	public Person currentUser;
//	public Long id;
	public String comment;

	private DartItemDao dartItemDao;
	private GuestDao guestDao;

	public String agendaReviewPrivateNotes;
	public Map<String, String> agendaItemPrivateNotesMap = new HashMap<String, String>();

	@Override
	public String execute() {
		super.execute();
		
		currentUser = securityContext.getUser();

		attendingGuests = guestDao.getAttendingGuests(meeting);

		startDate = meeting.startDateTime.format("yyyy-MM-dd");
		startTime = meeting.startDateTime.format("HH:mm");
		endDate = meeting.getEndDateTime().format("yyyy-MM-dd");
		endTime = meeting.getEndDateTime().format("HH:mm");

		PrivateNotes pn = privateNotesDao.getAgendaReviewNotes(getSecurityContext().getUser(), meeting);
		if(pn!=null){
			agendaReviewPrivateNotes = pn.agendaReviewNotes;
		}
		for(AgendaItem ai : meeting.agendaItems){
			pn = privateNotesDao.getAgendaItemNotes(getSecurityContext().getUser(), ai);
			if(pn!=null){
				agendaItemPrivateNotesMap.put(ai.getId().toString(), pn.agendaItemNotes);
			}
		}
		
		return SUCCESS;
	}

	public String startProgress() {
//		setTractionStatus(Status.IN_PROGRESS);
		return SUCCESS;
	}
	
	public String report(){
		execute();
		
		return ITEXT;
	}

	public String close() {
		setTractionStatus(Status.CLOSED);
		return SUCCESS;
	}
	
	public String saveComment () {
	    DartItem dartItem = dartItemDao.load(id);
	    dartItem.comment = comment;
	    
	    return NONE;
	}

	private void setTractionStatus(int status) {
		execute();
		DartItem dartItem = dartItemDao.load(dartItemId);

		dartItem.status = status;
	}



	public void setDartItemDao(DartItemDao dartItemDao) {
		this.dartItemDao = dartItemDao;
	}

	public void setGuestDao(GuestDao guestDao) {
		this.guestDao = guestDao;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Long getDartItemId() {
		return dartItemId;
	}

	public void setDartItemId(Long dartItemId) {
		this.dartItemId = dartItemId;
	}

	public List<Guest> getAttendingGuests() {
		return attendingGuests;
	}

	public void setAttendingGuests(List<Guest> attendingGuests) {
		this.attendingGuests = attendingGuests;
	}

	public Person getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(Person currentUser) {
		this.currentUser = currentUser;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	

}
