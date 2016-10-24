package subsystems.mail;

import java.util.List;

import model.Guest;


public class MailMessage {

	private String to;
	private String from;
	private String subject;
	private String content;
	private String serverLog;
	private boolean sent = false;
	private int failedAttempts = 0;
	Attachment attachment;

	@Override
	public String toString() {
		return super.toString() + " to:" + to + " subject:" + subject
				+ " from:" + from;
	}

	// Getters and setters
	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getServerLog() {
		return serverLog;
	}

	public void setServerLog(String serverLog) {
		this.serverLog = serverLog;
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	public int getFailedAttempts() {
		return failedAttempts;
	}

	public void setFailedAttempts(int failedAttempts) {
		this.failedAttempts = failedAttempts;
	}

	public void setTo(List<Guest> guests) {
		to = null;
		for(Guest guest: guests){
			if( to == null )
				to = guest.person.email;
			else
				to = to + " , " + guest.person.email;
		}
	}

}
