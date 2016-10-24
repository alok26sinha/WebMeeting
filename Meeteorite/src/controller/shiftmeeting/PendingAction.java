package controller.shiftmeeting;

import java.util.List;

import javax.annotation.Resource;

import model.Guest;
import model.StartingIdea;
import dao.GuestDao;
import dao.StartingIdeaDao;

@SuppressWarnings("serial")
public class PendingAction extends StartIdeasAction {

	public List<StartingIdea> myStartingIdeas;
	public String startingIdeaText;

	private GuestDao guestDao;
	@Resource
	private StartingIdeaDao startingIdeaDao;
	
	@Override
	public String execute() {
		super.execute();
		myStartingIdeas = startingIdeaDao.getMyStartingIdeas(meeting,
				getSecurityContext().getUser());
		return SUCCESS;
	}
	
	public String saveStartingIdea(){
		StartingIdea startingIdea = startingIdeaDao.load(id);
		startingIdea.description = startingIdeaText;
		return NONE;
	}

	public String accept() {
		return changeGuestStatus(Guest.ACCEPT_STATUS);
	}

	public String decline() {
		return changeGuestStatus(Guest.DECLINE_STATUS);
	}

	private String changeGuestStatus(int newStatus) {
		super.execute();
		Guest guest = guestDao.get(meeting, getSecurityContext().getUser());
		guest.status = newStatus;
		return redirectDashboard();
	}

	public void setGuestDao(GuestDao guestDao) {
		this.guestDao = guestDao;
	}

	public List<StartingIdea> getMyStartingIdeas() {
		return myStartingIdeas;
	}

	public void setMyStartingIdeas(List<StartingIdea> myStartingIdeas) {
		this.myStartingIdeas = myStartingIdeas;
	}

	public String getStartingIdeaText() {
		return startingIdeaText;
	}

	public void setStartingIdeaText(String startingIdeaText) {
		this.startingIdeaText = startingIdeaText;
	}
	
	

}
