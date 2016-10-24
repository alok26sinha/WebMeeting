package type;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.joda.time.LocalDate;

import common.UncheckedException;

/**
 * An immutable class that represents a date (no time component.)
 * 
 * This class is being changed over to use Joda Dates internally. All usage of
 * java.util.Date and java.util.Calendar to be removed
 */
public final class UsefulDate implements Comparable<UsefulDate>, Serializable {
	private static final long serialVersionUID = 2004137901147494216L;
	
	private final LocalDate localDate;

	private UsefulDate(Date date) {
		this(new LocalDate(date));
	}

	private UsefulDate(LocalDate localDate) {
		this.localDate = localDate;
	}

	// Methods that determine days of the week
	/**
	 * Return the Friday at the end of this week
	 */
	public UsefulDate friday() {
		return gotoDayOfWeek(Calendar.FRIDAY);
	}

	/**
	 * Return the Sunday at the start
	 */
	public UsefulDate sunday() {
		return gotoDayOfWeek(Calendar.SUNDAY);
	}

	/**
	 * Return the Saturday at the end
	 */
	public UsefulDate saturday() {
		return gotoDayOfWeek(Calendar.SATURDAY);
	}

	/**
	 * Go to a specific day in the same week.
	 * 
	 * 1 = Sunday 7 = Saturday
	 * 
	 * See Calendar class
	 */
	private UsefulDate gotoDayOfWeek(int dayNumber) {
		Calendar calendar = getCalendar();
		calendar.setFirstDayOfWeek(Calendar.SUNDAY);

		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(Calendar.DAY_OF_WEEK, dayNumber - dayOfWeek);
		Date day = calendar.getTime();
		return create(day);
	}

	// Methods that work with months
	/**
	 * Return the month number
	 * 
	 * 0 = January
	 */
	public int getMonthNumber() {
		Calendar calendar = getCalendar();
		int monthOfYear = calendar.get(Calendar.MONTH);
		return monthOfYear;
	}

	/**
	 * Return the last day of the month
	 */
	public UsefulDate firstDayOfMonth() {
		Calendar calendar = getCalendar();

		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		// Go to the first of the month
		calendar.add(Calendar.DAY_OF_MONTH, 1 - dayOfMonth);
		Date firstDay = calendar.getTime();
		return create(firstDay);
	}

	/**
	 * Return the last day of the month
	 */
	public UsefulDate lastDayOfMonth() {
		return firstDayOfMonth().addMonths(1).addDays(-1);
	}

	// Methods for working with quarters
	/**
	 * Get the first day in the quarter
	 */
	public UsefulDate firstDayOfQuarter() {
		UsefulDate firstDayOfMonth = firstDayOfMonth();
		Calendar calendar = firstDayOfMonth.getCalendar();
		int monthOfYear = calendar.get(Calendar.MONTH); // With zero as first
		int monthOfQuarter = monthOfYear % 3; // With zero as first
		calendar.add(Calendar.MONTH, -monthOfQuarter);
		return create(calendar.getTime());
	}

	/**
	 * Get the quarter number. 1 = first quarter.
	 */
	public int getQuarterNumber() {
		SimpleDateFormat format = new SimpleDateFormat("M");
		String monthString = format.format(getDate());
		int month = Integer.parseInt(monthString);
		int quarter = 1 + ((month - 1) / 3);
		return quarter;
	}

	// Date algebra
	public UsefulDate addDays(int days) {
		return addToCalendar(Calendar.DAY_OF_YEAR, days);
	}

	public UsefulDate addMonths(int months) {
		return addToCalendar(Calendar.MONTH, months);
	}

	// Internal methods
	/**
	 * Used internally for date algebra
	 */
	private UsefulDate addToCalendar(int field, int amount) {
		Calendar calendar = getCalendar();
		calendar.add(field, amount);
		return create(calendar.getTime());
	}

	/**
	 * Used to create the calendar
	 */
	private Calendar getCalendar() {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(getDate());
		return calendar;
	}

	// Tests on dates
	public boolean isOnOrBefore(UsefulDate otherDate) {
		return !isAfter(otherDate);
	}

	public boolean isAfter(UsefulDate otherDate) {
		return getDate().after(otherDate.getDate());
	}

	/**
	 * Construct a list of 'count' UsefulDate that are 'days' apart
	 */
	public DateRange dayDateRange(int count, int days) {
		List<UsefulDate> range = new ArrayList<UsefulDate>();

		UsefulDate currentDate = this;

		for (int i = 0; i < count; i++) {
			range.add(currentDate);
			currentDate = currentDate.addDays(days);
		}

		return new DateRange(range, true);
	}

	/**
	 * Construct a list of 'count' UsefulDate that are 'months' apart.
	 * 
	 * Each month is the first day of the month
	 */
	public DateRange monthDateRange(int count, int months) {
		List<UsefulDate> range = new ArrayList<UsefulDate>();

		UsefulDate currentDate = this.firstDayOfMonth();

		for (int i = 0; i < count; i++) {
			range.add(currentDate);
			currentDate = currentDate.addMonths(months);
		}

		return new DateRange(range, false);
	}

