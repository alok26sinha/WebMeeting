package controller.shiftmeeting;

import controller.support.BaseDescriptionEditAction;
import model.ForesightTraction;
import model.ShiftMeeting;
import dao.ForesightTractionDao;
import dao.ShiftMeetingDao;

@SuppressWarnings("serial")
public class TractionForesightEditAction extends BaseDescriptionEditAction<ShiftMeeting, ForesightTraction, ShiftMeetingDao, ForesightTractionDao> {

	// Getters and setters
	public void setStartingIdeaDao(ForesightTractionDao foresightTractionDao) {
		this.detailDao = foresightTractionDao;
	}

	public void setShiftMeetingDao(ShiftMeetingDao shiftMeetingDao) {
		this.meetingDao = shiftMeetingDao;
	}
}
