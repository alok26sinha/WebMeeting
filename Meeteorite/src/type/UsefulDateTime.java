package type;

import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import common.UncheckedException;

/**
 * An immutable class that represents a date and time to milliseconds
 */
public class UsefulDateTime implements Comparable<UsefulDateTime> {

	private final DateTime dateTime;

	private UsefulDateTime(DateTime dateTime) {
		if (dateTime == null)
			throw new UncheckedException("Cannot create with a null date");
		this.dateTime = dateTime;
	}

	private static UsefulDateTime create(DateTime dateTime) {
		return new UsefulDateTime(dateTime);
	}

	// Factories
	public static UsefulDateTime now() {
		return now(TimeZone.currentUser());
	}

	public static UsefulDateTime now(TimeZone timeZone) {
		DateTime time = new DateTime(timeZone.zone);
		return create(time);
	}

	public static UsefulDateTime create(int year, int monthOfYear,
			int dayOfMonth, int hourOfDay, int minuteOfHour,
			int secondOfMinute, int millisOfSecond) {
		return create(year, monthOfYear, dayOfMonth, hourOfDay,
				minuteOfHour, secondOfMinute, millisOfSecond,
				TimeZone.currentUser());
	}

	public static UsefulDateTime create(int year, int monthOfYear,
			int dayOfMonth, int hourOfDay, int minuteOfHour,
			int secondOfMinute, int millisOfSecond, TimeZone timeZone) {
		DateTime time = new DateTime(year, monthOfYear, dayOfMonth, hourOfDay,
				minuteOfHour, secondOfMinute, millisOfSecond,
				timeZone.zone);
		return create(time);
	}

	public static UsefulDateTime create(Timestamp timestamp) {
		return create(timestamp, TimeZone.currentUser());
	}

	public static UsefulDateTime create(Timestamp timestamp, type.TimeZone timeZone) {
		DateTime time = new DateTime(timestamp, timeZone.zone);
		return create(time);
	}
	
	public UsefulDateTime convertToUserTimeZone(){
		return create(getTimestamp());
	}

	public UsefulDateTime convertToTimeZone(TimeZone timeZone){
		return create(getTimestamp(), timeZone);
	}
	/**
	 * Create a UsefulDateTime from a string
	 */
	public static UsefulDateTime create(String dateTimeString) {
		try {
			// TODO: made default time zone configurable
			DateTimeFormatter formatter = DateTimeFormat.forPattern(
					"yyyy-MM-dd HH:mm").withZone(TimeZone.currentUser().zone);
			DateTime dt = formatter.parseDateTime(dateTimeString);
			return create(dt);
		} catch (Exception e) {
			throw new UncheckedException("Failed to parse:" + dateTimeString
					+ " as a date-time.", e);
		}
	}
	
	/**
	 * Create a UsefulDateTime from a dateTimestring, a given format and timezone id
	 */
	public static UsefulDateTime create(String dateTimeString, String format, String timeZoneId) {
		try {
			DateTimeFormatter formatter = DateTimeFormat.forPattern(
					format).withZone(TimeZone.forID(timeZoneId).zone);
			DateTime dt = formatter.parseDateTime(dateTimeString);
			return create(dt);
		} catch (Exception e) {
			throw new UncheckedException("Failed to parse:" + dateTimeString
					+ " as a date-time.", e);
		}
	}
	
	/**
	 * Create a UsefulDateTime from a dateTimestring, a given format and TimeZone
	 */
	public static UsefulDateTime create(String dateTimeString, String format, TimeZone timeZone) {
		try {
			DateTimeFormatter formatter = DateTimeFormat.forPattern(
					format).withZone(timeZone.zone);
			DateTime dt = formatter.parseDateTime(dateTimeString);
			return create(dt);
		} catch (Exception e) {
			throw new UncheckedException("Failed to parse:" + dateTimeString
					+ " as a date-time.", e);
		}
	}
	