	// Factory methods
	/**
	 * Create a UsefulDate from a java.util.date
	 */
	public static UsefulDate create(Date d) {
		if (d != null) {
			return new UsefulDate(d);
		} else {
			throw new UncheckedException("Cannot contain a null date");
		}
	}

	/**
	 * Create a UsefulDate from a string
	 */
	public static UsefulDate create(String dateString) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
		try {
			Date d = dateFormat.parse(dateString);
			return new UsefulDate(d);
		} catch (ParseException e) {
			throw new UncheckedException("Failed to parse:" + dateString
					+ " as a date.", e);
		}
	}

	public static UsefulDate createSaasuFormat(String dateString) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d = dateFormat.parse(dateString);
			return new UsefulDate(d);
		} catch (ParseException e) {
			throw new UncheckedException("Failed to parse:" + dateString
					+ " as a date.", e);
		}
	}
	
	/**
	 * Get hold of a UsefulDate representing today
	 */
	public static UsefulDate today() {
		return today(TimeZone.currentUser());
	}

	public static UsefulDate today(TimeZone timeZone) {
		LocalDate now = new LocalDate(timeZone.zone);
		return new UsefulDate(now);
	}

	// Working time
	private static final Duration DAILY_WOKRING_HOURS = Duration.create(8.0);
	/**
	 * Calculate a start date to complete this number of working hours
	 */
	public UsefulDate countBackWorkingHours(Duration hours) {
		if (hours == null)
			return this;
		else {
			
			if (isWorkDay()) {
				if( DAILY_WOKRING_HOURS.greaterThanOrEqualTo(hours))
					return this;
				
				hours = hours.minus(DAILY_WOKRING_HOURS);
			}
			
			UsefulDate previousDay = this.addDays(-1);
			return previousDay.countBackWorkingHours(hours);
		}
	}

	/**
	 * Calculate an end date to complete this number of working hours
	 */
	public UsefulDate countForwardWorkingHours(Duration hours) {
		if (hours == null)
			return this;
		else {
			
			if (isWorkDay()) {
				if( DAILY_WOKRING_HOURS.greaterThanOrEqualTo(hours))
					return this;
				
				hours = hours.minus(DAILY_WOKRING_HOURS);
			}
			
			UsefulDate previousDay = this.addDays(1);
			return previousDay.countForwardWorkingHours(hours);
		}
	}

	/**
	 * Is this a working day
	 */
	boolean isWorkDay() {
		Calendar calendar = getCalendar();

		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

		if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
			return false;
		else
			return true;

	}

	// Object support
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getDate() == null) ? 0 : getDate().hashCode());
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
		final UsefulDate other = (UsefulDate) obj;
		if (getDate() == null) {
			if (other.getDate() != null)
				return false;
		} else if (!getDate().equals(other.getDate()))
			return false;
		return true;
	}

	// Display as string
	@Override
	public String toString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
		return dateFormat.format(getDate());
	}

	public String getDayMonthFormat() {
		SimpleDateFormat dayMonthFormat = new SimpleDateFormat("d MMM");
		return dayMonthFormat.format(getDate());
	}

	public String getMonthYearFormat() {
		SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMM yyyy");
		return monthYearFormat.format(getDate());
	}

	public String getDayMonthYearFormat() {
		SimpleDateFormat monthYearFormat = new SimpleDateFormat("d MMM yyyy");
		return monthYearFormat.format(getDate());
	}
	
	public String getLongDayMonthYearFormat() {
		SimpleDateFormat monthYearFormat = new SimpleDateFormat("d MMMMM yyyy");
		return monthYearFormat.format(getDate());
	}

	public String getDayOfWeekFormat() {
		SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEEEEEEE");
		return dayOfWeekFormat.format(getDate());
	}

	public String getDayOfWeekShortFormat() {
		SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE");
		return dayOfWeekFormat.format(getDate());
	}

	public String getYearFormat() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		return format.format(getDate());
	}
	
	public String getFriendlyFormat(){
		UsefulDate today = UsefulDate.today();
		
		if( today.equals(this))
			return "Today";
		else if( today.addDays(1).equals(this))
			return "Tomorrow";
		else{
			SimpleDateFormat monthYearFormat = new SimpleDateFormat("d MMM yyyy");
			return monthYearFormat.format(getDate());
		}
	}

	public String getQuarterFormat() {
		return "Q " + getQuarterNumber();
	}

	public String getMonthFormat() {
		SimpleDateFormat format = new SimpleDateFormat("MMM");
		return format.format(getDate());
	}

	public String getICalendarFormat() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.format(getDate());
	}

	public String getSaasuFormat() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(getDate());
	}

	/**
	 * Used by hibernate to persist this type. Not recommended for other use.
	 */
	public java.sql.Date getSqlDate() {
		return new java.sql.Date(getDate().getTime());
	}

	private java.util.Date getDate() {
		return localDate.toDateTimeAtStartOfDay().toDate();
	}

	@Override
	public int compareTo(UsefulDate other) {
		return localDate.compareTo(other.localDate);
	}

}
