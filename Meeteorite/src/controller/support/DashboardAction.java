package controller.support;

import java.util.List;

import javax.annotation.Resource;

import model.Meeting;
import model.Person;
import model.ShiftMeeting;
import service.EventService;
import service.MeetingService;
import service.SubscriptionService;
import dao.MeetingDao;

@SuppressWarnings("serial")
public class DashboardAction extends BaseAction {
	public List<Meeting> meetingsPending;
	public List<Meeting> meetingsAttending;
	public List<Meeting> meetingsPast;
	public List<Meeting> meetingsDeclined;
	public List<Meeting> meetingsDrafts;
	public Long deleteId;
	public String lastTab;
	public boolean canCreateMeetings;

	private MeetingDao meetingDao;
	@Resource 
	private SubscriptionService subscriptionService;
	@Resource
	private EventService eventService;
	@Resource
	private MeetingService meetingService;

	public String execute() {
		Person user = getSecurityContext().getUser();
        meetingsPending = meetingDao.pendingDecision(user);
        meetingsDrafts = meetingDao.drafts(user);
		meetingsAttending = meetingDao.attending(user);
		meetingsPast = meetingDao.past(user);
		meetingsDeclined = meetingDao.declined(user);
		
		
		
		canCreateMeetings = subscriptionService.hasAFullSubscription(user);
		
		eventService.logEvent(user, "View Dashboard");

		return SUCCESS;
	}

	public boolean isShiftMeeting(Long meetingId) {
		Meeting meeting = meetingDao.load(meetingId);
		return meeting instanceof ShiftMeeting;
	}

	public String deleteMeeting() {
		Meeting meeting = meetingDao.load(deleteId);
		meetingDao.deleteAllMeetingData(meeting);
		return execute();
	}
	
	public String fullDeleteMeeting(){
		if(getSecurityContext().getUser().administrator){
			Meeting meeting = meetingDao.load(deleteId);
			meetingService.deleteMeeting(meeting);
		}
		return execute();
	}
	
	public String updateLastTab(){
		Person user = getSecurityContext().getUser();
		user.lastDashboardTab = lastTab;
		return NONE;
	}

	// Getters and setters
	public void setMeetingDao(MeetingDao meetingDao) {
		this.meetingDao = meetingDao;
	}

	public List<Meeting> getMeetingsPending() {
		return meetingsPending;
	}

	public void setMeetingsPending(List<Meeting> meetingsPending) {
		this.meetingsPending = meetingsPending;
	}

	public List<Meeting> getMeetingsAttending() {
		return meetingsAttending;
	}

	public void setMeetingsAttending(List<Meeting> meetingsAttending) {
		this.meetingsAttending = meetingsAttending;
	}

	public List<Meeting> getMeetingsPast() {
		return meetingsPast;
	}

	public void setMeetingsPast(List<Meeting> meetingsPast) {
		this.meetingsPast = meetingsPast;
	}

	public List<Meeting> getMeetingsDeclined() {
		return meetingsDeclined;
	}

	public void setMeetingsDeclined(List<Meeting> meetingsDeclined) {
		this.meetingsDeclined = meetingsDeclined;
	}

	public List<Meeting> getMeetingsDrafts() {
		return meetingsDrafts;
	}

	public void setMeetingsDrafts(List<Meeting> meetingsDrafts) {
		this.meetingsDrafts = meetingsDrafts;
	}

	public Long getDeleteId() {
		return deleteId;
	}

	public void setDeleteId(Long deleteId) {
		this.deleteId = deleteId;
	}

	public String getLastTab() {
		return lastTab;
	}

	public void setLastTab(String lastTab) {
		this.lastTab = lastTab;
	}

	public boolean isCanCreateMeetings() {
		return canCreateMeetings;
	}

	public void setCanCreateMeetings(boolean canCreateMeetings) {
		this.canCreateMeetings = canCreateMeetings;
	}
	
	

}
