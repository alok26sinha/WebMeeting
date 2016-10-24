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

import common.Config;

/**
 * In test and production environments redirects all traffic to HTTPS
 * 
 */
public class SslFilter implements Filter {
	private static Log log = LogFactory.getLog(SslFilter.class);

	FilterConfig fc;

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		if (development() || sslEncryption(request))
			chain.doFilter(req, res);
		else
			sendRedirect(response);

	}

	private void sendRedirect(HttpServletResponse response) throws IOException {
		log.info("http request is not secure. Redirecting to https");
		response.sendRedirect(Config.getInstance().getValue("app.url"));
	}

	private boolean sslEncryption(HttpServletRequest request) {
		String Url = request.getRequestURL().toString();
		
		if (Url.startsWith("https"))
			return true;
		else
			return false;
	}

	private boolean development() {
		return Config.getInstance().isDevelopmentEnvironment();
	}

	public void init(FilterConfig filterConfig) {
		this.fc = filterConfig;
	}

	public void destroy() {
		this.fc = null;
	}
}
