package model;

import javax.persistence.Column;
import javax.persistence.Entity;

import type.UsefulDate;
import type.UsefulDateTime;

@Entity
public class Person extends BaseModel {

	@Column(length = 30)
	public String name;
	@Column(length = 100, unique = true)
	public String email;
	@Column(length = 255, unique = true)
	public String userToken;
	public boolean administrator;
	@Column(length = 255, unique = true)
	public String passwordToken;
	@Column(length = 255)
	public String encryptedPassword;
	@Column(length = 255)
	public String salt;
	@Column(length = 255)
	public String userTimeZone;
    @Column(length = 255)
    public String lastDashboardTab;
    @org.hibernate.annotations.Type(type = "type.UsefulDateTimeUserType")
    public UsefulDateTime lastActivity;
	public int reminderPeriodDays;
	@org.hibernate.annotations.Type(type = "type.UsefulDateUserType")
	public UsefulDate lastReminderSent;
	@org.hibernate.annotations.Type(type = "type.UsefulDateUserType")
	public UsefulDate lastReportReminderSent;
	public Boolean acceptTermsAndConditions;


	public String getCombined() {
	    return name + " (" + email + ")";
	}

}
