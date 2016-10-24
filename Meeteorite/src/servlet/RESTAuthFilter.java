package servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import security.BaseAuthenticationIterceptor;
import security.LocalSecurityContext;
import security.SecurityContext;
import security.SecurityContextFactory;

@Service
public class RESTAuthFilter implements Filter, ApplicationContextAware {
	
	private static final Log LOG = LogFactory.getLog(RESTAuthFilter.class);
	
	@SuppressWarnings("unused")
	private FilterConfig filterConfig;
	
//	@Autowired	// proper processing of @Resource/@Authowired is not working for web-app 2.4
	private CookieManager cookieManager;
//	@Autowired	// proper processing of @Resource/@Authowired is not working for web-app 2.4
	private SecurityContextFactory securityContextFactory;

	private static ApplicationContext applicationContext;

	@Override
	public void destroy() {
		filterConfig = null;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String userToken = getUserToken(servletRequest);
		if (userToken != null) {
			SecurityContext securityContext = getSecurityContextFactory().createContextFromUserToken(userToken);
			LocalSecurityContext.set(securityContext);
			
			BaseAuthenticationIterceptor.addUserNameToThread(securityContext.getUserName());
			
			try {
				filterChain.doFilter(request, response);
			} finally {
				LocalSecurityContext.clear();
			}
		} else {
			((HttpServletResponse)response).sendError(403);
		}
	}

	private String getUserToken(HttpServletRequest servletRequest) {
		String userToken = getCookieManager().getUserTokenFromCookie(servletRequest);
		if (LOG.isDebugEnabled()) {
			LOG.debug("REST: user token [" + userToken + "]");
		}
		return userToken;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		RESTAuthFilter.applicationContext = applicationContext;
	}

	private CookieManager getCookieManager() {
		if (cookieManager == null) {
			cookieManager = (CookieManager) applicationContext.getBean("cookieManager");
		}
		return cookieManager;
	}

	private SecurityContextFactory getSecurityContextFactory() {
		if (securityContextFactory == null) {
			securityContextFactory = (SecurityContextFactory) applicationContext.getBean("securityContextFactory");
		}
		return securityContextFactory;
	}

}
