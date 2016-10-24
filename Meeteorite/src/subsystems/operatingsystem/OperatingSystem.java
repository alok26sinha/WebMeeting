package subsystems.operatingsystem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import common.UncheckedException;

/**
 * Abstracts calls the underlying operating system
 */
@Component
public class OperatingSystem {
	private static Log log = LogFactory.getLog(OperatingSystem.class);

	/**
	 * Execute an operating system command and return execution results
	 * 
	 * @param command
	 * @return
	 */
	public ExecutionResults execute(String command) {

		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(command);

			// Setup stream handling
			StreamGobbler errorGobbler = new StreamGobbler(proc
					.getErrorStream());
			StreamGobbler outputGobbler = new StreamGobbler(proc
					.getInputStream());

			errorGobbler.start();
			outputGobbler.start();

			// Execute and block until complete
			int exitVal = proc.waitFor();

			// Put the execution results into ExecutionResults
			ExecutionResults results = new ExecutionResults();
			results.setResultCode(exitVal);
			results.setError(errorGobbler.toByteArray());
			results.setOutput(outputGobbler.toByteArray());
			return results;

		} catch (Exception e) {
			throw new UncheckedException("Failed to run:" + command, e);
		}
	}

	/**
	 * Find the host name
	 */
	public String hostName() {
		InetAddress localMachine = getInetAddress();
		return localMachine.getHostName();
	}

	/**
	 * Find the local ip address
	 */
	public String ipAddress() {
		InetAddress localMachine = getInetAddress();
		return localMachine.getHostAddress();
	}

	private InetAddress getInetAddress() {
		try {
			InetAddress localMachine = InetAddress.getLocalHost();
			return localMachine;
		} catch (java.net.UnknownHostException e) {
			throw new UncheckedException("Failed to lookup host name", e);
		}
	}

	/**
	 * Get file in the WEB-INF classes directory
	 */
	public File getWebInfFile(String fileName) {
		File webInfClasses = getWebInfClassesDirectory();
		URI uri = webInfClasses.toURI();

		String webInfClassesPath = null;
		webInfClassesPath = uri.getPath();
		String webInfPath = webInfClassesPath.substring(0, webInfClassesPath
				.length() - 9);
		
		
		File file = getFile(webInfPath + "/" + fileName);
		
		createParentDirectories(file);

		return file;
	}
	
	private void createParentDirectories(File file){
		File dir = file.getParentFile();
		if(!dir.exists()){
			boolean success = dir.mkdir();
			if (!success) {
				log.info("Creating parent directory:" + dir);
				createParentDirectories(dir);
				//Try again
				success = dir.mkdir();
				if (!success) {
					throw new UncheckedException("Failed to create directory : " + dir.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Get a File pointing to the WEB-INF classes directory.
	 * 
	 * If we are not running in a servlet context then this points to the
	 * classes directory
	 */
	public File getWebInfClassesDirectory() {

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (classLoader == null) {
			throw new UncheckedException("Failed to load context class loader");
		}

		String thisClassName = OperatingSystem.class.getName();
		String thisClassFileName = thisClassName.replace(".", "/") + ".class";

		URL thisResource = classLoader.getResource(thisClassFileName);
		if (thisResource == null) {
			thisResource = ClassLoader.getSystemResource(thisClassFileName);
		}

		if (thisResource == null) {
			throw new UncheckedException(
					"Could not find the web-inf classes directory. Classloader failed to load a know class.");
		}

		String webInfClassesPath = thisResource.getPath();
		webInfClassesPath = webInfClassesPath.substring(0, webInfClassesPath
				.length()
				- thisClassFileName.length());

		if (log.isDebugEnabled())
			log.debug("This classes path:" + webInfClassesPath);

		File directory = getFile(webInfClassesPath);
		return directory;

	}

	/**
	 * Load a file and dealing with spaces in the path correctly
	 */
	public static File getFile(String path) {
		// Need to deal with the spaces in the path
		// See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4466485
		URI uri;
		try {
			uri = new URI(path);
		} catch (URISyntaxException ex) {
			throw new UncheckedException("Failed to get URI for path:" + path,
					ex);
		}

		File directory = new File(uri.getPath());
		return directory;
	}

	/**
	 * Get all the class files in a sub directory of classes
	 * 
	 * @param subdirectory
	 * @return list of all the class file names
	 */
	public ArrayList<String> getClassFilesInSubdirectory(String subdirectory) {
		if (log.isDebugEnabled())
			log.debug("Finding all class files in:" + subdirectory);

		ArrayList<String> classFiles = new ArrayList<String>();
		ArrayList<String> files = getClassFiles();
		for (String file : files) {
			if (file.startsWith(subdirectory)) {
				classFiles.add(file);
			}
		}

		if (log.isDebugEnabled())
			log.debug("Found " + classFiles.size() + " files");
		return classFiles;
	}

	/**
	 * Get all the class files in the classes directory
	 * 
	 * @return the list of file names
	 */
	public ArrayList<String> getClassFiles() {
		ArrayList<String> classFiles = new ArrayList<String>();
		ArrayList<String> files = getFiles();
		for (String file : files) {
			if (file.endsWith(".class")) {
				classFiles.add(file);
			}
		}
		return classFiles;
	}

	private static ArrayList<String> files;

	/**
	 * Get a list of all files in the resources folder. Note the directory is
	 * only scanned once
	 */
	public ArrayList<String> getFiles() {
		if (files == null) {
			files = readFilesFromClassPath();
		}
		return files;
	}

	private ArrayList<String> readFilesFromClassPath() {
		ArrayList<String> files = new ArrayList<String>();

		File webInfClassesDirectory = getWebInfClassesDirectory();
		if (webInfClassesDirectory.exists()) {
			addClassesFrom(webInfClassesDirectory, ".", files);
		} else {
			throw new UncheckedException("Failed to find classes directory.");
		}
		return files;
	}

	private void addClassesFrom(File directory, String parentDirectory,
			ArrayList<String> files) {
		File[] directoryList = directory.listFiles();
		for (int i = 0; i < directoryList.length; i++) {

			String fileName;
			if (".".equals(parentDirectory)) {
				fileName = directoryList[i].getName();
			} else {
				fileName = parentDirectory + "/" + directoryList[i].getName();
			}
			// log.debug("Found:" + fileName);

			if (directoryList[i].isFile()) {
				files.add(fileName);
			} else {
				String newParentDirectory = fileName;
				addClassesFrom(directoryList[i], newParentDirectory, files);
			}
		}
	}

}

/**
 * Captures the results of making operating system calls
 */
class StreamGobbler extends Thread {
	private static Log log = LogFactory.getLog(StreamGobbler.class);

	private InputStream input;
	private ByteArrayOutputStream output = new ByteArrayOutputStream();

	StreamGobbler(InputStream is) {
		this.input = is;
	}

	public void run() {

		try {
			int readInt = 0;
			while ((readInt = input.read()) != -1) {
				output.write(readInt);
			}

		} catch (IOException ioe) {
			log.error("Failed to process output from operating system call",
					ioe);
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					log.warn("Failed to close reader");
				}
		}
	}

	public byte[] toByteArray() {
		waitForReadToComplete();
		return output.toByteArray();
	}

	public String toString() {
		waitForReadToComplete();
		return output.toString();
	}

	private void waitForReadToComplete() {
		try {
			this.join();
		} catch (InterruptedException e) {
			log.warn("Got an unexpected interruption");
		}
	}

}