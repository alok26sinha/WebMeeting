package controller.shiftmeeting;

import model.StartingIdea;
import type.StringUtils;
import dao.StartingIdeaDao;

@SuppressWarnings("serial")
public class StartIdeasAction extends ShiftTimedAction {
	public String newStartingIdea;
	public Long deleteId;
	public Long editedId;
	public String caller;
    public String parkedThoughts;
    public String startingIdeaText;

	protected StartingIdeaDao startingIdeaDao;

	@Override
	public String execute() {
		super.execute();
		timeRemaining = startingIdeasTimeRemaining;
		return SUCCESS;
	}

	public String start() {
		// TODO hook in the timer to start his page here
		return execute();
	}

	public String addNew() {
		if (!StringUtils.isEmpty(newStartingIdea)) {
			StartingIdea startingIdea = new StartingIdea();
			startingIdea.description = newStartingIdea;
			startingIdea.contributor = getSecurityContext().getUser();
			if (this instanceof PendingAction)
				startingIdea.teamContribution = false;
			else
				startingIdea.teamContribution = true;
			startingIdea.shiftMeeting = shiftMeetingDao.load(id);
			startingIdeaDao.save(startingIdea);
			startingIdeaDao.flush();
		}
		newStartingIdea = null;
		return execute();
	}

	public String delete() {
		if (deleteId != null) {
			startingIdeaDao.delete(deleteId);
			startingIdeaDao.flush();
		}
        return redirect(caller + ".action?id=" + id);
	}
	
	public String edit() {
		return redirect("StartIdeaEdit.action?id=" + id + "&detailId=" + editedId + "&caller=" + caller);
	}
	
	public String saveParkedThoughts () {
	    meeting = shiftMeetingDao.load(id);
	    meeting.staringIdeaParkedThoughts = parkedThoughts;
	    
	    return NONE;
	}
	
	public String saveStartIdea(){
		StartingIdea startingIdea = startingIdeaDao.load(id);
		startingIdea.description = startingIdeaText;
		return NONE;
	}

	// Getters and setters
	public void setStartingIdeaDao(StartingIdeaDao startingIdeaDao) {
		this.startingIdeaDao = startingIdeaDao;
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

	public Long getEditedId() {
		return editedId;
	}

	public void setEditedId(Long editedId) {
		this.editedId = editedId;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getParkedThoughts() {
		return parkedThoughts;
	}

	public void setParkedThoughts(String parkedThoughts) {
		this.parkedThoughts = parkedThoughts;
	}

	public String getStartingIdeaText() {
		return startingIdeaText;
	}

	public void setStartingIdeaText(String startingIdeaText) {
		this.startingIdeaText = startingIdeaText;
	}
	
	
}
