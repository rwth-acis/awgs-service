package de.rwth.dbis.acis.awgs.resource;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.rwth.dbis.acis.awgs.entity.AuthorsAssociation;
import de.rwth.dbis.acis.awgs.service.AuthorsService;

@Path("/users/{jid}/authorships")
@Component
public class UserAuthorshipsResource {

	@Context UriInfo uriInfo;

	@Autowired
	AuthorsService ratingService;

	@GET
	@Produces("application/json")
	public JSONObject getUserRatings(@PathParam("jid") String jid) {

		List<AuthorsAssociation> media = ratingService.getAuthorshipsForUser(jid);
		Iterator<AuthorsAssociation> usit = media.iterator();

		Vector<JSONObject> vRatings = new Vector<JSONObject>();	

		try {
			while(usit.hasNext()){
				AuthorsAssociation m = usit.next();

				String itemResourceUri = uriInfo.getBaseUri().toASCIIString() + "/items/" + m.getItem();
				JSONObject rating = new JSONObject();
				rating.append("item",itemResourceUri);
				rating.append("time", m.getTime().toString());
				vRatings.add(rating);
			}

			JSONObject j = new JSONObject();
			j.append("authorships",vRatings);
			return j;
		} catch (JSONException e) {
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
}