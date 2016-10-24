package service;

import javax.annotation.Resource;

import model.Guest;
import model.Meeting;
import model.Person;

import org.springframework.stereotype.Component;

import dao.GuestDao;

@Component
public class GuestService {
	
	@Resource
	private GuestDao guestDao;

	public Guest addGuest(Person person, Meeting meeting){
		Guest guest = new Guest();
		guest.person = person;
		guest.meeting = meeting;
		guest.status = Guest.NO_RESPONSE_STATUS;
		guest.invitationStatus = Guest.INVITATION_SEND_INVITATION;
		guest = guestDao.save(guest);
		return guest;
	}

}
