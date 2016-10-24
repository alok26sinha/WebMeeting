package controller.dartmanage;

import java.util.List;


import model.AgendaItem;
import model.Guest;
import dao.AgendaItemDao;
import dao.DartMeetingDao;
import dao.GuestDao;

@SuppressWarnings("serial")
public class PendingAction extends BuildAgendaAction {
	public List<Guest> guests;
	
	private GuestDao guestDao;

	@Override
	public String execute() {
		super.execute();
		guests = guestDao.getAttendingGuests(meeting);
		return SUCCESS;
	}
	
	public boolean createdByUser(Long agendaItemId) {
		AgendaItem item = agendaItemDao.load(agendaItemId);
		return item.contributor == getSecurityContext().getUser();  
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
		if (newStatus == Guest.DECLINE_STATUS) {
			boolean needFlush = false;
			for (AgendaItem item : meeting.agendaItems) {
				if (item.itemOwner != null && item.itemOwner.getId().longValue() == guest.person.getId()
						.longValue()) {
					item.itemOwner = meeting.organiser;
					needFlush = true;
				}
			}
			if (needFlush) {
				dartMeetingDao.save(meeting);
				dartMeetingDao.flush();
			}
		}
		return redirectDashboard();
	}

	public void setGuestDao(GuestDao guestDao) {
		this.guestDao = guestDao;
	}

	public List<Guest> getGuests() {
		return guests;
	}

	public void setGuests(List<Guest> guests) {
		this.guests = guests;
	}
	
	
}
