package type;



import model.Person;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTimeZone;

import security.LocalSecurityContext;
import security.SecurityContext;

import common.UncheckedException;

public class TimeZone {
	private static final Log log = LogFactory.getLog(TimeZone.class);
	public static final TimeZone TZ_SYD = TimeZone.forID("Australia/NSW");
	public static final TimeZone UTC = new TimeZone(DateTimeZone.UTC);
	public static final TimeZone LOCAL = new TimeZone(DateTimeZone.getDefault());

	protected final DateTimeZone zone;

	private TimeZone(DateTimeZone zone) {
		this.zone = zone;
	}

	public static TimeZone forID(String id) {
		DateTimeZone zone = DateTimeZone.forID(id);
		return new TimeZone(zone);
	}
	
	//this method converts the timezone display name string to TimeZone object
	
	public static TimeZone getTimeZone(String timeZoneDisplayName){
		java.util.TimeZone timeZone = null;
		for (String availId : java.util.TimeZone.getAvailableIDs()){
		  if (timeZoneDisplayName.trim().equalsIgnoreCase(java.util.TimeZone.getTimeZone(availId).getDisplayName())){
			  timeZone = java.util.TimeZone.getTimeZone(availId);
		      break;
		  }
		}
		return forTimeZone(timeZone);
	}

	
	public static TimeZone forTimeZone(java.util.TimeZone tz) {
		DateTimeZone zone = DateTimeZone.forTimeZone(tz);
		return new TimeZone(zone);
	}

	public static TimeZone currentUser() {
		String timeZoneName = null;
		try {
			if (LocalSecurityContext.hasConextSet()) {
				SecurityContext securityContext = LocalSecurityContext.get();
				Person currentUser = securityContext.getUser();
				timeZoneName = currentUser.userTimeZone;
				return forID(timeZoneName);
			} else {
				return TZ_SYD;
			}
		} catch (Throwable e) {
			log.error("Could not determine timeZone from id:" + timeZoneName
					+ " Assuming Sydney", e);
			return TZ_SYD;
		}
	}

	public String getID() {
		return zone.getID();
	}
}
