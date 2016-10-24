package subsystems.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.mail.util.QPDecoderStream;
import common.UncheckedException;

/**
 * A message sourced from the imap server
 * 
 * Wraps calls to underlying mail api
 */
public class InboundMessage {
	private static Log log = LogFactory.getLog(InboundMessage.class);
	private Message message;

	private List<Part> textPlainParts = new ArrayList<Part>();
	private List<Part> textHtmlParts = new ArrayList<Part>();
	private List<InboundAttachment> attachments = new ArrayList<InboundAttachment>();

	public void setMessage(Message message) {
		this.message = message;
		setParts();
	}

	public List<String> getFrom() {
		try {
			List<String> froms = new ArrayList<String>();

			Address[] addresses = message.getFrom();
			for (int i = 0; i < addresses.length; i++) {
				Address address = addresses[i];
				String from;
				if (address instanceof InternetAddress) {
					InternetAddress internetAddress = (InternetAddress) address;
					from = internetAddress.getAddress();

				} else {
					from = address.toString();
				}
				froms.add(from);
			}
			return froms;

		} catch (MessagingException e) {
			throw new UncheckedException("Failed to read from", e);
		}
	}

	public String getSubject() {
		try {
			return message.getSubject();
		} catch (MessagingException e) {
			throw new UncheckedException("Failed to read subject  ", e);
		}
	}

	public String getContent() {
		// First try to get text from text/plain
		for (Part textPart : textPlainParts) {
			String content = (String) getContentSafe(textPart);
			if (content != null)
				return content;
		}

		// Then get text from text/html
		for (Part htmlPart : textHtmlParts) {
			String content = (String) getContentSafe(htmlPart);
			if (content != null)
				return content;
		}

		// Did not find any so return empty string
		log.warn("Did not find any content so returning the empty string");
		return "";
	}

	public DataHandler getDataHandler() throws MessagingException {
		return message.getDataHandler();
	}

	private Object getContentSafe(Part part) {
		try {
			return part.getContent();
		} catch (Exception e) {
			log.warn("Failed to get content " + e.getMessage());
			return null;
		}
	}

	private void setParts() {
		setParts(message, 0);
	}

	private void setParts(Part p, int depth) {
		try {
			if (log.isDebugEnabled()) {
				String padding = "";
				for (int i = 0; i < depth; i++) {
					padding += " ";
				}
				log.debug(padding + "processing part mimeType:"
						+ p.getContentType() + " description:"
						+ p.getDescription() + " size:" + p.getSize());
			}

			if (p.isMimeType("text/plain"))
				textPlainParts.add(p);
			else if (p.isMimeType("text/html"))
				textHtmlParts.add(p);
			else if (p.isMimeType("application/*")) {
				InboundAttachment attachment = new InboundAttachment();
				attachment.setPart((MimeBodyPart) p);
				attachments.add(attachment);
			} else if (p.isMimeType("multipart/*")) {
				// Recurse into each part
				Multipart mp = (Multipart) p.getContent();
				for (int i = 0; i < mp.getCount(); i++) {
					Part bp = mp.getBodyPart(i);
					setParts(bp, depth + 1);
				}
			} else if (p.isMimeType("text/calendar")) {
				// added the handling for calendar attachment

				
				if (p instanceof com.sun.mail.imap.IMAPMessage) {
					com.sun.mail.imap.IMAPMessage pm = (com.sun.mail.imap.IMAPMessage) p;
					// theoretically at this point a MimeBodyPart attachment
					// should
					// have been got from pm.getContent
					// but instead SharedByteArrayInputStream is coming which
					// makes
					// the handling complex, needed to convert stream into
					// VCALENDAR attachment
					InputStream bais = (InputStream) pm.getContent();
					extractCalendarPart(bais, p);
				}
				else if(p instanceof com.sun.mail.imap.IMAPBodyPart){
					com.sun.mail.imap.IMAPBodyPart bp = (com.sun.mail.imap.IMAPBodyPart)p;
					InputStream bais = (InputStream) bp.getContent();
					extractCalendarPart(bais, p);
				}
				else {
					log.warn("Did not recognise that type of part:" + p);
				}

				
			} else {
				log.warn("Did not know what to do with mimeType:"
						+ p.getContentType());
			}

		} catch (Exception e) {
			throw new UncheckedException("Failed to find content", e);
		}
	}


	private void extractCalendarPart(InputStream bais, Part p)
			throws IOException, MessagingException {
		Reader r = new InputStreamReader(bais);
		StringWriter sw = new StringWriter();
		char[] buffer = new char[1024];
		for (int n; (n = r.read(buffer)) != -1;)
			sw.write(buffer, 0, n);
		String str = sw.toString();

		BodyPart calendarPart = new MimeBodyPart();
		calendarPart.addHeader("Content-Class",
				"urn:content-classes:calendarmessage");
		calendarPart.setContent(str, "text/calendar;method=REQUEST");
		calendarPart.setDataHandler(p.getDataHandler());
		InboundAttachment attachment = new InboundAttachment();
		attachment.setPart((MimeBodyPart) calendarPart);
		attachments.add(attachment);
	}

	public List<InboundAttachment> getAttachments() {
		return attachments;
	}

	public void delete() {
		try {
			message.setFlag(Flags.Flag.DELETED, true);
		} catch (Exception e) {
			throw new UncheckedException("Failed to delete message", e);
		}
	}
}
