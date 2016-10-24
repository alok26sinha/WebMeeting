package controller.shiftmeeting;

import controller.support.BaseDescriptionEditAction;
import model.Hindsight;
import model.ShiftMeeting;
import dao.HindsightDao;
import dao.ShiftMeetingDao;

@SuppressWarnings("serial")
public class HindsightEditAction extends BaseDescriptionEditAction<ShiftMeeting, Hindsight, ShiftMeetingDao, HindsightDao> {

	// Getters and setters
	public void setShiftMeetingDao(ShiftMeetingDao shiftMeetingDao) {
		this.meetingDao = shiftMeetingDao;
	}

	public void setHindsightDao(HindsightDao hindsightDao) {
		this.detailDao = hindsightDao;
	}
}
