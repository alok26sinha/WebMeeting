package model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Guest extends BaseModel {

	//Response status
	public static final int NO_RESPONSE_STATUS = 0;
	public static final int ACCEPT_STATUS = 1;
	public static final int DECLINE_STATUS = 2;
	
	//Invitation status
	public static final int INVITATION_SEND_INVITATION = 10;
	public static final int INVITATION_SENT = 11;
	public static final int INVITATION_SEND_UPDATE = 12;
	
	
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	public Person person;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	public Meeting meeting;
	// Response
	public int status;
	// Invitation
	public int invitationStatus;
	
	
	public String getStatusResponse() {
		switch (status) {
		case NO_RESPONSE_STATUS:
			return "No response";
		case ACCEPT_STATUS:
			return "Accepted";
		case DECLINE_STATUS:
			return "Declined";
		default:
			return "<Undefined>";
		}
	}
	
	public String getStatusInvitation() {
		switch (invitationStatus) {
		case INVITATION_SEND_INVITATION:
			return "Pending";
		case INVITATION_SENT:
			return "Sent";
		case INVITATION_SEND_UPDATE:
			return "Pending Update";
		default:
			return "<Undefined>";
		}
	}
	
	
}
