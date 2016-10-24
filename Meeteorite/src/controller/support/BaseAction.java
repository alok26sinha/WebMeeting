package controller.support;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Company;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import security.SecurityContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import com.opensymphony.xwork2.util.ValueStack;
import common.Config;

public abstract class BaseAction extends ActionSupport implements ServletResponseAware, ServletRequestAware {
	private static Log log = LogFactory.getLog(BaseAction.class);

	// The standard results
	protected static String LIST = "list";
	protected static String EDIT = "edit";
	protected static String VIEW = "view";
	protected static String ITEXT = "itext";
	protected static String EXCEL = "excel";
	protected static String FILE = "file";

	protected SecurityContext securityContext;
	protected HttpServletResponse response;
	protected HttpServletRequest request;


	
	protected String redirect(String url){
		try {
			log.info("Redirecting to :" + url);
			response.sendRedirect(url);
		} catch (IOException e) {
			log.warn("Failed to send redirect " + e.getMessage());
		}
		// Sending to NONE prevents an IllegalStateException caused by some bots
		return NONE;
	}
	
	protected String redirectDashboard(){
		return redirect(Config.getInstance().getValue("app.url") + "/support/Dashboard.action");
	}
	
	protected void sort(List list){
		if( list !=  null){
			Collections.sort(list);
		}
	}
	
	// Session handling. We may choose to persist the session to the database in future versions.
	// For now we will use the crappy servlet session
	protected void putIntoSession(String key, Object value){
		request.getSession().setAttribute(key, value);
	}
	
	protected Object getFromSession(String key){
		return request.getSession().getAttribute(key);
	}
	
	// Utilities
	protected boolean isEmpty(String field) {
		if ((field == null) || (field.trim().equals(""))) {
			return true;
		} else {
			return false;
		}
	}

	// Utilities
	/**
	 * Set the model properties from the request parameters.
	 */
	@SuppressWarnings("unchecked")
	protected void setParameters() {
		ActionContext ac = ActionContext.getContext();
		Map<String, Object> parameters = ac.getParameters();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Setting params");
		}

		if (parameters != null) {
			 Map<String, Object> contextMap = ac.getContextMap();
                try {
                    ReflectionContextState.setCreatingNullObjects(contextMap, true);
                    ReflectionContextState.setDenyMethodExecution(contextMap, true);
                    ReflectionContextState.setReportingConversionErrors(contextMap, true);

                    ValueStack stack = ac.getValueStack();
                    setParameters(this, stack, parameters);
                } finally {
                    ReflectionContextState.setCreatingNullObjects(contextMap, false);
                    ReflectionContextState.setDenyMethodExecution(contextMap, false);
                    ReflectionContextState.setReportingConversionErrors(contextMap, false);
                }
		}
	}

	@SuppressWarnings("unchecked")
	private void setParameters(Object action, ValueStack stack,
			final Map<String, Object> parameters) {

		Map<String, Object> params = new TreeMap<String, Object>(parameters);

		for (Iterator iterator = params.entrySet().iterator(); iterator
				.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String name = entry.getKey().toString();

			boolean acceptableName = acceptableName(name);

			if (acceptableName) {
				Object value = entry.getValue();
				try {
					stack.setValue(name, value);
				} catch (RuntimeException e) {
					log
							.error("ParametersInterceptor - [setParameters]: Unexpected Exception caught setting '"
									+ name
									+ "' on '"
									+ action.getClass()
									+ ": " + e.getMessage());
				}
			}
		}
	}

	private boolean acceptableName(String name) {
		if (name.indexOf('=') != -1 || name.indexOf(',') != -1
				|| name.indexOf('#') != -1 || name.indexOf(':') != -1) {
			return false;
		} else {
			return true;
		}
	}

	//Getters and setters
	public void setSecurityContext(SecurityContext securityContext) {
		this.securityContext = securityContext;
	}

	public SecurityContext getSecurityContext() {
		return securityContext;
	}

	public void setServletResponse(HttpServletResponse req) {
		this.response = req;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}
