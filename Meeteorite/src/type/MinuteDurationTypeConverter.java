package type;

import java.util.Map;

import ognl.DefaultTypeConverter;

public class MinuteDurationTypeConverter extends DefaultTypeConverter {
	public Object convertValue(Map ctx, Object o, Class toType) {
		if (toType == MinuteDuration.class) {
			String stringValue = ((String[]) o)[0];
			long longValue = Long.parseLong(stringValue);
			return MinuteDuration.create(longValue);
		} else if (toType == String.class) {
			MinuteDuration duration = (MinuteDuration) o;
			return duration.toStringMinutes();
		}
		return null;
	}
}
