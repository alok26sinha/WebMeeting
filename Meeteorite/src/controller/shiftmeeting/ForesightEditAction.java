package controller.shiftmeeting;

import controller.support.BaseDescriptionEditAction;
import model.Foresight;
import model.ShiftMeeting;
import dao.ForesightDao;
import dao.ShiftMeetingDao;

@SuppressWarnings("serial")
public class ForesightEditAction extends BaseDescriptionEditAction<ShiftMeeting, Foresight, ShiftMeetingDao, ForesightDao> {

	// Getters and setters
	public void setShiftMeetingDao(ShiftMeetingDao shiftMeetingDao) {
		this.meetingDao = shiftMeetingDao;
	}

	public void setForesightDao(ForesightDao foresightDao) {
		this.detailDao = foresightDao;
	}
}
