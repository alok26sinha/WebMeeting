package security;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import common.UncheckedException;

/**
 * Authentication interceptor that uses basic authentication. 
 * 
 * Is used by the calendaring interface
 */
@SuppressWarnings("serial")
public class BasicAuthenticationInterceptor extends
		BaseAuthenticationIterceptor {
	private static Log log = LogFactory.getLog(FormAuthenticationInterceptor.class);

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		ActionContext actionContext = invocation.getInvocationContext();
		HttpServletRequest request = (HttpServletRequest) actionContext
				.get(ServletActionContext.HTTP_REQUEST);
		HttpServletResponse response = (HttpServletResponse) actionContext
				.get(ServletActionContext.HTTP_RESPONSE);

		logRequestHeaders(request);

		String result;
		
		UserPassword userPassword = getUserPassword(request);

		SecurityContext securityContext = securityContextFactory.createContext(
				userPassword.username, userPassword.password);
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
				log
						.warn("Not authenticated and attempted to access a secured resource. Sending basic authentication challenge.");
				response.setHeader("WWW-Authenticate", "Basic realm=\"Mo\"");
				response.setStatus(401);
				result = null;
			} else {
				UncheckedException invalidPageAccess = new UncheckedException(
						"Insufficient security privelages for action:"
								+ action.getClass().getCanonicalName()
								+ " person:" + securityContext.getUserName());
				log.error("Could not run action", invalidPageAccess);
				//mailService.sendErrorEmail(invalidPageAccess);
				result = INSUFFICIENT_PRIVILEGES;
			}

		}

		return result;
		
	}

	private UserPassword getUserPassword(HttpServletRequest request)
			throws IOException {
		UserPassword userPassword = new UserPassword();

		String authHeader = request.getHeader("Authorization");

		if (authHeader != null) {
			java.util.StringTokenizer st = new java.util.StringTokenizer(
					authHeader);
			if (st.hasMoreTokens()) {
				String basic = st.nextToken();
				
				if (basic.equalsIgnoreCase("Basic")) {
					String credentials = st.nextToken();

					throw new UncheckedException("Unused code. Remove comments to use");
					/*
					sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
					String userPass = new String(decoder
							.decodeBuffer(credentials));

					int p = userPass.indexOf(":");
					if (p != -1) {
						String userID = userPass.substring(0, p);
						String password = userPass.substring(p + 1);
						userPassword.username = userID;
						userPassword.password = password;
						log.info("User: " + userID );
					}
					*/
				}
			}
		}

		return userPassword;
	}

	private void logRequestHeaders(HttpServletRequest request) {
		// if (log.isDebugEnabled()) {
		StringBuilder requestHeaders = new StringBuilder();
		Enumeration headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			String headerName = (String) headers.nextElement();
			requestHeaders.append("\n  " + headerName);
			Enumeration values = request.getHeaders(headerName);
			// StringBuilder value= new StringBuilder();
			while (values.hasMoreElements()) {
				requestHeaders.append("\n    " + values.nextElement());
			}
		}
		log.info("Request headers\n" + requestHeaders.toString());
		// }
	}

	@Override
	public void destroy() {
		// No Cleanup required
	}

	@Override
	public void init() {
		// No cleanup required
	}

}

class UserPassword {
	String username;
	String password;
}
