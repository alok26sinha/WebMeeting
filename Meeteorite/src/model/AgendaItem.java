package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import type.MinuteDuration;

@Entity
public class AgendaItem extends BaseDescriptiveModel {
	@Column(nullable = false)
	public int number;

	@Column(nullable = false)
	@org.hibernate.annotations.Type(type = "type.MinuteDurationUserType")
	public MinuteDuration durationInMinutes;
	
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	public DartMeeting dartMeeting;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	public Person contributor;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = true)
	public Person itemOwner;
	
	@Lob
	public String discussionPoint;
	
	@Lob
	public String subAgendaItem;
	
	@Lob
	public String parkedThoughts;
	
	@OneToMany(mappedBy = "agendaItem")
	@OrderBy("id")
	public List<DartItem> dartItems;

	@Lob
	public String newAction;
}
