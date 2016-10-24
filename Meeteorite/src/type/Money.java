package type;

import java.io.Serializable;
import java.text.DecimalFormat;

import common.UncheckedException;

/**
 * Holds a dollar (in Australia and US) amount.
 * 
 * An immutable class.
 */
public final class Money implements Comparable<Money>, Serializable {
	private static final long serialVersionUID = -1931445384302295484L;
	private final long centValue;
	private transient static int COMMA_POSITION = 2;
	private transient static int CENT_MULTIPLIER = (int) Math.pow(10, COMMA_POSITION);
	public transient static final Money ZERO = new Money(0);

	private Money(long cents) {
		centValue = cents;
	}

	
	// Factory methods.
	public static Money create(String monetaryString){
		long dollars;
		long cents;

		// Strip out , and $
		monetaryString = monetaryString.replace(",", "");
		monetaryString = monetaryString.replace("$", "");

		int dotPosition = monetaryString.indexOf(".");
		if (dotPosition >= 0) {
			String dollarString = monetaryString.substring(0, dotPosition);
			if (dollarString.length() > 0) {
				dollars = Long.parseLong(dollarString);
			} else {
				dollars = 0;
			}
			String centsString = monetaryString.substring(dotPosition + 1,
					monetaryString.length());
			if (centsString.length() >= 2) {
				// Trim off decimal cents
				centsString = centsString.substring(0, 2);
				cents = Long.parseLong(centsString);
			} else if (centsString.length() == 1) {
				// Need to pad with decimal cents
				cents = Long.parseLong(centsString + "0");
			} else {
				// We have no cents value
				cents = 0;
			}
		} else {
			// no dot, assume value is given in dollars
			dollars = Long.parseLong(monetaryString);
			cents = 0;
		}
		long centValue;
		//Handle a negative entered value
		if( dollars >= 0){
			centValue = dollars * CENT_MULTIPLIER + cents;
		}
		else{
			centValue = dollars * CENT_MULTIPLIER - cents;
		}
		return create(centValue);
	}
	
	public static Money create(long cents){
		if( cents != 0){
			return new Money(cents);
		}
		else{
			return ZERO;
		}
	}

	// Calculation methods
	public Money times(long term) {
		long product = centValue * term;
		return create(product);
	}

	public Money times(double estimatedDuration) {
		long product = Math.round(centValue * estimatedDuration);
		return create(product);
	}
	
	public Money times(Duration duration){
		return times(duration.getTime());
	}

	public long percentOf(Money divisor) {
		if (!ZERO.equals(divisor)) {
			return (long) (((double) centValue * 100) / divisor.centValue);
		} else {
			throw new UncheckedException("Zero divide error");
		}
	}

	public Money addPercent(Integer markupPercent) {
		double markupPercentAsDouble;
		if (markupPercent != null) {
			markupPercentAsDouble = (double) markupPercent;
		} else {
			markupPercentAsDouble = 0.0;
		}
		double doubleResult = centValue * (1 + (markupPercentAsDouble / 100));
		long result = Math.round(doubleResult);
		return new Money(result);
	}

	public Money plus(Money value) {
		if (value != null) {
			long result = centValue + value.centValue;
			return create(result);
		} else {
			return this;
		}
	}

	public Money minus(Money value) {
		if (value != null) {
			long result = centValue - value.centValue;
			return create(result);
		} else {
			return this;
		}
	}
	
	/**
	 * Nulls are treated as zero
	 */
	public boolean isGreaterThan(Money value){
		if( value == null)
			value = Money.ZERO;
		return this.centValue > value.centValue;
	}
	
	public boolean isZero(){
		return equals(Money.ZERO);
	}

	// Object support methods
	public String toString() {
		return format("#,##0;-#,##0","00");
	}
	
	public String toStringSaasuFormat(){
		return format("###0;-###0","00");
	}
	
	private String format(String dollarFormatPattern, String centFormatPattern){
		DecimalFormat dollarFormat = new DecimalFormat(dollarFormatPattern);
		DecimalFormat centFormat = new DecimalFormat(centFormatPattern);
		
		long dollars = centValue / CENT_MULTIPLIER;
		long cents = Math.abs(centValue % CENT_MULTIPLIER);

		return dollarFormat.format(dollars) + "." + centFormat.format(cents);		
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (centValue ^ (centValue >>> 32));
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
		final Money other = (Money) obj;
		if (centValue != other.centValue)
			return false;
		return true;
	}

	@Override
	public int compareTo(Money otherAmount) {
		if (centValue < otherAmount.centValue) {
			return -1;
		} else if (centValue > otherAmount.centValue) {
			return 1;
		} else {
			return 0;
		}
	}

	public long getCents() {
		return centValue;
	}

}
