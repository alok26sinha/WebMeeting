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

import subsystems.mail.OutboundService;

import common.UncheckedException;

/**
 * The first filter in the chain. It logs the start and end of request
 * processing. It logs request for static (.js .css etc) and dynamic (.action
 * .jsp) content.
 * 
 * It also logs the duration of the processing at the end.
 * 
 * This filter is a window to the load being placed on the server.
 * 
 */
public class LoggingFilter implements Filter {
	private static Log log = LogFactory.getLog(LoggingFilter.class);
	private static long requestNumber = 0;
	public static final String HAVE_LOGGED_EXCEPTION = "Have Logged Exception";
	static OutboundService mailService = new OutboundService();

	static {
		// This switches on some nice logging of XWork calls
		// UtilTimerStack.setActive(true);
	}

	FilterConfig fc;

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		long startTime = System.currentTimeMillis();

		if (isRequestForStaticFile(req))
			processStaticRequest(req, res, chain, startTime);
		else
			processDynamicRequest(req, res, chain, startTime);
	}

	private void processStaticRequest(ServletRequest req, ServletResponse res,
			FilterChain chain, long startTime) throws IOException,
			ServletException {
		// Do nothing pass through
		chain.doFilter(req, res);
	}

	private boolean isRequestForStaticFile(ServletRequest req) {
		// Check if has static in url
		HttpServletRequest request = (HttpServletRequest) req;

		String uri = request.getRequestURI();

		if (uri.contains("/assets/"))
			return true;
		else
			return false;
	}

	protected void processDynamicRequest(ServletRequest req,
			ServletResponse res, FilterChain chain, long startTime)
			throws IOException, ServletException {
		try {

			// Name the thread
			Thread.currentThread().setName(Long.toString(++requestNumber));

			HttpServletResponse response = (HttpServletResponse) res;
			HttpServletRequest request = (HttpServletRequest) req;

			/*
			 * for (Enumeration e = fc.getInitParameterNames();
			 * e.hasMoreElements();) { String headerName = (String)
			 * e.nextElement(); .. fc.getInitParameter(headerName); }
			 */

			String clientIpAddress = getClientIpAddress(request);

			// Get the user agent
			String userAgent = getUserAgent(request);

			log.info("Received request:" + request.getRequestURI()
					+ " IP Address:" + clientIpAddress + " UserAgent:"
					+ userAgent);

			chain.doFilter(req, response);

			// Note this includes the time taken to send the response to the
			// client

			long executionTime = System.currentTimeMillis() - startTime;
			log.info("Request End in:" + executionTime + "ms");

		} catch (Throwable e) {
			HttpServletRequest request = (HttpServletRequest) req;
			logError(request, e);

			// Throw up if not a client abort exception
			if (!isClientAbortException(e)) {
				if (e instanceof RuntimeException)
					// This includes a UncheckedException
					throw (RuntimeException) e;
				if (e instanceof IOException)
					throw (IOException) e;
				else if (e instanceof ServletException)
					throw (ServletException) e;
				else
					throw new UncheckedException(e);
			}
		}
	}

	private void logError(HttpServletRequest request, Throwable e) {
		if (request.getAttribute(HAVE_LOGGED_EXCEPTION) == null) {
			if (!isClientAbortException(e)) {
				// The error will be logged by the container log.error("Failed
				// to
				// complete action", e);
				mailService.sendErrorEmail(e);
				request.setAttribute(HAVE_LOGGED_EXCEPTION, "True");
			} else {
				log.warn("Socket error sending back response. Message:"
						+ e.getMessage() + " Probably a client abort.");
			}
		}

	}

	public static boolean isClientAbortException(Throwable e) {
		if (e instanceof java.net.SocketException)
			return true;
		else {
			if (e.getCause() != null)
				return isClientAbortException(e.getCause());
			else
				return false;
		}
	}

	private String getClientIpAddress(HttpServletRequest request) {
		String remoteAddress = request.getRemoteAddr();
		return remoteAddress;
	}

	private String getUserAgent(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");
		return userAgent;
	}

	public void init(FilterConfig filterConfig) {
		this.fc = filterConfig;
	}

	public void destroy() {
		this.fc = null;
	}
}
