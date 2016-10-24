package model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class PrivateNotes extends BaseModel {

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	public Person person; 
	
	@Lob
	public String agendaReviewNotes;
	
	@Lob
	public String summaryCloseNotes;
	
	@Lob
	public String agendaItemNotes;
	
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	public Meeting meeting;
	
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	public AgendaItem agendaItem;
}
