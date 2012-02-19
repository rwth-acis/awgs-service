package de.rwth.dbis.acis.awgs.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.rwth.dbis.acis.awgs.entity.AuthorsAssociation;
import de.rwth.dbis.acis.awgs.entity.User;
import de.rwth.dbis.acis.awgs.service.AuthorsService;
import de.rwth.dbis.acis.awgs.service.ItemService;
import de.rwth.dbis.acis.awgs.service.UserService;
import de.rwth.dbis.acis.awgs.util.Authentication;
import de.rwth.dbis.acis.awgs.util.CORS;

@Path("/media/{id}/ratings")
@Component
public class ItemAuthorshipsResource {

	@Context UriInfo uriInfo;

	@Autowired
	AuthorsService authorsService;
	@Autowired
	UserService userService;
	@Autowired
	ItemService mediaService;

	private String _corsHeaders;

	@OPTIONS
	public Response corsResource(@HeaderParam("Access-Control-Request-Headers") String requestH) {
		_corsHeaders = requestH;
		return CORS.makeCORS(Response.ok(), requestH);
	}
	
	@GET
	@Produces("application/json")
	public Response getMediaRatings(@PathParam("id") String itemid) {

		List<AuthorsAssociation> media = authorsService.getAuthorshipsForItem(itemid);
		Iterator<AuthorsAssociation> usit = media.iterator();

		JSONObject j = new JSONObject();

		try {
			while(usit.hasNext()){
				AuthorsAssociation m = usit.next();

				String userUri = uriInfo.getBaseUri().toASCIIString() + "/users/" + m.getUser();
				String mediumUri = uriInfo.getBaseUri().toASCIIString() + "/media/" + m.getItem();
				
				JSONObject rating = new JSONObject();
				
				rating.put("item",mediumUri);
				rating.put("user",userUri);
				rating.put("time", m.getTime().toString());
				
				j.accumulate("ratings", rating);
			}
			
			Response.ResponseBuilder r = Response.ok(j);
			return CORS.makeCORS(r,_corsHeaders);
			
		} catch (JSONException e) {
			Response.ResponseBuilder r = Response.serverError();
			return CORS.makeCORS(r,_corsHeaders);
		}
	}
	
	@POST
	@Consumes("application/json")
    public Response rateMedium(@HeaderParam("authorization") String auth, @PathParam("id") String itemid,  JSONObject o) throws JSONException {
        
		if(o == null || !(o.has("rating"))){
			Response.ResponseBuilder r = Response.status(Status.BAD_REQUEST);
			return CORS.makeCORS(r,_corsHeaders);
		}
		
		else{
			String[] authorize = Authentication.parseAuthHeader(auth);
			String jid = authorize[0];
			String pass = authorize[1];
			
			User usr = userService.getByJid(jid);
			
			if(usr == null){
				Response.ResponseBuilder r = Response.status(Status.NOT_FOUND);
				return CORS.makeCORS(r,_corsHeaders);
			}
			
			if(!usr.getPass().equals(pass)){
				Response.ResponseBuilder r = Response.status(Status.UNAUTHORIZED);
				return CORS.makeCORS(r,_corsHeaders);
			}
			
        	AuthorsAssociation ra = new AuthorsAssociation();
        	ra.setItem(itemid);
        	ra.setUser(usr.getJid());
        	
        	Date ctime = new Date();
        	ra.setTime(ctime);
        	
        	if(authorsService.get(usr.getJid(),itemid,ctime) == null) {
        		authorsService.save(ra);
        		
        		AuthorsAssociation r = authorsService.get(usr.getJid(),itemid,ctime);
        		
        		URI location;
				try {
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
}
