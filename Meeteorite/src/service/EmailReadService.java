package service;

import hibernate.NoRecordsFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import model.Guest;
import model.Meeting;
import model.Person;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import security.ApplicationSecurityContext;
import security.LocalSecurityContext;
import subsystems.mail.InboundAttachment;
import subsystems.mail.InboundMessage;
import subsystems.mail.InboundServer;
import subsystems.mail.OutboundService;
import subsystems.operatingsystem.OperatingSystem;

import common.Config;
import common.StreamConnector;
import common.UncheckedException;

import dao.GuestDao;
import dao.MeetingDao;
import dao.SecurityDao;

@Component
public class EmailReadService {
	private static Log log = LogFactory.getLog(EmailReadService.class);

	private static final String FILE_SUBDIRECTORY = "temp/email";

	@Resource
	private SecurityDao securityDao;
	@Resource
	private MeetingDao meetingDao;
	@Resource
	private OutboundService outboundEmailService;
	@Resource
	private GuestDao guestDao;

	private OperatingSystem os = new OperatingSystem();
	private StreamConnector streamConnector = new StreamConnector();

	private static int fileNumber = 1;

	String user;

	public void processEmails() {
		InboundServer mailServer = new InboundServer();
		try {
			// Connect to mail server
			String host = Config.getInstance()
					.getValue("mail.file.reader.host");
			user = Config.getInstance().getValue("mail.file.reader.username");
			String password = Config.getInstance().getValue(
					"mail.file.reader.password");

			mailServer.connect(host, user, password);

			// Get list of new
			List<InboundMessage> messages = mailServer.getAllMessages();

			// Process each
			for (InboundMessage message : messages) {
				processMessage(message);
				message.delete();
			}

		} finally {
			// Release server
			mailServer.close();
		}
	}

	private void processMessage(InboundMessage message) {
		try {
			String from = message.getFrom().get(0);
			log.info("Processing incomming message from:" + from + " subject:"
					+ message.getSubject());

			// Setting the security contect to person this email is from will
			// naturally enforce the business
			// rules about if this person can load this meeting.
			Person person = securityDao.getPersonForEmail(from);

			// Set the security context for this thread
			ApplicationSecurityContext securityContext = new ApplicationSecurityContext();
			securityContext.setUser(person);
			LocalSecurityContext.set(securityContext);

			// process each attachment
			for (InboundAttachment attachment : message.getAttachments()) {
				File tempFile = getTempFile();
				log.info(tempFile);
				streamConnector.pipe(attachment.getInputStream(), tempFile);
				// streamConnector.pipe(attachment.getInputStream(),
				// System.out);

				String contentType = message.getDataHandler().getContentType();
				contentType = contentType.toLowerCase();

				processAttachment(from, person, tempFile, contentType);
				tempFile.delete();
			}

		} catch (Throwable t) {
			log.error("Failed to process message", t);
			outboundEmailService.sendErrorEmail(t);
		} finally {
			LocalSecurityContext.clear();
		}
	}

	protected void processAttachment(String from, Person person, File tempFile,
			String contentType) throws FileNotFoundException, IOException,
			ParserException {
		// Processing the calendar attachment
		if (contentType.contains("text/calendar")) {
			FileInputStream fin = new FileInputStream(tempFile);

			// using iCal4j framework to build iCalendar object from
			// VCALENDAR file
			CalendarBuilder builder = new CalendarBuilder();
			Calendar calendar = builder.build(fin);

			log.info("Processing vcalendar\n" + calendar.toString());

			Long meetingId = getMeetingId(calendar);

			Meeting meeting = meetingDao.load(meetingId);

			log.info("Loaded " + meeting);

			boolean attending = isAttending(calendar);

			try {
				Guest guest = guestDao.get(meeting, person);

				if (attending) {
					guest.status = Guest.ACCEPT_STATUS;
					log.info("Note guest as attending:" + guest);
				} else {
					guest.status = Guest.DECLINE_STATUS;
					log.info("Note guest as declining:" + guest);
				}
			} catch (NoRecordsFoundException nrf) {
				log.warn("Person is not invited to this meeting. Ignoring response.");
			}

		}
		else{
			log.warn("Content type not text/calendar. Ignoring. Reported type:" + contentType);
		}
	}



	private boolean isAttending(Calendar calendar) {
		try {
			net.fortuna.ical4j.model.Component component = calendar
					.getComponent(net.fortuna.ical4j.model.Component.VEVENT);
			Property property = component.getProperty(Property.ATTENDEE);
			Parameter parameter = property.getParameter("PARTSTAT");
			String value = parameter.getValue();

			log.info("Response :" + value);

			if ("ACCEPTED".equalsIgnoreCase(value))
				return true;
			else
				// either decline or tentative are taken as not attending
				return false;
		} catch (Throwable t) {
			throw new UncheckedException(
					"Failed to determine the accept/decline response", t);
		}
	}

	private Long getMeetingId(Calendar calendar) {
		try {
			net.fortuna.ical4j.model.Component component = calendar
					.getComponent(net.fortuna.ical4j.model.Component.VEVENT);
			Property property = component.getProperty(Property.UID);
			String value = property.getValue();
			return Long.parseLong(value);
		} catch (Throwable t) {
			throw new UncheckedException(
					"Failed to determine the meeting id from the UID in the message",
					t);
		}
	}

	private File getTempFile() {
		String fileName = FILE_SUBDIRECTORY + "/" + fileNumber++;
		File tempFile = os.getWebInfFile(fileName);
		return tempFile;
	}

	public void setSecurityDao(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	public void setMeetingDao(MeetingDao meetingDao) {
		this.meetingDao = meetingDao;
	}

}
