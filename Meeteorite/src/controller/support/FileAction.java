package controller.support;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import model.Meeting;
import model.PermanentFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import service.PermanentFileStoreService;
import type.UsefulDateTime;
import dao.MeetingDao;
import dao.PermanentFileDao;

public class FileAction extends BaseAction {
	private static Log log = LogFactory.getLog(FileAction.class);

	public Long id;
	public String error;
	public String message;
	public File fileToUpload;
	public String fileToUploadContentType;
	public String fileToUploadFileName;
	public List<PermanentFile> files;
	public PermanentFile permanentFile;
	public File tempFile;

	@Resource
	private PermanentFileStoreService permanentFileStoreService;
	@Resource
	private PermanentFileDao permanentFileDao;
	@Resource
	private MeetingDao meetingDao;

	public String upload() {
		try {

			if (fileToUpload != null) {
				log.info("Loading file name:" + fileToUploadFileName
						+ " contentType:" + fileToUploadContentType + " size:"
						+ fileToUpload.length());

				Meeting meeting = meetingDao.load(id);

				PermanentFile permanentFile = new PermanentFile();
				permanentFile.setDateTimeLoaded(UsefulDateTime.now());
				permanentFile.setSourceContentType(fileToUploadContentType);
				permanentFile.setSourceFileName(fileToUploadFileName);
				permanentFile.meeting = meeting;
				permanentFile = permanentFileDao.save(permanentFile);
				permanentFileStoreService.store(permanentFile, fileToUpload);

				permanentFileDao.flush();

				return list();
			} else {
				error = "Fail to upload file.";
				message = "File may be too large.";
				return SUCCESS;
			}
		} catch (Throwable t) {
			error = "Fail to upload file.";
			message = t.getMessage();
			log.error("Failed to upload file", t);
			return SUCCESS;
		}
	}

	public String list() {
		Meeting meeting = meetingDao.load(id);

		files = permanentFileDao.getAll(meeting);

		return SUCCESS;
	}

	public String download() {

		permanentFile = permanentFileDao.load(id);
		tempFile = permanentFileStoreService.retrieve(permanentFile);

		return FILE;
	}

	public String delete() {
		permanentFile = permanentFileDao.load(id);
		id = permanentFile.meeting.getId();
		permanentFileDao.delete(permanentFile);
		permanentFileDao.flush();

		return list();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public File getFileToUpload() {
		return fileToUpload;
	}

	public void setFileToUpload(File fileToUpload) {
		this.fileToUpload = fileToUpload;
	}

	public String getFileToUploadContentType() {
		return fileToUploadContentType;
	}

	public void setFileToUploadContentType(String fileToUploadContentType) {
		this.fileToUploadContentType = fileToUploadContentType;
	}

	public String getFileToUploadFileName() {
		return fileToUploadFileName;
	}

	public void setFileToUploadFileName(String fileToUploadFileName) {
		this.fileToUploadFileName = fileToUploadFileName;
	}

	public List<PermanentFile> getFiles() {
		return files;
	}

	public void setFiles(List<PermanentFile> files) {
		this.files = files;
	}

	public PermanentFile getPermanentFile() {
		return permanentFile;
	}

	public void setPermanentFile(PermanentFile permanentFile) {
		this.permanentFile = permanentFile;
	}

	public File getTempFile() {
		return tempFile;
	}

	public void setTempFile(File tempFile) {
		this.tempFile = tempFile;
	}
	
	

}
