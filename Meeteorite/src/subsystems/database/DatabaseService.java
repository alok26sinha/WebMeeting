package subsystems.database;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import subsystems.operatingsystem.OperatingSystem;
import subsystems.storage.BufferedStorageService;

import common.Config;
import common.UncheckedException;

public class DatabaseService {
	private static Log log = LogFactory.getLog(DatabaseService.class);

	// We could also use spring to inject these dependencies
	Database database = new PostgresDatabase();
	BufferedStorageService storage = new BufferedStorageService();
	OperatingSystem os = new OperatingSystem();

	/**
	 * Backup the database to storage
	 * @throws IOException 
	 */
	public void backup(String databaseName, String storageName) throws IOException {
		// Create new file
		File localTempBackupFile = createFile("tempBackupFile.txt");

		// Backup to file
		database.backup(databaseName, localTempBackupFile);


		// Check this to see if this file has changed
		File oldBackupFile = createFile("oldDatabaseBackup.txt");
		
		if( backupHasChanged(localTempBackupFile, oldBackupFile)){
			// Copy file to storage
			storage.store(localTempBackupFile, "symmetricbackup", storageName);
			long size = localTempBackupFile.length();
			log.info("Backup of database:" + databaseName + " complete. Size:"
					+ size + " File:" + storageName);

			// Copy to old
			oldBackupFile.delete();
			localTempBackupFile.renameTo(oldBackupFile);
		}
		else{
			log.debug("No backup made. Database did not change.");
			localTempBackupFile.delete();
		}

	}

	
	private boolean backupHasChanged(File localTempBackupFile,
			File oldBackupFile) throws IOException {
		return !filesIdentical(localTempBackupFile, oldBackupFile);
	}


	boolean filesIdentical(File a, File b) throws IOException {
		if (a.length() != b.length())
			return false;
		else {
			InputStream aInput = new BufferedInputStream(
					new FileInputStream(a), 1024);
			InputStream bInput = new BufferedInputStream(
					new FileInputStream(b), 1024);
			try {
				int aInt;
				int bInt;
				while (-1 != (aInt = aInput.read())) {
					bInt = bInput.read();
					if (aInt != bInt)
						return false;
				}
				return true;
			} finally {
				aInput.close();
				bInput.close();
			}
		}

	}

	File createFile(String fileName) {
		File localTempBackupFile = os.getWebInfFile(fileName);
		try {
			localTempBackupFile.createNewFile();
		} catch (IOException e) {
			throw new UncheckedException("Failed to run backup of database. "
					+ " Failed to create file:" + fileName, e);
		}
		return localTempBackupFile;
	}

	/**
	 * Restore the database from storage overwriting the current database
	 */
	public void restore(String databaseName, String storageName) {

		log.info("Retrieving database backup :" + storageName);

		File localTempRestoreFile = os.getWebInfFile("tempRestoreFile.txt");
		try {
			localTempRestoreFile.createNewFile();
		} catch (IOException e) {
			throw new UncheckedException("Failed to run backup of database:"
					+ databaseName + " to storage:" + storageName, e);
		}

		storage.retrieve(storageName, "symmetricbackup", localTempRestoreFile);

		restore(databaseName, localTempRestoreFile);

		localTempRestoreFile.delete();
		log.info("Restoration complete:" + databaseName);

	}

	public void restore(String databaseName, File localFile) {
		if (!Config.getInstance().isProductionEnvironment()) {
			log.info("Restoring database:" + databaseName + " from:"
					+ localFile.getName());

			// Get rid of database if it exists
			try {
				database.drop(databaseName);
			} catch (Exception e) {
				log.warn("Could not delete :" + databaseName + " message:"
						+ e.getMessage());
			}

			database.create(databaseName);

			database.restore(databaseName, localFile);

			log.info("Database restored:" + databaseName);

		} else {
			log.warn("Cannot restore to production environment");
		}

	}

	public static void main(String[] args) {
		DatabaseService databaseService = new DatabaseService();

		databaseService
				.restore("Development",
						"domU-12-31-36-00-2C-92.z-1.compute-1.internal_pro_080323_1008");
	}
}
