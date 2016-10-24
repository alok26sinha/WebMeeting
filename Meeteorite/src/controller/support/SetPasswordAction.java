package controller.support;

import javax.annotation.Resource;

import model.Person;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.NoAuthenticationRequired;
import security.SecurityService;
import service.EventService;
import servlet.CookieManager;

@SuppressWarnings("serial")
@NoAuthenticationRequired
public class SetPasswordAction extends BaseAction {
	private static Log log = LogFactory.getLog(SetPasswordAction.class);

	private String passwordToken;
	private SecurityService securityService;
	private String password1;
	private String password2;
	public boolean acceptTermsAndConditions;
	private static CookieManager cookieManager = new CookieManager();
	
	@Resource
	private EventService eventService;

	public String displayPage() {
		if (getSecurityContext().isValidUser())
			/*
			 * If the user is logged on we go straight to the main application.
			 * This allows use of the login link as the main link to the
			 * application
			 */
			return redirectToApplication();
		else {
			if (isTokenValid()) {
				addActionMessage("Please enter your chosen password. Passwords need have 8 or more characters.");
				addActionMessage("Please also review and accept the Terms and Conditions and Privacy Policy of this service.");
				return EDIT;
			} else {
				return redirect("../support/ForgottenPassword!linkExpired.action");
			}
		}
	}

	private String redirectToLogin() {
		return redirect("../support/Login!displayPage.action");
	}

	private String redirectToApplication() {
		return redirect("../");
	}

	private boolean isTokenValid() {
		if (securityService.passwordTokenExists(passwordToken))
			return true;
		else {
			log.info("Invalid password token:" + passwordToken);
			return false;
		}
	}

	public String setPassword() {
		
		if(!acceptTermsAndConditions){
			addActionMessage("Please review and accept the Terms and Conditions and Privacy Policy to use this service.");
			return EDIT;
		}

		if (isTokenValid()) {
			validate();
			if (!hasErrors()) {
				log.info("Updating password");
				Person person = securityService.setPassword(passwordToken,
						password1);
				person.acceptTermsAndConditions = true;
				cookieManager.setUserTokenInCookie(person.userToken,
						false, request, response);
				eventService.logEvent(person, "Set pword and accept T&Cs");
				return redirectToApplication();
			}
			else{
				//go back and edit page
				return EDIT;
			}
		}
		else{
			log.info("Invalid token");
			return redirectToLogin();
		}
		
	}

	public void validate() {

		if (isEmpty(password1)) {
			addActionError("Please enter your new password of 8 or more characters.");
		} else if (password1.length() < 8) {
			addActionError("Passwords need to be 8 or more characters.");
		} else if (!password1.equals(password2)) {
			addActionError("The passwords do not match, please enter them again.");
		}

	}

	
	// Getters and setters
	public String getPasswordToken() {
		return passwordToken;
	}

	public void setPasswordToken(String passwordToken) {
		this.passwordToken = passwordToken;
	}

	public String getPassword1() {
		return password1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public boolean isAcceptTermsAndConditions() {
		return acceptTermsAndConditions;
	}

	public void setAcceptTermsAndConditions(boolean acceptTermsAndConditions) {
		this.acceptTermsAndConditions = acceptTermsAndConditions;
	}
	
	
}
