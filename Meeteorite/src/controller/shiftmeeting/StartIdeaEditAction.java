package controller.shiftmeeting;

import controller.support.BaseDescriptionEditAction;
import model.ShiftMeeting;
import model.StartingIdea;
import dao.ShiftMeetingDao;
import dao.StartingIdeaDao;

@SuppressWarnings("serial")
public class StartIdeaEditAction extends BaseDescriptionEditAction<ShiftMeeting, StartingIdea, ShiftMeetingDao, StartingIdeaDao> {

	// Getters and setters
	public void setStartingIdeaDao(StartingIdeaDao startingIdeaDao) {
		this.detailDao = startingIdeaDao;
	}

	public void setShiftMeetingDao(ShiftMeetingDao shiftMeetingDao) {
		this.meetingDao = shiftMeetingDao;
	}
}
