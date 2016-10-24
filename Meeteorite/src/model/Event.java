package model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import type.UsefulDateTime;

@Entity
public class Event extends BaseModel{

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	public Person person;
    @org.hibernate.annotations.Type(type = "type.UsefulDateTimeUserType")
    public UsefulDateTime eventDateTime;
    @Column(length = 25)
    public String name;
    
    @Override
    public String toString(){
    	return super.toString() + " " + eventDateTime + " " + name;
    }
}
