package de.rwth.dbis.ugnm.resource;

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

import de.rwth.dbis.ugnm.entity.RatesAssociation;
import de.rwth.dbis.ugnm.entity.User;
import de.rwth.dbis.ugnm.service.MediaService;
import de.rwth.dbis.ugnm.service.RatingService;
import de.rwth.dbis.ugnm.service.UserService;
import de.rwth.dbis.ugnm.util.Authentication;
import de.rwth.dbis.ugnm.util.CORS;

@Path("/media/{id}/ratings")
@Component
public class MediaRatingsResource {

	@Context UriInfo uriInfo;

	@Autowired
	RatingService ratingService;
	@Autowired
	UserService userService;
	@Autowired
	MediaService mediaService;

	private String _corsHeaders;

	@OPTIONS
	public Response corsResource(@HeaderParam("Access-Control-Request-Headers") String requestH) {
		_corsHeaders = requestH;
		return CORS.makeCORS(Response.ok(), requestH);
	}
	
	@GET
	@Produces("application/json")
	public Response getMediaRatings(@PathParam("id") int mediaid) {

		List<RatesAssociation> media = ratingService.getRatingsForMedium(mediaid);
		Iterator<RatesAssociation> usit = media.iterator();

		JSONObject j = new JSONObject();

		try {
			while(usit.hasNext()){
				RatesAssociation m = usit.next();

				String userUri = uriInfo.getBaseUri().toASCIIString() + "/users/" + m.getUser();
				String mediumUri = uriInfo.getBaseUri().toASCIIString() + "/media/" + m.getMedium();
				
				JSONObject rating = new JSONObject();
				
				rating.put("medium",mediumUri);
				rating.put("user",userUri);
				rating.put("rating", m.getRating());
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
    public Response rateMedium(@HeaderParam("authorization") String auth, @PathParam("id") int mediaid,  JSONObject o) throws JSONException {
        
		if(o == null || !(o.has("rating"))){
			Response.ResponseBuilder r = Response.status(Status.BAD_REQUEST);
			return CORS.makeCORS(r,_corsHeaders);
		}
		
		else{
			String[] authorize = Authentication.parseAuthHeader(auth);
			String login = authorize[0];
			String pass = authorize[1];
			
			User usr = userService.getByLogin(login);
			
			if(usr == null){
				Response.ResponseBuilder r = Response.status(Status.NOT_FOUND);
				return CORS.makeCORS(r,_corsHeaders);
			}
			
			if(!usr.getPass().equals(pass)){
				Response.ResponseBuilder r = Response.status(Status.UNAUTHORIZED);
				return CORS.makeCORS(r,_corsHeaders);
			}
			
        	RatesAssociation ra = new RatesAssociation();
        	ra.setMedium(mediaid);
        	ra.setUser(usr.getLogin());
        	ra.setRating(o.getInt("rating"));
        	
        	Date ctime = new Date();
        	ra.setTime(ctime);
        	
        	if(ratingService.get(usr.getLogin(),mediaid,ctime) == null) {
        		ratingService.save(ra);
        		
        		int xp = usr.getXp() + 25;
        		usr.setXp(xp);
        		
        		userService.update(usr);
        		
        		RatesAssociation r = ratingService.get(usr.getLogin(),mediaid,ctime);
        		
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
