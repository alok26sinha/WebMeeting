package controller.shiftmeeting;

import model.Person;
import model.Status;
import model.Traction;
import security.LocalSecurityContext;
import dao.TractionDao;

@SuppressWarnings("serial")
public class FollowUpReportAction extends KeyOutputsAction {
	public String startDate;
	public String startTime;
	public String endDate;
	public String endTime;
	public Long tractionId;
	public Person currentUser;
	public String comments;

	private TractionDao tractionDao;

	@Override
	public String execute() {
		super.execute();
		currentUser = LocalSecurityContext.get().getUser();

		startDate = meeting.startDateTime.format("yyyy-MM-dd");
		startTime = meeting.startDateTime.format("HH:mm");
		endDate = meeting.getEndDateTime().format("yyyy-MM-dd");
		endTime = meeting.getEndDateTime().format("HH:mm");

		return SUCCESS;
	}

	public String startProgress() {
//		setTractionStatus(Status.IN_PROGRESS);
		return SUCCESS;
	}

    public String report(){
        execute();
        
        return ITEXT;
    }

	public String close() {
		setTractionStatus(Status.CLOSED);
		return SUCCESS;
	}
	
	public String saveComment () {
	    Traction traction = tractionDao.load(id);
	    traction.comments = comments;
	    
	    return NONE;
	}

	private void setTractionStatus(int status) {
		execute();
		Traction traction = tractionDao.load(tractionId);

		traction.status = status;
	}

	public void setTractionDao(TractionDao tractionDao) {
		this.tractionDao = tractionDao;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Long getTractionId() {
		return tractionId;
	}

	public void setTractionId(Long tractionId) {
		this.tractionId = tractionId;
	}

	public Person getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(Person currentUser) {
		this.currentUser = currentUser;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	

}
