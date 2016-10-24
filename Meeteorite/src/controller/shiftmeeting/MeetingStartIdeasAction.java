package controller.shiftmeeting;

import javax.annotation.Resource;

import model.ShiftMeeting;
import model.StartingIdea;
import type.StringUtils;
import controller.support.BaseAction;
import dao.ShiftMeetingDao;
import dao.StartingIdeaDao;

@SuppressWarnings("serial")
public class MeetingStartIdeasAction extends BaseAction {
	public Long id;
	public ShiftMeeting meeting;
	public String newStartingIdea;
	public Long deleteId;
	public String submitButton;
	public String startingIdeaText;

	@Resource
	private StartingIdeaDao startingIdeaDao;
	@Resource
	private ShiftMeetingDao shiftMeetingDao;

	@Override
	public String execute() {
		meeting = shiftMeetingDao.load(id);
		return SUCCESS;
	}

	public String addNew() {
		if (!StringUtils.isEmpty(newStartingIdea)) {
			StartingIdea startingIdea = new StartingIdea();
			startingIdea.description = newStartingIdea;
			startingIdea.contributor = getSecurityContext().getUser();
			startingIdea.teamContribution = false;
			startingIdea.shiftMeeting = shiftMeetingDao.load(id);
			startingIdeaDao.save(startingIdea);
			startingIdeaDao.flush();
		}
		newStartingIdea = null;

		if ("SendInvitation".equals(submitButton)) {
			return redirect("MeetingSummary.action?id=" + id);
		} else if ("ManageGuests".equals(submitButton)) {
			return redirect("ManageGuests.action?id=" + id);
		}
		return execute();
	}

	public String delete() {
		if (deleteId != null) {
			startingIdeaDao.delete(deleteId);
			startingIdeaDao.flush();
		}
		return execute();
	}
	
	public String saveStartIdea(){
		StartingIdea startingIdea = startingIdeaDao.load(id);
		startingIdea.description = startingIdeaText;
		return NONE;
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

	public String getNewStartingIdea() {
		return newStartingIdea;
	}

	public void setNewStartingIdea(String newStartingIdea) {
		this.newStartingIdea = newStartingIdea;
	}

	public Long getDeleteId() {
		return deleteId;
	}

	public void setDeleteId(Long deleteId) {
		this.deleteId = deleteId;
	}

	public String getSubmitButton() {
		return submitButton;
	}

	public void setSubmitButton(String submitButton) {
		this.submitButton = submitButton;
	}

	public String getStartingIdeaText() {
		return startingIdeaText;
	}

	public void setStartingIdeaText(String startingIdeaText) {
		this.startingIdeaText = startingIdeaText;
	}
	
	

}
