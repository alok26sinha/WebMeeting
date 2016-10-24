package service.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import model.AgendaItem;
import model.DartMeeting;
import model.Foresight;
import model.ForesightTraction;
import model.Hindsight;
import model.Insight;
import model.Person;
import model.ShiftMeeting;
import model.StartingIdea;
import model.StartingIdeaTraction;
import model.Traction;

import org.springframework.stereotype.Service;

import security.LocalSecurityContext;
import security.SecurityContext;
import service.DartTimeMeasurementService;
import type.MinuteDuration;
import dao.AgendaItemDao;
import dao.DartMeetingDao;
import dao.ForesightDao;
import dao.ForesightTractionDao;
import dao.HindsightDao;
import dao.InsightDao;
import dao.PersonDao;
import dao.ShiftMeetingDao;
import dao.StartingIdeaDao;
import dao.StartingIdeaTractionDao;
import dao.TractionDao;

@Service("meeting.agenda")
@Path("/agenda")
public class MeetingAgendaService {

	@Resource
	public DartTimeMeasurementService dartTimeMeasurementService;

	public static final class SightDTO {

		public Long id;
		public String description;

		public SightDTO(Long id, String description) {
			this.id = id;
			this.description = description;
		}
	}

	public static final class StartIdeaDTO {

		public Long id;
		public String description;
		public String name;
		public String email;

		public StartIdeaDTO(Long id, String description, String name,
				String email) {
			this.id = id;
			this.description = description;
			this.name = name;
			this.email = email;
		}

	}

	public static final class MeetingTotals {

		public String totalTime;
		public String agendaTime;
		public int unplannedTime;
		public boolean overtime;

		public MeetingTotals(MinuteDuration totalTime, MinuteDuration agendaTime) {
			this.totalTime = totalTime.toString();
			this.agendaTime = agendaTime.toString();
			this.unplannedTime = (int) (totalTime.getMinutes() - agendaTime
					.getMinutes());
			this.overtime = agendaTime.getMinutes() > totalTime.getMinutes();
		}

	}

	public static final class AgendaDTO {

		public Long id;
		public int number;
		public String description;
		public String name;
		public long duration;
		public String durationStr;
		public String email;
		public String subAgendaItem;

		public AgendaDTO(Long id, int number, String description, String name,
				String email, MinuteDuration minuteDuration,
				String subAgendaItem) {
			this.id = id;
			this.number = number;
			this.description = description;
			this.name = name;
			this.email = email;
			this.duration = minuteDuration.getMinutes();
			this.durationStr = minuteDuration.toString();
			if (subAgendaItem != null)
				this.subAgendaItem = subAgendaItem;
			else
				this.subAgendaItem = "";
		}

	}

	@Resource
	private DartMeetingDao dartMeetingDao;

	@Resource
	private ShiftMeetingDao shiftMeetingDao;

	@Resource
	private AgendaItemDao agendaItemDao;

	@Resource
	private StartingIdeaDao startingIdeaDao;

	@Resource
	private HindsightDao hindsightDao;

	@Resource
	private InsightDao insightDao;

	@Resource
	private ForesightDao foresightDao;

	@Resource
	private TractionDao tractionDao;

	@Resource
	private ForesightTractionDao foresightTractionDao;

	@Resource
	private StartingIdeaTractionDao startingIdeaTractionDao;

	@Resource
	private PersonDao personDao;

	@GET
	@Path("dart/list/{meetingId}")
	@Produces("application/json")
	public AgendaDTO[] listDartAgendaItems(
			@PathParam("meetingId") Long meetingId) {
		DartMeeting meeting = dartMeetingDao.load(meetingId);
		return buildRestltList(meeting);
	}

	@GET
	@Path("shift/list/{meetingId}/idea")
	@Produces("application/json")
	public StartIdeaDTO[] listShiftStartIdeas(
			@PathParam("meetingId") Long meetingId) {
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		return buildRestltList(meeting);
	}

	@GET
	@Path("shift/list/{meetingId}/hindsight")
	@Produces("application/json")
	public SightDTO[] listShiftHindsights(@PathParam("meetingId") Long meetingId) {
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		return buildHindsightsList(meeting);
	}

	@GET
	@Path("shift/list/{meetingId}/insight")
	@Produces("application/json")
	public SightDTO[] listShiftInsights(@PathParam("meetingId") Long meetingId) {
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		return buildInsightsList(meeting);
	}

