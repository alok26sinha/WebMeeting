package security;

import spring.LocalApplicationContext;
import model.Company;
import model.Person;
import dao.CompanyDao;

/**
 * Used in running system test
 */
public class TestSecurityContext extends SecurityContext {

	
	private static Person testUser;
	
	static {
		testUser = new Person();
		testUser.name = "Test User";
		testUser.userTimeZone = "Australia/Sydney";
	}

	@Override
	public String getUserName() {
		return testUser.name;
	}

	@Override
	public boolean isValidUser() {
		return true;
	}
	
	@Override
	public void invalidateUser(){
	}

	@Override
	public Person getUser() {
		return testUser;
	}

	@Override
	public boolean isAllowedAccess(String securedItem) {
		return true;
	}

}
