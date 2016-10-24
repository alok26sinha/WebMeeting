package spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Holds the local application context against the current thread.
 */
public class LocalApplicationContext {
	private static Log log = LogFactory.getLog(LocalApplicationContext.class);
	
	private static ApplicationContext context;
	
	/**
	 * Called by the spring intercepter when running in a web application.
	 */
	public static void setContext(ApplicationContext webContext){
		context = webContext;
	}
	
	public static Object getBean(String name){
		return get().getBean(name);
	}
	
	public static ApplicationContext get(){
		if( context != null){
			return context;
		}
		else{
			//The context has not been set. We must be running in tests.
			//Construct the context and return
			log.info("Building application context");
			context = new ClassPathXmlApplicationContext(
					new String[] { "applicationContext.xml" });
			log.info("Application context ready");
			return context;
		}
	}
	
	public static String[] getBeanDefinitionNames(){
		return get().getBeanDefinitionNames();
	}
	
}
