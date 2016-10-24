package controller.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.NoAuthenticationRequired;
import security.SecurityService;
import type.StringUtils;

@SuppressWarnings("serial")
@NoAuthenticationRequired
public class ForgottenPasswordAction extends BaseAction {
	private static Log log = LogFactory.getLog(ForgottenPasswordAction.class);

	public String email;
	public boolean linkExpired;

	private SecurityService securityService;

	public String execute() {
		linkExpired = false;
		addActionMessage("Please enter your email address below and we will send you a set password link.");
		return SUCCESS;
	}
	
	public String linkExpired(){
		linkExpired = true;
		addActionMessage("This set password link has expired.  Please enter your email address below and we will send you a new set password link.");
		return SUCCESS;
	}
	
	public String sendLink(){
		if(StringUtils.isEmpty(email)){
			addActionMessage("Please enter your email address below and we will send you a set password link.");
		}
		else{
			securityService.sendLinkEmail(email);
			addActionMessage("Please check your emails.  A new set password link has been sent to you.");
		}
		return SUCCESS;
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

	public boolean isLinkExpired() {
		return linkExpired;
	}

	public void setLinkExpired(boolean linkExpired) {
		this.linkExpired = linkExpired;
	}
	
	
}
