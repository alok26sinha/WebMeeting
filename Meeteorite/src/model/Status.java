package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Status {
	public static final int OPEN = 0;
//	public static final int IN_PROGRESS = 1;
	public static final int CLOSED = 2;

	
	public static List<String> statusList = Arrays.asList("Open", "Not Existing", "Closed");
	
	public static String getStatusString(int code) {
		return statusList.get(code);
	}
	
	public static List<Status> getStatusList() {
		List<Status> statuses = new ArrayList<Status>();
		for (int code=0; code<4; code++) {
			Status status = new Status();
			status.code = code;
			status.description = statusList.get(code);
			statuses.add(status);
		}
		
		return statuses;
	}
	
	public int code;
	public String description;
}
