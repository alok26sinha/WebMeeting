package service.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import model.AgendaItem;
import model.DartItem;
import model.DartMeeting;
import model.Guest;
import model.Person;
import model.Status;

import org.springframework.stereotype.Service;

import type.StringUtils;
import type.TimeZone;
import type.UsefulDate;
import type.UsefulDateTime;
import dao.AgendaItemDao;
import dao.DartItemDao;
import dao.DartMeetingDao;
import dao.PersonDao;

@Service("run.action")
@Path("/run")
public class ActionItemService {

	public static final class DartItemResponseDTO {

		public GuestDTO[] guests;
		public ItemDTO[] items;

	}

	public static final class ItemDTO {

		public Long id;
		public String action;
		public String name;
		public String timingString;
		public String email;

		public ItemDTO(Long id, String action, String name, String email,
				UsefulDate timing) {
			this.id = id;
			this.action = action;
			this.name = name;
			this.email = email;
			this.timingString = timing.getSaasuFormat();
		}

	}

	@Resource
	private DartMeetingDao dartMeetingDao;

	@Resource
	private AgendaItemDao agendaItemDao;

	@Resource
	private DartItemDao dartItemDao;

	@Resource
	private PersonDao personDao;

	@GET
	@Path("dart/{meetingId}/{item}")
	@Produces("application/json")
	public DartItemResponseDTO listDartItems(
			@PathParam("meetingId") Long meetingId,
			@PathParam("item") int itemNo) {
		DartMeeting meeting = dartMeetingDao.load(meetingId);
		AgendaItem item = meeting.agendaItems.get(itemNo);
		return buildDartResponse(item);
	}

	@DELETE
	@Path("dart/{meetingId}/{item}/{actionId}")
	@Produces("application/json")
	public DartItemResponseDTO deleteDartItem(
			@PathParam("meetingId") Long meetingId,
			@PathParam("item") int itemNo, @PathParam("actionId") Long actionId) {
		dartItemDao.delete(actionId);
		dartItemDao.flush();
		DartMeeting meeting = dartMeetingDao.load(meetingId);
		AgendaItem item = meeting.agendaItems.get(itemNo);
		return buildDartResponse(item);
	}

	@POST
	@Path("dart")
	@Produces("application/json")
	public DartItemResponseDTO newDartItem(@FormParam("id") Long meetingId,
			@FormParam("item") int intemNo,
			@FormParam("discussionPoint") String discussion,
			@FormParam("newAction") String action,
			@FormParam("newResponsiblePersonId") Long personId,
			@FormParam("newTiming") String newTiming) {
		DartMeeting meeting = dartMeetingDao.load(meetingId);
		AgendaItem agendaItem = meeting.agendaItems.get(intemNo);
		agendaItem.newAction = null;
		agendaItem.discussionPoint = discussion;
		agendaItemDao.flush();

		if (!StringUtils.isEmpty(action)) {
			DartItem dartItem = new DartItem();
			dartItem.action = action;

			Person person = personDao.load(personId);
			dartItem.responsiblePerson = person;

			dartItem.timing = UsefulDate.createSaasuFormat(newTiming);

			dartItem.agendaItem = agendaItem;
			dartItem.status = Status.OPEN;

			dartItemDao.save(dartItem);
			dartItemDao.flush();
		}
		return buildDartResponse(agendaItem);
	}

	@POST
	@Path("dart/action")
	public String editAction(@FormParam("itemId") Long itemId,
			@FormParam("action") String action) {
		DartItem item = dartItemDao.load(itemId);
		item.action = action;
		dartItemDao.save(item);
		dartItemDao.flush();
		return item.action;
	}

	@PUT
	@Path("dart/due/{itemId}/{dueDate}")
	public void setActionDate(@PathParam("itemId") Long itemId,
			@PathParam("dueDate") String dueDate) {
		DartItem item = dartItemDao.load(itemId);
		item.timing = UsefulDate.createSaasuFormat(dueDate);
		dartItemDao.save(item);
		dartItemDao.flush();
	}

	private DartItemResponseDTO buildDartResponse(AgendaItem item) {
		DartItemResponseDTO result = new DartItemResponseDTO();
		List<ItemDTO> resultItems = new ArrayList<ActionItemService.ItemDTO>(
				item.dartItems.size());
		for (DartItem dartItem : item.dartItems) {
			ItemDTO dto = new ItemDTO(dartItem.getId(), dartItem.action,
					dartItem.responsiblePerson.name,
					dartItem.responsiblePerson.email, dartItem.timing);
			resultItems.add(dto);
		}
		List<GuestDTO> resultGuests = new ArrayList<GuestDTO>(
				item.dartMeeting.guests.size());
		for (Guest dbGuest : item.dartMeeting.guests) {
			GuestDTO guest = new GuestDTO();
			guest.setId(dbGuest.getId());
			guest.person = new Person();
			guest.person.setId(dbGuest.person.getId());
			guest.person.name = dbGuest.person.name;
			guest.person.email = dbGuest.person.email;
			resultGuests.add(guest);
		}
		result.guests = resultGuests.toArray(new GuestDTO[resultGuests.size()]);
		result.items = resultItems.toArray(new ItemDTO[resultItems.size()]);
		;
		return result;
	}
}