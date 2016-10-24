package model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * The base type for all models.
 * 
 * Note that in order for hash code and equals to distinguish between different
 * models (e.g. two different model.Person) the id's must be set to different values
 *
 */
@MappedSuperclass
public abstract class BaseModel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	public final Long getId() {
		return id;
	}

	public final void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return this.getClass().getCanonicalName() + " id:" + getId();
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		String className = this.getClass().getCanonicalName();
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final BaseModel other = (BaseModel) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
