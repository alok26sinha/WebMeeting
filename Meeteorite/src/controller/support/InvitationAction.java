package controller.support;

import java.util.UUID;

import model.Guest;
import model.Person;
import model.ShiftMeeting;
import security.NoAuthenticationRequired;
import security.SecurityService;
import servlet.CookieManager;
import dao.InvitationDao;

@NoAuthenticationRequired
@SuppressWarnings("serial")
public class InvitationAction extends BaseAction {
	public String id;
	
	private static CookieManager cookieManager = new CookieManager();
	private SecurityService securityService;
	private InvitationDao invitationDao;
	
	@Override
	public String execute() {
		Guest guest = invitationDao.loadInvitation(UUID.fromString(id));
		
		// login guest
		Person person = securityService.loginGuest(guest);
		cookieManager.setUserTokenInCookie(person.userToken, false, request, response);

		// redirect to shift / dart pending page
		if (guest.meeting instanceof ShiftMeeting) {
			return redirect("../shiftmeeting/Pending.action?id=" + guest.meeting.getId());
		} else {
			return redirect("../dartmeeting/Pending.action?id=" + guest.meeting.getId());
		}
	}
	
	public void setInvitationDao(InvitationDao invitationDao) {
		this.invitationDao = invitationDao;
	}
	
	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
