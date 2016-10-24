package application;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

//import service.rest.MeetingGuestsService;

@ApplicationPath("rest")
@Service
public class MeetingsApplication extends Application implements ApplicationContextAware {
	private HashSet<Object> singletons = new HashSet<Object>();
	private HashSet<Class<?>> classes = new HashSet<Class<?>>();
	private static ApplicationContext context;

	public MeetingsApplication() {
		//singletons.add(new Meetings());
	}

	@Override
	public Set<Class<?>> getClasses() {
//		if (classes.isEmpty()) {
//			classes.add(MeetingGuestsService.class);
//		}
		return classes;
	}

	@Override
	public Set<Object> getSingletons() {
		if (singletons.isEmpty()) {
			for (String name : new String [] {"meeting.guests", "meeting.agenda", "run.action", "meeting.followup"}) {
				Object bean = context.getBean(name);
				singletons.add(bean);
			}
		}
		return singletons;
	}

	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		MeetingsApplication.context = context;
	}
}
