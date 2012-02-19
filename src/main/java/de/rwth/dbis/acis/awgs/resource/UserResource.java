package de.rwth.dbis.acis.awgs.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.dbis.acis.awgs.entity.User;
import de.rwth.dbis.acis.awgs.service.UserService;
import de.rwth.dbis.acis.awgs.util.Authentication;
import de.rwth.dbis.acis.awgs.util.CORS;

@Path("/users/{jid}")
@Component
public class UserResource {

	@Autowired
	UserService userService;
	
	private String _corsHeaders;

	@OPTIONS
	public Response corsResource(@HeaderParam("Access-Control-Request-Headers") String requestH) {
		_corsHeaders = requestH;
		return CORS.makeCORS(Response.ok(), requestH);
	}
	
	@GET
	@Produces("application/json")
	public Response getUser(@PathParam("jid") String jid, @HeaderParam("authorization") String auth) {
		
		User u = userService.getByJid(jid);
		
		if (u == null){
			Response.ResponseBuilder r = Response.status(Status.NOT_FOUND);
			return CORS.makeCORS(r,_corsHeaders);
		}
		
		// if authorization header is sent, then check, if authorization succeeds
		System.out.println("Authentication Header: " + auth);
		if(null != auth && "" != auth){
			if(!Authentication.authenticated(auth, u)){
				Response.ResponseBuilder r = Response.status(Status.UNAUTHORIZED);
				return CORS.makeCORS(r,_corsHeaders);
			}
		}
		
		try {
			JSONObject jo = new JSONObject();
			jo.put("login", u.getJid());
			jo.put("name", u.getName());
			jo.put("mail", u.getMail());
			
			Response.ResponseBuilder r = Response.ok(jo);
			return CORS.makeCORS(r,_corsHeaders);
			
		} catch (JSONException e) {
			Response.ResponseBuilder r = Response.serverError();
			return CORS.makeCORS(r,_corsHeaders);		
		}
	}
	
	@PUT
    @Consumes("application/json")
    public Response updateUser(@HeaderParam("authorization") String auth, @PathParam("login") String login, JSONObject o) throws JSONException {
        
		if(o == null){
			Response.ResponseBuilder r = Response.status(Status.NOT_FOUND);
			return CORS.makeCORS(r,_corsHeaders);
		}
		
		if(o.length() == 0){
			Response.ResponseBuilder r = Response.notModified();
			return CORS.makeCORS(r,_corsHeaders);
		}
		
		User u = userService.getByJid(login);
		
		if(u == null){
			Response.ResponseBuilder r = Response.status(Status.NOT_FOUND);
			return CORS.makeCORS(r,_corsHeaders);
		}
		
		if(!Authentication.authenticated(auth, u)){
			Response.ResponseBuilder r = Response.status(Status.UNAUTHORIZED);
			return CORS.makeCORS(r,_corsHeaders);
		}
		
		boolean changed = false;
		
        if (o.has("name") && !o.getString("name").equals(u.getName())){
        	u.setName(o.getString("name"));
			changed = true;
		}
        if (o.has("pass") && !o.getString("pass").equals(u.getPass())){
        	u.setPass(o.getString("pass"));
			changed = true;
		}
        
		if(changed){
			userService.update(u);
			
			Response.ResponseBuilder r = Response.ok();
			return CORS.makeCORS(r,_corsHeaders);
		} else {
			Response.ResponseBuilder r = Response.notModified();
			return CORS.makeCORS(r,_corsHeaders);
		}
    }
	
	@DELETE
	public Response deleteUser(@HeaderParam("authorization") String auth, @PathParam("jid") String jid){
		User u = userService.getByJid(jid);
		
		if(u == null){
			Response.ResponseBuilder r = Response.status(Status.NOT_FOUND);
			return CORS.makeCORS(r,_corsHeaders);
		}
		
		if(!Authentication.authenticated(auth, u)){
			Response.ResponseBuilder r = Response.status(Status.UNAUTHORIZED);
			return CORS.makeCORS(r,_corsHeaders);
		}
		
		userService.delete(u);
		
		Response.ResponseBuilder r = Response.ok();
		return CORS.makeCORS(r,_corsHeaders);
	}
	
	
}