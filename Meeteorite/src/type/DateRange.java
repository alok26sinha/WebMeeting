package type;

import java.util.List;

import type.UsefulDate;

import common.UncheckedException;

public class DateRange {
	private List<UsefulDate> range;
	private boolean weekly;
	
	public DateRange(List<UsefulDate> range, boolean weekly){
		this.weekly = weekly;
		if( range != null && range.size() >1){
			this.range = range;	
		}
		else{
			throw new UncheckedException("Date range requires at lease two dates. Parameter: " + range);
		}
		
	}
	
	public List<UsefulDate> getList(){
		return range;
	}
	
	public UsefulDate getFirstDate(){
		return range.get(0); 
	}
	
	public UsefulDate getLastDate(){
		return range.get(range.size() - 1);
	}

	public int size() {
		return range.size();
	}

	public UsefulDate get(int i) {
		return range.get(i);
	}
	
	public boolean isWeekly(){
		return weekly;
	}
}