	@GET
	@Path("shift/list/{meetingId}/foresight")
	@Produces("application/json")
	public SightDTO[] listShiftForesights(@PathParam("meetingId") Long meetingId) {
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		return buildForesightsList(meeting);
	}

	@GET
	@Path("shift/list/{meetingId}/traction/foresight")
	@Produces("application/json")
	public SightDTO[] listShiftFTractions(@PathParam("meetingId") Long meetingId) {
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		return buildFTractionsList(meeting);
	}

	@GET
	@Path("shift/list/{meetingId}/traction/starting-idea")
	@Produces("application/json")
	public SightDTO[] listShiftSITractions(
			@PathParam("meetingId") Long meetingId) {
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		return buildSITractionsList(meeting);
	}

	@DELETE
	@Path("dart/{itemId}")
	@Produces("application/json")
	public AgendaDTO[] deleteDartAgendaItem(@PathParam("itemId") Long itemId) {
		AgendaItem item = agendaItemDao.load(itemId);
		Long meetingId = item.dartMeeting.getId();
		agendaItemDao.delete(item);
		agendaItemDao.flush();
		DartMeeting meeting = dartMeetingDao.load(meetingId);
		for (int i = 0; i < meeting.agendaItems.size(); i++) {
			meeting.agendaItems.get(i).number = i + 1;
		}
		dartMeetingDao.save(meeting);
		dartMeetingDao.flush();
		return buildRestltList(meeting);
	}

	@DELETE
	@Path("shift/idea/{ideaId}")
	@Produces("application/json")
	public StartIdeaDTO[] deleteShiftIdea(@PathParam("ideaId") Long itemId) {
		StartingIdea item = startingIdeaDao.load(itemId);
		Long meetingId = item.shiftMeeting.getId();
		startingIdeaDao.delete(item);
		startingIdeaDao.flush();
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		return buildRestltList(meeting);
	}

	@DELETE
	@Path("shift/hindsight/{hindsightId}")
	@Produces("application/json")
	public SightDTO[] deleteHindsight(@PathParam("hindsightId") Long hindsightId) {
		Hindsight item = hindsightDao.load(hindsightId);
		Long meetingId = item.shiftMeeting.getId();
		hindsightDao.delete(item);
		hindsightDao.flush();
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		return buildHindsightsList(meeting);
	}

	@DELETE
	@Path("shift/insight/{insightId}")
	@Produces("application/json")
	public SightDTO[] deleteInsight(@PathParam("insightId") Long insightId) {
		Insight item = insightDao.load(insightId);
		Long meetingId = item.shiftMeeting.getId();
		insightDao.delete(item);
		insightDao.flush();
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		return buildInsightsList(meeting);
	}

	@DELETE
	@Path("shift/foresight/{foresightId}")
	@Produces("application/json")
	public SightDTO[] deleteForesight(@PathParam("foresightId") Long foresightId) {
		Foresight item = foresightDao.load(foresightId);
		Long meetingId = item.shiftMeeting.getId();
		foresightDao.delete(item);
		foresightDao.flush();
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		return buildForesightsList(meeting);
	}

	@DELETE
	@Path("shift/traction/{tractionId}/foresight")
	@Produces("application/json")
	public SightDTO[] deleteFTraction(@PathParam("tractionId") Long tractionId) {
		ShiftMeeting meeting = deleteTraction(tractionId);
		return buildFTractionsList(meeting);
	}

	@DELETE
	@Path("shift/traction/{tractionId}/starting-idea")
	@Produces("application/json")
	public SightDTO[] deleteSITraction(@PathParam("tractionId") Long tractionId) {
		ShiftMeeting meeting = deleteTraction(tractionId);
		return buildSITractionsList(meeting);
	}

	@PUT
	@Path("dart/up/{itemId}")
	@Produces("application/json")
	public AgendaDTO[] moveUp(@PathParam("itemId") Long itemId) {
		AgendaItem item = agendaItemDao.load(itemId);
		DartMeeting meeting = item.dartMeeting;
		int itemIndex = findMovedItemById(meeting, itemId);
		if (itemIndex > 0) {
			// clicked element position in range [1..agendaItems.size()[
			switchItems(itemIndex, itemIndex - 1, meeting);
		}
		meeting = dartMeetingDao.load(meeting.getId());
		return buildRestltList(meeting);
	}

