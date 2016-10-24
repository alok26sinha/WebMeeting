package controller.shiftmeeting;

import model.ShiftMeeting;
import controller.support.BaseAction;
import dao.ShiftMeetingDao;

@SuppressWarnings("serial")
public class StartAction extends BaseAction {
	public Long id;
	public ShiftMeeting meeting;

	private ShiftMeetingDao shiftMeetingDao;

	@Override
	public String execute() {
		meeting = shiftMeetingDao.load(id);
		return SUCCESS;
	}

	public String startMeeting() {
		execute();
		return redirect("Overview!startMeeting.action?id=" + id);
	}

	// Getters and setters
	public void setShiftMeetingDao(ShiftMeetingDao shiftMeetingDao) {
		this.shiftMeetingDao = shiftMeetingDao;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ShiftMeeting getMeeting() {
		return meeting;
	}

	public void setMeeting(ShiftMeeting meeting) {
		this.meeting = meeting;
	}
	
	

}
