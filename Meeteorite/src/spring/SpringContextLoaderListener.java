package spring;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

/**
 * A modified version of the listener that also set the local application context
 */
public class SpringContextLoaderListener extends ContextLoaderListener{

	public void contextInitialized(ServletContextEvent event) {
		//Call the standard initialise
		super.contextInitialized(event);
		
		//Get the spring context from the servlet context
		ServletContext servletContext = event.getServletContext();
		ApplicationContext context = (ApplicationContext)servletContext.getAttribute(
				WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		LocalApplicationContext.setContext(context);
	}
}
