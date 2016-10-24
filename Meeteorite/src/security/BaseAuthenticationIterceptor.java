package security;

import java.lang.annotation.Annotation;

import spring.LocalApplicationContext;

import com.opensymphony.xwork2.interceptor.Interceptor;

import controller.support.BaseAction;

@SuppressWarnings("serial")
public abstract class BaseAuthenticationIterceptor implements Interceptor {

	public static final String INSUFFICIENT_PRIVILEGES = "insufficientPrivileges";
	protected SecurityContextFactory securityContextFactory;

	public BaseAuthenticationIterceptor() {
		/*
		 * Do our own dependency injection. This is required so we don't have to
		 * bother with changing how interceptors are created
		 */
		securityContextFactory = (SecurityContextFactory) LocalApplicationContext
				.getBean("securityContextFactory");
	}

	public static void addUserNameToThread(String name) {
		String oldName = Thread.currentThread().getName();
		Thread.currentThread().setName(oldName + "-" + name);
	}

	protected boolean accessAllowed(SecurityContext securityContext,
			Object action) {
		if (noAuthenticationRequired(action)) {
			return true;
		} else if (securityContext.isValidUser()
				&& canRunAction(securityContext, action)) {
			return true;
		} else {
			return false;
		}
	}

	boolean canRunAction(SecurityContext securityContext, Object action) {
		if (!(action instanceof BaseAction)) {
			return true;
		} else {
			BaseAction baseAction = (BaseAction) action;
			/*
			 * Menu based security check baseAction.openMenuTo(); Menu menu =
			 * baseAction.getMenu(); TopMenuItem selectedTopMenuItem =
			 * menu.getTopMenu().getSelected(); if
			 * (!securityContext.isAllowedAccess(selectedTopMenuItem)) return
			 * false; MiddleMenuItem selectedMiddleMenuItem =
			 * menu.getMiddleMenu() .getSelected(); if (selectedMiddleMenuItem
			 * != null &&
			 * !securityContext.isAllowedAccess(selectedMiddleMenuItem)) return
			 * false; else return true;
			 */
			return true;
		}
	}

	boolean noAuthenticationRequired(Object action) {
		Class actionClass = action.getClass();
		Annotation[] annotations = actionClass.getAnnotations();
		if (actionClass.isAnnotationPresent(NoAuthenticationRequired.class))
			return true;
		else
			return false;
	}

	protected void setActionSecurityContext(Object calledAction,
			SecurityContext securityContext) {
		if (calledAction instanceof BaseAction) {
			((BaseAction) calledAction).setSecurityContext(securityContext);
		}
	}

}
