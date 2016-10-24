package controller.shiftmeeting;

import hibernate.NoRecordsFoundException;

import java.util.List;

import model.Foresight;
import model.ForesightTraction;
import model.StartingIdea;
import model.StartingIdeaTraction;
import model.Status;
import model.Traction;
import type.StringUtils;
import dao.ForesightDao;
import dao.ForesightTractionDao;
import dao.StartingIdeaTractionDao;

@SuppressWarnings("serial")
public class TractionAction extends ShiftTimedAction {
	
	public List<Foresight> foresights;
	public List<StartingIdea> startingIdeas;
	public List<ForesightTraction> foresightTractions;
	public List<StartingIdeaTraction> startingIdeaTractions;
	
	public String newForesightTraction;
	public Long deleteForesightTractionId;
	public String newStartingIdeaTraction;
	public Long deleteStartingIdeaTractionId;

	public String caller;
	public Long editStartingIdeaTractionId;
	public Long editForesightTractionId;

    public String parkedThoughts;
    public String description;

    private ForesightDao foresightDao;
	private ForesightTractionDao foresightTractionDao;
	private StartingIdeaTractionDao startingIdeaTractionDao;

	@Override
	public String execute() {
		super.execute();
		timeRemaining = tractionTimeRemaining;
		foresights = foresightDao.getAll(meeting);
		startingIdeas = meeting.startingIdeas;
		foresightTractions = foresightTractionDao.getAll(meeting);
		startingIdeaTractions = startingIdeaTractionDao.getAll(meeting);

		return SUCCESS;
	}

	public String start() {
		return execute();
	}

	// foresight tractions
	public String addNewForesightTraction() {
		if (!StringUtils.isEmpty(newForesightTraction)) {
			ForesightTraction foresightTraction = new ForesightTraction();
			foresightTraction.description = newForesightTraction;
			meeting = shiftMeetingDao.load(id);
			foresightTraction.shiftMeeting = meeting;
			foresightTraction.status = Status.OPEN;
			foresightTractionDao.save(foresightTraction);
			foresightTractionDao.flush();
		}
		newForesightTraction = null;
		return execute();
	}

	public String deleteForesightTraction() {
		if (deleteForesightTractionId != null) {
			foresightTractionDao.delete(deleteForesightTractionId);
			foresightTractionDao.flush();
		}
		return execute();
	}
	
	// starting idea tractions
	public String addNewStartingIdeaTraction() {
		if (!StringUtils.isEmpty(newStartingIdeaTraction)) {
			StartingIdeaTraction startingIdeaTraction = new StartingIdeaTraction();
			startingIdeaTraction.description = newStartingIdeaTraction;
			meeting = shiftMeetingDao.load(id);
			startingIdeaTraction.shiftMeeting = meeting;
			startingIdeaTraction.status = Status.OPEN;
			startingIdeaTractionDao.save(startingIdeaTraction);
			startingIdeaTractionDao.flush();
		}
		newStartingIdeaTraction = null;
		return execute();
	}

	public String deleteStartingIdeaTraction() {
		if (deleteStartingIdeaTractionId != null) {
			startingIdeaTractionDao.delete(deleteStartingIdeaTractionId);
			startingIdeaTractionDao.flush();
		}
		return execute();
	}

	public String editStartingIdeaTraction() {
		return redirect("TractionStartIdeaEdit.action?id=" + id + "&detailId=" + editStartingIdeaTractionId + "&caller=" + caller);
	}

	public String editForesightTraction() {
		return redirect("TractionForesightEdit.action?id=" + id + "&detailId=" + editForesightTractionId + "&caller=" + caller);
	}

    public String saveParkedThoughts () {
        meeting = shiftMeetingDao.load(id);
        meeting.tractionParkedThoughts = parkedThoughts;
        
        return NONE;
    }
    
    public String saveDescription(){
    	Traction traction;
    	
    	try{
    		traction = foresightTractionDao.load(id);
    	}
    	catch(NoRecordsFoundException e){
    		traction = startingIdeaTractionDao.load(id);
    	}
    	
    	traction.description = description;
    	
    	return NONE;
    }

	// Getters and setters
	public void setForesightDao(ForesightDao foresightDao) {
		this.foresightDao = foresightDao;
	}

	public void setForesightTractionDao(ForesightTractionDao foresightTractionDao) {
		this.foresightTractionDao = foresightTractionDao;
	}
	
	public void setStartingIdeasTractionDao(StartingIdeaTractionDao startingIdeaTractionDao) {
		this.startingIdeaTractionDao = startingIdeaTractionDao;
	}

	public List<Foresight> getForesights() {
		return foresights;
	}

	public void setForesights(List<Foresight> foresights) {
		this.foresights = foresights;
	}

	public List<StartingIdea> getStartingIdeas() {
		return startingIdeas;
	}

	public void setStartingIdeas(List<StartingIdea> startingIdeas) {
		this.startingIdeas = startingIdeas;
	}

	public List<ForesightTraction> getForesightTractions() {
		return foresightTractions;
	}

	public void setForesightTractions(List<ForesightTraction> foresightTractions) {
		this.foresightTractions = foresightTractions;
	}

	public List<StartingIdeaTraction> getStartingIdeaTractions() {
		return startingIdeaTractions;
	}

	public void setStartingIdeaTractions(
			List<StartingIdeaTraction> startingIdeaTractions) {
		this.startingIdeaTractions = startingIdeaTractions;
	}

	public String getNewForesightTraction() {
		return newForesightTraction;
	}

	public void setNewForesightTraction(String newForesightTraction) {
		this.newForesightTraction = newForesightTraction;
	}

	public Long getDeleteForesightTractionId() {
		return deleteForesightTractionId;
	}

	public void setDeleteForesightTractionId(Long deleteForesightTractionId) {
		this.deleteForesightTractionId = deleteForesightTractionId;
	}

	public String getNewStartingIdeaTraction() {
		return newStartingIdeaTraction;
	}

	public void setNewStartingIdeaTraction(String newStartingIdeaTraction) {
		this.newStartingIdeaTraction = newStartingIdeaTraction;
	}

	public Long getDeleteStartingIdeaTractionId() {
		return deleteStartingIdeaTractionId;
	}

	public void setDeleteStartingIdeaTractionId(Long deleteStartingIdeaTractionId) {
		this.deleteStartingIdeaTractionId = deleteStartingIdeaTractionId;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public Long getEditStartingIdeaTractionId() {
		return editStartingIdeaTractionId;
	}

	public void setEditStartingIdeaTractionId(Long editStartingIdeaTractionId) {
		this.editStartingIdeaTractionId = editStartingIdeaTractionId;
	}

	public Long getEditForesightTractionId() {
		return editForesightTractionId;
	}

	public void setEditForesightTractionId(Long editForesightTractionId) {
		this.editForesightTractionId = editForesightTractionId;
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
