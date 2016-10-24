package service;

import hibernate.HibernateSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import model.DartItem;
import model.DartMeeting;
import model.Meeting;
import model.Person;
import model.Traction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import background.ReminderEmailProcess;

import security.ApplicationSecurityContext;
import security.LocalSecurityContext;
import subsystems.mail.MailMessage;
import subsystems.mail.OutboundService;
import type.TimeZone;
import type.UsefulDate;
import type.UsefulDateTime;

import common.Config;
import common.UncheckedException;

import dao.DartItemDao;
import dao.MeetingDao;
import dao.PersonDao;
import dao.TractionDao;

@Component
public class ReminderEmailService {
	private static Log log = LogFactory.getLog(ReminderEmailService.class);
	static OutboundService mailService = new OutboundService();

	@Resource
	private PersonDao personDao;
	@Resource
	private DartItemDao dartItemDao;
	@Resource
	private TractionDao tractionDao;
	@Resource
	private MeetingDao meetingDao;

	public void runReminders(UsefulDateTime utcTime) {

		try {
			HibernateSession.beginTransaction();

			// Setup the context as an adminsitrator
			/*
			 * ApplicationSecurityContext context = new
			 * ApplicationSecurityContext(); Person person = new Person();
			 * person.administrator = true; context.setUser(person);
			 * LocalSecurityContext.set(context);
			 */

			// Get list of all people for who it is 8am or later and have not
			// had
			// reminders run for the day.
			List<Person> runningList = getPeopleWhoNeedReminders(utcTime);

			// Run reminders
			runReminders(runningList, utcTime);
			
			// Run reminders about meetings without follow-up reports
			runPDFReportRemindersIfNecessary(utcTime);

			HibernateSession.commit();

		} catch (Throwable t) {
			HibernateSession.rollback();
			throw new UncheckedException("Failed to run reminders", t);
		}
	}

	private void runPDFReportRemindersIfNecessary(UsefulDateTime utcTime) {
		ApplicationSecurityContext context = new ApplicationSecurityContext();
		context.setUser(new Person() {
			{
				this.administrator = true;
			}
		});
		LocalSecurityContext.set(context);
		try {
			Map<Person, List<Meeting>> lazyOrganizers = meetingDao.findMeetingsWithoutReports ();
			for (Map.Entry<Person, List<Meeting>> entry : lazyOrganizers.entrySet()) {
				Person person = entry.getKey();
				UsefulDateTime localTime = getLocalTime(utcTime, person);
				UsefulDate localDate = localTime.getUsefulDate();
				if (person.lastReportReminderSent != null) {
					
					if (localTime.onOrAfter8am()
							&& localDate.isAfter(person.lastReportReminderSent)) {
						sendReminderEmails(person, entry.getValue());
					}
				}
				person.lastReportReminderSent = localDate;
			}
		} finally {
			LocalSecurityContext.clear();
		}
	}

	private void sendReminderEmails(Person person, List<Meeting> meetings) {
		log.info("Sending reminders to:" + person);
		log.info("Meetings:" + meetings);

		MailMessage mailMessage = new MailMessage();
		mailMessage.setTo(person.email);
		String adminUser = Config.getInstance().getValue("mail.smtp.user");
		mailMessage.setFrom(adminUser);
		mailMessage.setSubject("Send Meeting PDF Reminders");

		StringBuffer content = new StringBuffer();
		content.append("<p>Please note that you have completed the following meetings but still need to send the PDF report.</p>\n" +
				"<p>You can click on the links below, review the content and then send out the PDF report.</p>\n");
		content.append("<table><tr><th>Report</th><th>Date</th></tr>");

		String appUrl = Config.getInstance().getValue("app.url");
		for (Meeting meeting : meetings) {
			content.append("<tr><td><a href='"
					+ appUrl
					+ (meeting instanceof DartMeeting 
							? "/dartrun/SummaryClose.action?id=" 
							: "/shiftmeeting/EndMeeting.action?id=") 
					+ meeting.getId()	+"'>"
					+ meeting.name
					+ "</a></td><td>"
					+ meeting.actualStartDateTime.getDayMonthFormat()
					+ "</td></tr>");
		}

		content.append("</table>");

		mailMessage.setContent(content.toString());

		mailService.sendAsynchronous(mailMessage, null);
	}

	private List<Person> getPeopleWhoNeedReminders(UsefulDateTime utcTime) {
		// Ignore people added today so they do not get the first reminder in
		// the middle of the day
		setPeopleWithNoLastReminderToToday(utcTime);

		return peopleWhereIt8orLaterAndNoReminderSent(utcTime);
	}

	private List<Person> peopleWhereIt8orLaterAndNoReminderSent(
			UsefulDateTime utcTime) {
		List<Person> eightOrLaterAndNoReminder = new ArrayList<Person>();

		for (Person person : personDao.getAll()) {
			UsefulDateTime localTime = getLocalTime(utcTime, person);
			UsefulDate localDate = localTime.getUsefulDate();
			if (localTime.onOrAfter8am()
					&& localDate.isAfter(person.lastReminderSent)
					&& person.reminderPeriodDays != -1 ) {
				log.info("No reminders have been sent for:" + person + " on:"
						+ localDate + " and it is after 8am:" + localTime);
				eightOrLaterAndNoReminder.add(person);
			}
		}

		return eightOrLaterAndNoReminder;
	}

