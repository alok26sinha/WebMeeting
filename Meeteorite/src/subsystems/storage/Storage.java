package subsystems.storage;

import java.io.File;

/**
 * An API around permanent storage 
 */
public interface Storage {
	/**
	 * Copy a local file into permanent storage
	 * @param file a fully qualified path to a local file
	 * @param bucket the remote bucket to use
	 * @param name the name to store the file under
	 */
	public void store(File file, String bucket, String name);
	
	/**
	 * Retrieve a file from permanent storage to a local file
	 * @param bucket the remote bucket where the file is stored
	 * @param name the name where the file is stored
	 * @param file a fully qualified local file where it will be retrieved to
	 */
	public void retrieve(String name, String bucket, File file);
	
	/**
	 * Delete an object from permanent storage
	 * @param name the name of the object
	 * @param bucket the bucket it is located in
	 */
	public void delete(String name, String bucket);
	
	/**
	 * Determine the size of a file on storage 
	 * @param name
	 * @param bucket
	 * @return
	 */
	public long size(String name, String bucket);
}
