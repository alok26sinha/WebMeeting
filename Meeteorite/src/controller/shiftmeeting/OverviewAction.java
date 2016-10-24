package controller.shiftmeeting;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import controller.dartrun.AgendaRecapAction;

import service.EventService;
import type.UsefulDateTime;

@SuppressWarnings("serial")
public class OverviewAction extends ShiftTimedAction {

	private static Log log = LogFactory.getLog(OverviewAction.class);
	
	public String overViewTime;
	public String startingIdeasTime;
	public String hindsightTime;
	public String insightTime;
	public String foresightTime;
	public String TractionTime;
	public String keyOutputsTime;
	public String totalTime;
	
	public boolean confirmEarlyStart = false;
	
	@Resource
	private EventService eventService;

	@Override
	public String execute() {
		super.execute();
		timeRemaining = overviewTimeRemaining;

		int total = meeting.durationInMinutes;

		overViewTime = timeRatio(total, overViewRatio);
		startingIdeasTime = timeRatio(total, startingIdeasRatio);
		hindsightTime = timeRatio(total, hindsightRatio);
		insightTime = timeRatio(total, insightRatio);
		foresightTime = timeRatio(total, foresightRatio);
		TractionTime = timeRatio(total, TractionRatio);
		keyOutputsTime = timeRatio(total, keyOutputsRatio);

		float totalHours = (float) total / 60;
		totalTime = totalHours + " hours";

		return SUCCESS;
	}

	public String startMeeting() {
		
		meeting = shiftMeetingDao.load(id);
		
		if (meeting.actualStartDateTime == null) {
			// meeting starting after planned time
			if (UsefulDateTime.now().isAfter(meeting.startDateTime)) {
				
				log.info("After the planned SHIFT meeting start. Starting the timer.");
				startTimer();
				
			} else { // meeting starting before planned time
				
				log.info("Before the SHIFT meeting planned start time. Seeking confirmation.");
				// this flag is accessed from value stack
				// in Overview.jsp to show popup
				confirmEarlyStart = true;
			}
		} else {
			
			log.info("SHIFT Meeting clock already running");
		}		

		return execute();
	}
	
	public String confirmEarlyStart() {
		
		meeting = shiftMeetingDao.load(id);
		startTimer();
		return execute();
	}

	// start the meeting
	protected void startTimer() {
		
		if (meeting.actualStartDateTime == null) {
			
			log.info("Starting SHIFT meeting clock");
			meeting.actualStartDateTime = UsefulDateTime.now();
			shiftMeetingDao.flush();

			eventService.logEvent(getSecurityContext().getUser(),
					"Start SHIFT Meeting");
		}
	}

	private String timeRatio(int total, float ratio) {
		int minutes = Math.round(total * ratio);
		return splitHoursMinutes(minutes);
	}

	private String splitHoursMinutes(int minutes) {
		int hours = minutes / 60;
		minutes = minutes % 60;
		String time = "";
		if (hours > 0) {
			if (hours > 1) {
				time = hours + " hours ";
			} else {
				time = hours + " hour ";
			}
		}

		return minutes > 0 ? time = time + minutes + " min " : time;
	}

	public String getOverViewTime() {
		return overViewTime;
	}

	public void setOverViewTime(String overViewTime) {
		this.overViewTime = overViewTime;
	}

	public String getStartingIdeasTime() {
		return startingIdeasTime;
	}

	public void setStartingIdeasTime(String startingIdeasTime) {
		this.startingIdeasTime = startingIdeasTime;
	}

	public String getHindsightTime() {
		return hindsightTime;
	}

	public void setHindsightTime(String hindsightTime) {
		this.hindsightTime = hindsightTime;
	}

	public String getInsightTime() {
		return insightTime;
	}

	public void setInsightTime(String insightTime) {
		this.insightTime = insightTime;
	}

	public String getForesightTime() {
		return foresightTime;
	}

	public void setForesightTime(String foresightTime) {
		this.foresightTime = foresightTime;
	}

	public String getTractionTime() {
		return TractionTime;
	}

	public void setTractionTime(String tractionTime) {
		TractionTime = tractionTime;
	}

	public String getKeyOutputsTime() {
		return keyOutputsTime;
	}

	public void setKeyOutputsTime(String keyOutputsTime) {
		this.keyOutputsTime = keyOutputsTime;
	}

	public String getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}

	public boolean isConfirmEarlyStart() {
		return confirmEarlyStart;
	}

	public void setConfirmEarlyStart(boolean confirmEarlyStart) {
		this.confirmEarlyStart = confirmEarlyStart;
	}
	
	

}
