package model;

import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseCompanyModel extends BaseModel {

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	public Company company;


}
