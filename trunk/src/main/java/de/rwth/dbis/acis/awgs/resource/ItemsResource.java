package de.rwth.dbis.ugnm.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.rwth.dbis.ugnm.entity.Medium;
import de.rwth.dbis.ugnm.entity.User;
import de.rwth.dbis.ugnm.service.MediaService;
import de.rwth.dbis.ugnm.util.CORS;

@Path("/media")
@Component
public class MediaResource {

	@Autowired
	MediaService mediaService;
	
	@Context UriInfo uriInfo;
	
	private String _corsHeaders;

	@OPTIONS
	public Response corsResource(@HeaderParam("Access-Control-Request-Headers") String requestH) {
		_corsHeaders = requestH;
		return CORS.makeCORS(Response.ok(), requestH);
	}
	
	@GET
	@Produces("application/json")
	public Response getMedia() {
		
		System.out.println("Media Resource: " + uriInfo.getAbsolutePath());
		
		List<Medium> media = mediaService.getAll();
		Iterator<Medium> medit = media.iterator();

		JSONObject jo = new JSONObject();

		try {
			while(medit.hasNext()){
				Medium m = medit.next();
				JSONObject jom = new JSONObject();
				
				String mUri = uriInfo.getAbsolutePath().toASCIIString() + "/" + m.getId();
				jom.put("resource", mUri);
				jom.put("id", m.getId());
				jom.put("description", m.getDescription());
				jom.put("url", m.getUrl());
				
				jo.accumulate("media", jom);
			}
		} catch (JSONException e) {
			Response.ResponseBuilder r = Response.serverError();
			return CORS.makeCORS(r,_corsHeaders);
		}

		Response.ResponseBuilder r = Response.ok(jo);
		return CORS.makeCORS(r,_corsHeaders);
		
	}
	
	@POST
    @Consumes("application/json")
    public Response putMedium(JSONObject o) throws JSONException {
        
		if(o == null || !(o.has("url"))){
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		else{
        	Medium newMedium = new Medium();
        	String murl = (String) o.get("url");
        	newMedium.setUrl(murl);
        	
        	if(o.has("description")){
        		newMedium.setDescription((String) o.get("description"));
        	}
        	
        	if(mediaService.getByUrl(murl) == null) {
        		mediaService.save(newMedium);
        		
        		Medium mfdb = mediaService.getByUrl(murl);
        		
        		URI location;
				try {
					location = new URI(uriInfo.getAbsolutePath().toASCIIString() + "/" + mfdb.getId());
					
					Response.ResponseBuilder r = Response.created(location);
					return CORS.makeCORS(r,_corsHeaders);
					
					
				} catch (URISyntaxException e) {
					throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
				}
        	}
        	else{
        		throw new WebApplicationException(Status.CONFLICT);
        	}
        }
    }
	
}

