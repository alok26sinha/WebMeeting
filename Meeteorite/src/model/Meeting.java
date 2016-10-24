package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import type.UsefulDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Meeting extends BaseModel {

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	public Person organiser;
	public String name;
	@Lob
	public String invitation;
	@OneToMany(mappedBy = "meeting")
	@OrderBy("id")
	public List<Guest> guests;
	@org.hibernate.annotations.Type(type = "type.UsefulDateTimeUserType")
	public UsefulDateTime startDateTime;
	public int durationInMinutes;
	@org.hibernate.annotations.Type(type = "type.UsefulDateTimeUserType")
	public UsefulDateTime actualStartDateTime;
	public String location;
	public boolean complete;
	public boolean invitationSend;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	public Company company;
	@OneToMany(mappedBy = "meeting")
	@OrderBy("id")
	public List<PermanentFile> permanentFiles;
	public boolean followupReportSent;
	@org.hibernate.annotations.Type(type = "type.UsefulDateTimeUserType")
	public UsefulDateTime meetingClosedTime; 
	
	@Transient
	private UsefulDateTime endDateTime;

	// helper method to handle calculation of meeting end time
	public UsefulDateTime getEndDateTime() {
		if (endDateTime == null) {
			endDateTime = startDateTime.addSeconds(durationInMinutes * 60);
		}
		return endDateTime;
	}
	
}
