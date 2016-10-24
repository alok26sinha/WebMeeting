package controller.dartmanage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import model.Company;
import model.DartMeeting;
import model.Guest;
import service.EventService;
import service.GuestService;
import service.SubscriptionService;
import type.MinuteDuration;
import type.UsefulDateTime;
import controller.support.BaseAction;
import dao.CompanyDao;
import dao.DartMeetingDao;
import dao.GuestDao;
import dao.SubscriptionDao;

@SuppressWarnings("serial")
public class MeetingDetailsAction extends BaseAction {

	// UsefulDateTime provides the following formats as "member" functions only
	private static final String HH_MM = "HH:mm";
	public static final String YYYY_MM_DD = "yyyy-MM-dd";

	public static final String DEFAULT_MEETING_START_TIME = "10:00";

	public Long id;
	public List<String> times;
	public DartMeeting meeting;
	public String startDate;
	public String startTime;
	public String rawTime;
	public String oldName;
	public String oldLocation;
	public String oldTiming;
	public String meetingName;
	public String meetingVenu;
	public List<Company> companiesWithFullSubscription;
	public Long companyId;

	public ArrayList<MinuteDuration> durationOptions;

	@Resource
	private SubscriptionService subscriptionService;
	@Resource
	private SubscriptionDao subscriptionDao;
	@Resource
	private DartMeetingDao dartMeetingDao;
	@Resource
	private GuestDao guestDao;
	@Resource
	private CompanyDao companyDao;
	@Resource
	private EventService eventService;
	@Resource 
	private GuestService guestService;
	

	@Override
	public String execute() {
		Calendar calendar = Calendar.getInstance();

		if (id != null) {
			meeting = dartMeetingDao.load(id);
		} else {
			meeting = new DartMeeting();
			meeting.organiser = getSecurityContext().getUser();
			String temp = UsefulDateTime.now().addDays(1).format(YYYY_MM_DD)
					+ " " + DEFAULT_MEETING_START_TIME;
			meeting.startDateTime = UsefulDateTime.create(temp);
			meeting.durationInMinutes = 60;
			meeting.organiser = getSecurityContext().getUser();
			meeting.complete = false;
			Company company = subscriptionService
					.getDefaultCompany(securityContext.getUser());
			meeting.company = company;
			meeting = dartMeetingDao.save(meeting);
			Guest guest = guestService.addGuest(meeting.organiser, meeting);
			guest.status = Guest.ACCEPT_STATUS;
			guestDao.save(guest);
			
			eventService.logEvent(getSecurityContext().getUser(), "Create DART Meeting");
			
			return redirect("MeetingDetails.action?id=" + meeting.getId());
		}

		companyId = meeting.company.getId();

		times = new ArrayList<String>();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		while (calendar.get(Calendar.DAY_OF_MONTH) < 2) {
			times.add(timeFormat.format(calendar.getTime()));
			calendar.add(Calendar.MINUTE, 15);
		}

		startDate = meeting.startDateTime.format(YYYY_MM_DD);
		startTime = meeting.startDateTime.format(HH_MM);
		durationOptions = new ArrayList<MinuteDuration>();
		durationOptions.add(MinuteDuration.create(15));
		durationOptions.add(MinuteDuration.create(30));
		durationOptions.add(MinuteDuration.create(45));
		durationOptions.add(MinuteDuration.create(60));
		durationOptions.add(MinuteDuration.create(90));
		durationOptions.add(MinuteDuration.create(120));
		durationOptions.add(MinuteDuration.create(150));
		durationOptions.add(MinuteDuration.create(180));
		durationOptions.add(MinuteDuration.create(210));
		durationOptions.add(MinuteDuration.create(240));
		durationOptions.add(MinuteDuration.create(270));
		durationOptions.add(MinuteDuration.create(300));
		durationOptions.add(MinuteDuration.create(330));
		durationOptions.add(MinuteDuration.create(360));
		durationOptions.add(MinuteDuration.create(390));
		durationOptions.add(MinuteDuration.create(420));
		durationOptions.add(MinuteDuration.create(450));
		durationOptions.add(MinuteDuration.create(480));
		durationOptions.add(MinuteDuration.create(510));
		durationOptions.add(MinuteDuration.create(540));
		durationOptions.add(MinuteDuration.create(570));
		durationOptions.add(MinuteDuration.create(600));

		oldName = meeting.name == null ? "" : meeting.name;
		oldLocation = meeting.location == null ? "" : meeting.location;
		oldTiming = meeting.startDateTime == null ? "" : meeting.startDateTime
				.getLongFormat();

		companiesWithFullSubscription = subscriptionDao
				.getAllFullUserCompanies(securityContext.getUser());

		return SUCCESS;
	}

