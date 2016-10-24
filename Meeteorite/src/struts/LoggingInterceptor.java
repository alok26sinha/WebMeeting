package struts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import servlet.LoggingFilter;
import subsystems.mail.OutboundService;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import common.Config;

public class LoggingInterceptor implements Interceptor {
	private static final long serialVersionUID = 1660885717975879304L;
	private final transient Log log = LogFactory
			.getLog(LoggingInterceptor.class);
	static Config config = Config.getInstance();
	
	static OutboundService mailService = new OutboundService();
	public static ActionTimes actionTimes = new ActionTimes();

	// private static XStream xstream = new XStream();

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {

		String actionName = getActionName(invocation);

		ActionContext actionContext = invocation.getInvocationContext();
		HttpServletRequest request = (HttpServletRequest) actionContext
				.get(ServletActionContext.HTTP_REQUEST);

		log.info("Start action:" + actionName + getParameters(request));

		String result;
		/*
		 * We catch any exceptions here and choose not to re-throw them to the
		 * servlet container.
		 * 
		 * This allows us to process with error within the current security
		 * context and transaction This means we can log the error and it will
		 * create an error email in the database
		 */
		long startTime = System.currentTimeMillis();
		try {
			result = invocation.invoke();

		} catch (Exception e) {
			if (!LoggingFilter.isClientAbortException(e)) {
				log.error("Failed to complete action", e);
				mailService.sendErrorEmail(e);
			} else {
				log.warn("Socket error sending back response. Message:"
						+ e.getMessage() + " Probably a client abort.");
			}
			request.setAttribute(LoggingFilter.HAVE_LOGGED_EXCEPTION, "True");
			return Action.ERROR;
		}

		long executionTime = System.currentTimeMillis() - startTime;

		actionTimes.addTime(actionName, executionTime);

		log.info("Action End in:" + executionTime + "ms Result:" + result
		/* + getValueStack(actionContext) */);

		return result;
	}

	private String getActionName(ActionInvocation invocation) {
		StringBuffer message = new StringBuffer();
		String namespace = invocation.getProxy().getNamespace();

		if ((namespace != null) && (namespace.trim().length() > 0)) {
			message.append(namespace).append("/");
		}

		message.append(invocation.getProxy().getActionName());

		message.append("!");
		message.append(invocation.getProxy().getMethod());

		return message.toString();

	}

	/*
	 * private String getValueStack(ActionContext actionContext) { StringBuilder
	 * builder = new StringBuilder(); builder.append("\nValue Stack:");
	 * 
	 * ValueStack valueStack = actionContext.getValueStack(); CompoundRoot
	 * compoundRoot = valueStack.getRoot(); for (Object object : compoundRoot) {
	 * builder.append("\n"); builder.append(xstream.toXML(object)); }
	 * builder.append("\n"); return builder.toString(); }
	 */
	public static String getParameters(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder();
		builder.append("\nRequest Parameters:");

		List<String> keys = new ArrayList<String>();
		Enumeration<?> nameEnumeration = request.getParameterNames();
		while (nameEnumeration.hasMoreElements()) {
			String key = (String) nameEnumeration.nextElement();
			keys.add(key);
		}

		Collections.sort(keys);
		for (String key : keys) {
			builder.append("\n  " + key + ":");
			String[] values = request.getParameterValues(key);
			// Don't print password fields into the log
			if ("password".equals(key) || "password1".equals(key)
					|| "password2".equals(key)) {
				builder.append("***hidden***");
			} else
				for (int i = 0; i < values.length; i++) {
					builder.append("[" + values[i] + "],");
				}
		}
		builder.append("\n");
		return builder.toString();
	}

	@Override
	public void destroy() {
		// No action required. This would be a natural place to log the
		// average execution times but it does not seem to be reliably called.
	}

	@Override
	public void init() {
		// No initialisation required
	}

}

class ActionTimes {
	private Map<String, TimingPoint> actionTimes = new HashMap<String, TimingPoint>();

	public void addTime(String actionName, long millisecondsToExecute) {
		synchronized (actionTimes) {
			TimingPoint p = actionTimes.get(actionName);
			if (p == null) {
				p = new TimingPoint(actionName);
				actionTimes.put(actionName, p);
			}
			p.add(millisecondsToExecute);
		}
	}

	public String toString() {
		StringBuffer output = new StringBuffer();
		synchronized (actionTimes) {
			List<TimingPoint> points = new ArrayList<TimingPoint>();
			points.addAll(actionTimes.values());
			Collections.sort(points);

			for (TimingPoint point : points) {
				output.append(point.toString());
			}
		}
		return output.toString();
	}

}

class TimingPoint implements Comparable<TimingPoint> {
	private String actionName;
	private long millisecondsToExecute = 0;
	private long count = 1;
	private long ignored = 1;
	private long ignoreCount = 3;

	public TimingPoint(String actionName) {
		this.actionName = actionName;
	}

	public void add(long newExecutionTime) {
		if (ignored < ignoreCount)
			ignored++;
		else {
			millisecondsToExecute += newExecutionTime;
			count++;
		}
	}

	public long averageExecution() {
		return millisecondsToExecute / count;
	}

	public String toString() {
		return "\nAverage Execution:" + averageExecution() + "ms Action:"
				+ actionName + " count:" + count;
	}

	@Override
	public int compareTo(TimingPoint other) {
		if (other.averageExecution() > averageExecution())
			return -1;
		else if (other.averageExecution() == averageExecution())
			return 0;
		else
			return 1;
	}
}
