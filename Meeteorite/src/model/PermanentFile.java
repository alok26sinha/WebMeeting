package model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import type.UsefulDateTime;

@Entity
public class PermanentFile extends BaseModel {

	@Column(length = 256)
	private String sourceContentType;
	@Column(length = 256)
	private String sourceFileName;
	private long length;
	@Column(nullable = false)
	@org.hibernate.annotations.Type(type = "type.UsefulDateTimeUserType")
	private UsefulDateTime dateTimeLoaded;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	public Meeting meeting;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	// File name processing
	public String getExtension() {
		int lastPeriod = sourceFileName.lastIndexOf(".");
		if (lastPeriod > -1) {
			return sourceFileName.substring(lastPeriod + 1, sourceFileName
					.length());
		} else
			return "";
	}

	@Override
	public String toString() {
		return super.toString() + " length:" + length + " sourceContentType:"
				+ sourceContentType + " sourceFileName:" + sourceFileName
				+ " dateTimeLoaded:" + dateTimeLoaded.toString();
	}

	public String getSourceContentType() {
		return sourceContentType;
	}

	public void setSourceContentType(String sourceContentType) {
		this.sourceContentType = sourceContentType;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public UsefulDateTime getDateTimeLoaded() {
		return dateTimeLoaded;
	}

	public void setDateTimeLoaded(UsefulDateTime dateTimeLoaded) {
		this.dateTimeLoaded = dateTimeLoaded;
	}

}
