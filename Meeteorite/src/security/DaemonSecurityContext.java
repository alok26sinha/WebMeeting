package security;

import model.Company;
import model.Person;

/**
 * The security context used by daemon (background) process
 */
public class DaemonSecurityContext extends SecurityContext{

	private final Person daemonUser;
	/**
	 * Create a new context based that will modify records based on the 
	 * supplied company 
	 */
	public DaemonSecurityContext(Company company){
		daemonUser = new Person();
		daemonUser.name = "Daemon Process";
	}

	@Override
	public String getUserName() {
		return daemonUser.name;
	}

	@Override
	public boolean isValidUser() {
		return true;
	}
	
	@Override
	public void invalidateUser(){
		//Do nothing. Daemon always valid
	}

	@Override
	public Person getUser() {
		return daemonUser;
	}

	@Override
	public boolean isAllowedAccess(String securedItem) {
		return true;
	}

}
