package controller.shiftmeeting;

import java.util.ArrayList;
import java.util.List;

import model.ForesightTraction;
import model.Guest;
import model.Person;
import model.StartingIdeaTraction;
import model.Traction;
import dao.ForesightTractionDao;
import dao.GuestDao;
import dao.PersonDao;
import dao.StartingIdeaTractionDao;

@SuppressWarnings("serial")
public class KeyOutputsAction extends ShiftTimedAction {

	public List<Long> foresightTractionsPeople = new ArrayList<Long>();
	public List<String> foresightTractionsDates = new ArrayList<String>();
	public List<Long> startingIdeaTractionsPeople = new ArrayList<Long>();
	public List<String> startingIdeaTractionsDates = new ArrayList<String>();

	public List<Guest> attendingGuests;
	public List<Traction> tractions;
	public List<ForesightTraction> foresightTractions;
	public List<StartingIdeaTraction> startingIdeaTractions;

    public String parkedThoughts;

    private GuestDao guestDao;
	private PersonDao personDao;
	private ForesightTractionDao foresightTractionDao;
	private StartingIdeaTractionDao startingIdeaTractionDao;

	@Override
	public String execute() {
		super.execute();
		timeRemaining = keyOutputsTimeRemaining;
		foresightTractions = foresightTractionDao.getAll(meeting);
		startingIdeaTractions = startingIdeaTractionDao.getAll(meeting);
		attendingGuests = guestDao.getAttendingGuests(meeting);

		return SUCCESS;
	}

	public String start() {
		return execute();
	}

	public String save() {
		meeting = shiftMeetingDao.load(id);

		// save foresight tractions
		int resultCount = 0;
		for (ForesightTraction traction : foresightTractionDao.getAll(meeting)) {
			resultCount++;
			updateTraction(traction, foresightTractionsPeople.get(resultCount),
					foresightTractionsDates.get(resultCount));
			foresightTractionDao.save(traction);
		}

		foresightTractionDao.flush();

		// save starting ideas tractions
		resultCount = 0;
		for (StartingIdeaTraction traction : startingIdeaTractionDao
				.getAll(meeting)) {
			resultCount++;
			updateTraction(traction,
					startingIdeaTractionsPeople.get(resultCount),
					startingIdeaTractionsDates.get(resultCount));
			startingIdeaTractionDao.save(traction);
		}

		startingIdeaTractionDao.flush();

		if ("Back".equals(submitButton)) {
			return redirect("Traction.action?id=" + id);
		}
		return redirect("EndMeeting.action?id=" + id);
	}

    public String saveParkedThoughts () {
        meeting = shiftMeetingDao.load(id);
        meeting.keyOutputParkedThoughts = parkedThoughts;
        
        return NONE;
    }

	// update the person and due date on the traction
	private void updateTraction(Traction traction, Long personId,
			String dueDateString) {
		if (personId != null) {
			if (personId != -1) {
				Person person = personDao.load(personId);
				traction.personResponsible = person;
			} else
				traction.personResponsible = null;

		}

		if (dueDateString != null) {
			traction.setDueDateString(dueDateString);
		}
	}

	public void setForesightTractionDao(
			ForesightTractionDao foresightTractionDao) {
		this.foresightTractionDao = foresightTractionDao;
	}

	public void setStartingIdeasTractionDao(
			StartingIdeaTractionDao startingIdeaTractionDao) {
		this.startingIdeaTractionDao = startingIdeaTractionDao;
	}

	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}

	public void setGuestDao(GuestDao guestDao) {
		this.guestDao = guestDao;
	}

	public List<Long> getForesightTractionsPeople() {
		return foresightTractionsPeople;
	}

	public void setForesightTractionsPeople(List<Long> foresightTractionsPeople) {
		this.foresightTractionsPeople = foresightTractionsPeople;
	}

	public List<String> getForesightTractionsDates() {
		return foresightTractionsDates;
	}

	public void setForesightTractionsDates(List<String> foresightTractionsDates) {
		this.foresightTractionsDates = foresightTractionsDates;
	}

	public List<Long> getStartingIdeaTractionsPeople() {
		return startingIdeaTractionsPeople;
	}

	public void setStartingIdeaTractionsPeople(
			List<Long> startingIdeaTractionsPeople) {
		this.startingIdeaTractionsPeople = startingIdeaTractionsPeople;
	}

	public List<String> getStartingIdeaTractionsDates() {
		return startingIdeaTractionsDates;
	}

	public void setStartingIdeaTractionsDates(
			List<String> startingIdeaTractionsDates) {
		this.startingIdeaTractionsDates = startingIdeaTractionsDates;
	}

	public List<Guest> getAttendingGuests() {
		return attendingGuests;
	}

	public void setAttendingGuests(List<Guest> attendingGuests) {
		this.attendingGuests = attendingGuests;
	}

	public List<Traction> getTractions() {
		return tractions;
	}

	public void setTractions(List<Traction> tractions) {
		this.tractions = tractions;
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

	public String getParkedThoughts() {
		return parkedThoughts;
	}

	public void setParkedThoughts(String parkedThoughts) {
		this.parkedThoughts = parkedThoughts;
	}
	
	
}
