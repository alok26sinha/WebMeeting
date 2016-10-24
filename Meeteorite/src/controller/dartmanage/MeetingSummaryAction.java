package controller.dartmanage;

import javax.annotation.Resource;

import model.DartMeeting;
import model.Meeting;
import type.StringUtils;
import controller.support.BaseSendInvitationAction;
import dao.DartMeetingDao;

@SuppressWarnings("serial")
public class MeetingSummaryAction extends BaseSendInvitationAction {

	@Override
	public String execute() {
		super.execute();

		// if (StringUtils.isEmpty(meeting.invitation))
		meeting.invitation = "You have been invited by "
				+ meeting.organiser.name
				+ " to attend a meeting called: "
				+ meeting.name
				+ "\n\n"
				+ dateAndTime(meeting)
				+ "\n"
				+ (!StringUtils.isEmpty(meeting.location) ? "Place: "
						+ meeting.location + "\n" : "") + "\n"
				+ "Please accept or decline the meeting invitation.\n\n";

		// + "Meeting details can be found at: "
		// + Config.getInstance().getValue("app.url")
		// + "/dartmanage/Dashboard.action\n\n"
		// + "\n\nYours sincerely,\n" + meeting.organiser.name;

		meetingDao.save(meeting);

		return SUCCESS;
	}

	private String dateAndTime(Meeting meeting) {
		return "Date and Time: " + meeting.startDateTime.getLongFormat()
				+ " - " + meeting.getEndDateTime().getTimeFormat()
				+ " (Time Zone " + meeting.startDateTime.getTimeZoneFormat()
				+ ")";
	}

	public boolean isCorrectlyFilled() {
		// if (meeting.location == null || "".equals(meeting.location.trim())) {
		// return false;
		// }
		if (meeting.name == null || "".equals(meeting.name.trim())) {
			return false;
		}
		if (meeting.guests.size() <= 1) {
			return false;
		}
		if (meeting instanceof DartMeeting
				&& ((DartMeeting) meeting).agendaItems.isEmpty()) {
			return false;
		}
		return true;
	}

	public DartMeeting getMeeting() {
		return (DartMeeting) meeting;
	}

	public void setMeeting(DartMeeting meeting) {
		this.meeting = meeting;
	}

}
