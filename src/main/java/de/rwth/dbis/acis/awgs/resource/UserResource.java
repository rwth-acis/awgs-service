package de.rwth.dbis.acis.awgs.resource;

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
import org.jivesoftware.smack.XMPPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.dbis.acis.awgs.module.realtime.RealtimeModule;
import de.rwth.dbis.acis.awgs.util.CORS;

@Path("/users/{jid}")
@Component
public class UserResource {

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
	public Response getUser(@PathParam("jid") String jid) {
		
		try{
			String vcard = realtimeModule.getUserVCard(jid);
			Response.ResponseBuilder r = Response.ok(vcard);
			JSONObject o = new JSONObject();
			o.put("vcard", vcard);
			return CORS.makeCORS(r,_corsHeaders);
		} 
		catch(XMPPException e){
			if(e.getXMPPError().getCode() == 404){
				Response.ResponseBuilder r = Response.status(Status.NOT_FOUND);
				return CORS.makeCORS(r,_corsHeaders);
			}
			else{
				Response.ResponseBuilder r = Response.status(Status.INTERNAL_SERVER_ERROR);
				return CORS.makeCORS(r,_corsHeaders);
			}
		}
		catch(JSONException e){
			Response.ResponseBuilder r = Response.status(Status.INTERNAL_SERVER_ERROR);
			return CORS.makeCORS(r,_corsHeaders);
		}
	}

}