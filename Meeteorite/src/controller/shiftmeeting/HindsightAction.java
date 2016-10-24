package controller.shiftmeeting;

import java.util.List;

import model.Hindsight;
import type.StringUtils;
import dao.HindsightDao;

@SuppressWarnings("serial")
public class HindsightAction extends ShiftTimedAction {
	
	public List<Hindsight> hindsights;
	public String newHindsight;
	public Long deleteId;
	public Long editedId;
	public String caller;
    public String parkedThoughts;
    public String description;

    private HindsightDao hindsightDao;

	@Override
	public String execute() {
		super.execute();
		timeRemaining = hindsightTimeRemaining;
		hindsights = hindsightDao.getAll(meeting);

		return SUCCESS;
	}
	
	public String start(){
		return execute();
	}

	public String addNew() {
		if (!StringUtils.isEmpty(newHindsight)) {
			Hindsight hindsight = new Hindsight();
			hindsight.description = newHindsight;
			meeting = shiftMeetingDao.load(id);
			hindsight.shiftMeeting = meeting;
			hindsightDao.save(hindsight);
			hindsightDao.flush();
		}
		newHindsight = null;
		return execute();
	}

	public String delete() {
		if (deleteId != null) {
			hindsightDao.delete(deleteId);
			hindsightDao.flush();
		}
		return execute();
	}
	
	public String edit() {
		return redirect("HindsightEdit.action?id=" + id + "&detailId=" + editedId + "&caller=" + caller);
	}

    public String saveParkedThoughts () {
        meeting = shiftMeetingDao.load(id);
        meeting.hindsightParkedThoughts = parkedThoughts;
        return NONE;
    }
    
    public String saveDescription(){
    	Hindsight hindsight = hindsightDao.load(id);
    	hindsight.description = description;
    	return NONE;
    }

	// Getters and setters
	public void setHindsightDao(HindsightDao hindsightDao) {
		this.hindsightDao = hindsightDao;
	}

	public List<Hindsight> getHindsights() {
		return hindsights;
	}

	public void setHindsights(List<Hindsight> hindsights) {
		this.hindsights = hindsights;
	}

	public String getNewHindsight() {
		return newHindsight;
	}

	public void setNewHindsight(String newHindsight) {
		this.newHindsight = newHindsight;
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
