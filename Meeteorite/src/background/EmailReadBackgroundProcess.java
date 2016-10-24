package background;

import hibernate.HibernateSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import service.EmailReadService;
import spring.LocalApplicationContext;

import common.UncheckedException;

public class EmailReadBackgroundProcess extends BackgroundProcess {
	private static Log log = LogFactory.getLog(EmailReadBackgroundProcess.class);

	@Override
	public void execute() {

		
		try {
			HibernateSession.beginTransaction();
			
			EmailReadService service = (EmailReadService) LocalApplicationContext
			.getBean("emailReadService");
			service.processEmails();
			
			HibernateSession.commit();
		} catch (Throwable t) {
			log.error("Failed to run EmailReadService", t);
			HibernateSession.rollback();
			throw new UncheckedException("Failed to read emails", t);
		}
		finally{
			HibernateSession.close();
		}

	}
	
	/**
	 * Used to run the mail process in development
	 * 
	 */
	public static void main(String[] args){

		EmailReadBackgroundProcess mailProcess = new EmailReadBackgroundProcess();
		mailProcess.execute();
	}

}
