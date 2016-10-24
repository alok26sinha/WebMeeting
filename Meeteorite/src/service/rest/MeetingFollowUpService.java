package service.rest;

import javax.annotation.Resource;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import model.DartItem;
import model.Status;
import model.Traction;

import org.springframework.stereotype.Service;

import dao.DartItemDao;
import dao.TractionDao;

@Service("meeting.followup")
@Path("/followup")
public class MeetingFollowUpService {
	
	@Resource
	private DartItemDao dartItemDao;
	
	@Resource
	private TractionDao tractionDao;
	
	@PUT
	@Path("dart/start/{dartItemId}")
	public String startProgressDart (@PathParam("dartItemId") Long dartItemId) {
		DartItem item = dartItemDao.load(dartItemId);
		item.status = Status.OPEN;
		return updateItemStatus(item);
	}

    @PUT
    @Path("shift/start/{shiftItemId}")
    public String startProgressShift (@PathParam("shiftItemId") Long shiftItemId) {
        Traction item = tractionDao.load(shiftItemId);
        item.status = Status.OPEN;
        return updateItemStatus(item);
    }

    @PUT
	@Path("dart/close/{dartItemId}")
	public String closeItemDart (@PathParam("dartItemId") Long dartItemId) {
		DartItem item = dartItemDao.load(dartItemId);
		item.status = Status.CLOSED;
		return updateItemStatus(item);
	}
	
    @PUT
    @Path("shift/close/{shiftItemId}")
    public String closeItemShift (@PathParam("shiftItemId") Long shiftItemId) {
        Traction item = tractionDao.load(shiftItemId);
        item.status = Status.CLOSED;
        return updateItemStatus(item);
    }
    
	@POST
	@Path("dart/item")
	public String changeCommentDart(@FormParam("itemId") Long dartItemIt, @FormParam("comment") String comment) {
	    DartItem item = dartItemDao.load(dartItemIt);
	    item.comment = comment;
	    dartItemDao.save(item);
	    dartItemDao.flush();
	    return comment;
	}

    @POST
    @Path("shift/item")
    public String changeCommentShift(@FormParam("itemId") Long dartItemIt, @FormParam("comment") String comment) {
        Traction item = tractionDao.load(dartItemIt);
        item.comments = comment;
        tractionDao.save(item);
        tractionDao.flush();
        return comment;
    }

	private String updateItemStatus(DartItem item) {
		dartItemDao.save(item);
		dartItemDao.flush();
		return item.getStatusString();
	}

    private String updateItemStatus(Traction item) {
        tractionDao.save(item);
        tractionDao.flush();
        return item.getStatusString();
    }

}
