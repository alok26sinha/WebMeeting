package security;

import java.util.HashSet;
import java.util.List;

import model.Company;
import model.Person;
import model.SecuredItem;

public class ApplicationSecurityContext extends SecurityContext {

	private boolean validUser;
	private Person user;
	private HashSet<String> allowedSecuredItems = new HashSet<String>();

	@Override
	public boolean isValidUser() {
		return validUser;
	}
	
	@Override
	public void invalidateUser(){
		validUser = false;
		//Not sure we want to do this.  Will force other logged in sessions to relog in.
		//user.userToken = null;
	}

	protected void setValidUser(boolean validUser) {
		this.validUser = validUser;
	}

	@Override
	public String getUserName() {
		return user.name;
	}
	


	public Person getUser() {
		return user;
	}

	public void setUser(Person user) {
		this.user = user;
	}
	
	public void addAllowedSecuredItem(List<SecuredItem> securedItems){
		for( SecuredItem securedItem: securedItems){
			addAllowedSecuredItem(securedItem);
		}
	}

	public void addAllowedSecuredItem(SecuredItem securedItem) {
		allowedSecuredItems.add(securedItem.getKey());
	}
	
	

	@Override
	public boolean isAllowedAccess(String securedItem) {
		if( user.administrator ){
			return true;
		}
		else{
			return allowedSecuredItems.contains(securedItem);
		}
	}

}
