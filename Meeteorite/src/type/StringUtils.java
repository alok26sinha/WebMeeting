package type;

public class StringUtils {
	public static boolean isEmpty(String string) {
		if (string == null)
			return true;
		else {
			string = string.trim();
			return "".equals(string);
		}
	}

	public static String convertNewLine(String string) {
		if (isEmpty(string))
			return string;
		else
			return string.replaceAll("\n", "<br/>");

	}

	public static String trim(String string, int length) {
		if (string == null || "".equals(string))
			return string;
		else if (string.length() <= length)
			return string;
		else
			return string.substring(0, length) + "...";

	}
	
	public static boolean nullSafeEquals(String s1, String s2){
		if( s1 == null && s2 == null)
			return true;
		else if ( s1 != null )
			return s1.equals(s2);
		else
			// s1 == null and s2 != null
			return false;
		
	}
	
	public static String returnEmptyForNull(String value){
		if( value == null )
			return "";
		else
			return value;
	}

    public static boolean isEmail(String email) {
        return email.toUpperCase().matches("^[A-Z0-9\\._%+-]+@(?:[A-Z0-9-]+\\.)+[A-Z]{2,4}$");
    }
}