	public boolean onOrAfter8am(){
		int hourOfDay = dateTime.getHourOfDay();
		if( hourOfDay >= 8)
			return true;
		else 
			return false;
	}

	public int secondsToNextHour() {
		int minuteOfHour = dateTime.getMinuteOfHour();
		int secondOfMinute = dateTime.getSecondOfMinute();
		return (60*60) - (60* minuteOfHour + secondOfMinute);
	}


	// Output
	public String toString() {
		return dateTime.toString();
	}

	public String format(String pattern) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
		return formatter.print(dateTime);
	}

	public String getLongFormat() {
		return format("d MMMM yyyy HH:mm");
	}

	public String getDateFormat() {
		return format("yyyy-MM-dd");
	}
	
	public String getDayMonthFormat() {
		return format("d MMM");
	}

	public String getTimeFormat() {
		return format("HH:mm");
	}
	
	public UsefulDate getUsefulDate(){
		String dateFormat = getDateFormat();
		return UsefulDate.createSaasuFormat(dateFormat);
	}
	public String getFriendlyFormat(){
		UsefulDate date = getUsefulDate();	
		return date.getFriendlyFormat();
	}
	
	public String getTimeZoneFormat(){
		TimeZone zone = TimeZone.currentUser();
		return zone.getID();
	}

	// Date algebra
	public boolean isAfter(UsefulDateTime otherDateTime) {
		return dateTime.isAfter(otherDateTime.dateTime);
	}

	public UsefulDateTime addSeconds(int seconds) {
		DateTime newDateTime = dateTime.plusSeconds(seconds);
		return create(newDateTime);
	}

	public UsefulDateTime addDays(int days) {
		DateTime newDateTime = dateTime.plusDays(days);
		return create(newDateTime);
	}

	public int minutesBetween(UsefulDateTime otherDateTime) {
		Minutes minutes = Minutes.minutesBetween(dateTime,
				otherDateTime.dateTime);
		return minutes.getMinutes();
	}
	
	public MinuteDuration minuteDurationBetween(UsefulDateTime otherDateTime){
		long millisecondsBetween = this.dateTime.getMillis() - otherDateTime.dateTime.getMillis();
		
		if( millisecondsBetween < 0)
			millisecondsBetween = -1 * millisecondsBetween;
		
		
		float decimalMinutesBetween = (float)millisecondsBetween / (1000 * 60);
		
		return MinuteDuration.create(decimalMinutesBetween);
	}
	

	public int secondsBetween(UsefulDateTime otherDateTime) {
		Seconds seconds = Seconds.secondsBetween(dateTime,
				otherDateTime.dateTime);
		return seconds.getSeconds();
	}

	// Object support
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dateTime == null) ? 0 : dateTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final UsefulDateTime other = (UsefulDateTime) obj;
		if (dateTime == null) {
			if (other.dateTime != null)
				return false;
		} else if (!dateTime.equals(other.dateTime))
			return false;
		return true;
	}

	@Override
	public int compareTo(UsefulDateTime other) {
		return dateTime.compareTo(other.dateTime);
	}

	/**
	 * This is use by hibernate to save this type.
	 * 
	 * Do not recommend direct use
	 */
	java.sql.Timestamp getTimestamp() {
		return new java.sql.Timestamp(dateTime.getMillis());
	}
	
	public java.util.Date getDate(){
		return new Date(dateTime.getMillis());
	}

	public DateTime toDateTime() {
		return new DateTime(dateTime);
	}

	/**
	 * Returns dateTime formated for iCalendar in UTC time zone
	 * 
	 * format: yyyyMMddTHHmmssZ, example: 20051208T060000Z seconds are always 00
	 */
	public String dateToMime() {
		DateTime utcTimestamp = dateTime.toDateTime(DateTimeZone.UTC);
		DateTimeFormatter formatter = DateTimeFormat
				.forPattern("yyyyMMdd'T'HHmm'00Z'");
		String formattedDate = formatter.print(utcTimestamp);
		return formattedDate;
	}

}
