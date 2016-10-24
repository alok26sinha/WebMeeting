package controller.dartrun;

import java.util.List;

import common.Config;

import model.DartMeeting;
import model.Guest;
import controller.support.BaseAction;
import dao.DartMeetingDao;
import dao.GuestDao;

@SuppressWarnings("serial")
public class EndMeetingAction extends BaseAction {
	public Long id;
	public DartMeeting meeting;
	public List<Guest> guests;
	public String message;
	public String startDate;
	public String startTime;
	public String endDate;
	public String endTime;

	private DartMeetingDao dartMeetingDao;
	private GuestDao guestDao;

	@Override
	public String execute() {
		meeting = dartMeetingDao.load(id);
		guests = guestDao.getAttendingGuests(meeting);

		String meetingDate = meeting.startDateTime.format("dd MMM yyyy")
				+ " at " + meeting.startDateTime.format("HH:mm");
		message = "Thank you for attending the meeting on the "
				+ meetingDate
				+ ".  Attached please find the meeting outcomes in a report.  You can also access this at any time through the SHIFT Meetings website.\n\n"
				//+ "Meeting details can be found at: "
				//+ Config.getInstance().getValue("app.url")
				//+ "/dartmanage/Dashboard.action\n"
				;

		startDate = meeting.startDateTime.format("yyyy-MM-dd");
		startTime = meeting.startDateTime.format("HH:mm");
		endDate = meeting.getEndDateTime().format("yyyy-MM-dd");
		endTime = meeting.getEndDateTime().format("HH:mm");

		return SUCCESS;
	}

	public String sendSummary() {
		meeting = dartMeetingDao.load(id);
		meeting.complete = true;
		meeting.invitationSend = true;
		return redirectDashboard();
	}

	public void setGuestDao(GuestDao guestDao) {
		this.guestDao = guestDao;
	}

	public void setDartMeetingDao(DartMeetingDao dartMeetingDao) {
		this.dartMeetingDao = dartMeetingDao;
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

	public List<Guest> getGuests() {
		return guests;
	}

	public void setGuests(List<Guest> guests) {
		this.guests = guests;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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
	
	

}
