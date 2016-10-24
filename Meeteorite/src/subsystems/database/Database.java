package subsystems.database;

import java.io.File;

public interface Database {

	/**
	 * Create a new database
	 */
	public void create(String databaseName);
	
	/**
	 * Drop a database
	 */
	public void drop(String databaseName);
	
	/**
	 * Backup a database to a file
	 */
	public void backup(String databaseName, File backupFile);
	
	/**
	 * Restore a database from file
	 */
	public void restore(String databaseName, File backupFile);
}
