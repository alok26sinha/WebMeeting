package security;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Person;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import servlet.CookieManager;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import common.UncheckedException;

/**
 * Authentication interceptor that is backed by a form based login.
 * 
 * Used for all browser base interaction
 */
public class FormAuthenticationInterceptor extends BaseAuthenticationIterceptor {
	public static final String LAST_UNSUCCESSFUL_URI = "lastUnsuccessfulUri";
	private static final long serialVersionUID = 1119774631539653644L;
	private static Log log = LogFactory
			.getLog(FormAuthenticationInterceptor.class);
	public static final String USER_TOKEN = "usertoken";
	// static OutboundService mailService = new OutboundService();

	public static final int FIVE_YEARS = 60 * 60 * 24 * 365 * 5;

	private CookieManager cookieManager = new CookieManager();

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {

		String result;

		String userToken = getUserToken(invocation);
		SecurityContext securityContext = securityContextFactory
				.createContextFromUserToken(userToken);
		addUserNameToThread(securityContext.getUserName());

		Object action = invocation.getAction();

		if (accessAllowed(securityContext, action)) {
			log.debug("Access allowed. Passing through to normal action.");

			try {
				LocalSecurityContext.set(securityContext);
				setActionSecurityContext(action, securityContext);
				result = invocation.invoke();
			} finally {
				LocalSecurityContext.clear();
			}
		} else {
			if (!securityContext.isValidUser()) {
				log.warn("Not authenticated and attempted to access a secured resource. Forward to login.");
				ActionContext actionContext = invocation.getInvocationContext();

				HttpServletResponse response = (HttpServletResponse) actionContext
						.get(ServletActionContext.HTTP_RESPONSE);

				recordLastRequest(securityContext.getUser(), actionContext);

				response.sendRedirect("../support/Login!displayPage.action");
				
				result = null;
			} else {
				UncheckedException invalidPageAccess = new UncheckedException(
						"Insufficient security privelages for action:"
								+ action.getClass().getCanonicalName()
								+ " person:" + securityContext.getUserName());
				log.error("Could not run action", invalidPageAccess);
				// TODO email send
				// mailService.sendErrorEmail(invalidPageAccess);
				result = INSUFFICIENT_PRIVILEGES;
			}

		}

		return result;
	}

	private void recordLastRequest(Person user, ActionContext actionContext) {
		String requestURI = getRequest(actionContext);
		Map session = actionContext.getSession();
		session.put(LAST_UNSUCCESSFUL_URI, requestURI);
		log.info("Recording unsuccessful uri:" + requestURI);
	}

	private String getRequest(ActionContext actionContext) {
		HttpServletRequest request = (HttpServletRequest) actionContext
				.get(ServletActionContext.HTTP_REQUEST);
		String requestURI = request.getRequestURI();
		String parameters = request.getQueryString();
		if (parameters != null)
			requestURI = requestURI + "?" + parameters;
		return requestURI;
	}

	private String getUserToken(ActionInvocation invocation) {
		ActionContext actionContext = invocation.getInvocationContext();
		HttpServletRequest request = (HttpServletRequest) actionContext
				.get(ServletActionContext.HTTP_REQUEST);

		String userToken = cookieManager.getUserTokenFromCookie(request);
		return userToken;
	}

	@Override
	public void destroy() {
		// No cleanup required
	}

	@Override
	public void init() {
		// No initialisation required
	}

}
