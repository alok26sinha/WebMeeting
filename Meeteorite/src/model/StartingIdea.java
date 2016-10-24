package model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement(name = "startingIdea")
public class StartingIdea extends BaseDescriptiveModel {

	public boolean teamContribution;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	public Person contributor;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	public ShiftMeeting shiftMeeting;

	public String getRecognition() {
		if (teamContribution)
			return "Team";
		else if (contributor != null)
			return contributor.name;
		else
			return "";
	}
	
	public String getContributorEmail() {
	    if (teamContribution || contributor == null) {
	        return "";
	    } else {
	        return contributor.email;
	    }
	}

}