	@PUT
	@Path("dart/down/{itemId}")
	@Produces("application/json")
	public AgendaDTO[] moveDown(@PathParam("itemId") Long itemId) {
		AgendaItem item = agendaItemDao.load(itemId);
		DartMeeting meeting = item.dartMeeting;
		int itemIndex = findMovedItemById(meeting, itemId);
		if (itemIndex >= 0 && itemIndex < meeting.agendaItems.size() - 1) {
			// clicked element position in range [0..agendaItems.size() - 1[
			switchItems(itemIndex + 1, itemIndex, meeting);
		}
		meeting = dartMeetingDao.load(meeting.getId());
		return buildRestltList(meeting);
	}

	@GET
	@Path("dart/totals/{meetingId}")
	@Produces("application/json")
	public MeetingTotals getTotals(@PathParam("meetingId") Long meetingId) {
		DartMeeting meeting = dartMeetingDao.load(meetingId);
		return calculateTotals(meeting);
	}

	@POST
	@Path("dart/newItem")
	@Produces("application/json")
	public AgendaDTO[] addNewItem(@FormParam("id") Long meetingId,
			@FormParam("newAgendaItemDescription") String description,
			@FormParam("newAgendaItemDurationInMinutes") Long duration,
			@DefaultValue("0") @FormParam("itemOwnerId") Long itemOwnerId,
			@FormParam("newSubAgendaItem") String subAgendaItem) {
		DartMeeting meeting = dartMeetingDao.load(meetingId);
		meeting.newAgendaItemDescription = null;
		meeting.newSubAgendaItem = null;
		AgendaItem newAgendaItem = new AgendaItem();
		newAgendaItem.dartMeeting = meeting;
		newAgendaItem.description = description;
		newAgendaItem.subAgendaItem = subAgendaItem;
		newAgendaItem.durationInMinutes = MinuteDuration.create(duration);
		newAgendaItem.contributor = getCurrentUser();
		if (itemOwnerId != null && itemOwnerId.longValue() != 0L) {
			newAgendaItem.itemOwner = personDao.load(itemOwnerId);
		}
		int maximumNumber = agendaItemDao.getMaximumNumber(meeting);
		int nextNumber = maximumNumber + 1;
		newAgendaItem.number = nextNumber;

		agendaItemDao.save(newAgendaItem);
		agendaItemDao.flush();

		return buildRestltList(meeting);
	}

	@POST
	@Path("shift/newIdea/owned")
	@Produces("application/json")
	public StartIdeaDTO[] addNewPersonIdea(@FormParam("id") Long meetingId,
			@FormParam("newIdeaDescription") String description) {
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		StartingIdea idea = new StartingIdea();
		idea.teamContribution = false;
		return addNewStartingIdea(description, meeting, idea);
	}

	@POST
	@Path("shift/newIdea/team")
	@Produces("application/json")
	public StartIdeaDTO[] addNewTeamIdea(@FormParam("id") Long meetingId,
			@FormParam("newIdeaDescription") String description) {
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		StartingIdea idea = new StartingIdea();
		idea.teamContribution = true;
		return addNewStartingIdea(description, meeting, idea);
	}

	@POST
	@Path("shift/newHindsight")
	@Produces("application/json")
	public SightDTO[] addNewHindsight(@FormParam("id") Long meetingId,
			@FormParam("newIdeaDescription") String description) {
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		Hindsight hindsight = new Hindsight();
		hindsight.shiftMeeting = meeting;
		hindsight.description = description;
		hindsightDao.save(hindsight);
		hindsightDao.flush();
		return buildHindsightsList(meeting);
	}

	@POST
	@Path("shift/newInsight")
	@Produces("application/json")
	public SightDTO[] addNewInsight(@FormParam("id") Long meetingId,
			@FormParam("newIdeaDescription") String description) {
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		Insight insight = new Insight();
		insight.shiftMeeting = meeting;
		insight.description = description;
		insightDao.save(insight);
		insightDao.flush();
		return buildInsightsList(meeting);
	}

	@POST
	@Path("shift/newForesight")
	@Produces("application/json")
	public SightDTO[] addNewForesight(@FormParam("id") Long meetingId,
			@FormParam("newIdeaDescription") String description) {
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		Foresight foresight = new Foresight();
		foresight.shiftMeeting = meeting;
		foresight.description = description;
		foresightDao.save(foresight);
		foresightDao.flush();
		return buildForesightsList(meeting);
	}

