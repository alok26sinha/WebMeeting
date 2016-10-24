package controller.support;

import model.Person;
import security.SecurityContext;

import common.UncheckedException;

@SuppressWarnings("serial")
public abstract class BaseAdminLesdAction extends BaseLesdAction {

	protected void checkCurrentUserIsAdmin() {
		SecurityContext securityContext = getSecurityContext();
		Person person = securityContext.getUser();
		if( !person.administrator)
			throw new UncheckedException("Person with out admin permissions attempted to access admin pages");
	}

}
