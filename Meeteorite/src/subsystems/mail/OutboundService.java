package subsystems.mail;

import java.util.List;

import model.AgendaItem;
import model.DartMeeting;
import model.Guest;
import model.Meeting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import type.TimeZone;
import type.UsefulDateTime;

import common.Config;
import common.UncheckedException;

@Component
public class OutboundService {
	private static Log log = LogFactory.getLog(OutboundServer.class);

	static Config config = Config.getInstance();

	/**
	 * Attempts to send a mail message out.
	 * 
	 * If it is sent successfully then the mailMessage.sent = true and
	 * mailMessage.serverLog is appended with the date and time sent.
	 * 
	 * If the send fails the the retry count is increased and the error message
	 * is appended to mailMessage.serverLog
	 * 
	 * This method should not throw an exception
	 */
	public void send(MailMessage message) {
		UsefulDateTime now = UsefulDateTime.now(TimeZone.UTC);
		String nowString = now.format("yyyy MMM dd  HH:mm:ss zzz");
		try {
			if(Config.getInstance().isDevelopmentEnvironment())
				message.setTo(Config.getInstance().getValue("send.all.email"));
			
			if(!Config.getInstance().isProductionEnvironment())
				message.setSubject(Config.getInstance().getEnvironment() + " " + message.getSubject());
			
			log.info("Sending email: " + message);
			
			OutboundServer.send(message);

			// No exception thrown so mail sent successfully
			message.setSent(true);
			addServerLog(message, "Email sent successfully " + nowString);
			log.info("Email sent successfully :" + message);
		} catch (Throwable t) {
			addServerLog(message, "Failed to send message " + nowString + "\n"
					+ t.getMessage());
			message.setFailedAttempts(message.getFailedAttempts() + 1);
			log.error("Failed to send email:" + message, t);
		}
	}
	
	public void sendMeetingInvite(MailMessage message, Meeting meeting){
		UsefulDateTime now = UsefulDateTime.now(TimeZone.UTC);
		String nowString = now.format("yyyy MMM dd  HH:mm:ss zzz");
		try {
			if(Config.getInstance().isDevelopmentEnvironment())
				message.setTo(Config.getInstance().getValue("send.all.email"));
			
			if(!Config.getInstance().isProductionEnvironment())
				message.setSubject(Config.getInstance().getEnvironment() + " " + message.getSubject());
			
			log.info("Sending email: " + message);
			
			OutboundServer.sendMeetingInvite(message, meeting);

			// No exception thrown so mail sent successfully
			message.setSent(true);
			addServerLog(message, "Email sent successfully " + nowString);
			log.info("Email sent successfully :" + message);
		} catch (Throwable t) {
			addServerLog(message, "Failed to send message " + nowString + "\n"
					+ t.getMessage());
			message.setFailedAttempts(message.getFailedAttempts() + 1);
			log.error("Failed to send email:" + message, t);
		}
	}

	/**
	 * Send message asynchronously
	 * 
	 * With three retries and 60 seconds apart
	 * @param meeting 
	 * @param meeting 
	 */
	public void sendAsynchronous(MailMessage mailMessage, Meeting meeting) {
		sendAsynchronous(mailMessage, 3, 60 * 1000, meeting);
	}

	/**
	 * Send message asynchronously
	 * 
	 * It will retry the specified number of times and then give up.
	 * @param meeting 
	 */
	public void sendAsynchronous(MailMessage message, int retries,
			int millisecondsBetweenRetries, Meeting meeting) {
		AsynchronousMailSender sender = new AsynchronousMailSender(message,
				retries, millisecondsBetweenRetries, this, meeting);
		Thread senderThread = new Thread(sender);
		// Label the thread for logging
		senderThread.setName("AsynchronousMailSender");
		// VM will exit without waiting for this thread to stop
		senderThread.setDaemon(true);
		senderThread.start();
	}

