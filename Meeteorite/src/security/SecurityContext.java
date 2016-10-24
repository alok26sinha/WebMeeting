package security;

import model.Company;
import model.Person;

public abstract class SecurityContext {

	public static final String DELIVERY_TIME_SHEETS_ACCESS_OTHERS = "Delivery#Time Sheets#Access Everyones";

	public abstract boolean isValidUser();

	public abstract void invalidateUser();

	public abstract String getUserName();

	public abstract Person getUser();

	public abstract boolean isAllowedAccess(String securedItem);

	public boolean isAllowedAccess(Class clazz) {
		return isAllowedAccess(clazz.getCanonicalName());
	}

	public boolean isAllowedAccess(Class clazz, String qualifier) {
		return isAllowedAccess(clazz.getCanonicalName() + "#" + qualifier);
	}

	/*
	 * public boolean isAllowedAccess(TopMenuItem item) { return
	 * isAllowedAccess(item.getSecuredItemName()); }
	 * 
	 * public boolean isAllowedAccess(MiddleMenuItem item) { return
	 * isAllowedAccess(item.getSecuredItemName()); }
	 */

}
