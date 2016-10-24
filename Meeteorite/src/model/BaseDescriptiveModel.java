package model;

import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlElement;

@MappedSuperclass
public abstract class BaseDescriptiveModel extends BaseModel {

	@Lob
	@XmlElement
	public String description;

}
