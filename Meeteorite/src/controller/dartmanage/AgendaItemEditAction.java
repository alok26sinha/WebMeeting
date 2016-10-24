package controller.dartmanage;

import java.util.ArrayList;
import java.util.List;

import type.MinuteDuration;
import model.AgendaItem;
import model.DartMeeting;
import controller.support.BaseDescriptionEditAction;
import dao.AgendaItemDao;
import dao.DartMeetingDao;

@SuppressWarnings("serial")
public class AgendaItemEditAction extends BaseDescriptionEditAction<DartMeeting, AgendaItem, DartMeetingDao, AgendaItemDao> {
	
	public List<MinuteDuration> durationOptions;
	public MinuteDuration durationInMinutes;
	
	@Override
	public String execute() {
		
		durationOptions = new ArrayList<MinuteDuration>();
		durationOptions.add(MinuteDuration.create(2));
		durationOptions.add(MinuteDuration.create(5));
		durationOptions.add(MinuteDuration.create(10));
		durationOptions.add(MinuteDuration.create(15));
		durationOptions.add(MinuteDuration.create(20));
		durationOptions.add(MinuteDuration.create(25));
		durationOptions.add(MinuteDuration.create(30));
		durationOptions.add(MinuteDuration.create(35));
		durationOptions.add(MinuteDuration.create(40));
		durationOptions.add(MinuteDuration.create(45));
		durationOptions.add(MinuteDuration.create(50));
		durationOptions.add(MinuteDuration.create(55));
		durationOptions.add(MinuteDuration.create(60));
		durationOptions.add(MinuteDuration.create(90));
		durationOptions.add(MinuteDuration.create(120));

		String result = super.execute();
		if (SUCCESS.equals(result)) {
			AgendaItem item = detailDao.load(detailId);
			durationInMinutes = item.durationInMinutes;
		}
		return result;
	}
	
	@Override
	protected void populateDetails () {
		AgendaItem item = detailDao.load(detailId);
		item.description = description;
		item.durationInMinutes = durationInMinutes;
		detailDao.save(item);
	}
	
	// setters
	public void setDartMeetingDao (DartMeetingDao dartMeetingDao) {
		this.meetingDao = dartMeetingDao;
	}
	
	public void setAgendaItemDao (AgendaItemDao agendaItemDao) {
		this.detailDao = agendaItemDao;
	}

	public List<MinuteDuration> getDurationOptions() {
		return durationOptions;
	}

	public void setDurationOptions(List<MinuteDuration> durationOptions) {
		this.durationOptions = durationOptions;
	}

	public MinuteDuration getDurationInMinutes() {
		return durationInMinutes;
	}

	public void setDurationInMinutes(MinuteDuration durationInMinutes) {
		this.durationInMinutes = durationInMinutes;
	}
	
}
