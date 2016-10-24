package controller.shiftmeeting;

import itext.SymDocument;
import itext.SymPdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.annotation.Resource;

import model.ForesightTraction;
import model.Guest;
import model.ShiftMeeting;
import model.StartingIdeaTraction;
import service.EventService;
import spring.LocalApplicationContext;
import subsystems.mail.OutboundService;
import type.StringUtils;
import type.UsefulDateTime;
import view.ShiftFollowUpReport;

import common.Config;

import controller.support.BaseAction;
import dao.ForesightTractionDao;
import dao.GuestDao;
import dao.ShiftMeetingDao;
import dao.StartingIdeaTractionDao;

@SuppressWarnings("serial")
public class EndMeetingAction extends BaseAction {
	public Long id;
	public ShiftMeeting meeting;
	public List<Guest> guests;
	public String message;
	public String startDate;
	public String startTime;
	public String endDate;
	public String endTime;
	public List<ForesightTraction> foresightTractions;
	public List<StartingIdeaTraction> startingIdeaTractions;
	
	private ShiftMeetingDao shiftMeetingDao;
	private GuestDao guestDao;
	@Resource
	public ForesightTractionDao foresightTractionDao;
	@Resource
	public StartingIdeaTractionDao startingIdeaTractionDao;
	@Resource
	private EventService eventService;
	
	@Override
	public String execute() {
		meeting = shiftMeetingDao.load(id);
		guests = guestDao.getAttendingGuests(meeting);
		foresightTractions = foresightTractionDao.getAll(meeting);
		startingIdeaTractions = startingIdeaTractionDao.getAll(meeting);
		
		String meetingDate = meeting.startDateTime.format("dd MMM yyyy") + " at " + meeting.startDateTime.format("HH:mm");
		message = "Thank you for attending the meeting on the " + meetingDate 
				+ ".  Attached please find the meeting outcomes in a report.  You can also access this at any time through the SHIFT Meetings website.\n\n"
				+ "Meeting details can be found at: " + Config.getInstance().getValue("app.url") 
				+ "/support/Dashboard.action\n";

		startDate = meeting.startDateTime.format("yyyy-MM-dd");
		startTime = meeting.startDateTime.format("HH:mm");
		endDate = meeting.getEndDateTime().format("yyyy-MM-dd");
		endTime = meeting.getEndDateTime().format("HH:mm");
		
		return SUCCESS;
	}
	
	public String sendSummary() throws Exception {
		meeting = shiftMeetingDao.load(id);
		meeting.complete = true;
		if(meeting.meetingClosedTime==null){
			meeting.meetingClosedTime = UsefulDateTime.now();
		}
		meeting.invitationSend = true;
		
		OutboundService mailService = new OutboundService();
		// notification email is send to everybody, including meeting
		// organizer and guest who declined invitation
		
		byte[] report = writeReport();

		mailService.sendReportMessage(meeting, meeting.guests,
				"SHIFT Meeting - ",
				StringUtils.isEmpty(meeting.name) ? "SHIFT Meeting"
						: meeting.name, "application/pdf", report);
		
		eventService.logEvent(getSecurityContext().getUser(), "End SHIFT meeting");
		
		return redirectDashboard();
	}
	
	private byte[] writeReport() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		SymDocument document = new SymDocument(true);
		
		SymPdfWriter.getInstance(document, out);
		
		document.open();
		
		ShiftFollowUpReport report = new ShiftFollowUpReport(document, meeting, LocalApplicationContext.get());
		report.render();
		
		document.close();
		
		return out.toByteArray();
	}
	
	public void setGuestDao(GuestDao guestDao) {
		this.guestDao = guestDao;
	}
	
	public void setShiftMeetingDao(ShiftMeetingDao shiftMeetingDao) {
		this.shiftMeetingDao = shiftMeetingDao;
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

	public List<ForesightTraction> getForesightTractions() {
		return foresightTractions;
	}

	public void setForesightTractions(List<ForesightTraction> foresightTractions) {
		this.foresightTractions = foresightTractions;
	}

	public List<StartingIdeaTraction> getStartingIdeaTractions() {
		return startingIdeaTractions;
	}

	public void setStartingIdeaTractions(
			List<StartingIdeaTraction> startingIdeaTractions) {
		this.startingIdeaTractions = startingIdeaTractions;
	}
	
	
}
