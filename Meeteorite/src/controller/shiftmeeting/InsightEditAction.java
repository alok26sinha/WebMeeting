package controller.shiftmeeting;

import controller.support.BaseDescriptionEditAction;
import model.Insight;
import model.ShiftMeeting;
import dao.InsightDao;
import dao.ShiftMeetingDao;


@SuppressWarnings("serial")
public class InsightEditAction extends BaseDescriptionEditAction<ShiftMeeting, Insight, ShiftMeetingDao, InsightDao> {

	// Getters and setters
	public void setShiftMeetingDao(ShiftMeetingDao shiftMeetingDao) {
		this.meetingDao = shiftMeetingDao;
	}

	public void setInsightDao(InsightDao insightDao) {
		this.detailDao = insightDao;
	}
}
