package hibernate;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.UncheckedException;

public class HibernateSessionRequestFilter implements Filter {

	private static Log log = LogFactory
			.getLog(HibernateSessionRequestFilter.class);

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		try {
			HibernateSession.beginTransaction();
			
			//If you want to log statistics after every request
			//HibernateSession.clearStatistics();
			
			// Call the next filter (continue request processing)
			chain.doFilter(request, response);

			// Commit and cleanup
			HibernateSession.commit();
			
			//If you want to log statistics after every request
			//HibernateSession.logStatistics();

		} catch (Throwable ex) {
			
			try {
				HibernateSession.rollback();
			} catch (Throwable rbEx) {
				log.warn("Failed to rollback transcation. Message:" + rbEx.getMessage());
			}
			
			if (ex instanceof RuntimeException)
				throw (RuntimeException) ex;
			else if (ex instanceof IOException)
				throw (IOException) ex;
			else if (ex instanceof ServletException)
				throw (ServletException) ex;
			else {
				// Wrap throwable in UncheckedException
				throw new UncheckedException(ex);
			}
		}
		finally{
			try{
				HibernateSession.close();
			}
			catch(Throwable t){
				log.error("Failed to close session", t);
				//TODO put in mail notifications
				//OutboundService mailService = new OutboundService();
				//mailService.sendErrorEmail(t);
			}
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		HibernateSession.openSessionFactory();
	}

	public void destroy() {
		HibernateSession.closeSessionFactory();
	}

}