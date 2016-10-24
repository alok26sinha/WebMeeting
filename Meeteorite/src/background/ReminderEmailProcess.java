package background;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import service.ReminderEmailService;
import spring.LocalApplicationContext;
import type.TimeZone;
import type.UsefulDateTime;

public class ReminderEmailProcess extends BackgroundProcess {
	private static Log log = LogFactory.getLog(ReminderEmailProcess.class);

	@Override
	public void execute() throws Exception {
		log.info("Running Reminder Email");
		UsefulDateTime utcTime = UsefulDateTime.now(TimeZone.UTC);
		ReminderEmailService service = (ReminderEmailService)LocalApplicationContext.getBean("reminderEmailService");
		service.runReminders(utcTime);
		
		adjustWaitTimeSoNextRunHappensOnTheHour();
	}

	private void adjustWaitTimeSoNextRunHappensOnTheHour() {
		int secondsUntilNextHour = UsefulDateTime.now(TimeZone.UTC).secondsToNextHour();
		log.info("Sleeping for:" + secondsUntilNextHour);
		millisecondsBetweenRuns = secondsUntilNextHour * 1000;
	}

}
