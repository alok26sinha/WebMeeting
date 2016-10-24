package controller.shiftmeeting;

import model.Hindsight;
import model.Insight;
import type.StringUtils;
import dao.InsightDao;

@SuppressWarnings("serial")
public class InsightAction extends ShiftTimedAction {
	public String newInsight;
	public Long editedId;
	public Long deleteId;
	public String caller;
    public String parkedThoughts;
    public String description;

	private InsightDao insightDao;
	
	@Override
	public String execute() {
		super.execute();
		timeRemaining = insightTimeRemaining;
		return SUCCESS;
	}
	
	public String start(){
		return execute();
	}

	public String addNew() {
		if (!StringUtils.isEmpty(newInsight)) {
			Insight insight = new Insight();
			insight.description = newInsight;
			meeting = shiftMeetingDao.load(id);
			insight.shiftMeeting = meeting;
			insightDao.save(insight);
			insightDao.flush();
		}
		newInsight = null;
		return execute();
	}

	public String delete() {
		if (deleteId != null) {
			insightDao.delete(deleteId);
			insightDao.flush();
		}
		return execute();
	}

	public String edit() {
		return redirect("InsightEdit.action?id=" + id + "&detailId=" + editedId + "&caller=" + caller);
	}

    public String saveParkedThoughts () {
        meeting = shiftMeetingDao.load(id);
        meeting.insightParkedThoughts = parkedThoughts;     
        return NONE;
    }
    
    public String saveDescription(){
    	Insight insight = insightDao.load(id);
    	insight.description = description;
    	return NONE;
    }

	// Getters and setters
	public void setInsightDao(InsightDao insightDao) {
		this.insightDao = insightDao;
	}

	public String getNewInsight() {
		return newInsight;
	}

	public void setNewInsight(String newInsight) {
		this.newInsight = newInsight;
	}

	public Long getEditedId() {
		return editedId;
	}

	public void setEditedId(Long editedId) {
		this.editedId = editedId;
	}

	public Long getDeleteId() {
		return deleteId;
	}

	public void setDeleteId(Long deleteId) {
		this.deleteId = deleteId;
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
