package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import model.Event;
import model.Person;

import org.springframework.stereotype.Component;

import type.UsefulDateTime;

import common.Config;

import dao.EventDao;

@Component
public class EventService {

	@Resource
	private EventDao eventDao;

	public List<Event> getRecentEvents(Person person) {
		return eventDao.getAllByDateDesc(person);
	}

	public void logEvent(Person person, String eventName) {

		Event event;
		// Has this event been logged yet
		if ((event = eventDao.getEvent(person, eventName)) != null) {
			// Update the time
			event.eventDateTime = UsefulDateTime.now();
		} else {
			List<Event> personEvents = eventDao.getAllByDateDesc(person);

			// Check if we have reached the maximum number of events
			int maxNumberEvents = Config.getInstance().getValueInt(
					"max.number.events");
			if (personEvents.size() >= maxNumberEvents) {
				// Update the oldest
				Event oldestEvent = personEvents.get(personEvents.size() - 1);
				oldestEvent.eventDateTime = UsefulDateTime.now();
				oldestEvent.name = eventName;
			} else {
				// Add in this event
				Event newEvent = new Event();
				newEvent.eventDateTime = UsefulDateTime.now();
				newEvent.name = eventName;
				newEvent.person = person;
				eventDao.save(newEvent);
			}

		}

	}
	
	public List<Person> getMostRecentlyActive(){
		List<Event> orderedEvents = eventDao.getAllByDateDesc();
		
		List<Person> activePeople = new ArrayList<Person>();
		
		for(Event event: orderedEvents){
			if(!activePeople.contains(event.person))
				activePeople.add(event.person);
		}
		
		return activePeople;
	}

	public Map<Person, ArrayList<Event>> getPersonEventMap() {
		Map<Person, ArrayList<Event>> map = new HashMap<Person, ArrayList<Event>>();
		
		List<Event> orderedEvents = eventDao.getAllByDateDesc();
		for(Event event: orderedEvents){
			Person person = event.person;
			
			ArrayList<Event> eventList = map.get(person);
			if( eventList == null){
				eventList = new ArrayList<Event>();
				map.put(person, eventList);
			}
			
			eventList.add(event);
		}
		
		return map;
	}

}
