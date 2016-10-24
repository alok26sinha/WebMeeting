package controller.support;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import service.EventService;

import model.AgendaItem;
import model.DartItem;
import model.DartMeeting;
import model.Meeting;
import model.Person;
import model.ShiftMeeting;
import model.Status;
import model.Traction;
import dao.DartItemDao;
import dao.ForesightTractionDao;
import dao.MeetingDao;
import dao.StartingIdeaTractionDao;
import dao.TractionDao;

@SuppressWarnings("serial")
public class ActionItemsAction extends BaseAction {
	public List<Meeting> invitedMeetings;
	public Long dartItemId;
	public Long id;
	public Person currentUser;
	public String comment;

	@Resource
	private MeetingDao meetingDao;
	@Resource
	private TractionDao tractionDao;
	private DartItemDao dartItemDao;
	private StartingIdeaTractionDao startingIdeaTractionDao;
	private ForesightTractionDao foresightTractionDao;
	@Resource
	private EventService eventService;

	public String execute() {
	    currentUser = securityContext.getUser();
		invitedMeetings = meetingDao.onInviteListAndComplete(currentUser);
		
		eventService.logEvent(currentUser, "View Actions");

		return SUCCESS;
	}

	public List<DartItem> getDartItemsFor(Meeting meeting) {
		List<DartItem> dartItems = new ArrayList<DartItem>();

		if (meeting instanceof DartMeeting) {
            List<AgendaItem> agendaItems = ((DartMeeting)meeting).agendaItems;
            for (AgendaItem agendaItem : agendaItems) {
                dartItems.addAll(agendaItem.dartItems);
            }
        }
        return dartItems;
	}
	
	public List<Traction> getShiftItemsFor(Meeting meeting) {
	    List<Traction> tractions = new ArrayList<Traction>();
	    if (meeting instanceof ShiftMeeting) {
	        tractions.addAll(startingIdeaTractionDao.getAll((ShiftMeeting) meeting));
	        tractions.addAll(foresightTractionDao.getAll((ShiftMeeting) meeting));
	    }
	    return tractions;
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
	
	public String saveDartComment(){
		DartItem dartItem = dartItemDao.load(id);
		dartItem.comment = comment;
		return NONE;
	}

	public String saveShiftComment(){
		Traction traction = tractionDao.load(id);
		traction.comments = comment;
		return NONE;
	}
	
	// Getters and setters

	public void setDartItemDao(DartItemDao dartItemDao) {
		this.dartItemDao = dartItemDao;
	}


    public void setStartingIdeaTractionDao(StartingIdeaTractionDao startingIdeaTractionDao) {
        this.startingIdeaTractionDao = startingIdeaTractionDao;
    }

    public void setForesightTractionDao(ForesightTractionDao foresightTractionDao) {
        this.foresightTractionDao = foresightTractionDao;
    }

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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
