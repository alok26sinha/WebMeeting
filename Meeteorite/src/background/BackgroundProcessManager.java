package background;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.Config;

public class BackgroundProcessManager implements ServletContextListener {
	private static Log log = LogFactory.getLog(BackgroundProcessManager.class);
	static Config config = Config.getInstance();

	private static DatabaseBackupProcess databaseBackupProcess;
	private static ReminderEmailProcess reminderEmailProcess;
	private static EmailReadBackgroundProcess emailFileBackgroundProcess;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		log.info("#################################################################");
		log.info("#                 Starting "  + config.getValue("application.name") + " " + config.getEnvironment() + "                                  #");
		log.info("#################################################################");

		// Database backup to run hourly. 60 second startup
		databaseBackupProcess = new DatabaseBackupProcess();
		// 1 minute
		databaseBackupProcess.setMillisecondsBeforeStart(60 * 1000);
		
		emailFileBackgroundProcess = new EmailReadBackgroundProcess();
		// 120 second startup
		emailFileBackgroundProcess.setMillisecondsBeforeStart(120 * 1000);


		if (Config.getInstance().isProductionEnvironment()) {
					
			databaseBackupProcess.setMillisecondsBetweenRuns(Config.getInstance().getValueInt("database.backup.minutes.between.runs") * 60 * 1000);
			databaseBackupProcess.start();
			
			emailFileBackgroundProcess.setMillisecondsBetweenRuns(Config.getInstance().getValueInt("mail.read.minutes.between.runs") * 60 * 1000);
			//Disable until get the Outlook format figured out
			emailFileBackgroundProcess.start();

			
		}
		
		// Reminder email process.  
		reminderEmailProcess = new ReminderEmailProcess();
		
		//15 minutes
		reminderEmailProcess.setMillisecondsBeforeStart(2 * 60 * 1000);
		
		//Note this number will be adjusted after each run so that process runs on the hour
		reminderEmailProcess.setMillisecondsBetweenRuns(60 * 60 * 1000);
		
		reminderEmailProcess.start();
				
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if (Config.getInstance().isProductionEnvironment()) {
			databaseBackupProcess.stop();
			emailFileBackgroundProcess.stop();
		}
		
		reminderEmailProcess.stop();

//		log.info("Action execution times:"
//				+ struts.LoggingInterceptor.actionTimes + "\n");
		
//		JobDao jobDao = (JobDao)LocalApplicationContext.getBean("jobDao");
//		jobDao.logStatistics();
	}

	public static DatabaseBackupProcess getDatabaseBackupProcess() {
		return databaseBackupProcess;
	}
	
}
