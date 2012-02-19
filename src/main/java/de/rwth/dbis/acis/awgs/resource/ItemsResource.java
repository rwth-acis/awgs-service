package de.rwth.dbis.acis.awgs.resource;

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

import de.rwth.dbis.acis.awgs.entity.Item;
import de.rwth.dbis.acis.awgs.service.ItemService;
import de.rwth.dbis.acis.awgs.util.CORS;

@Path("/items")
@Component
public class ItemsResource {

	@Autowired
	ItemService itemService;
	
	@Context UriInfo uriInfo;
	
	private String _corsHeaders;

	@OPTIONS
	public Response corsResource(@HeaderParam("Access-Control-Request-Headers") String requestH) {
		_corsHeaders = requestH;
		return CORS.makeCORS(Response.ok(), requestH);
	}
	
	@GET
	@Produces("application/json")
	public Response getItems() {
		
		List<Item> items = itemService.getAll();
		Iterator<Item> itemit = items.iterator();

		JSONObject jo = new JSONObject();

		try {
			while(itemit.hasNext()){
				Item i = itemit.next();
				JSONObject jom = new JSONObject();
				
				jom.put("id", i.getId());
				jom.put("name",i.getName());
				jom.put("description", i.getDescription());
				jom.put("url", i.getUrl());
				jom.put("status", i.getStatus());
				jo.accumulate("items", jom);
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
    public Response putItem(JSONObject o) throws JSONException {
        
		if(o == null || !(o.has("url"))){
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		else{
        	Item newItem = new Item();
        	String murl = (String) o.get("url");
        	newItem.setUrl(murl);
        	
        	if(o.has("description")){
        		newItem.setDescription((String) o.get("description"));
        	}
        	
        	if(itemService.getByUrl(murl) == null) {
        		itemService.save(newItem);
        		
        		Item mfdb = itemService.getByUrl(murl);
        		
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

