package controller.support;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import model.DartMeeting;
import model.Guest;
import model.Meeting;
import model.ShiftMeeting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import service.EventService;
import subsystems.mail.MailMessage;
import subsystems.mail.OutboundService;
import type.StringUtils;

import common.Config;

import dao.GuestDao;
import dao.MeetingDao;

@SuppressWarnings("serial")
public abstract class BaseSendInvitationAction extends BaseAction {
	private static Log log = LogFactory.getLog(BaseSendInvitationAction.class);

	public Long id;
	public Meeting meeting;
	public List<Guest> guests;
	public String startDate;
	public String startTime;
	public String endDate;
	public String endTime;
	public String submitButton;
	public List<Guest> guestsNeedingInvitation;

	protected MeetingDao meetingDao;
	protected GuestDao guestDao;
	@Resource
	private EventService eventService;

	private OutboundService outboundMailService = new OutboundService();

	@Override
	public String execute() {
		meeting = meetingDao.load(id);
		guests = guestDao.getAttendingGuests(meeting);

		startDate = meeting.startDateTime.format("yyyy-MM-dd");
		startTime = meeting.startDateTime.format("HH:mm");
		endDate = meeting.getEndDateTime().format("yyyy-MM-dd");
		endTime = meeting.getEndDateTime().format("HH:mm");

		guestsNeedingInvitation = guestDao.getNeedInvitationSent(meeting);

		return SUCCESS;
	}

	public String sendInvitation() {
		meeting = meetingDao.load(id);
		setParameters();
		meetingDao.save(meeting);
		meetingDao.flush();
		if (!(meeting instanceof DartMeeting)
				&& ("StartIdeas".equals(submitButton))) {
			return redirect("MeetingStartIdeas.action?id=" + id);
		} else if ("Back".equals(submitButton)
				|| (submitButton != null && submitButton.endsWith("Back"))) {
			return redirect("BuildAgenda.action?id=" + id);
		} else if ("AllMeetings".equals(submitButton)
				|| (submitButton != null && submitButton
						.endsWith("All Meetings"))) {
			return redirectDashboard();
		} else {
			log.info("Sending invitations");
			List<Guest> guestsNeedingInvitation = guestDao
					.getNeedInvitationSent(meeting);
			for (Guest guest : guestsNeedingInvitation) {
				sendInvitation(guest);
			}

			meeting.invitationSend = true;
			meeting = meetingDao.save(meeting);
			meetingDao.flush();

			logEvent(meeting);

			return redirectDashboard();
		}

	}

	private void logEvent(Meeting meeting) {
		String eventName = "";
		if (meeting instanceof DartMeeting)
			eventName = "DART";
		else if (meeting instanceof ShiftMeeting)
			eventName = "SHIFT";
		else
			log.warn("Unknown meeting type:" + meeting.getClass());

		eventService.logEvent(getSecurityContext().getUser(),
				"Send Invitation " + eventName);

	}

	private void sendInvitation(Guest guest) {
		MailMessage mailMessage = new MailMessage();
		mailMessage.setTo(guest.person.email);

		String adminUser = Config.getInstance().getValue("mail.smtp.user");
		mailMessage.setFrom(adminUser);
		if (meeting instanceof DartMeeting)
			mailMessage.setSubject("DART Meeting Invitation - "
					+ (meeting.name != null ? meeting.name : "")
					+ organisedBy(meeting));
		else
			mailMessage.setSubject("SHIFT Meeting Invitation - "
					+ (meeting.name != null ? meeting.name : "")
					+ organisedBy(meeting));

		String path;
		if (meeting instanceof ShiftMeeting)
			path = "shiftmeeting";
		else
			path = "dartmanage";

		String invitationUrl = Config.getInstance().getValue("app.url") + "/"
				+ path + "/Pending.action?id=" + id;
		String message = StringUtils.convertNewLine(guest.meeting.invitation)
				+ "<br/><br/>" + invitationUrl;

		mailMessage.setContent(message);

		outboundMailService.sendAsynchronous(mailMessage, meeting);

		guest.invitationStatus = Guest.INVITATION_SENT;
	}

	private String organisedBy(Meeting meeting) {
		if (meeting.organiser != null)
			return " (Organised by: " + meeting.organiser.name + ")";
		else
			return "";
	}

	// Getters and setters
	public void setMeetingDao(MeetingDao meetingDao) {
		this.meetingDao = meetingDao;
	}

	public void setGuestDao(GuestDao guestDao) {
		this.guestDao = guestDao;
	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Guest> getGuests() {
		return guests;
	}

	public void setGuests(List<Guest> guests) {
		this.guests = guests;
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

	public String getSubmitButton() {
		return submitButton;
	}

	public void setSubmitButton(String submitButton) {
		this.submitButton = submitButton;
	}

	public List<Guest> getGuestsNeedingInvitation() {
		return guestsNeedingInvitation;
	}

	public void setGuestsNeedingInvitation(List<Guest> guestsNeedingInvitation) {
		this.guestsNeedingInvitation = guestsNeedingInvitation;
	}
	
	
}
