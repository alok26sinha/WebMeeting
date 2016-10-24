package controller.shiftmeeting;

import type.UsefulDateTime;
import model.ShiftMeeting;
import controller.support.BaseAction;
import dao.ShiftMeetingDao;

@SuppressWarnings("serial")
public class ShiftTimedAction extends BaseAction {
	public Long id;
	public ShiftMeeting meeting;
	public String submitButton;
	
	public int overviewTotalTime;
	public int startingIdeasTotalTime;
	public int hindsightTotalTime;
	public int insightTotalTime;
	public int foresightTotalTime;
	public int tractionTotalTime;
	public int keyOutputsTotalTime;
	
	public int timeRemaining;
	public int overviewTimeRemaining = -1;
	public int startingIdeasTimeRemaining;
	public int hindsightTimeRemaining;
	public int insightTimeRemaining;
	public int foresightTimeRemaining;
	public int tractionTimeRemaining;
	public int keyOutputsTimeRemaining;
	
	public float overViewRatio = 5f / 60f;
	public float startingIdeasRatio = 10f / 60f;
	public float hindsightRatio = 10f / 60f;
	public float insightRatio = 5f / 60f;
	public float foresightRatio = 10f / 60f;
	public float TractionRatio = 15f / 60f;
	public float keyOutputsRatio = 5f / 60f;
	
	public ShiftMeetingDao shiftMeetingDao;
	
	@Override
	public String execute() {
		meeting = shiftMeetingDao.load(id);
		
		int total = meeting.durationInMinutes;
		overviewTotalTime = Math.round(total * overViewRatio);
		startingIdeasTotalTime = Math.round(total * startingIdeasRatio);
		hindsightTotalTime = Math.round(total * hindsightRatio);
		insightTotalTime = Math.round(total * insightRatio);
		foresightTotalTime = Math.round(total * foresightRatio);
		tractionTotalTime = Math.round(total * TractionRatio);
		keyOutputsTotalTime = Math.round(total * keyOutputsRatio);
		
		int meetingDuration = meeting.durationInMinutes;
		int elapsedTime;
		if( meeting.actualStartDateTime != null )
			elapsedTime = meeting.actualStartDateTime.minutesBetween(UsefulDateTime.now());
		else 
			elapsedTime = 0;
		int meetingTimeRemaining = meetingDuration - elapsedTime;
		
		overviewTimeRemaining = meetingTimeRemaining - keyOutputsTotalTime - tractionTotalTime - foresightTotalTime - insightTotalTime - hindsightTotalTime - startingIdeasTotalTime;
		startingIdeasTimeRemaining = meetingTimeRemaining - keyOutputsTotalTime - tractionTotalTime - foresightTotalTime - insightTotalTime - hindsightTotalTime;
		hindsightTimeRemaining = meetingTimeRemaining - keyOutputsTotalTime - tractionTotalTime - foresightTotalTime - insightTotalTime;
		insightTimeRemaining = meetingTimeRemaining - keyOutputsTotalTime - tractionTotalTime - foresightTotalTime;
		foresightTimeRemaining = meetingTimeRemaining - keyOutputsTotalTime - tractionTotalTime;
		tractionTimeRemaining = meetingTimeRemaining - keyOutputsTotalTime;
		keyOutputsTimeRemaining = meetingTimeRemaining;
		
		return SUCCESS;
	}
	
