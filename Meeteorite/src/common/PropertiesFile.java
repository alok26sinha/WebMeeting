package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import subsystems.operatingsystem.OperatingSystem;
import type.UsefulDateTime;

/**
 * Wrapper around the java Properties class
 * 
 * It will only look to see if the timestamp has changed every X seconds and
 * will reload if necessary
 */
public class PropertiesFile {
	private static Log log = LogFactory.getLog(PropertiesFile.class);
	private int secondsBetweenChecks;
	private String fileName;
	private Properties properties;
	private long loadedVersion = -1;
	private UsefulDateTime nextFileSystemCheckDue;

	OperatingSystem os = new OperatingSystem();

	public PropertiesFile(String fileName, int secondsBetweenChecks)
			throws ConfigException {
		this.fileName = "classes/" + fileName;
		this.secondsBetweenChecks = secondsBetweenChecks;
	}

	/**
	 * Get this manditory property from the file. Causes the file to be reloaded
	 * if the file timestamp has changed.
	 * 
	 * @throws ConfigException
	 *             if property not found or could not load file.
	 */
	public String getManditory(String key) {
		String value = get(key);

		if ((value == null) || value.equals("")) {
			throw new ConfigException("Could not find the value for "
					+ " mandatory key : " + key);
		}

		return value;
	}

	/**
	 * Return the value of this key from the property file.
	 * 
	 * @param key
	 *            the key to lookup
	 * @return the value found
	 * @throws ConfigException
	 *             if the file could not be loaded
	 */
	public String get(String key) {
		if (isTimeForAFileSystemCheck()) {
			// Only let one thread in to do file work
			synchronized (this) {
				// If this is a thread that was blocked, recheck if a read is
				// necessary
				if (isTimeForAFileSystemCheck()) {
					if (isFileOutOfDate()) {
						load();
					}

					setNextSystemCheckTime();
				}
			}
		}

		return properties.getProperty(key);
	}

	private boolean isTimeForAFileSystemCheck() {
		if (nextFileSystemCheckDue == null) {
			// Not checked yet
			log.debug("File system not read yet");

			return true;
		} else {
			UsefulDateTime now = UsefulDateTime.now();

			if (now.isAfter(nextFileSystemCheckDue)) {
				if (log.isDebugEnabled())
					log.debug("File system check due");

				return true;
			} else {
				log.debug("File system check not due");

				return false;
			}
		}
	}

	private void setNextSystemCheckTime() {
		nextFileSystemCheckDue = UsefulDateTime.now();
		nextFileSystemCheckDue = nextFileSystemCheckDue
				.addSeconds(secondsBetweenChecks);
		if (log.isDebugEnabled())
			log.debug("Setting next check time:" + nextFileSystemCheckDue);

	}

	/**
	 * Does this file need to be reloaded
	 */
	private boolean isFileOutOfDate() throws ConfigException {
		File file = getFile();

		if (file.lastModified() != loadedVersion) {
			if (log.isDebugEnabled())
				log.debug("Out of date");
			return true;
		} else {
			if (log.isDebugEnabled())
				log.debug("File not changed");
			return false;
		}
	}

	private void load() {
		if (log.isDebugEnabled())
			log.debug("Loading properties file : " + fileName);

		File file = getFile();

		try {
			Properties newProperties = new Properties();
			InputStream in = new FileInputStream(file);
			newProperties.load(in);
			properties = newProperties;
			in.close();
		} catch (Exception fileNotFound) {
			if (fileName.endsWith("local.properties")) {
				String msg = "Could not load properties file : "
						+ fileName
						+ "\nCopy the local.properties.default file to local.properties and modify as required.";
				ConfigException exception =  new ConfigException(msg, fileNotFound);
				log.fatal("", exception);
				System.exit(1);
			} else {
				String msg = "Could not load properties file : "
						+ fileName
						+ "\n This file needs to be in the java classpath for the software to run. "
						+ "This file should be copied from the src folder or created by the ant script.";
				throw new ConfigException(msg, fileNotFound);
			}
		}

		loadedVersion = file.lastModified();
		log.debug("Load properties file complete.");
	}

	private File getFile() {
		return os.getWebInfFile(fileName);
	}

	// Some quick test code
	public static void main(String[] args) throws Exception {
		PropertiesFile propertiesFile = new PropertiesFile(
				"environment.properties", 3);

		for (int i = 0; i < 50; i++) {
			System.out
					.println("context : " + propertiesFile.get("environment"));

			try {
				Thread.sleep(1000);
			} catch (Exception exception) {
			}
		}
	}
}
