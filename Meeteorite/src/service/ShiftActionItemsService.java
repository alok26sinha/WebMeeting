package service;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import model.Meeting;
import model.Person;
import model.ShiftMeeting;
import model.Traction;

import org.springframework.stereotype.Component;

import dao.TractionDao;

@Component
public class ShiftActionItemsService {

	TractionDao tractionDao;

	public SortedSet<ShiftMeeting> getMeetingsWithOpenTractionItems(Person assignedTo) {
		List<Traction> tractionsAssignedToMe = tractionDao
				.getAllOpenAndInProgress(assignedTo);

		SortedSet<ShiftMeeting> meetingsWithOpenTractions = new TreeSet<ShiftMeeting>(new MeetingComparitor());

		for (Traction traction : tractionsAssignedToMe) {
			meetingsWithOpenTractions.add(traction.shiftMeeting);
		}

		return meetingsWithOpenTractions;
	}


	public void setTractionDao(TractionDao tractionDao) {
		this.tractionDao = tractionDao;
	}
}

class MeetingComparitor implements Comparator<Meeting>{

	@Override
	public int compare(Meeting o1, Meeting o2) {
		if( o1 == null && o2 == null)
			return 0;
		else if ( o1 == null && o2 != null )
			return -1;
		else if ( o1 != null && o2 == null)
			return 1;
		else
			return o1.getId().compareTo(o2.getId());
	}
	
}
