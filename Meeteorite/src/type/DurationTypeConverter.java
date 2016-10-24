package type;

import java.util.Map;

import ognl.DefaultTypeConverter;

public class DurationTypeConverter extends DefaultTypeConverter {
	@SuppressWarnings("unchecked")
	public Object convertValue(Map ctx, Object o, Class toType) {
		if (toType == Duration.class) {
			String durationString = ((String[]) o)[0];
			return Duration.create(durationString);
		} else if (toType == String.class) {
			Duration duration = (Duration) o;
			return duration.toString();
		}
		return null;
	}
}
