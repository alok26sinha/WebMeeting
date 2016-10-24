package subsystems.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.S3ObjectsChunk;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.springframework.stereotype.Component;

import common.Config;
import common.ConfigException;
import common.StreamConnector;
import common.UncheckedException;

/**
 * Implements the storage API for Amazon S3 storage
 */
@Component
public class S3Storage implements Storage {
	private static Log log = LogFactory.getLog(S3Storage.class);

	private StreamConnector streamConnector = new StreamConnector();

	@Override
	public void store(File file, String bucket, String name) {
		try {
			S3Service s3Service = getS3Service();
			S3Bucket s3Bucket = new S3Bucket(bucket);

			/*
			 * It makes sense to compress the files when writing them to storage
			 * and then uncompressing them on the way back. The problem is to do
			 * this in a way that is compatible with standard tools (s3fox and
			 * 7zip.)
			 * 
			 * Unfortunately the compress side to of GZIP filters works with
			 * output streams It will require some tricky pipe and threading
			 * code to use it.
			 * 
			 * The commented out code below works but is not compatible with the
			 * standard tools
			 * 
			 * We will stick to uncompressed for the time being.
			 */
			// InputStream input = new DeflaterInputStream(new
			// FileInputStream(file));
			InputStream input = new FileInputStream(file);
			S3Object s3Object = new S3Object(name);
			s3Object.setDataInputStream(input);
			s3Object.setContentLength(file.length());
			s3Service.putObject(s3Bucket, s3Object);

			// Now check the whole file has been written
			long storageFileSize = size(name, bucket);
			long originalFileSize = file.length();
			if (storageFileSize != originalFileSize) {
				throw new UncheckedException(
						"Failed to write full file to storage. StorageFileSize:"
								+ storageFileSize + " OrignialFileSize:"
								+ originalFileSize);
			}

			// We could also do some checking against MD5 hash
			// objectDetails.getMd5HashAsBase64();

			log.info("File written to S3. name:" + name + " bucket:" + bucket);

		} catch (S3ServiceException s3Exception) {
			throw new UncheckedException("Failed to store:" + name + " bucket:"
					+ bucket + "  to AWS. Message:" + s3Exception.getMessage()
					+ " ErrorCode:" + s3Exception.getS3ErrorCode()
					+ " S3ErrorMessage:" + s3Exception.getS3ErrorMessage(),
					s3Exception);
		} catch (FileNotFoundException e) {
			throw new UncheckedException("Failed to store:" + name, e);
		} catch (ConfigException e){
			if( Config.getInstance().isProductionEnvironment())
				throw e;
			else
				log.info("Not writing to S3 as configuration not provided. File is kept in local file system so it will retrieved from there.");
		}
		
	}

	@Override
	public void retrieve(String name, String bucket, File file) {
		InputStream input = null;
		OutputStream output = null;
		try {
			S3Service s3Service = getS3Service();
			S3Bucket s3Bucket = new S3Bucket(bucket);
			S3Object s3Object = s3Service.getObject(s3Bucket, name);

			// See comments about compression above
			// InputStream input = new
			// InflaterInputStream(s3Object.getDataInputStream());

			input = s3Object.getDataInputStream();
			output = new FileOutputStream(file);

			// Read from input and write to output
			streamConnector.pipe(input, output);

			log.info("File retrieved S3. name:" + name + " bucket:" + bucket);

		} catch (S3ServiceException s3Exception) {
			throw new UncheckedException("Failed to retrieve:" + name
					+ " bucket:" + bucket + " from AWS. Message:"
					+ s3Exception.getMessage() + " ErrorCode:"
					+ s3Exception.getS3ErrorCode() + " S3ErrorMessage:"
					+ s3Exception.getS3ErrorMessage(), s3Exception);
		} catch (IOException e) {
			throw new UncheckedException("Failed to retrieve:" + name, e);
		}
	}

	@Override
	public void delete(String name, String bucket) {
		try {
			S3Service s3Service = getS3Service();
			S3Bucket s3Bucket = new S3Bucket(bucket);
			s3Service.deleteObject(s3Bucket, name);

			log.info("File deleted on S3. name:" + name + " bucket:" + bucket);
		} catch (S3ServiceException s3Exception) {
			throw new UncheckedException("Failed to delete:" + name
					+ " in bucket:" + bucket + " from AWS. Message:"
					+ s3Exception.getMessage() + " ErrorCode:"
					+ s3Exception.getS3ErrorCode() + " S3ErrorMessage:"
					+ s3Exception.getS3ErrorMessage(), s3Exception);
		}
	}

	@Override
	public long size(String name, String bucket) {
		try {
			S3Service s3Service = getS3Service();
			S3Bucket s3Bucket = new S3Bucket(bucket);
			S3Object objectDetails = s3Service.getObjectDetails(s3Bucket, name);
			return objectDetails.getContentLength();

		} catch (S3ServiceException s3Exception) {
			throw new UncheckedException("Failed to size:" + name
					+ " in bucket:" + bucket + " from AWS. Message:"
					+ s3Exception.getMessage() + " ErrorCode:"
					+ s3Exception.getS3ErrorCode() + " S3ErrorMessage:"
					+ s3Exception.getS3ErrorMessage(), s3Exception);
		}

	}

	/**
	 * Not yet used by the main application. Used by tidy up routines
	 */
	S3ObjectsChunk listObjectsChunked(java.lang.String bucket,
			java.lang.String prefix, java.lang.String delimiter,
			long maxListingLength, java.lang.String priorLastKey) {
		try {
			S3Service s3Service = getS3Service();
			return s3Service.listObjectsChunked(bucket, prefix, delimiter,
					maxListingLength, priorLastKey);

		} catch (S3ServiceException s3Exception) {
			throw new UncheckedException("Failed to list prefix:" + prefix
					+ " delimiter:" + delimiter + " in bucket:" + bucket
					+ " from AWS. Message:" + s3Exception.getMessage()
					+ " ErrorCode:" + s3Exception.getS3ErrorCode()
					+ " S3ErrorMessage:" + s3Exception.getS3ErrorMessage(),
					s3Exception);
		}

	}

	private S3Service getS3Service() throws S3ServiceException {
		String awsAccessKey = Config.getInstance().getValue("s3.access.key");
		String awsSecretKey = Config.getInstance().getValue("s3.secret.key");
		AWSCredentials awsCredentials = new AWSCredentials(awsAccessKey,
				awsSecretKey);
		S3Service s3Service = new RestS3Service(awsCredentials);
		return s3Service;
	}

}
