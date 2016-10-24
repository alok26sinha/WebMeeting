package model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Company extends BaseModel {
	@Column(length = 50)
	public String name;

}