	public String saveMeetingName() {
		meeting = dartMeetingDao.load(id);
		meeting.name = meetingName;
		return NONE;
	}

	public String saveMeetingVenu() {
		meeting = dartMeetingDao.load(id);
		meeting.location = meetingVenu;
		return NONE;
	}

	public String saveAndContinue() {
		meeting = dartMeetingDao.load(id);
		String parts[] = rawTime.split(":");
		meeting.durationInMinutes = Integer.valueOf(parts[0]) * 60
				+ Integer.valueOf(parts[1]);

		setParameters();

		meeting.startDateTime = UsefulDateTime.create(startDate + " "
				+ startTime);
//		if (meeting.invitation != null && !"".equals(meeting.invitation.trim())) {
//			if (oldName != null && !oldName.trim().isEmpty()
//					&& !oldName.equals(meeting.name)) {
//				meeting.invitation = meeting.invitation.replaceAll(oldName,
//						meeting.name);
//			}
//			if (oldLocation != null && !oldLocation.trim().isEmpty()
//					&& !oldLocation.equals(meeting.location)) {
//				meeting.invitation = meeting.invitation.replaceAll(oldLocation,
//						meeting.location);
//			}
//			if (oldTiming != null && !oldTiming.trim().isEmpty()
//					&& !oldTiming.equals(meeting.startDateTime.getLongFormat())) {
//				meeting.invitation = meeting.invitation.replaceAll(oldTiming,
//						meeting.startDateTime.getLongFormat());
//			}
//		}

		if (companyId != null) {
			Company company = companyDao.load(companyId);
			meeting.company = company;
		}

		meeting = dartMeetingDao.save(meeting);

		// add the organizer as a guest
		if (id == null) {
			Guest guest = guestService.addGuest(getSecurityContext().getUser(), meeting);
			guest.status = Guest.ACCEPT_STATUS;
			guest = guestDao.save(guest);
			guestDao.flush();
		}

		id = meeting.getId();
		dartMeetingDao.flush();
		if (meeting.name == null || "".equals(meeting.name.trim())) {
			addActionError("Meeting name must be entered");
			return execute();
		}

		if (UsefulDateTime.now().isAfter(meeting.startDateTime)) {
			addActionError("Meeting Date & Time cannot be in the past");
			return execute();
		}

		return redirect("InviteGuests.action?id=" + id);
	}

	public DartMeeting getMeeting() {
		return meeting;
	}

	public void setMeeting(DartMeeting meeting) {
		this.meeting = meeting;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<String> getTimes() {
		return times;
	}

	public void setTimes(List<String> times) {
		this.times = times;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getRawTime() {
		return rawTime;
	}

	public void setRawTime(String rawTime) {
		this.rawTime = rawTime;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String getOldLocation() {
		return oldLocation;
	}

	public void setOldLocation(String oldLocation) {
		this.oldLocation = oldLocation;
	}

	public String getOldTiming() {
		return oldTiming;
	}

	public void setOldTiming(String oldTiming) {
		this.oldTiming = oldTiming;
	}

	public String getMeetingName() {
		return meetingName;
	}

	public void setMeetingName(String meetingName) {
		this.meetingName = meetingName;
	}

	public String getMeetingVenu() {
		return meetingVenu;
	}

	public void setMeetingVenu(String meetingVenu) {
		this.meetingVenu = meetingVenu;
	}

	public List<Company> getCompaniesWithFullSubscription() {
		return companiesWithFullSubscription;
	}

	public void setCompaniesWithFullSubscription(
			List<Company> companiesWithFullSubscription) {
		this.companiesWithFullSubscription = companiesWithFullSubscription;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public ArrayList<MinuteDuration> getDurationOptions() {
		return durationOptions;
	}

	public void setDurationOptions(ArrayList<MinuteDuration> durationOptions) {
		this.durationOptions = durationOptions;
	}

	// Getters and setters
	

}
