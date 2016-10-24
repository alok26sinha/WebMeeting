package controller.support;

import model.BaseDescriptiveModel;
import model.Meeting;
import dao.BaseDaoNew;

@SuppressWarnings("serial")
public abstract class BaseDescriptionEditAction
	<MeetingT extends Meeting, 
	DetailT extends BaseDescriptiveModel, 
	MeetingDao extends BaseDaoNew<MeetingT, Long>, 
	DetailDao extends BaseDaoNew<DetailT, Long>> extends BaseAction {

	public String description;
	public Long id;
	public Long detailId;
	public String caller;
	public MeetingT meeting;
	public String submitButton;
	
	protected DetailDao detailDao;
	protected MeetingDao meetingDao;
	
	@Override
	public String execute() {
		meeting = meetingDao.load(id);
		if (submitButton == null || "".equals(submitButton.trim())) {
			description = detailDao.load(detailId).description;
			return SUCCESS;
		} else {
			if ("Save".equals(submitButton)) {
				populateDetails();
			}
			return redirect(caller + ".action?id=" + id);
		}
	}
	
	protected void populateDetails() {
		DetailT idea = detailDao.load(detailId);
		idea.description = description;
		detailDao.save(idea);
	}
	public String edit() {
		return execute();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDetailId() {
		return detailId;
	}

	public void setDetailId(Long detailId) {
		this.detailId = detailId;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public MeetingT getMeeting() {
		return meeting;
	}

	public void setMeeting(MeetingT meeting) {
		this.meeting = meeting;
	}

	public String getSubmitButton() {
		return submitButton;
	}

	public void setSubmitButton(String submitButton) {
		this.submitButton = submitButton;
	}

	
}
