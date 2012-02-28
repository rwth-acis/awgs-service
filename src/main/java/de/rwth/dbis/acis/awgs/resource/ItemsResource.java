package de.rwth.dbis.acis.awgs.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.dbis.acis.awgs.entity.Item;
import de.rwth.dbis.acis.awgs.module.realtime.RealtimeModule;
import de.rwth.dbis.acis.awgs.service.ItemService;
import de.rwth.dbis.acis.awgs.util.Authentication;
import de.rwth.dbis.acis.awgs.util.CORS;



@Path("/items")
@Component
public class ItemsResource {

	@Autowired
	ItemService itemService;

	@Autowired
	RealtimeModule realtimeModule;

	@Context UriInfo uriInfo;

	private String _corsHeaders;

	@OPTIONS
	public Response corsResource(@HeaderParam("Access-Control-Request-Headers") String requestH) {
		_corsHeaders = requestH;
		return CORS.makeCORS(Response.ok(), requestH);
	}

	@GET
	@Produces("application/json")
	public Response getItems(@QueryParam(value="search") String query) {
		try {
			List<Item> items;
			if(null == query || query.equals("")){
				items = itemService.getAll();
			}
			else {
				items = itemService.search(query);
			}
			Iterator<Item> itemit = items.iterator();

			JSONObject jo = new JSONObject();

			while(itemit.hasNext()){
				Item i = itemit.next();
				JSONObject jom = new JSONObject();
				
				jom.put("resource", uriInfo.getAbsolutePath() + i.getId());
				jom.put("id", i.getId());
				jom.put("name",i.getName());
				jom.put("description", i.getDescription());
				jom.put("url", i.getUrl());
				jom.put("status", i.getStatus());
				jom.put("owner" , i.getOwner());
				jom.put("lastupdate",i.getLastUpdate().toGMTString());
				jo.accumulate("items", jom);
			}
			
			Response.ResponseBuilder r = Response.ok(jo);
			return CORS.makeCORS(r,_corsHeaders);
		} catch (JSONException e) {
			Response.ResponseBuilder r = Response.serverError();
			return CORS.makeCORS(r,_corsHeaders);
		}
	}

	@POST
	@Consumes("application/json")
	public Response putItem(@HeaderParam("authorization") String auth, JSONObject o) throws JSONException {

		if(!realtimeModule.isAuthenticated(auth)){
			Response.ResponseBuilder r = Response.status(Status.UNAUTHORIZED);
			return CORS.makeCORS(r,_corsHeaders);
		}

		if(o == null || !(o.has("name")) || !(o.has("description")) || !(o.has("url")) || !(o.has("status"))){
			Response.ResponseBuilder r = Response.status(Status.BAD_REQUEST);
			return CORS.makeCORS(r,_corsHeaders);
		}

		Item newItem = new Item();

		String newid = itemService.getNextItemId();

		newItem.setId(newid);
		newItem.setName(o.getString("name"));
		newItem.setDescription(o.getString("description"));
		newItem.setUrl(o.getString("url"));

		String[] authorize = Authentication.parseAuthHeader(auth);
		String jid = authorize[0];

		newItem.setOwner(jid);
		newItem.setStatus(o.getInt("status"));
		newItem.setLastUpdate(new Date());

		if(itemService.getByUrl(newItem.getUrl()) == null) {
			itemService.save(newItem);



			URI location;
			try {
				location = new URI(uriInfo.getAbsolutePath().toASCIIString() + "/" + newItem.getId());

				String msg = "A new AWGS item was added: " + location;
				realtimeModule.broadcastToRooms(msg, null);

				Response.ResponseBuilder r = Response.created(location);
				return CORS.makeCORS(r,_corsHeaders);


			} catch (URISyntaxException e) {
				Response.ResponseBuilder r = Response.serverError();
				return CORS.makeCORS(r,_corsHeaders);
			}
		}
		else{
			Response.ResponseBuilder r = Response.status(Status.CONFLICT);
			return CORS.makeCORS(r,_corsHeaders);
		}

	}

}

