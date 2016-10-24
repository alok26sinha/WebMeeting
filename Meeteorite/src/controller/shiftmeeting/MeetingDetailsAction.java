package controller.shiftmeeting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import model.Company;
import model.Guest;
import model.ShiftMeeting;
import service.EventService;
import service.GuestService;
import service.SubscriptionService;
import type.MinuteDuration;
import type.UsefulDateTime;
import controller.support.BaseAction;
import dao.CompanyDao;
import dao.GuestDao;
import dao.ShiftMeetingDao;
import dao.SubscriptionDao;

@SuppressWarnings("serial")
public class MeetingDetailsAction extends BaseAction {
	public Long id;
	public ShiftMeeting meeting;
	public List<String> times;
	public String startDate;
	public String startTime;
    public String rawTime;
	public ArrayList<MinuteDuration> durationOptions;
	public String oldName;
	public String oldLocation;
	public String meetingName;
	public String meetingVenu;
	public List<Company> companiesWithFullSubscription;
	public Long companyId;

	@Resource
	private CompanyDao companyDao;
	@Resource
	private SubscriptionService subscriptionService;
	@Resource
	private SubscriptionDao subscriptionDao;
	private ShiftMeetingDao shiftMeetingDao;
	private GuestDao guestDao;
	private Calendar calendar = Calendar.getInstance();
	@Resource
	private EventService eventService;
	@Resource
	private GuestService guestService;

	@Override
	public String execute() {
		meeting = shiftMeetingDao.load(id);
		
		companyId = meeting.company.getId();

		times = new ArrayList<String>();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		while (calendar.get(Calendar.DAY_OF_MONTH) < 2) {
			times.add(timeFormat.format(calendar.getTime()));
			calendar.add(Calendar.MINUTE, 15);
		}

		durationOptions = new ArrayList<MinuteDuration>();
		durationOptions.add(MinuteDuration.create(30));
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
		durationOptions.add(MinuteDuration.create(600));

		startDate = meeting.startDateTime.format("yyyy-MM-dd");
		startTime = meeting.startDateTime.format("HH:mm");
		
		oldName = meeting.name == null ? "" : meeting.name;
		oldLocation = meeting.location == null ? "" : meeting.location;
		
		companiesWithFullSubscription = subscriptionDao
				.getAllFullUserCompanies(securityContext.getUser());

		return SUCCESS;
	}

	public String newShiftMeeting() {
		meeting = new ShiftMeeting();

		// Default values for a new meeting
		meeting.organiser = getSecurityContext().getUser();

		String temp = UsefulDateTime.now().format(
				controller.dartmanage.MeetingDetailsAction.YYYY_MM_DD)
				+ " "
				+ controller.dartmanage.MeetingDetailsAction.DEFAULT_MEETING_START_TIME;
		meeting.startDateTime = UsefulDateTime.create(temp);
		meeting.durationInMinutes = 60;
		meeting.complete = false;
		Company company = subscriptionService
				.getDefaultCompany(securityContext.getUser());
		meeting.company = company;

		meeting = shiftMeetingDao.save(meeting);

		// add the organizer as a guest
		Guest guest = guestService.addGuest(getSecurityContext().getUser(), meeting);
		guest.status = Guest.ACCEPT_STATUS;
		guestDao.flush();
		
		eventService.logEvent(getSecurityContext().getUser(), "Create Shift Meeting");

		return redirect("MeetingDetails.action?id=" + meeting.getId());
	}

	public String saveAndContinue() {
		meeting = shiftMeetingDao.load(id);
        String parts[] = rawTime.split(":");
        meeting.durationInMinutes = Integer.valueOf(parts[0]) * 60 + Integer.valueOf(parts[1]);
		setParameters();

		meeting.startDateTime = UsefulDateTime.create(startDate + " "
				+ startTime);

//        if (meeting.invitation != null && !"".equals(meeting.invitation.trim())) {
//            if (oldName != null && !oldName.equals(meeting.name)) {
//                meeting.invitation = meeting.invitation.replaceAll(oldName, meeting.name);
//            }
//            if (oldLocation != null && !oldLocation.equals(meeting.location)) {
//                meeting.invitation = meeting.invitation.replaceAll(oldLocation, meeting.location);
//            }
//            //TODO: change time?????
//        }

        if (companyId != null) {
			Company company = companyDao.load(companyId);
			meeting.company = company;
		}
        
		shiftMeetingDao.save(meeting);
		shiftMeetingDao.flush();

		if (meeting.name == null || "".equals(meeting.name.trim())) {
			addActionError("Business challenge must be entered");
			return execute();
		}
		return redirect("ManageGuests.action?id=" + id);
	}
	
	public String saveMeetingName(){
		meeting = shiftMeetingDao.load(id);
		meeting.name = meetingName;
		return NONE;
	}

	public String saveMeetingVenu(){
		meeting = shiftMeetingDao.load(id);
		meeting.location = meetingVenu;
		return NONE;
	}
	
	// Getters and setters
	public void setShiftMeetingDao(ShiftMeetingDao shiftMeetingDao) {
		this.shiftMeetingDao = shiftMeetingDao;
	}

	public void setGuestDao(GuestDao guestDao) {
		this.guestDao = guestDao;
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

	public ArrayList<MinuteDuration> getDurationOptions() {
		return durationOptions;
	}

	public void setDurationOptions(ArrayList<MinuteDuration> durationOptions) {
		this.durationOptions = durationOptions;
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

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	
	
}
