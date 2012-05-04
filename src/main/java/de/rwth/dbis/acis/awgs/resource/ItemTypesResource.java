package de.rwth.dbis.acis.awgs.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.dbis.acis.awgs.entity.ItemType;
import de.rwth.dbis.acis.awgs.module.realtime.RealtimeModule;
import de.rwth.dbis.acis.awgs.service.ItemTypeService;
import de.rwth.dbis.acis.awgs.util.Authentication;
import de.rwth.dbis.acis.awgs.util.CORS;

@Path("/items/types")
@Component
public class ItemTypesResource extends URIAwareResource{
	
	@Autowired
	ItemTypeService itemTypeService;

	@Autowired
	RealtimeModule realtimeModule;

	@GET
	@Produces("application/json")
	public Response getItemTypes(@QueryParam(value="q") String query) {
		try {
			List<ItemType> itemTypes;
			if(null == query || query.equals("")){
				itemTypes = itemTypeService.getAll();
			}
			else {
				itemTypes = itemTypeService.search(query);
			}
			Iterator<ItemType> itemTypeIt = itemTypes.iterator();

			JSONObject jo = new JSONObject();
			
			System.out.println("Searched for query: " + query);
			System.out.println("Found results: " + itemTypes.size());
			
			//System.out.println("Passed URI: " + System.getProperty("service.uri"));
			
			while(itemTypeIt.hasNext()){
				ItemType i = itemTypeIt.next();
				JSONObject jom = new JSONObject();
				jom.put("resource", getEndpointUri() + "/" +  i.getId());
				jom.put("id", i.getId());
				jom.put("name",i.getName());
				jom.put("description", i.getDescription());
				jo.accumulate("itemtypes", jom);
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
	public Response putItemType(@HeaderParam("authorization") String auth, JSONObject o) throws JSONException {

		if(!realtimeModule.isAuthenticated(auth)){
			Response.ResponseBuilder r = Response.status(Status.UNAUTHORIZED);
			return CORS.makeCORS(r,_corsHeaders);
		}

		if(o == null || !(o.has("name")) || !(o.has("description"))){
			Response.ResponseBuilder r = Response.status(Status.BAD_REQUEST);
			return CORS.makeCORS(r,_corsHeaders);
		}
		
		ItemType newItemType = new ItemType();

		newItemType.setName(o.getString("name"));
		newItemType.setDescription(o.getString("description"));

		if(itemTypeService.getByName(newItemType.getName()) == null) {
			itemTypeService.save(newItemType);
			
			ItemType retItemType = itemTypeService.getByName(newItemType.getName());

			URI location;
			try {
				location = new URI(getEndpointUri().toASCIIString() + "/" + retItemType.getId());
				System.out.println("Created item type: " + location.toASCIIString());
				String jid = Authentication.parseAuthHeader(auth)[0];
				
				String msg = jid + " added new item type '" + retItemType.getName() + "' ("  + retItemType.getDescription() + ").";
				realtimeModule.broadcastToRooms(msg, null);
				
				JSONObject jom = new JSONObject();
				jom.put("resource", getEndpointUri().toASCIIString() + "/" +  retItemType.getId());
				jom.put("id", retItemType.getId());
				jom.put("name",retItemType.getName());
				jom.put("description", retItemType.getDescription());

				Response.ResponseBuilder r = Response.created(location).entity(jom);
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


