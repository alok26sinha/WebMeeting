package type;

import java.util.Map;

import ognl.DefaultTypeConverter;

public class UsefulDateTypeConverter extends DefaultTypeConverter {
	

	public Object convertValue(Map ctx, Object o, Class toType) {
		if (toType == UsefulDate.class) {
			String dateString = ((String[]) o)[0];
			return UsefulDate.create(dateString);
		} else if (toType == String.class) {
			UsefulDate date = (UsefulDate) o;
			return date.toString();
		}
		return null;
	}
}