package type;

import java.io.Serializable;
import java.text.DecimalFormat;

import common.UncheckedException;

/**
 * Represents a duration of time. Units of decimal hours.
 */
public final class Duration implements Comparable<Duration>, Serializable {
	private static final long serialVersionUID = -5616679510404964004L;

	private final double hours;
	// This is used as a display convenience. The value is always stored as hours
	private final boolean enteredAsHours;

	public static final Duration ZERO = new Duration(0.0, true);

	private Duration(double hours, boolean enteredAsHours) {
		this.hours = hours;
		this.enteredAsHours = enteredAsHours;
	}

	// Factory methods
	public static Duration create(double hours) {
		return create(hours, true);
	}

	public static Duration create(double hours, boolean enteredAsHours) {
		if (hours != 0.0) {
			return new Duration(hours, enteredAsHours);
		} else {
			return ZERO;
		}
	}

	public static Duration create(String durationString) {
		double totalTime;
		boolean enteredAsHours;

		if (durationString.endsWith("d")) {
			enteredAsHours = false;
			totalTime = parseDouble(durationString);
			totalTime = totalTime * 8;
		} else {
			enteredAsHours = true;
			int seperator = durationString.indexOf(":");
			if (seperator == -1) {

				totalTime = parseDouble(durationString);
			} else {
				String hoursString = durationString.substring(0, seperator);

				String minutesString = durationString.substring(seperator + 1);
				// Trim off anything past two decimal places
				if (minutesString.length() > 2) {
					minutesString = minutesString.substring(0, 2);
				}
				double hours = parseDouble(hoursString);
				double minutes = parseDouble(minutesString);
				totalTime = hours + (minutes / 60);
			}
		}

		return create(totalTime, enteredAsHours);
	}

	private static double parseDouble(String string) {
		try {
			return Double.parseDouble(string);
		} catch (NumberFormatException e) {
			RuntimeException exception = new UncheckedException(
					"Could not parse:" + string + " as a double. ", e);
			throw exception;
		}
	}

	// Duration algebra
	public Duration plus(Duration other) {
		double totalHours = hours + other.hours;
		return create(totalHours);
	}

	public Duration minus(Duration other) {
		double totalHours = hours - other.hours;
		return create(totalHours);
	}

	public boolean lessThan(Duration other) {
		return this.hours < other.hours;
	}

	public boolean greaterThan(Duration other) {
		return this.hours > other.hours;
	}

	public boolean greaterThanOrEqualTo(Duration other) {
		return this.hours >= other.hours;
	}

	public boolean isPositive() {
		return greaterThan(Duration.ZERO);
	}

	public boolean isNegative() {
		return lessThan(Duration.ZERO);
	}

	/**
	 * Used by user type to get hold of underlying double to set database query
	 * parameters.
	 * 
	 * Use discouraged
	 */
	public double getTime() {
		return hours;
	}
	
	public boolean isEnteredAsHours(){
		return enteredAsHours;
	}

	// -------------------------------------------------------------------
	// Output
	@Override
	public String toString() {
		if (enteredAsHours)
			return toString(2);
		else {
			double days = hours / 8;
			//Round off two decimal places
			days = (double)Math.round(days * 100) / 100;
			
			return days + "d";
		}
	}

	public String toString(int decimalPlaces) {
		String pattern = "#";
		if (decimalPlaces > 0) {
			pattern = pattern + ".";
			for (int i = 0; i < decimalPlaces; i++) {
				pattern = pattern + "0";
			}
		}

		DecimalFormat multiplePlaces = new DecimalFormat(pattern);
		return multiplePlaces.format(hours);
	}

	public String getOneDecimalPlace() {
		return toString(1);
	}

	/**
	 * If zero it will return the empty string otherwise it will return the
	 * duration to one decimal place
	 */
	public String getEmptyOrOneDecimalPlace() {
		if (ZERO.equals(this))
			return "";
		else
			return getOneDecimalPlace();
	}

	// ----------------------------------------------------------------------
	// Object support
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(hours);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		final Duration other = (Duration) obj;
		if (Double.doubleToLongBits(hours) != Double
				.doubleToLongBits(other.hours))
			return false;
		return true;
	}

	@Override
	public int compareTo(Duration other) {
		if (hours < other.hours) {
			return -1;
		} else if (hours > other.hours) {
			return 1;
		} else {
			return 0;
		}
	}

}
