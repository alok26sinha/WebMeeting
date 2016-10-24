package model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;

import type.UsefulDate;

@Entity
public class DartItem extends BaseModel {
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	@OrderBy("id")
	public AgendaItem agendaItem;
	@Lob
	public String action;
	
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	public Person responsiblePerson;
	
	@org.hibernate.annotations.Type(type = "type.UsefulDateUserType")
	public UsefulDate timing;
	
	public int status;
	
	@Lob
    public String comment;
	
	public Boolean reminderSent;
	
	public String getStatusString() {
		return Status.getStatusString(status);
	}
	
	public String getTimingString() {
		return timing.getSaasuFormat();
	}
}
