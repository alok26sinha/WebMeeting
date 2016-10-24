package controller.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import model.Event;
import model.Person;
import service.EventService;

@SuppressWarnings("serial")
public class SystemActivityAction extends BaseAdminLesdAction {

	public List<Person> recentlyActivePeople;
	public Map<Person, ArrayList<Event>> events;
	
	@Resource
	private EventService eventService;
	
	public String execute(){
		checkCurrentUserIsAdmin();
		recentlyActivePeople = eventService.getMostRecentlyActive();
		events = eventService.getPersonEventMap();
		return SUCCESS;
	}

	@Override
	public String list() {
		// not used
		return null;
	}

	@Override
	public String edit() {
		// not used
		return null;
	}

	@Override
	public String save() {
		// not used
		return null;
	}

	@Override
	public String delete() {
		// not used
		return null;
	}

	public List<Person> getRecentlyActivePeople() {
		return recentlyActivePeople;
	}

	public void setRecentlyActivePeople(List<Person> recentlyActivePeople) {
		this.recentlyActivePeople = recentlyActivePeople;
	}

	public Map<Person, ArrayList<Event>> getEvents() {
		return events;
	}

	public void setEvents(Map<Person, ArrayList<Event>> events) {
		this.events = events;
	}
	
	
	
}
