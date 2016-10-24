package service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import model.StartingIdea;
import model.StartingIdeaList;

@Path("meetings")
public class Meetings {

	private List<StartingIdea> startingIdeas = new ArrayList<StartingIdea>();

	public Meetings() {/*
		startingIdeas.add(new StartingIdea(
				"Need to standardise and simplify processes", "Peter McKeown"));
		startingIdeas
				.add(new StartingIdea(
						"Process have evolved over many years and need to be changed carefully",
						"Paul Epstein"));
		startingIdeas.add(new StartingIdea(
				"Any solution needs to involve customer support staff",
				"Nik Von Veh"));
		startingIdeas
				.add(new StartingIdea(
						"Online channels are more cost effective",
						"Esse Spadavecchia"));*/
	}

	@GET
	@Path("starting-ideas")
	@Produces("application/json")
	// @Mapped // mapped is the default format
	public StartingIdeaList getStartingIdeasMapped() {
		
		ArrayList<StartingIdea> list = new ArrayList<StartingIdea>();
		list.addAll(startingIdeas);
		return new StartingIdeaList(list);
	}
}
