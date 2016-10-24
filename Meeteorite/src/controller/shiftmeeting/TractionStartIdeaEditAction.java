package controller.shiftmeeting;

import controller.support.BaseDescriptionEditAction;
import model.ShiftMeeting;
import model.StartingIdeaTraction;
import dao.ShiftMeetingDao;
import dao.StartingIdeaTractionDao;

@SuppressWarnings("serial")
public class TractionStartIdeaEditAction extends BaseDescriptionEditAction<ShiftMeeting, StartingIdeaTraction, ShiftMeetingDao, StartingIdeaTractionDao> {

	// Getters and setters
	public void setStartingIdeaDao(StartingIdeaTractionDao startingIdeaTractionDao) {
		this.detailDao = startingIdeaTractionDao;
	}

	public void setShiftMeetingDao(ShiftMeetingDao shiftMeetingDao) {
		this.meetingDao = shiftMeetingDao;
	}
}
