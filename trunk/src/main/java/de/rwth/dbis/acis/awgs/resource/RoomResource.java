package de.rwth.dbis.acis.awgs.resource;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.dbis.acis.awgs.entity.RoomsAssociation;
import de.rwth.dbis.acis.awgs.module.realtime.RealtimeModule;
import de.rwth.dbis.acis.awgs.service.RoomsService;
import de.rwth.dbis.acis.awgs.util.Authentication;
import de.rwth.dbis.acis.awgs.util.CORS;


@Path("/rooms/{jid}")
@Component
public class RoomResource {

	@Autowired
	RoomsService roomService;
	
	@Autowired
	RealtimeModule realtimeModule;

	private String _corsHeaders;

	@OPTIONS
	public Response corsResource(@HeaderParam("Access-Control-Request-Headers") String requestH) {
		_corsHeaders = requestH;
		return CORS.makeCORS(Response.ok(), requestH);
	}
	
	@GET
	@Produces("application/json")
	public Response getItem(@PathParam("jid") String jid) {
		
		RoomsAssociation i = roomService.getByRoom(jid);
		
		if (i == null){
			Response.ResponseBuilder r = Response.status(Status.NOT_FOUND);
			return CORS.makeCORS(r,_corsHeaders);
		}
		JSONObject j = new JSONObject();
		try {
			j.put("room", i.getRoom());
			j.put("user", i.getUser());
			j.put("id", i.getId());
			j.put("nick", i.getNick());
			
			Response.ResponseBuilder r = Response.ok(j);
			return CORS.makeCORS(r,_corsHeaders);
			
		} catch (JSONException e) {
			Response.ResponseBuilder r = Response.status(Status.INTERNAL_SERVER_ERROR);
			return CORS.makeCORS(r,_corsHeaders);
		}	
	}
	
	@DELETE
	public Response deleteItem(@HeaderParam("authorization") String auth, @PathParam("jid") String id){
		
		System.out.println(" ***************** DELETE Begin ");
		RoomsAssociation i = roomService.getByRoom(id);
		
		if(null == i){
			Response.ResponseBuilder r = Response.status(Status.NOT_FOUND);
			return CORS.makeCORS(r,_corsHeaders);
		}
		
		System.out.println(" ***************** DELETE Room exists ");
		
		if(!realtimeModule.isAuthenticated(auth)){
			Response.ResponseBuilder r = Response.status(Status.UNAUTHORIZED);
			return CORS.makeCORS(r,_corsHeaders);
		}
		
		String[] authorize = Authentication.parseAuthHeader(auth);
		String jid = authorize[0];
		
		if(!i.getUser().equals(jid)){
			Response.ResponseBuilder r = Response.status(Status.FORBIDDEN);
			return CORS.makeCORS(r,_corsHeaders);	
		}
		
		roomService.delete(i);
		
		Response.ResponseBuilder r = Response.ok();
		return CORS.makeCORS(r,_corsHeaders);
	}

}