	@POST
	@Path("shift/newTraction/foresight")
	@Produces("application/json")
	public SightDTO[] addNewFTraction(@FormParam("id") Long meetingId,
			@FormParam("newIdeaDescription") String description) {
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		ForesightTraction traction = new ForesightTraction();
		traction.shiftMeeting = meeting;
		traction.description = description;
		foresightTractionDao.save(traction);
		foresightTractionDao.flush();
		return buildFTractionsList(meeting);
	}

	@POST
	@Path("shift/newTraction/starting-idea")
	@Produces("application/json")
	public SightDTO[] addNewSITraction(@FormParam("id") Long meetingId,
			@FormParam("newIdeaDescription") String description) {
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		StartingIdeaTraction traction = new StartingIdeaTraction();
		traction.shiftMeeting = meeting;
		traction.description = description;
		startingIdeaTractionDao.save(traction);
		startingIdeaTractionDao.flush();
		return buildSITractionsList(meeting);
	}

	@POST
	@Path("dart/item")
	public String editDartDescription(@FormParam("itemId") Long itemId,
			@FormParam("description") String description) {
		AgendaItem item = agendaItemDao.load(itemId);
		item.description = description;
		agendaItemDao.save(item);
		agendaItemDao.flush();
		return item.description;
	}

	@POST
	@Path("shift/idea")
	public String editStartIdeaDescription(@FormParam("ideaId") Long ideaId,
			@FormParam("description") String description) {
		StartingIdea idea = startingIdeaDao.load(ideaId);
		idea.description = description;
		startingIdeaDao.save(idea);
		startingIdeaDao.flush();
		return idea.description;
	}

	@POST
	@Path("shift/hindsight")
	public String editHindsightDescription(
			@FormParam("hindsightId") Long hindsightId,
			@FormParam("description") String description) {
		Hindsight hindsight = hindsightDao.load(hindsightId);
		hindsight.description = description;
		hindsightDao.save(hindsight);
		hindsightDao.flush();
		return hindsight.description;
	}

	@POST
	@Path("shift/insight")
	public String editInsightDescription(
			@FormParam("insightId") Long insightId,
			@FormParam("description") String description) {
		Insight insight = insightDao.load(insightId);
		insight.description = description;
		insightDao.save(insight);
		insightDao.flush();
		return insight.description;
	}

	@POST
	@Path("shift/foresight")
	public String editForesightDescription(
			@FormParam("foresightId") Long foresightId,
			@FormParam("description") String description) {
		Foresight foresight = foresightDao.load(foresightId);
		foresight.description = description;
		foresightDao.save(foresight);
		foresightDao.flush();
		return foresight.description;
	}

	@POST
	@Path("shift/traction")
	public String editTractionDescription(
			@FormParam("tractionId") Long foresightId,
			@FormParam("description") String description) {
		Traction traction = tractionDao.load(foresightId);
		traction.description = description;
		tractionDao.save(traction);
		tractionDao.flush();
		return traction.description;
	}

	@PUT
	@Path("dart/owner/{itemId}/{personId}")
	public void changeOwner(@PathParam("itemId") Long itemId,
			@PathParam("personId") Long personId) {
		AgendaItem item = agendaItemDao.load(itemId);
		Person person;
		if( personId.longValue() != 0L)
			person = personDao.load(personId);
		else 
			person = null;
		item.itemOwner = person;
		agendaItemDao.save(item);
		agendaItemDao.flush();
	}

	@PUT
	@Path("dart/duration/{itemId}/{duration}")
	@Produces("application/json")
	public MeetingTotals changeDuration(@PathParam("itemId") Long itemId,
			@PathParam("duration") Long duration) {
		AgendaItem item = agendaItemDao.load(itemId);
		item.durationInMinutes = MinuteDuration.create(duration.longValue());
		agendaItemDao.save(item);
		agendaItemDao.flush();
		return calculateTotals(item.dartMeeting);
	}

	private Person getCurrentUser() {
		SecurityContext context = LocalSecurityContext.get();
		return context.getUser();
	}

	private void switchItems(int indexFrom, int indexTo, DartMeeting meeting) {
		meeting.agendaItems.get(indexFrom).number--;
		meeting.agendaItems.get(indexTo).number++;
		agendaItemDao.save(meeting.agendaItems.get(indexFrom));
		agendaItemDao.save(meeting.agendaItems.get(indexTo));
		agendaItemDao.flush();
		dartMeetingDao.evict(meeting);
	}

