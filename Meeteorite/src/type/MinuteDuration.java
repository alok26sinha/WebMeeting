package type;

import java.io.Serializable;

/**
 * Immutable class that represents a duration of time in minutes
 */
public class MinuteDuration implements Comparable<MinuteDuration>, Serializable {
	private static final long serialVersionUID = 1289436528588251035L;

	private final float minutes;

	private MinuteDuration(float minutes) {
		this.minutes = minutes;
	}

	public static final MinuteDuration ZERO = new MinuteDuration(0l);

	// Factory methods
	public static MinuteDuration create(long minutes) {
		if (minutes != 0l)
			return new MinuteDuration(minutes);
		else
			return ZERO;
	}
	
	public static MinuteDuration create(float minutes) {
		if (minutes != 0.0)
			return new MinuteDuration(minutes);
		else
			return ZERO;
	}

	public static MinuteDuration create(String minuteString) {
		if( minuteString == null)
			return null;
		
		long minutes = Long.parseLong(minuteString);
		
		if (minutes != 0l)
			return new MinuteDuration(minutes);
		else
			return ZERO;
	}
	
	public MinuteDuration add(MinuteDuration other){
		return MinuteDuration.create(minutes + other.minutes);
	}
	
	public MinuteDuration subtract(MinuteDuration other) {
		return MinuteDuration.create(minutes - other.minutes);
	}
	
	public long getMinutes() {
		return (long)minutes;
	}
	
	public float getMinutesFloat(){
		return minutes;
	}

	@Override
	public String toString() {
		if (minutes < 60)
			return (long)minutes + " mins";
		else {
			long hours = (long)minutes / 60;
			long remainingMins = (long)minutes % 60;
			if (remainingMins == 0l)
				return hours + " h";
			else
				return hours + " h " + remainingMins + " mins";
		}
	}

	public String toStringMinutes() {
		return "" + (long)minutes;
	}

	// Object support methods
	@Override
	public int compareTo(MinuteDuration other) {
		if (minutes < other.minutes)
			return -1;
		else if (minutes > other.minutes)
			return +1;
		else
			return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(minutes);
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
		MinuteDuration other = (MinuteDuration) obj;
		if (Float.floatToIntBits(minutes) != Float
				.floatToIntBits(other.minutes))
			return false;
		return true;
	}

	
}