	private void setPeopleWithNoLastReminderToToday(UsefulDateTime utcTime) {
		List<Person> noRemindersSet = personDao.getAllWithNoLastReminder();

		for (Person person : noRemindersSet) {

			UsefulDateTime localTime = getLocalTime(utcTime, person);
			UsefulDate localDateToday = localTime.getUsefulDate();

			if (localTime.onOrAfter8am()) {
				// Mark as last reminder sent for today
				log.info("Person added after 8am local time. Setting last reminder date to today:"
						+ localDateToday);
				person.lastReminderSent = localDateToday;
			} else {
				// Mark as last reminder sent for previous day
				UsefulDate previousDay = localDateToday.addDays(-1);
				log.info("Person added before 8 am local time. Setting last reminder date to yesterday:"
						+ previousDay);
				person.lastReminderSent = previousDay;
			}
		}

		// Flush to write to database
		personDao.flush();
	}

	protected UsefulDateTime getLocalTime(UsefulDateTime utcTime, Person person) {
		// If no zone set assume Sydney
		TimeZone tz;
		if (person.userTimeZone != null)
			tz = TimeZone.forID(person.userTimeZone);
		else
			tz = TimeZone.TZ_SYD;

		// It it is past 8am the set for today
		UsefulDateTime localTime = utcTime.convertToTimeZone(tz);
		return localTime;
	}

	private void runReminders(List<Person> runningList, UsefulDateTime utcTime) {

		for (Person person : runningList) {

			try {

				ApplicationSecurityContext context = new ApplicationSecurityContext();
				context.setUser(person);
				LocalSecurityContext.set(context);

				UsefulDateTime localTime = getLocalTime(utcTime, person);
				UsefulDate startDate = localTime.getUsefulDate();

				int daysIntoFuture = person.reminderPeriodDays;
				UsefulDate endDate = startDate.addDays(daysIntoFuture);

				log.info("Getting reminders for:" + person + ":from "
						+ startDate + " to:" + endDate);
				List<DartItem> dartItems = dartItemDao.getReminders(person,
						startDate, endDate);
				List<Traction> tractions = tractionDao.getReminders(person,
						startDate, endDate);

				if (dartItems.size() > 0 || tractions.size() > 0) {
					// Send person reminders
					sendReminderEmails(person, dartItems, tractions);
				} else {
					log.info("No items found needing reminders");
				}

				// Update fields to sent
				person.lastReminderSent = startDate;
				for (DartItem dartItem : dartItems) {
					dartItem.reminderSent = true;
				}
				for (Traction traction : tractions) {
					traction.reminderSent = true;
				}

			} catch (Throwable t) {
				log.error("Failed to send reminders to:" + person, t);

			}

		}

	}

	protected void sendReminderEmails(Person person, List<DartItem> dartItems,
			List<Traction> tractions) {
		log.info("Sending reminders to:" + person);
		log.info("Dart items:" + dartItems);
		log.info("Shift tractions:" + tractions);

		MailMessage mailMessage = new MailMessage();
		mailMessage.setTo(person.email);
		String adminUser = Config.getInstance().getValue("mail.smtp.user");
		mailMessage.setFrom(adminUser);
		mailMessage.setSubject("Action Item Reminders");

		StringBuffer content = new StringBuffer();
		content.append("<p>Please note the following items are due in the next "
				+ person.reminderPeriodDays * 24 + " hours.</p>\n");
		content.append("<table><tr><th>Action Item</th><th>Due Date</th><th>Meeting Name</th><th>Meeting Date</th></tr>");

		String appUrl = Config.getInstance().getValue("app.url");

		for (DartItem dartItem : dartItems) {
			content.append("<tr><td><a href='"
					+ appUrl
					+ "/support/ActionItems.action#dart" + dartItem.getId()	+"'>"
					+ dartItem.action
					+ "</a></td><td>"
					+ dartItem.timing.getDayMonthFormat()
					+ "</td><td>"
					+ dartItem.agendaItem.dartMeeting.name
					+ "</td><td>"
					+ dartItem.agendaItem.dartMeeting.startDateTime
							.getDayMonthFormat() + "</td></tr>");
		}

		for (Traction traction : tractions) {
			content.append("<tr><td><a href='" + appUrl
					+ "/support/ActionItems.action#shift" + traction.getId()	+"'>" 
					+ traction.description
					+ "</a></td><td>"
					+ (UsefulDate.create(traction.dueDate)).getDayMonthFormat()
					+ "</td></tr>");
		}

		content.append("</table>");

		mailMessage.setContent(content.toString());

		mailService.sendAsynchronous(mailMessage, null);
	}
	
	public static void main(String[] args) throws Exception{
		
		//Test code to run the reminder process in development
		// Reminder email process.  
		ReminderEmailProcess reminderEmailProcess = new ReminderEmailProcess();
		
		//Immediate
		reminderEmailProcess.setMillisecondsBeforeStart(0);
		
		//Will never rerun
		reminderEmailProcess.setMillisecondsBetweenRuns(60 * 60 * 1000);
		
		reminderEmailProcess.start();
		
		Thread.sleep(60 * 1000);
		
		//reminderEmailProcess.stop();
		
		
		
	}

}
