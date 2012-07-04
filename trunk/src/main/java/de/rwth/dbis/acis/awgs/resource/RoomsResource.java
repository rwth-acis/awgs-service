package de.rwth.dbis.acis.awgs.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jivesoftware.smack.XMPPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.dbis.acis.awgs.entity.RoomsAssociation;
import de.rwth.dbis.acis.awgs.module.realtime.RealtimeModule;
import de.rwth.dbis.acis.awgs.service.RoomsService;
import de.rwth.dbis.acis.awgs.util.Authentication;
import de.rwth.dbis.acis.awgs.util.CORS;

@Path("/rooms")
@Component
public class RoomsResource extends URIAwareResource{

	@Autowired
	RoomsService roomsService;

	@Autowired
	RealtimeModule realtimeModule;
	
	@GET
	@Produces("application/json")
	public Response getRooms() {

		List<RoomsAssociation> roomass = roomsService.getAll();
		Iterator<RoomsAssociation> roomassit = roomass.iterator();

		Vector<JSONObject> vRatings = new Vector<JSONObject>();	

		System.out.println("Rooms Associations:" + roomass.size());
		try {
			while(roomassit.hasNext()){
				RoomsAssociation m = roomassit.next();

				String itemResourceUri =  getEndpointUri() + "/" + m.getRoom();
				JSONObject rooma = new JSONObject();
				rooma.put("resource",itemResourceUri);
				rooma.put("user",m.getUser());
				rooma.put("room",m.getRoom());
				vRatings.add(rooma);
			}

			JSONObject j = new JSONObject();
			j.append("rooms",vRatings);
			Response.ResponseBuilder r = Response.ok(j);
			return CORS.makeCORS(r,_corsHeaders);
			
		} catch (JSONException e) {
			Response.ResponseBuilder res = Response.serverError();
			return CORS.makeCORS(res,_corsHeaders);
		}
	}
	
	@POST
	@Consumes("application/json")
	public Response addRoom(@HeaderParam("authorization") String auth, JSONObject o) throws JSONException {

		if(o == null || !(o.has("room") || !o.has("nick"))){
			Response.ResponseBuilder r = Response.status(Status.BAD_REQUEST);
			return CORS.makeCORS(r,_corsHeaders);
		}

		if(!realtimeModule.isAuthenticated(auth)){
			Response.ResponseBuilder r = Response.status(Status.UNAUTHORIZED);
			return CORS.makeCORS(r,_corsHeaders);
		}

		String[] authorize = Authentication.parseAuthHeader(auth);
		String jid = authorize[0];

		RoomsAssociation ra = new RoomsAssociation();
		
		ra.setRoom(StringEscapeUtils.escapeXml(o.getString("room")));
		ra.setNick(StringEscapeUtils.escapeXml(o.getString("nick")));
		ra.setUser(jid);

		if(roomsService.get(ra.getUser(),ra.getRoom()) == null) {
			roomsService.save(ra);

			RoomsAssociation r = roomsService.get(ra.getUser(),ra.getRoom());
			
			try {
				realtimeModule.joinRoom(ra.getRoom(), ra.getNick());
			} catch (XMPPException e1) {
				System.err.println("Could not join room " + ra.getRoom());
				e1.printStackTrace();
			}
			
			
			try {
				URI location;
				location = new URI(uriInfo.getAbsolutePath().toASCIIString() + "/" + r.getId());

				Response.ResponseBuilder res = Response.created(location);
				return CORS.makeCORS(res,_corsHeaders);

			} catch (URISyntaxException e) {
				Response.ResponseBuilder res = Response.serverError();
				return CORS.makeCORS(res,_corsHeaders);
			}
		}
		else{
			Response.ResponseBuilder res = Response.status(Status.CONFLICT);
			return CORS.makeCORS(res,_corsHeaders);
		}

	}
}