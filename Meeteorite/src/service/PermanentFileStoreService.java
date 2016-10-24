package service;

import java.io.File;

import model.PermanentFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import spring.LocalApplicationContext;
import subsystems.operatingsystem.OperatingSystem;
import subsystems.storage.BufferedStorageService;

import common.Config;
import common.UncheckedException;

@Component
public class PermanentFileStoreService {
	private static Log log = LogFactory.getLog(PermanentFileStoreService.class);

	private static final String BUCKET = "symmetric";
	

	private BufferedStorageService bufferedStorageService;;
	private OperatingSystem os;

	public void store(PermanentFile permanentFile, File file) {
		String storageName = getStoredName(permanentFile);
		if( bufferedStorageService == null){
			log.warn("Spring failed to inject dependencies");
			bufferedStorageService =(BufferedStorageService)LocalApplicationContext.getBean("bufferedStorageService");
		}
		bufferedStorageService.store(file, BUCKET, storageName);
		permanentFile.setLength(file.length());
		log.info("File submitted for storage " + permanentFile);
	}

	public File retrieve(PermanentFile permanentFile) {
		String storageName = getStoredName(permanentFile);

		File file = os.getWebInfFile(storageName);
		bufferedStorageService.retrieve(storageName, BUCKET, file);

		if (file.length() != permanentFile.getLength()) {
			UncheckedException sizeMismatch = new UncheckedException(
					"File size on retrieve does not match what was written. storageName:"
							+ storageName + " retrievedSize:" + file.length()
							+ permanentFile);
			log.error("Size mismatch", sizeMismatch);
		}
		
		log.info("File retrieved storageName:" + storageName
				+ " retrievedSize:" + file.length() + permanentFile);
		return file;
	}

	private String getStoredName(PermanentFile permanentFile) {
		if (permanentFile == null) {
			throw new UncheckedException(
					"Need a not null permanent file to manage storage");
		}
		if (permanentFile.getId() == null) {
			throw new UncheckedException(
					"The permanent file has not yet been saved in the database. We need an id to copy work with permanent storage");
		}

		String applicationName = Config.getInstance().getValue(
				"application.name");
		String environment = Config.getInstance().getEnvironment();
		return applicationName + "/" + environment + "/permanentfiles/"
				+ permanentFile.getId().toString();
	}

	// Getters and setters


	public void setOs(OperatingSystem os) {
		this.os = os;
	}

	public void setStorageService(BufferedStorageService storageService) {
		this.bufferedStorageService = storageService;
	}

}
