package background;

import subsystems.database.DatabaseService;
import subsystems.operatingsystem.OperatingSystem;
import type.UsefulDateTime;

import common.Config;

public class DatabaseBackupProcess extends BackgroundProcess {
	OperatingSystem os = new OperatingSystem();

	@Override
	public void execute() throws Exception {
		DatabaseService databaseService = new DatabaseService();
		
		String databaseName = Config.getInstance().getValue("database.name");
		String backupFilename = backupName(databaseName);

		databaseService.backup(databaseName, backupFilename);
	}

	private String backupName(String database) {
		String environment = Config.getInstance().getEnvironment();
		String host = os.hostName();
		UsefulDateTime now = UsefulDateTime.now();
		String backupFilename =  host + "_" + environment + "_" + database + "_" + now.format("yyMMdd_HHmm");
		return backupFilename;
	}
	
	public static void main(String[] args) throws Exception{
		DatabaseBackupProcess process = new DatabaseBackupProcess();
		process.execute();
		
		//Wait for write to happen
		Thread.sleep(15000);
	}

}
