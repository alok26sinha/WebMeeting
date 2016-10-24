package model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="listing")
public class StartingIdeaList {
	
	private List<StartingIdea> startingIdeas;
	
	public StartingIdeaList(){
	}
	
	public StartingIdeaList(List<StartingIdea> startingIdeas){
		this.startingIdeas = startingIdeas;
	}
	
	@XmlElement(name="startingIdeas")
	public List<StartingIdea> getStartingIdeas(){
		return startingIdeas;
	}

}
