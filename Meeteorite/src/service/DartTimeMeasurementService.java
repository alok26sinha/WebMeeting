package service;

import model.AgendaItem;
import model.DartMeeting;

import org.springframework.stereotype.Component;

import type.MinuteDuration;
import type.UsefulDateTime;

@Component
public class DartTimeMeasurementService {

	public static int OVERVIEW_TIME = 1;
	public static int SUMMARY_TIME = 4;

	public float getTimeRemaining(DartMeeting meeting, int itemId) {
		MinuteDuration meetingTimeRemaining = getTimeRemaining(meeting);

		MinuteDuration timeRequiredByFutureTasks = getTimeRequiredByFutureTasks(meeting,
				itemId);

		MinuteDuration timeRemainingForThisTask = meetingTimeRemaining.subtract(timeRequiredByFutureTasks);
		return timeRemainingForThisTask.getMinutesFloat();
	}

	public float getTimeRemainingForOverview(DartMeeting meeting) {
		return getTimeRemaining(meeting, -1);
	}

	private MinuteDuration getTimeRequiredByFutureTasks(DartMeeting meeting, int itemId) {
		MinuteDuration totalTimeRequired = MinuteDuration.create(SUMMARY_TIME); // Allow for summary

		for (int i = itemId + 1; i < meeting.agendaItems.size(); i++) {
			totalTimeRequired = totalTimeRequired.add(meeting.agendaItems.get(i).durationInMinutes);
		}

		return totalTimeRequired;
	}

	public float getTimeRemainingForSummaryClose(DartMeeting meeting) {
		MinuteDuration meetingTimeRemaining = getTimeRemaining(meeting);

		return meetingTimeRemaining.getMinutesFloat();
	}

	private MinuteDuration getTimeRemaining(DartMeeting meeting) {
		MinuteDuration totalDuration = getTotalInMinuteDuration(meeting);
		
		MinuteDuration timeTakenSoFar;
		if(meeting.actualStartDateTime != null){
			timeTakenSoFar = meeting.actualStartDateTime.minuteDurationBetween(UsefulDateTime
					.now());
		}else{
			timeTakenSoFar = MinuteDuration.ZERO;
		}
		
		MinuteDuration timeRemaining = totalDuration.subtract(timeTakenSoFar);
		return timeRemaining;
	}

	public int getTotal(DartMeeting meeting) {
		MinuteDuration totalDuration = getTotalInMinuteDuration(meeting);
		return (int) totalDuration.getMinutes();
	}

	public MinuteDuration getTotalInMinuteDuration(DartMeeting meeting) {
		MinuteDuration totalDuration = MinuteDuration.create(SUMMARY_TIME
				+ OVERVIEW_TIME);
		for (AgendaItem item : meeting.agendaItems) {
			totalDuration = totalDuration.add(item.durationInMinutes);
		}
		return totalDuration;
	}
}