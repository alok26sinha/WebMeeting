package service;

import java.util.List;

import javax.annotation.Resource;

import model.AgendaItem;
import model.DartMeeting;
import model.Meeting;
import model.ShiftMeeting;

import org.springframework.stereotype.Component;

import dao.AgendaItemDao;
import dao.DartItemDao;
import dao.ForesightDao;
import dao.GuestDao;
import dao.HindsightDao;
import dao.InsightDao;
import dao.MeetingDao;
import dao.PermanentFileDao;
import dao.PrivateNotesDao;
import dao.StartingIdeaDao;
import dao.TractionDao;

@Component
public class MeetingService {

	@Resource
	private GuestDao guestDao;
	@Resource
	private AgendaItemDao agendaItemDao;
	@Resource
	private DartItemDao dartItemDao;
	@Resource
	StartingIdeaDao startIdeaDao;
	@Resource
	TractionDao tractionDao;
	@Resource
	HindsightDao hindsightDao;
	@Resource
	InsightDao insightDao;
	@Resource
	ForesightDao foresightDao;
	@Resource
	MeetingDao meetingDao;
	@Resource
	PermanentFileDao permanentFileDao;
	@Resource
	PrivateNotesDao privateNotesDao;

	public void deleteMeeting(Meeting meeting) {

		guestDao.deleteAll(meeting);
		privateNotesDao.deleteAllFor(meeting);

		if (meeting instanceof DartMeeting) {
			DartMeeting dartMeeting = (DartMeeting) meeting;
			List<AgendaItem> agendaItems = agendaItemDao.getAll(dartMeeting);
			for (AgendaItem agendaItem : agendaItems) {
				dartItemDao.deleteAllFor(agendaItem);
				privateNotesDao.deleteAllFor(agendaItem);
			}
			agendaItemDao.deleteAll(meeting);
		} else {
			ShiftMeeting shiftMeeting = (ShiftMeeting) meeting;
			startIdeaDao.deleteAll(shiftMeeting);
			tractionDao.deleteAll(shiftMeeting);
			hindsightDao.deleteAll(shiftMeeting);
			insightDao.deleteAll(shiftMeeting);
			foresightDao.deleteAll(shiftMeeting);
		}

		permanentFileDao.deleteAll(meeting);
		
		meetingDao.delete(meeting);

	}
}
