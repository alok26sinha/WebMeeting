package servlet;

import java.io.IOException;
import java.util.Enumeration;

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

/**
 * This filter places adds response headers to tell the browser to cache as much
 * static content as possible.
 * 
 */
public class CacheControlFilter implements Filter {
	private static Log log = LogFactory.getLog(CacheControlFilter.class);

	FilterConfig fc;

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;

		logRequestHeaders(request);

		DebuggingHttpServletResponse response = new DebuggingHttpServletResponse(
				(HttpServletResponse) res);

		log.info("Setting headers in the response to instruct "
				+ "browser to cache content for 8 hours.");

		/*
		 * We need to set these headers before any content start being sent to
		 * the browser so set them before calling chain
		 */
		// 8 Hours
		response.setDateHeader("Expires", System.currentTimeMillis() + 8 * 60
				* 60 * 1000);
		response.addHeader("Cache-Control", "max-age=" + 8 * 60 * 60);

		chain.doFilter(req, response);

		logResponseHeaders(response);

	}

	private void logResponseHeaders(DebuggingHttpServletResponse response) {
		if (log.isDebugEnabled())
			log.debug(response.getHeaders());
	}

	private void logRequestHeaders(HttpServletRequest request) {
		if (log.isDebugEnabled()) {
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
			log.debug("Request headers\n" + requestHeaders.toString());
		}
	}

	public void init(FilterConfig filterConfig) {
		this.fc = filterConfig;
	}

	public void destroy() {
		this.fc = null;
	}

}