	private int findMovedItemById(DartMeeting meeting, Long itemId) {
		for (int i = 0; i < meeting.agendaItems.size(); i++) {
			if (meeting.agendaItems.get(i).getId().longValue() == itemId
					.longValue()) {
				return i;
			}
		}
		return -1;
	}

	private AgendaDTO[] buildRestltList(DartMeeting meeting) {
		List<AgendaDTO> result = new ArrayList<MeetingAgendaService.AgendaDTO>(
				meeting.agendaItems.size());
		for (AgendaItem item : meeting.agendaItems) {
			AgendaDTO agendaDTO = new AgendaDTO(item.getId(), item.number,
					item.description, item.itemOwner != null ? item.itemOwner.name : "All",
					item.itemOwner != null ? item.itemOwner.email : "", item.durationInMinutes,
					item.subAgendaItem);
			result.add(agendaDTO);
		}
		return result.toArray(new AgendaDTO[result.size()]);
	}

	private MeetingTotals calculateTotals(DartMeeting meeting) {
		MinuteDuration totalTime = MinuteDuration
				.create(meeting.durationInMinutes);
		MinuteDuration agendaTime = dartTimeMeasurementService
				.getTotalInMinuteDuration(meeting);
		return new MeetingTotals(totalTime, agendaTime);
	}

	private StartIdeaDTO[] buildRestltList(ShiftMeeting meeting) {
		List<StartIdeaDTO> startIdeas = new ArrayList<StartIdeaDTO>(
				meeting.startingIdeas.size());
		for (StartingIdea idea : meeting.startingIdeas) {
			startIdeas.add(new StartIdeaDTO(idea.getId(), idea.description,
					idea.getRecognition(), idea.getContributorEmail()));
		}
		return startIdeas.toArray(new StartIdeaDTO[startIdeas.size()]);
	}

	private StartIdeaDTO[] addNewStartingIdea(String description,
			ShiftMeeting meeting, StartingIdea idea) {
		idea.shiftMeeting = meeting;
		idea.contributor = LocalSecurityContext.get().getUser();
		idea.description = description;
		startingIdeaDao.save(idea);
		startingIdeaDao.flush();
		return buildRestltList(meeting);
	}

	private SightDTO[] buildHindsightsList(ShiftMeeting meeting) {
		List<SightDTO> sights = new ArrayList<SightDTO>(
				meeting.hindsights.size());
		for (Hindsight hindsight : meeting.hindsights) {
			sights.add(new SightDTO(hindsight.getId(), hindsight.description));
		}
		return sights.toArray(new SightDTO[sights.size()]);
	}

	private SightDTO[] buildInsightsList(ShiftMeeting meeting) {
		List<SightDTO> sights = new ArrayList<SightDTO>(meeting.insights.size());
		for (Insight insight : meeting.insights) {
			sights.add(new SightDTO(insight.getId(), insight.description));
		}
		return sights.toArray(new SightDTO[sights.size()]);
	}

	private SightDTO[] buildForesightsList(ShiftMeeting meeting) {
		List<SightDTO> sights = new ArrayList<SightDTO>(
				meeting.foresights.size());
		for (Foresight foresight : meeting.foresights) {
			sights.add(new SightDTO(foresight.getId(), foresight.description));
		}
		return sights.toArray(new SightDTO[sights.size()]);
	}

	private SightDTO[] buildSITractionsList(ShiftMeeting meeting) {
		List<SightDTO> sights = new ArrayList<SightDTO>();
		for (Traction traction : startingIdeaTractionDao.getAll(meeting)) {
			sights.add(new SightDTO(traction.getId(), traction.description));
		}
		return sights.toArray(new SightDTO[sights.size()]);
	}

	private SightDTO[] buildFTractionsList(ShiftMeeting meeting) {
		List<SightDTO> sights = new ArrayList<SightDTO>();
		for (Traction traction : foresightTractionDao.getAll(meeting)) {
			sights.add(new SightDTO(traction.getId(), traction.description));
		}
		return sights.toArray(new SightDTO[sights.size()]);
	}

	private ShiftMeeting deleteTraction(Long tractionId) {
		Traction traction = tractionDao.load(tractionId);
		Long meetingId = traction.shiftMeeting.getId();
		tractionDao.delete(traction);
		tractionDao.flush();
		ShiftMeeting meeting = shiftMeetingDao.load(meetingId);
		return meeting;
	}

}
