package model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class ShiftMeeting extends Meeting {

	@Lob
	public String businessChallenge;
	@OneToMany(mappedBy = "shiftMeeting")
	@OrderBy("id")
	public List<StartingIdea> startingIdeas;
	@OneToMany(mappedBy = "shiftMeeting")
	@OrderBy("id")
	public List<Hindsight> hindsights;
	@OneToMany(mappedBy = "shiftMeeting")
	@OrderBy("id")
	public List<Insight> insights;
	@OneToMany(mappedBy = "shiftMeeting")
	@OrderBy("id")
	public List<Foresight> foresights;
	
	// parked thoughts, one per meeting element
	@Lob
	public String staringIdeaParkedThoughts;
	@Lob
	public String hindsightParkedThoughts;
	@Lob
	public String insightParkedThoughts;
    @Lob
    public String foresightParkedThoughts;
    @Lob
    public String tractionParkedThoughts;
    @Lob
    public String keyOutputParkedThoughts;

}
