package model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames={"company_id","person_id"})})
public class Subscription extends BaseModel{
	
	public static final int FULL_USER = 1;
	public static final int GUEST = 2;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	public Company company;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	public Person person;
	public int type; //1 = full user, 2 = guest;
}
