package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import type.NullSafeComparator;

/**
 * Represents a secured item.
 * 
 * Examples: 1. the Winning Work Tab 2. The Winning Work - Analysis Menu 3.
 * Saving the Estimate
 * 
 */
@Entity
public class SecuredItem extends BaseModel implements Comparable<SecuredItem> {
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private SecuredItem parent;
	@OneToMany(mappedBy = "parent")
	@OrderBy("id")
	private List<SecuredItem> children;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String key;

	// Getters and setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public SecuredItem getParent() {
		return parent;
	}

	public void setParent(SecuredItem parent) {
		this.parent = parent;
	}

	public List<SecuredItem> getChildren() {
		return children;
	}

	public void setChildren(List<SecuredItem> children) {
		this.children = children;
	}

	@Override
	public int compareTo(SecuredItem o) {
		if (o == null)
			return -1;
		return NullSafeComparator.compare(getId(), o.getId());
	}
}