	/**
	 * Send an exception asynchronously
	 * 
	 * @param e
	 *            the exception to send
	 */
	public void sendErrorEmail(Throwable e) {
		if (config.isProductionEnvironment()) {
			String eventHeading = e.getClass().getCanonicalName() + " "
					+ e.getMessage();

			MailMessage mailMessage = new MailMessage();
			String sendErrorsTo = config.getValue("error.email.recipient");
			mailMessage.setTo(sendErrorsTo);
			String adminUser = config.getValue("mail.smtp.user");
			mailMessage.setFrom(adminUser);
			String environment = config.getEnvironment();
			String application = config.getValue("application.name");
			mailMessage.setSubject("Exception raised in " + application + " "
					+ environment + " " + UsefulDateTime.now().toString() + " "
					+ eventHeading);

			StringBuffer content = new StringBuffer();
			content.append(eventHeading);
			content.append("\n<br/>");
			content.append("<div style='white-space: pre;'>");
			content.append(OutboundServer.getStackTrace(e));
			content.append("</div>");

			mailMessage.setContent(content.toString());
			// Note that sending asynchronously will allow this thread to
			// continue and
			// render the response (the error page in this case)
			sendAsynchronous(mailMessage, null);
		}
	}

	private void addServerLog(MailMessage message, String log) {
		String logSoFar = message.getServerLog();
		if (logSoFar == null)
			message.setServerLog(log);
		else
			message.setServerLog(logSoFar + "\n" + log);

	}

	public void sendCancelMessage(Meeting meeting, Guest guest, String meetingHeader) {
		MailMessage mailMessage = new MailMessage();
		mailMessage.setTo(guest.person.email);
		String adminUser = config.getValue("mail.smtp.user");
		mailMessage.setFrom(adminUser);
		mailMessage.setSubject(meetingHeader
				+ (meeting.name != null ? meeting.name : "") + " canceled");
		mailMessage.setContent("Sorry, but meeting is canceled\n\n<br/><br/>Your's sincerely\n<br/>" + meeting.organiser.name);
		sendAsynchronous(mailMessage, null);
	}

	public void sendReportMessage(Meeting meeting, List<Guest> guests, String meetingHeader, String attachmentName, String attahcmentMimeType, byte[] attachmentData) {
		MailMessage mailMessage = new MailMessage();
		mailMessage.setTo(guests);
		String adminUser = config.getValue("mail.smtp.user");
		mailMessage.setFrom(adminUser);
		mailMessage.setSubject(meetingHeader
				+ (meeting.name != null ? meeting.name : ""));
		mailMessage.setContent("This meeting is now finished.  Thank you for your participation.\n\n<br/><br/>The meeting report can be found at: " 
				+ config.getValue("app.url") 
				+ (meeting instanceof DartMeeting ? "/dartrun" : "/shiftmeeting") 
				+ "/FollowUpReport.action?id=" + meeting.getId() + " <br/<br/>"
				+ "Please check the report to confirm your action items.  Once your action items are complete please mark them as closed. "
				/*+ "\n\n<br/><br/>Your's sincerely\n<br/>" + meeting.organiser.name*/);
		
		if( attachmentData != null){
			Attachment attachment = new Attachment();
			attachment.name = attachmentName;
			attachment.mimeType = attahcmentMimeType;
			attachment.data = attachmentData;
			
			mailMessage.attachment = attachment;
		}
		
		sendAsynchronous(mailMessage, null);
	}
}

class AsynchronousMailSender implements Runnable {
	private static Log log = LogFactory.getLog(AsynchronousMailSender.class);

	private MailMessage message;
	private int retries;
	private int millisecondsBetweenRetries;
	private OutboundService mailService;
	private Meeting meeting;

	public AsynchronousMailSender(MailMessage message, int retries,
			int millisecondsBetweenRetries, OutboundService mailService, Meeting meeting) {
		this.message = message;
		this.retries = retries;
		this.millisecondsBetweenRetries = millisecondsBetweenRetries;
		this.mailService = mailService;
		this.meeting = meeting;
		if (meeting != null) {
			for (Guest guest : meeting.guests) {
				if (guest.person.email == null || "".equals(guest.person.email.trim())) {
					log.debug("guest email is null or empty!");
				}
			}
		}
	}

	@Override
	public void run() {
		message.setSent(false);
		message.setFailedAttempts(0);

		while (!message.isSent() && message.getFailedAttempts() < retries) {

			if (meeting == null) {
				mailService.send(message);
			} else {
				mailService.sendMeetingInvite(message, meeting);
			}

			try {
				Thread.sleep(millisecondsBetweenRetries);
			} catch (InterruptedException e) {
				// Don't really care
			}
		}

		if (!message.isSent()) {
			UncheckedException e = new UncheckedException(
					"Failed to send email." + message);
			log.error(e);
			// mailService.sendErrorEmail(e);
		}

	}

}
