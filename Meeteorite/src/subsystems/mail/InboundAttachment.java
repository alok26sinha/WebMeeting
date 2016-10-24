package subsystems.mail;

import java.io.InputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import common.UncheckedException;

public class InboundAttachment {

	private MimeBodyPart part;

	public void setPart(MimeBodyPart part) {
		this.part = part;
	}

	public String getFileName() {
		try {
			return part.getFileName();
		} catch (MessagingException e) {
			throw new UncheckedException("Failed to read info", e);
		}
	}
	
	public String getContentType() {
		try {
			return part.getContentType();
		} catch (MessagingException e) {
			throw new UncheckedException("Failed to read info", e);
		}
	}

	public InputStream getInputStream() {
		try {
			return part.getInputStream();
		} catch (Exception e) {
			throw new UncheckedException("Failed to read info", e);
		}
	}
	@Override
	public String toString() {
		try {
			return "ImapAttachment fileName:" + getFileName() + " contentType:"
					+ part.getContentType() + " mimeType:"
					+ part.getContentType() + " description:"
					+ part.getDescription() + " size:" + part.getSize();
		} catch (MessagingException e) {
			throw new UncheckedException("Failed to read info", e);
		}
	}
}
