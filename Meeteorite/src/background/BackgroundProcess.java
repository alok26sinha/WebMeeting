package background;

import model.Company;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import security.DaemonSecurityContext;
import security.SecurityContext;
import spring.LocalApplicationContext;

import common.UncheckedException;

import dao.CompanyDao;


public abstract class BackgroundProcess implements Runnable {
	private static Log log = LogFactory.getLog(BackgroundProcess.class);
	
	//static OutboundService mailService = new OutboundService();
	private boolean exceptionSent = false;

	protected int millisecondsBetweenRuns;
	private int millisecondsBeforeStart;
	private boolean running;
	private Thread thread;

	public void start() {
		if (millisecondsBetweenRuns == 0) {
			throw new UncheckedException(
					"Cannot start a background process that has no delay between runs");
		} else {
			log.info("Starting Background Process");
			running = true;
			thread = new Thread(this);
			// Label the thread for logging
			thread.setName(this.getClass().getSimpleName());
			// VM will exit without waiting for this thread to stop
			//thread.setDaemon(true);
			thread.start();
		}
	}

	public void stop() {
		log.info("Stopping Background Process");
		running = false;
		if (isAlive())
			wakeUp();
	}

	public boolean isAlive() {
		return thread != null && thread.isAlive();
	}

	public void runNow() {
		if (isAlive())
			wakeUp();
	}
	
	private void wakeUp(){
		thread.interrupt();
	}
	
	/**
	 * The background process register to be called when another transaction has been completed.
	 */
	public void transactionComplete(){
		log.info("Waking background process as transaction has completed");
		runNow();
	}

	@Override
	public final void run() {
		// Wait for application to start
		sleep(millisecondsBeforeStart);

		log.info("Process running");
		while (running) {
			try {
				execute();
			} catch (Throwable t) {
				log.error("Exception running background process", t);
				//We only want to send the first exception raised. Sending all could
				//be a lot of errors.
				if(!exceptionSent){
					//mailService.sendErrorEmail(t);
					exceptionSent = true;
				}
			}

			if (running) {
				sleep(millisecondsBetweenRuns);
			}
		}
		log.info("Process stopped");
	}

	private void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			// Do nothing. We expect this to happen when the thread
			// is stopped
		}
	}

	public abstract void execute() throws Exception;
	
	protected SecurityContext getDaemonContext() {
		CompanyDao companyDao = (CompanyDao) LocalApplicationContext
				.getBean("companyDao");
		Company testCompany = companyDao.load(1L);
		SecurityContext daemonContext = new DaemonSecurityContext(testCompany);
		return daemonContext;
	}

	public void setMillisecondsBetweenRuns(int millisecondsBetweenRuns) {
		this.millisecondsBetweenRuns = millisecondsBetweenRuns;
	}

	public void setMillisecondsBeforeStart(int millisecondsBeforeStart) {
		this.millisecondsBeforeStart = millisecondsBeforeStart;
	}


}
