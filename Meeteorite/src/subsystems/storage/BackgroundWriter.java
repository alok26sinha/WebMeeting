package subsystems.storage;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.Config;

public class BackgroundWriter implements Runnable {
	private static Log log = LogFactory.getLog(BackgroundWriter.class);

	private File file;
	private String bucket;
	private String name;
	private Storage storage;
	
	//private OutboundService mailService = new OutboundService();

	BackgroundWriter(File file, String bucket, String name, Storage storage) {
		this.file = file;
		this.bucket = bucket;
		this.name = name;
		this.storage = storage;
	}

	@Override
	public void run() {
		int retryCount = Config.getInstance()
				.getValueInt("storage.retry.count");
		int retryWaitSeconds = Config.getInstance().getValueInt(
				"storage.retry.wait.seconds");

		boolean sent = false;
		int count = 0;

		while (!sent && count < retryCount) {
			sent = store();

			if (!sent)
				try {
					Thread.sleep(retryWaitSeconds * 1000);
				} catch (InterruptedException e) {
					// We don't really care
				}

			count++;
		}

		// If we did not manage to store the file try again and log the error
		if (!sent)
			try {
				storage.store(file, bucket, name);
				log.info("File sent successfully.  Bucket:" + bucket + " Name:" + name);
			} catch (Throwable t) {
				log.error("Failed to send file:" + file + " name:" + name
						+ " bucket:" + bucket + " " + t.getMessage(), t);
				//mailService.sendErrorEmail(t);
			}
	}

	private boolean store() {
		try {
			storage.store(file, bucket, name);
			log.info("File sent successfully.  Bucket:" + bucket + " Name:" + name);
			return true;
		} catch (Throwable t) {
			log.info("Failed to send file, will retry." + t.getMessage());
			return false;
		}
	}

}
