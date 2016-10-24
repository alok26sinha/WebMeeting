package model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class DartMeeting extends Meeting {
	@OneToMany(mappedBy = "dartMeeting")
	@OrderBy("number")
	public List<AgendaItem> agendaItems;
	
	@Lob
	public String agendaReviewParkedThoughts;
	
	@Lob
	public String summaryCloseParkedThoughts;
	
	@Lob
	public String newAgendaItemDescription;
	
	@Lob
	public String newSubAgendaItem;
}
