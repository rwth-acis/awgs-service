package de.rwth.dbis.acis.awgs.resource;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.dbis.acis.awgs.module.realtime.RealtimeModule;
import de.rwth.dbis.acis.awgs.util.CORS;

@Path("/auth")
@Component
public class AuthenticateResource {

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
	public Response isAuthenticated(@HeaderParam("authorization") String auth) {
		if(!realtimeModule.isAuthenticated(auth)){
			Response.ResponseBuilder r = Response.status(Status.UNAUTHORIZED);
			return CORS.makeCORS(r,_corsHeaders);
		}
		else{
			Response.ResponseBuilder r = Response.ok();
			return CORS.makeCORS(r,_corsHeaders);
		}
	}

}