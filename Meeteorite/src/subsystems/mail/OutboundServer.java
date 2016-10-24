package subsystems.mail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.MailcapCommandMap;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import model.AgendaItem;
import model.DartMeeting;
import model.Guest;
import model.Meeting;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactoryImpl;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import type.UsefulDateTime;

import common.Config;
import common.UncheckedException;

/**
 * Wraps a SMTP server.
 * 
 * Configuration is done via the Config class
 */
public class OutboundServer {
	private static Log log = LogFactory.getLog(OutboundServer.class);

	private static Session session;

	/**
	 * Send a message. The message is formatted in a simple html pattern
	 * 
	 * If the send fails an UncheckedException is thrown
	 */
	public static void send(MailMessage mailMessage) {
		try {

			Session session = getSession();

			Message msg;

			if (mailMessage.attachment == null)
				msg = generateSimpleMessage(mailMessage, session);
			else
				msg = generateComplexMessage(mailMessage, session);

			msg.setHeader("X-Mailer", "ShiftMailSubsystem");
			msg.setSentDate(new Date());

			// send the thing off
			Transport.send(msg);

		} catch (Throwable t) {
			throw new UncheckedException("Failed to send mail message:"
					+ mailMessage, t);
		}
	}

	private static Message generateComplexMessage(MailMessage mailMessage,
			Session session) throws AddressException, MessagingException,
			IOException {
		Message msg = createMimeMessage(mailMessage, session);

		MimeBodyPart messageBodyPart = new MimeBodyPart();

		String subject = msg.getSubject();
		StringBuffer sb = buildHtmlContent(mailMessage.getContent(), subject);
		messageBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(
				sb.toString(), "text/html")));

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		messageBodyPart = new MimeBodyPart();
		multipart.addBodyPart(messageBodyPart);
		ByteArrayDataSource dataSource = new ByteArrayDataSource(
				mailMessage.attachment.data, mailMessage.attachment.mimeType);
		messageBodyPart.setDataHandler(new DataHandler(dataSource));
		messageBodyPart.setFileName(mailMessage.attachment.name);

		msg.setContent(multipart);

		return msg;
	}

	protected static Message generateSimpleMessage(MailMessage mailMessage,
			Session session) throws AddressException, MessagingException,
			IOException {
		Message msg = createMimeMessage(mailMessage, session);

		/*
		 * Here is where we can extend for cc and bcc
		 * 
		 * if (cc != null) msg.setRecipients(Message.RecipientType.CC,
		 * InternetAddress .parse(cc, false)); if (bcc != null)
		 * msg.setRecipients(Message.RecipientType.BCC, InternetAddress
		 * .parse(bcc, false));
		 */

		addHtmlContent(mailMessage.getContent(), msg);
		return msg;
	}

	protected static Message createMimeMessage(MailMessage mailMessage,
			Session session) throws AddressException, MessagingException {
		Message msg = new MimeMessage(session);

		InternetAddress fromAddress = new InternetAddress(mailMessage.getFrom());
		msg.setFrom(fromAddress);

		InternetAddress[] toAddresses = InternetAddress.parse(
				mailMessage.getTo(), false);
		msg.setRecipients(Message.RecipientType.TO, toAddresses);

		msg.setSubject(mailMessage.getSubject());

		return msg;
	}

	private static Session getSession() {

		if (session == null) {
			// Setup smtp config
			Properties props = new Properties();

			setPropertyValue(props, "mail.smtp.host");
			setPropertyValue(props, "mail.smtp.port");
			setPropertyValue(props, "mail.smtp.auth");
			// props.put("mail.smtp.auth", true);
			setPropertyValue(props, "mail.smtp.starttls.enable");
			// props.put("mail.smtp.starttls.enable", true);
			setPropertyValue(props, "mail.smtp.socketFactory.port");
			setPropertyValue(props, "mail.smtp.socketFactory.class");

			final String username = Config.getInstance().getValue(
					"mail.smtp.user");
			final String password = Config.getInstance().getValue(
					"mail.smtp.password");

			session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username,
									password);
						}
					});
		}

		if (log.isDebugEnabled())
			session.setDebug(true);
		return session;
	}

	private static void setPropertyValue(Properties props, String key) {
		String value = Config.getInstance().getValue(key);
		props.put(key, value);
	}

	private static void addHtmlContent(String content, Message msg)
			throws MessagingException, IOException {
		String subject = msg.getSubject();
		StringBuffer sb = buildHtmlContent(content, subject);

		msg.setDataHandler(new DataHandler(new ByteArrayDataSource(sb
				.toString(), "text/html")));
	}

	private static StringBuffer buildHtmlContent(String content, String subject) {
		StringBuffer sb = new StringBuffer();
		sb.append("<HTML>\n");
		sb.append("<HEAD>\n");
		sb.append("<TITLE>\n");
		sb.append(subject + "\n");
		sb.append("</TITLE>\n");
		sb.append("<style type='text/css'>");
		sb.append("body { color: #111111; font-family: Arial, Sans-Serif; font-size: 12px; }\n");
		sb.append("</style>\n");
		sb.append("</HEAD>\n");

		sb.append("<BODY>\n");
		sb.append("<div style='color:#666666; font-size:20px; font-weight:bold; margin:5px 5px 5px 5px; padding:0px;'>"
				+ subject + "</div>" + "\n");

		sb.append(content);

		sb.append("</BODY>\n");
		sb.append("</HTML>\n");
		return sb;
	}

	static String getStackTrace(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String stackTrace = sw.toString();
		stackTrace.replaceAll("\n", "\n<br/>");
		return stackTrace;
	}

	public static void sendMeetingInvite(MailMessage mailMessage,
			Meeting meeting) {
		try {
			Session session = getSession();

			StringBuffer emailBody = new StringBuffer();
			emailBody.append(meeting.invitation);

			// TODO agenda, maybe not here
			/*
			 * if (agendaItems != null) { if (agendaItems.size() > 0) {
			 * emailBody.append("Agenda:\n"); int row = 1; for (AgendaItem item
			 * : agendaItems) { emailBody.append(row++ + ". " + item.description
			 * + "\n"); } } }
			 */

			MimeMessage mimeMessage = new MimeMessage(session);
			mimeMessage.setSubject(mailMessage.getSubject());
			mimeMessage.setFrom(new InternetAddress(mailMessage.getFrom()));
			InternetAddress[] toAddresses = InternetAddress.parse(
					mailMessage.getTo(), false);
			mimeMessage.setRecipients(Message.RecipientType.TO, toAddresses);
			Multipart multipart = new MimeMultipart();
			MimeBodyPart iCalAttachment = new MimeBodyPart();
			byte[] invite = createICalInvitation(meeting.getId() + "",
					meeting.name, emailBody.toString(), meeting.startDateTime,
					meeting.getEndDateTime(), meeting);
			iCalAttachment.setDataHandler(new DataHandler(
					new ByteArrayDataSource(new ByteArrayInputStream(invite),
							"text/calendar;method=REQUEST;charset=\"UTF-8\"")));
			multipart.addBodyPart(iCalAttachment);
			mimeMessage.setContent(multipart);

			Transport.send(mimeMessage);
		} catch (Throwable t) {
			throw new UncheckedException("Failed to send mail message:"
					+ mailMessage, t);
		}

	}

	private static byte[] createICalInvitation(String _meetingID,
			String _subject, String _content, UsefulDateTime _start,
			UsefulDateTime _end, Meeting meeting) throws IOException,
			ValidationException, URISyntaxException, ParseException {
		CompatibilityHints.setHintEnabled(
				CompatibilityHints.KEY_OUTLOOK_COMPATIBILITY, true);

		VEvent vEvent = new VEvent();
		vEvent.getProperties().add(new Uid(_meetingID));
		vEvent.getProperties().add(new Summary(_subject));
		vEvent.getProperties().add(new Description(_content));

		vEvent.getProperties().add(new DtStart(_start.dateToMime()));
		vEvent.getProperties().add(new DtEnd(_end.dateToMime()));

		ParameterList organiserParameters = new ParameterList();
		organiserParameters.add(ParameterFactoryImpl.getInstance()
				.createParameter(Parameter.CN, "Meeteorite"));
		vEvent.getProperties().add(
				new Organizer(organiserParameters, Config.getInstance()
						.getValue("mail.smtp.user")));

		for (Guest guest : meeting.guests) {
			if (!guest.person.equals(meeting.organiser)) {
				ParameterList attendeeParameters = new ParameterList();
				attendeeParameters.add(ParameterFactoryImpl.getInstance()
						.createParameter(Parameter.CN, guest.person.name));
				attendeeParameters.add(ParameterFactoryImpl.getInstance()
						.createParameter(Parameter.ROLE, "REQ-PARTICIPANT"));
				attendeeParameters.add(ParameterFactoryImpl.getInstance()
						.createParameter(Parameter.RSVP, "TRUE"));
				vEvent.getProperties().add(
						new Attendee(attendeeParameters, guest.person.email));
			}
		}

		net.fortuna.ical4j.model.Calendar cal = new net.fortuna.ical4j.model.Calendar();
		cal.getProperties().add(
				new ProdId("-//Meeteorite Calendar Interface//iCal4j 1.0//EN"));
		cal.getProperties().add(
				net.fortuna.ical4j.model.property.Version.VERSION_2_0);
		cal.getProperties().add(CalScale.GREGORIAN);
		cal.getProperties().add(
				net.fortuna.ical4j.model.property.Method.REQUEST);
		/*
		 * TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance()
		 * .createRegistry(); VTimeZone tz =
		 * registry.getTimeZone(_start.getTimeZoneFormat()) .getVTimeZone();
		 * cal.getComponents().add(tz);
		 */
		cal.getComponents().add(vEvent);

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		CalendarOutputter outputter = new CalendarOutputter();
		outputter.output(cal, bout);
		return bout.toByteArray();
	}

	public static void sendMeetingInvite2(MailMessage mailMessage,
			Meeting meeting) {
		try {
			Session session = getSession();

			// addHtmlContent(mailMessage.getContent(), msg);
			// register the text/calendar mime type
			MimetypesFileTypeMap mimetypes = (MimetypesFileTypeMap) MimetypesFileTypeMap
					.getDefaultFileTypeMap();
			mimetypes.addMimeTypes("text/calendar ics ICS");

			// register the handling of text/calendar mime type
			MailcapCommandMap mailcap = (MailcapCommandMap) MailcapCommandMap
					.getDefaultCommandMap();
			mailcap.addMailcap("text/calendar;; x-java-content-handler=com.sun.mail.handlers.text_plain");

			MimeMessage message = new MimeMessage(session);
			message.setHeader("X-Mailer", "ShiftMailSubsystem");
			message.addHeaderLine("method=REQUEST");
			message.addHeaderLine("charset=UTF-8");
			message.addHeaderLine("component=VEVENT");
			message.setSentDate(new Date());

			message.setFrom(new InternetAddress(mailMessage.getFrom()));
			InternetAddress[] toAddresses = InternetAddress.parse(
					mailMessage.getTo(), false);
			message.setRecipients(Message.RecipientType.TO, toAddresses);

			message.setSubject(mailMessage.getSubject());

			StringBuffer buffer = new StringBuffer("BEGIN:VCALENDAR\n"
					+ "PRODID:-//Meeteorite Calendar System\n"
					+ "VERSION:2.0\n" + "CALSCALE:GREGORIAN\n"
					+ "METHOD:REQUEST\n" + "BEGIN:VEVENT\n");
			for (Guest guest : meeting.guests) {
				if (!guest.person.equals(meeting.organiser)) {
					buffer.append("ATTENDEE;CN=" + guest.person.email
							+ ";ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:"
							+ guest.person.email + "\n");
				}
			}
			buffer.append("ORGANIZER;CN=Meeteorite:MAILTO:"
					+ Config.getInstance().getValue("mail.smtp.user") + "\n"
					+ "DTSTART:" + meeting.startDateTime.dateToMime() + "\n"
					+ "DTEND:" + meeting.getEndDateTime().dateToMime() + "\n"
					+ "LOCATION:" + decodeLocation(meeting) + "\n"
					+ "TRANSP:OPAQUE\n" + "SEQUENCE:0\n" + "UID:"
					+ meeting.getId() + "\n" + "DTSTAMP:"
					+ UsefulDateTime.now().dateToMime() + "\n"
					+ "CATEGORIES:Meeting\n" + "SUMMARY:"
					+ mailMessage.getSubject() + "\n" + "PRIORITY:5\n"
					+ "CLASS:PUBLIC\n" + "BEGIN:VALARM\n" + "TRIGGER:PT15M\n"
					+ "ACTION:DISPLAY\n" + "DESCRIPTION:Reminder\n"
					+ "END:VALARM\n" + "END:VEVENT\n" + "END:VCALENDAR\n");

			// Create the message part
			BodyPart calendarBodyPart = new MimeBodyPart();

			// Fill the message
			calendarBodyPart.setFileName("Invitation.ics");
			calendarBodyPart.setHeader("Content-Class",
					"urn:content-classes:calendarmessage");
			calendarBodyPart.setHeader("Content-ID", "calendar_message");
			calendarBodyPart.setDataHandler(new DataHandler(
					new ByteArrayDataSource(buffer.toString(),
							"text/calendar; charset=UTF-8; method=REQUEST")));// very
			// important

			BodyPart bodyPart = new MimeBodyPart();
			StringBuffer bodyBuffer = buildHtmlContent(
					mailMessage.getContent(), mailMessage.getSubject());
			bodyPart.setContent(bodyBuffer.toString(), "text/html");

			// Create a Multipart
			Multipart multipart = new MimeMultipart();

			// Add part one
			multipart.addBodyPart(bodyPart);
			multipart.addBodyPart(calendarBodyPart);

			// Put parts in message
			message.setContent(multipart);

			// send the thing off
			Transport.send(message);

		} catch (Throwable t) {
			throw new UncheckedException("Failed to send mail message:"
					+ mailMessage, t);
		}
	}

	private static String decodeLocation(Meeting meeting) {
		// Config.getInstance().getValue("meeting.location.undefined")
		return meeting.location == null || "".equals(meeting.location.trim()) ? "<undefined>"
				: meeting.location;
	}

}