	// Getters and setters
	public final void setShiftMeetingDao(ShiftMeetingDao shiftMeetingDao) {
		this.shiftMeetingDao = shiftMeetingDao;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ShiftMeeting getMeeting() {
		return meeting;
	}

	public void setMeeting(ShiftMeeting meeting) {
		this.meeting = meeting;
	}

	public String getSubmitButton() {
		return submitButton;
	}

	public void setSubmitButton(String submitButton) {
		this.submitButton = submitButton;
	}

	public int getOverviewTotalTime() {
		return overviewTotalTime;
	}

	public void setOverviewTotalTime(int overviewTotalTime) {
		this.overviewTotalTime = overviewTotalTime;
	}

	public int getStartingIdeasTotalTime() {
		return startingIdeasTotalTime;
	}

	public void setStartingIdeasTotalTime(int startingIdeasTotalTime) {
		this.startingIdeasTotalTime = startingIdeasTotalTime;
	}

	public int getHindsightTotalTime() {
		return hindsightTotalTime;
	}

	public void setHindsightTotalTime(int hindsightTotalTime) {
		this.hindsightTotalTime = hindsightTotalTime;
	}

	public int getInsightTotalTime() {
		return insightTotalTime;
	}

	public void setInsightTotalTime(int insightTotalTime) {
		this.insightTotalTime = insightTotalTime;
	}

	public int getForesightTotalTime() {
		return foresightTotalTime;
	}

	public void setForesightTotalTime(int foresightTotalTime) {
		this.foresightTotalTime = foresightTotalTime;
	}

	public int getTractionTotalTime() {
		return tractionTotalTime;
	}

	public void setTractionTotalTime(int tractionTotalTime) {
		this.tractionTotalTime = tractionTotalTime;
	}

	public int getKeyOutputsTotalTime() {
		return keyOutputsTotalTime;
	}

	public void setKeyOutputsTotalTime(int keyOutputsTotalTime) {
		this.keyOutputsTotalTime = keyOutputsTotalTime;
	}

	public int getTimeRemaining() {
		return timeRemaining;
	}

	public void setTimeRemaining(int timeRemaining) {
		this.timeRemaining = timeRemaining;
	}

	public int getOverviewTimeRemaining() {
		return overviewTimeRemaining;
	}

	public void setOverviewTimeRemaining(int overviewTimeRemaining) {
		this.overviewTimeRemaining = overviewTimeRemaining;
	}

	public int getStartingIdeasTimeRemaining() {
		return startingIdeasTimeRemaining;
	}

	public void setStartingIdeasTimeRemaining(int startingIdeasTimeRemaining) {
		this.startingIdeasTimeRemaining = startingIdeasTimeRemaining;
	}

	public int getHindsightTimeRemaining() {
		return hindsightTimeRemaining;
	}

	public void setHindsightTimeRemaining(int hindsightTimeRemaining) {
		this.hindsightTimeRemaining = hindsightTimeRemaining;
	}

	public int getInsightTimeRemaining() {
		return insightTimeRemaining;
	}

	public void setInsightTimeRemaining(int insightTimeRemaining) {
		this.insightTimeRemaining = insightTimeRemaining;
	}

	public int getForesightTimeRemaining() {
		return foresightTimeRemaining;
	}

	public void setForesightTimeRemaining(int foresightTimeRemaining) {
		this.foresightTimeRemaining = foresightTimeRemaining;
	}

	public int getTractionTimeRemaining() {
		return tractionTimeRemaining;
	}

	public void setTractionTimeRemaining(int tractionTimeRemaining) {
		this.tractionTimeRemaining = tractionTimeRemaining;
	}

	public int getKeyOutputsTimeRemaining() {
		return keyOutputsTimeRemaining;
	}

	public void setKeyOutputsTimeRemaining(int keyOutputsTimeRemaining) {
		this.keyOutputsTimeRemaining = keyOutputsTimeRemaining;
	}

	public float getOverViewRatio() {
		return overViewRatio;
	}

	public void setOverViewRatio(float overViewRatio) {
		this.overViewRatio = overViewRatio;
	}

	public float getStartingIdeasRatio() {
		return startingIdeasRatio;
	}

	public void setStartingIdeasRatio(float startingIdeasRatio) {
		this.startingIdeasRatio = startingIdeasRatio;
	}

	public float getHindsightRatio() {
		return hindsightRatio;
	}

	public void setHindsightRatio(float hindsightRatio) {
		this.hindsightRatio = hindsightRatio;
	}

	public float getInsightRatio() {
		return insightRatio;
	}

	public void setInsightRatio(float insightRatio) {
		this.insightRatio = insightRatio;
	}

	public float getForesightRatio() {
		return foresightRatio;
	}

	public void setForesightRatio(float foresightRatio) {
		this.foresightRatio = foresightRatio;
	}

	public float getTractionRatio() {
		return TractionRatio;
	}

	public void setTractionRatio(float tractionRatio) {
		TractionRatio = tractionRatio;
	}

	public float getKeyOutputsRatio() {
		return keyOutputsRatio;
	}

	public void setKeyOutputsRatio(float keyOutputsRatio) {
		this.keyOutputsRatio = keyOutputsRatio;
	}
	
	
	
	
}
