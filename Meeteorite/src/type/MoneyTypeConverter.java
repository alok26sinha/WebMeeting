package type;

import ognl.DefaultTypeConverter;
import java.util.Map;

public class MoneyTypeConverter extends DefaultTypeConverter {
	public Object convertValue(Map ctx, Object o, Class toType) {
		if (toType == Money.class) {
			String dollarString = ((String[]) o)[0];
			return Money.create(dollarString);
		} else if (toType == String.class) {
			Money amount = (Money) o;
			return amount.toString();
		}
		return null;
	}
}
