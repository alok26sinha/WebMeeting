package controller.shiftmeeting;

import java.util.List;

import model.DartMeeting;
import model.Guest;
import model.ShiftMeeting;
import type.StringUtils;
import controller.support.BaseSendInvitationAction;

@SuppressWarnings("serial")
public class MeetingSummaryAction extends BaseSendInvitationAction {
	public List<Guest> attendingGuests;

	@Override
	public String execute() {
		super.execute();

		//if (StringUtils.isEmpty(meeting.invitation))
			meeting.invitation = "You have been invited by "
					+ meeting.organiser.name
					+ " to attend a meeting to solve the challenge: "
					+ meeting.name
					+ "\n\n"
					+ "Date and Time: "
					+ meeting.startDateTime.getLongFormat()
					+ " - "
					+ meeting.getEndDateTime().getLongFormat()
					+ "\n"
					+ ( !StringUtils.isEmpty(meeting.location) ? "Place: " + meeting.location
							+ "\n" : "")
					+ "\n"
					+ "Please accept or decline the meeting. "
					+ "Please also add some starting ideas of possible "
					+ "solutions to overcome the business challenge.\n\n"
					+ "\n\nYours sincerely,\n"
					+ meeting.organiser.name;

		meetingDao.save(meeting);
		attendingGuests = guestDao.getAttendingGuests(meeting);
		return SUCCESS;
	}

	public boolean isCorrectlyFilled() {
//        if (meeting.location == null || "".equals(meeting.location.trim())) {
//            return false;
//        }
        if (meeting.name == null || "".equals(meeting.name.trim())) {
            return false;
        }
        if (meeting.guests.size() <= 1) {
            return false;
        }
        if (meeting instanceof ShiftMeeting && ((ShiftMeeting)meeting).startingIdeas.isEmpty()) {
            return false;
        }
        // always null for now
//        if (meeting instanceof ShiftMeeting && (((ShiftMeeting)meeting).businessChallenge == null || "".equals(((ShiftMeeting)meeting).businessChallenge.trim()))) {
//            return false;
//        }
        return true;
    }

	public List<Guest> getAttendingGuests() {
		return attendingGuests;
	}

	public void setAttendingGuests(List<Guest> attendingGuests) {
		this.attendingGuests = attendingGuests;
	}
	
	public ShiftMeeting getMeeting() {
		return (ShiftMeeting)meeting;
	}

	public void setMeeting(ShiftMeeting meeting) {
		this.meeting = meeting;
	}
	
	

}
