package controller.dartmanage;

import java.util.Comparator;

import model.Person;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import type.NullSafeComparator;


public class PersonComparator implements  Comparator<Person>{
	private static Log log = LogFactory.getLog(PersonComparator.class);
	@Override
	public int compare(Person p1, Person p2) {
		int difference = NullSafeComparator.compare(p1.name, p2.name);
		if( difference != 0)
			return difference;
		else{
			difference = NullSafeComparator.compare(p1.email, p2.email);
			if( difference != 0)
				return difference;
			else{
				log.warn("Could not find a difference between to people when at least email addresses should be unique " + p1 + p2);
				return 0;
			}
		}
	}
}