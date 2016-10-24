package model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

import type.UsefulDate;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Traction extends BaseDescriptiveModel {

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = true)
	public Person personResponsible;
	public Date dueDate;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	public ShiftMeeting shiftMeeting;
	
	public int status;
	public String comments;
	public Boolean reminderSent;
	

	public String getDueDateString() {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return dueDate != null ? formatter.format(dueDate) : formatter.format(new Date());
	}

	public void setDueDateString(String dueDateString) {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			dueDate = (Date) formatter.parse(dueDateString);
		} catch (ParseException e) {
		}
	}
	
	public UsefulDate getUsefulDate(){
		return UsefulDate.create(dueDate);
	}
	
	public String getStatusString() {
		return Status.getStatusString(status);
	}
}
