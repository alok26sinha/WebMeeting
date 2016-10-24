package controller.shiftmeeting;

import model.Foresight;
import security.NoAuthenticationRequired;
import type.StringUtils;
import dao.ForesightDao;

@NoAuthenticationRequired
@SuppressWarnings("serial")
public class ForesightAction extends ShiftTimedAction {
	
	public String newForesight;
	public Long deleteId;
	public Long editedId;
	public String caller;
    public String parkedThoughts;
    public String description;

	private ForesightDao foresightDao;

	@Override
	public String execute() {
		super.execute();
		timeRemaining = foresightTimeRemaining;
		return SUCCESS;
	}

	public String start() {
		return execute();
	}

	public String addNew() {
		if (!StringUtils.isEmpty(newForesight)) {
			Foresight foresight = new Foresight();
			foresight.description = newForesight;
			meeting = shiftMeetingDao.load(id);
			foresight.shiftMeeting = meeting;
			foresightDao.save(foresight);
			foresightDao.flush();
		}
		newForesight = null;
		return execute();
	}

	public String delete() {
		if (deleteId != null) {
			foresightDao.delete(deleteId);
			foresightDao.flush();
		}
		return execute();
	}

	public String edit() {
		return redirect("ForesightEdit.action?id=" + id + "&detailId=" + editedId + "&caller=" + caller);
	}

    public String saveParkedThoughts () {
        meeting = shiftMeetingDao.load(id);
        meeting.foresightParkedThoughts = parkedThoughts;
        return NONE;
    }
    
    public String saveDescription(){
    	Foresight foresight = foresightDao.load(id);
    	foresight.description = description;
    	return NONE;
    }

	// Getters and setters
	public void setForesightDao(ForesightDao foresightDao) {
		this.foresightDao = foresightDao;
	}

	public String getNewForesight() {
		return newForesight;
	}

	public void setNewForesight(String newForesight) {
		this.newForesight = newForesight;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
