package subsystems.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import subsystems.operatingsystem.ExecutionResults;
import subsystems.operatingsystem.OperatingSystem;

import common.Config;
import common.UncheckedException;

public class PostgresDatabase implements Database {
	private static Log log = LogFactory.getLog(PostgresDatabase.class);

	private String pg_home = Config.getInstance().getValue("postgres.home");
	OperatingSystem os = new OperatingSystem();

	@Override
	public void create(String databaseName) {
		String command = pg_home + "/bin/createdb -U postgres -E UTF8 "
				+ databaseName;
		ExecutionResults results = os.execute(command);

		if (!results.isSuccess()) {
			throw new UncheckedException("Failed to create database:"
					+ databaseName + "\nCommandLine:" + results);
		}
	}

	@Override
	public void drop(String databaseName) {
		String command = pg_home + "/bin/dropdb -U postgres " + databaseName;
		ExecutionResults results = os.execute(command);

		if (!results.isSuccess()) {
			throw new UncheckedException("Failed to drop database:"
					+ databaseName + "\nCommandLine:" + results);
		}
	}

	@Override
	public void backup(String databaseName, File backupFile) {
		String command = pg_home + "/bin/pg_dump -U postgres " + databaseName;
		ExecutionResults results = os.execute(command);

		if (!results.isSuccess()) {
			throw new UncheckedException("Failed to run backup. " + results);
		}

		// Check we are getting the right size
		long minBackupSize = Config.getInstance().getValueLong(
				"database.backup.min.size");
		if (results.getOutputByteArray().length < minBackupSize) {
			throw new UncheckedException("Backup file too small. Size:"
					+ results.getOutputByteArray().length
					+ " Should be larger than" + minBackupSize );
		}

		try {
			OutputStream output = new FileOutputStream(backupFile);
			output.write(results.getOutputByteArray());
			output.close();
		} catch (IOException e) {
			throw new UncheckedException("Failed to backup database:"
					+ databaseName, e);
		}

	}

	@Override
	public void restore(String databaseName, File backupFile) {
		String command = pg_home + "/bin/psql -U postgres -d " + databaseName
				+ " -f " + backupFile.getAbsolutePath();
		ExecutionResults results = os.execute(command);
		
		if( log.isDebugEnabled() )
			log.debug(results);

		if (!results.isSuccess()) {
			throw new UncheckedException("Failed to run backup. " + results);
		}

	}

}
