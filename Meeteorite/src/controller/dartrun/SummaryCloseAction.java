package controller.dartrun;

import itext.SymDocument;
import itext.SymPdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.annotation.Resource;

import model.AgendaItem;
import model.DartItem;
import model.DartMeeting;
import model.Person;
import model.PrivateNotes;
import service.DartTimeMeasurementService;
import service.EventService;
import subsystems.mail.OutboundService;
import type.StringUtils;
import type.UsefulDateTime;
import view.DartFollowUpReport;
import controller.support.BaseAction;
import dao.AgendaItemDao;
import dao.DartItemDao;
import dao.DartMeetingDao;
import dao.PrivateNotesDao;

@SuppressWarnings("serial")
public class SummaryCloseAction extends BaseAction {
	public Long id;
	public DartMeeting meeting;
	public String submitButton;
	public float timeRemaining;
	public String action;
	public String parkedThoughts;
	public String privateNotes;

	private DartItemDao dartItemDao;
	private DartMeetingDao dartMeetingDao;
	private AgendaItemDao agendaItemDao;
	protected PrivateNotesDao privateNotesDao;
	private DartTimeMeasurementService timeService;
	@Resource
	private EventService eventService;

	@Override
	public String execute() {
		meeting = dartMeetingDao.load(id);

		timeRemaining = timeService.getTimeRemainingForSummaryClose(meeting);
		
		PrivateNotes pn = privateNotesDao.getSummaryCloseNotes(getSecurityContext().getUser(), meeting);
		if(pn!=null){
			privateNotes = pn.summaryCloseNotes;
		}
		
		return SUCCESS;
	}

	public String convertNewLine(String content) {
		return type.StringUtils.convertNewLine(content);
	}

	public String endMeeting() throws Exception {
		meeting = dartMeetingDao.load(id);

		if ("Back".equals(submitButton)
				|| (submitButton != null && submitButton.endsWith("Back"))) {
			return redirect("AgendaItem.action?id=" + id + "&item="
					+ (meeting.agendaItems.size() - 1));
		} else if ("CloseAndPdf".equals(submitButton)) {

			if (meeting.followupReportSent == false) {
				OutboundService mailService = new OutboundService();
				// notification email is send to everybody, including meeting
				// organizer and guest who declined invitation

				byte[] report = writeReport();

				meeting.complete = true;
				if(meeting.meetingClosedTime==null){
					meeting.meetingClosedTime = UsefulDateTime.now();
				}
				// just to make sure
				meeting.invitationSend = true;
				meeting.followupReportSent = true;

				mailService.sendReportMessage(meeting, meeting.guests,
						"DART Meeting - ",
						StringUtils.isEmpty(meeting.name) ? "DART Meeting"
								: meeting.name, "application/pdf", report);

			}

			eventService.logEvent(getSecurityContext().getUser(),
					"End DART Meeting");

			return redirectDashboard();

		} else {
			meeting.complete = true;
			if(meeting.meetingClosedTime==null){
				meeting.meetingClosedTime = UsefulDateTime.now();
			}
			eventService.logEvent(getSecurityContext().getUser(),
					"End DART Meeting");
			return redirectDashboard();
		}
	}

	public String saveAction() {
		DartItem dartItem = dartItemDao.load(id);
		dartItem.action = action;
		return NONE;
	}

	private byte[] writeReport() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		SymDocument document = new SymDocument(true);

		SymPdfWriter.getInstance(document, out);

		document.open();

		DartFollowUpReport report = new DartFollowUpReport(document, meeting);
		report.render();

		document.close();

		return out.toByteArray();
	}

	public String saveParkedThoughts() {
		meeting = dartMeetingDao.load(id);
		meeting.summaryCloseParkedThoughts = parkedThoughts;
		return NONE;
	}
	
	public String savePrivateNotes() {
		
		meeting = dartMeetingDao.load(id);
		Person person = getSecurityContext().getUser();		
		
		PrivateNotes notes = privateNotesDao.getSummaryCloseNotes(person, meeting);
		if(notes==null){
			notes = new PrivateNotes();
			notes.person = person;
			notes.meeting = meeting;
		}		
		
		notes.summaryCloseNotes = privateNotes;
		privateNotesDao.save(notes);
		return NONE;
	}

	public List<DartItem> dartItems(Long agendaItemId) {
		AgendaItem agendaItem = agendaItemDao.load(agendaItemId);
		return dartItemDao.getAllForAgendaItem(agendaItem);
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

	public void setTimeService(DartTimeMeasurementService timeService) {
		this.timeService = timeService;
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

	public String getSubmitButton() {
		return submitButton;
	}

	public void setSubmitButton(String submitButton) {
		this.submitButton = submitButton;
	}

	public float getTimeRemaining() {
		return timeRemaining;
	}

	public void setTimeRemaining(float timeRemaining) {
		this.timeRemaining = timeRemaining;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getParkedThoughts() {
		return parkedThoughts;
	}

	public void setParkedThoughts(String parkedThoughts) {
		this.parkedThoughts = parkedThoughts;
	}

	public void setPrivateNotesDao(PrivateNotesDao privateNotesDao) {
		this.privateNotesDao = privateNotesDao;
	}
	
}
