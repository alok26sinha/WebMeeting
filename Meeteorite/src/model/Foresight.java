package model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Foresight extends BaseDescriptiveModel {

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	public ShiftMeeting shiftMeeting;
	
}
