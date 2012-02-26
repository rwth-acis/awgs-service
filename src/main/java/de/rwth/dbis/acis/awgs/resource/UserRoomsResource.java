package de.rwth.dbis.acis.awgs.resource;


import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.dbis.acis.awgs.entity.RoomsAssociation;
import de.rwth.dbis.acis.awgs.module.realtime.RealtimeModule;
import de.rwth.dbis.acis.awgs.service.RoomsService;
import de.rwth.dbis.acis.awgs.util.CORS;

@Path("/users/{jid}/rooms")
@Component
public class UserRoomsResource {

	@Context UriInfo uriInfo;

	@Autowired
	RoomsService roomsService;

	@Autowired
	RealtimeModule realtimeModule;

	private String _corsHeaders;

	@OPTIONS
	public Response corsResource(@HeaderParam("Access-Control-Request-Headers") String requestH) {
		this._corsHeaders = requestH;
		return CORS.makeCORS(Response.ok(), requestH);
	}

	@GET
	@Produces("application/json")
	public JSONObject getUserRooms(@PathParam("jid") String jid) {

		List<RoomsAssociation> roomass = roomsService.getRoomsForUser(jid);
		Iterator<RoomsAssociation> roomassit = roomass.iterator();

		Vector<JSONObject> vRatings = new Vector<JSONObject>();	

		System.out.println("Rooms Associations:" + roomass.size());
		try {
			while(roomassit.hasNext()){
				RoomsAssociation m = roomassit.next();

				String itemResourceUri = uriInfo.getBaseUri().toASCIIString() + "/rooms/" + m.getId();
				JSONObject rooma = new JSONObject();
				rooma.put("resource",itemResourceUri);
				rooma.put("user",m.getUser());
				rooma.put("room",m.getRoom());
				vRatings.add(rooma);
			}

			JSONObject j = new JSONObject();
			j.append("rooms",vRatings);
			return j;
		} catch (JSONException e) {
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
}
