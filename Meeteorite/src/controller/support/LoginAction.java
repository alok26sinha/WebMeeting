package controller.support;

import java.util.Map;

import model.Person;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.FormAuthenticationInterceptor;
import security.NoAuthenticationRequired;
import security.SecurityService;
import servlet.CookieManager;
import type.StringUtils;

import com.opensymphony.xwork2.ActionContext;
import common.UncheckedException;

import controller.support.BaseAction;
import dao.RecordNotFoundException;

@NoAuthenticationRequired
@SuppressWarnings("serial")
public class LoginAction extends BaseAction {
	private static Log log = LogFactory.getLog(LoginAction.class);

	public String email;
	public String password;
	public boolean rememberMe;
	public String forgottenPassword;

	private static CookieManager cookieManager = new CookieManager();
	private SecurityService securityService;

	@Override
	public String execute() {

		return EDIT;
	}

	public String displayPage() {
		email = lowerCase(email);

		if (!getSecurityContext().isValidUser())
			addActionMessage("Please enter you email address and password below to login.");

		return EDIT;
	}

	public String post() {
		email = lowerCase(email);

		// Check if forgotten password was pressed
		if (forgottenPassword == null)
			return login();
		else
			return forgottenPassword();

	}

	public String login() {
		getSecurityContext().invalidateUser();
		cookieManager.deleteUserTokenCookie(request, response);
		
		email = lowerCase(email);

		if (alreadyLoggedIn()) {
			log.info("Already logged in");
			return redirectHome();
		} else
			try {

				if (password == null) {
					addActionError("Please enter you password");
					return EDIT;
				}
				if (email == null) {
					addActionError("Please enter your email address");
					return EDIT;
				}

				Person person = securityService.login(email, password);

				// Set cookie
				cookieManager.setUserTokenInCookie(person.userToken,
						rememberMe, request, response);

				// Check if has a last page request was unsuccessful
				Map session = ActionContext.getContext().getSession();
				String lastUnsuccessfulUri = (String) session
						.get(FormAuthenticationInterceptor.LAST_UNSUCCESSFUL_URI);
				if (StringUtils.isEmpty(lastUnsuccessfulUri))
					// Redirect to home page
					return redirectHome();
				else {
					String forwardTo = lastUnsuccessfulUri;
					session.put(
							FormAuthenticationInterceptor.LAST_UNSUCCESSFUL_URI,
							null);
					return redirect(forwardTo);
				}
			} catch (RecordNotFoundException e) {
				log.info("Invalid password for:" + email);
				addActionError("Invalid password. If you have forgotten you password please enter your email address and click forgotten password.");
				return EDIT;
			}
	}

	private String redirectHome() {
		return redirectDashboard();
	}

	private boolean alreadyLoggedIn() {
		String userToken = cookieManager.getUserTokenFromCookie(request);
		if (userToken != null)
			return securityService.passwordTokenExists(userToken);
		else
			return false;
	}

	public String logout() {
		getSecurityContext().invalidateUser();
		cookieManager.deleteUserTokenCookie(request, response);
		return displayPage();
	}

	public String forgottenPassword() {
		throw new UncheckedException("Not yet implemented");
		// TODO
		/*
		 * email = lowerCase(email);
		 * 
		 * securityService.sendLinkEmail(email);
		 * 
		 * addActionMessage(
		 * "A new link has been emailed to you. Please click on that link to set your new password."
		 * ); return EDIT;
		 */
	}

	private String lowerCase(String inputEmail) {
		if (inputEmail != null)
			return inputEmail.toLowerCase();
		else
			return null;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	public String getForgottenPassword() {
		return forgottenPassword;
	}

	public void setForgottenPassword(String forgottenPassword) {
		this.forgottenPassword = forgottenPassword;
	}

	// Getters and setters

}
