package de.rwth.dbis.acis.awgs.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.dbis.acis.awgs.service.ItemService;
import de.rwth.dbis.acis.awgs.util.CORS;

@Path("/items/next")
@Component
public class NextItemResource extends URIAwareResource{

	@Autowired
	ItemService itemService;

	@GET
	@Produces("application/json")
	public Response getNextId() {
		try {
			JSONObject jom = new JSONObject();
			jom.put("id", itemService.getNextItemId());
				
			Response.ResponseBuilder r = Response.ok(jom);
			return CORS.makeCORS(r,_corsHeaders);
		} catch (JSONException e) {
			Response.ResponseBuilder r = Response.serverError();
			return CORS.makeCORS(r,_corsHeaders);
		}
	}
}

