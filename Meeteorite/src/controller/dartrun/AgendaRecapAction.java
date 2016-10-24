package controller.dartrun;

import javax.annotation.Resource;

import model.DartMeeting;
import model.Person;
import model.PrivateNotes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import service.DartTimeMeasurementService;
import service.EventService;
import type.MinuteDuration;
import type.UsefulDateTime;
import controller.support.BaseAction;
import dao.AgendaItemDao;
import dao.DartMeetingDao;
import dao.PrivateNotesDao;

@SuppressWarnings("serial")
public class AgendaRecapAction extends BaseAction {
	private static Log log = LogFactory.getLog(AgendaRecapAction.class);

	public Long id;
	public DartMeeting meeting;
	public MinuteDuration totalDuration;
	public Long itemId;
	public float timeRemaining;
	public String parkedThoughts;
	public String privateNotes;
	public boolean confirmEarlyStart = false;

	private DartMeetingDao dartMeetingDao;
	private DartTimeMeasurementService timeService;
	private AgendaItemDao agendaItemDao;
	private PrivateNotesDao privateNotesDao;
	@Resource
	private EventService eventService;

	@Override
	public String execute() {
		meeting = dartMeetingDao.load(id);

		totalDuration = timeService.getTotalInMinuteDuration(meeting);
		timeRemaining = timeService.getTimeRemainingForOverview(meeting);

		PrivateNotes pn = privateNotesDao.getAgendaReviewNotes(getSecurityContext().getUser(), meeting);
		if(pn!=null){
			privateNotes = pn.agendaReviewNotes;
		}
		return SUCCESS;
	}

	public String start() {
		meeting = dartMeetingDao.load(id);

		if (meeting.actualStartDateTime == null) {
			if (UsefulDateTime.now().isAfter(meeting.startDateTime)) {
				log.info("After the planned meeting start. Starting the timer.");
				startTimer();
			} else {
				log.info("Before the meeting planned start time. Seeking confirmation.");
				confirmEarlyStart = true;
			}
		} else {
			log.info("Meeting clock already running");
		}
		return execute();
	}

	public String confirmEarlyStart() {
		meeting = dartMeetingDao.load(id);
		startTimer();
		return execute();
	}

	protected void startTimer() {
		if (meeting.actualStartDateTime == null) {
			log.info("Starting meeting clock");
			meeting.actualStartDateTime = UsefulDateTime.now();
			dartMeetingDao.flush();

			eventService.logEvent(getSecurityContext().getUser(),
					"Start DART Meeting");
		}
	}

	public String firstAgendaItem() {
		return redirect("AgendaItem.action?id=" + id + "&item=0");
	}

	public String moveUp() {
		meeting = dartMeetingDao.load(id);
		int itemIndex = findMovedItemById();
		if (itemIndex > 0) {
			// clicked element position in range [1..agendaItems.size()[
			switchItems(itemIndex, itemIndex - 1);
		}
		return execute();
	}

	public String moveDown() {
		meeting = dartMeetingDao.load(id);
		int itemIndex = findMovedItemById();
		if (itemIndex >= 0 && itemIndex < meeting.agendaItems.size() - 1) {
			// clicked element position in range [0..agendaItems.size() - 1[
			switchItems(itemIndex + 1, itemIndex);
		}
		return execute();
	}

	private void switchItems(int indexFrom, int indexTo) {
		meeting.agendaItems.get(indexFrom).number--;
		meeting.agendaItems.get(indexTo).number++;
		agendaItemDao.save(meeting.agendaItems.get(indexFrom));
		agendaItemDao.save(meeting.agendaItems.get(indexTo));
		agendaItemDao.flush();
		dartMeetingDao.evict(meeting);
	}

	private int findMovedItemById() {
		for (int i = 0; i < meeting.agendaItems.size(); i++) {
			if (meeting.agendaItems.get(i).getId().longValue() == itemId
					.longValue()) {
				return i;
			}
		}
		return -1;
	}

	public String saveParkedThoughts() {
		meeting = dartMeetingDao.load(id);
		meeting.agendaReviewParkedThoughts = parkedThoughts;
		return NONE;
	}
	
	public String savePrivateNotes() {
		
		meeting = dartMeetingDao.load(id);
		Person person = getSecurityContext().getUser();		
		
		PrivateNotes notes = privateNotesDao.getAgendaReviewNotes(person, meeting);
		if(notes==null){
			notes = new PrivateNotes();
			notes.person = person;
			notes.meeting = meeting;		
		}
		
		notes.agendaReviewNotes = privateNotes;
		privateNotesDao.save(notes);
		return NONE;
	}

	// Getters and setters
	public void setAgendaItemDao(AgendaItemDao agendaItemDao) {
		this.agendaItemDao = agendaItemDao;
	}

	public void setDartMeetingDao(DartMeetingDao dartMeetingDao) {
		this.dartMeetingDao = dartMeetingDao;
	}

	public String continueMeeting() {
		return redirect("AgendaItem.action?id=" + id + "&item=1");
	}

	public void setTimeService(DartTimeMeasurementService timeService) {
		this.timeService = timeService;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DartMeeting getMeeting() {
		return meeting;
	}

	public void setMeeting(DartMeeting meeting) {
		this.meeting = meeting;
	}

	public MinuteDuration getTotalDuration() {
		return totalDuration;
	}

	public void setTotalDuration(MinuteDuration totalDuration) {
		this.totalDuration = totalDuration;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public float getTimeRemaining() {
		return timeRemaining;
	}

	public void setTimeRemaining(float timeRemaining) {
		this.timeRemaining = timeRemaining;
	}

	public String getParkedThoughts() {
		return parkedThoughts;
	}

	public void setParkedThoughts(String parkedThoughts) {
		this.parkedThoughts = parkedThoughts;
	}

	public boolean isConfirmEarlyStart() {
		return confirmEarlyStart;
	}

	public void setConfirmEarlyStart(boolean confirmEarlyStart) {
		this.confirmEarlyStart = confirmEarlyStart;
	}

	public DartTimeMeasurementService getTimeService() {
		return timeService;
	}

	public void setPrivateNotesDao(PrivateNotesDao privateNotesDao) {
		this.privateNotesDao = privateNotesDao;
	}
	
	
}
