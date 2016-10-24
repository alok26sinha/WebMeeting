package subsystems.storage;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import subsystems.operatingsystem.OperatingSystem;

import common.StreamConnector;

/**
 * The facade class used for the storage service. Buffers files to a temp
 * directory and writes to the store asyncrounously.
 * 
 * It adds the following on to the standard S3 service: -on store it copies the
 * file to the 'files' subdirectory on the classpath and then does the store
 * asyncronously -on retrieve it checks if it exists in the 'file' subdirectory
 * before getting from S3
 */
@Component
public class BufferedStorageService {
	private static Log log = LogFactory.getLog(BufferedStorageService.class);

	private static final String BUCKET = "symmetric";
	private static final String FILE_SUBDIRECTORY = "files";

	private OperatingSystem os = new OperatingSystem();
	private StreamConnector streamConnector = new StreamConnector();
	private Storage storage = new S3Storage();

	public void store(File file, String bucket, String name) {
		log.info("Start storing Bucket:" + bucket + " Name:" + name);
		// Copy to file to a temp file
		String tempFileName = getTempFileName(name);
		File tempFile = os.getWebInfFile(tempFileName);
		streamConnector.pipe(file, tempFile);

		// Set the background writer to copy the file
		BackgroundWriter backgroundWriter = new BackgroundWriter(tempFile,
				bucket, name, storage);
		Thread thread = new Thread(backgroundWriter);
		thread.setDaemon(true);
		thread.setName("BackgroundStorageWriter");
		thread.start();

		// Useful for running junit tests to check store happens
		// try {
		// thread.join();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
	}

	public void retrieve(String name, String bucket, File file) {
		// Does the temp file exits
		String tempFileName = getTempFileName(name);
		File tempFile = os.getWebInfFile(tempFileName);

		if (!tempFile.exists()) {
			log.info("Temp file does not exist. Loading from storage. Bucket:"
					+ bucket + " Name:" + name);
			storage.retrieve(name, bucket, tempFile);
		} else {
			log.info("Found file in temp storage, returning tempFile:"
					+ tempFileName + " Bucket:" + bucket + " Name:" + name);
		}

		streamConnector.pipe(tempFile, file);
	}

	public void delete(String name, String bucket) {
		storage.delete(name, bucket);
	}

	private String getTempFileName(String name) {
		return FILE_SUBDIRECTORY + "/" + name;
	}
}
