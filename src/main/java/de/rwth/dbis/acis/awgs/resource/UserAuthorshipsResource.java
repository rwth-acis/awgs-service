package de.rwth.dbis.ugnm.resource;

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

import de.rwth.dbis.ugnm.entity.RatesAssociation;
import de.rwth.dbis.ugnm.service.RatingService;

@Path("/users/{login}/ratings")
@Component
public class UserRatingsResource {

	@Context UriInfo uriInfo;

	@Autowired
	RatingService ratingService;

	@GET
	@Produces("application/json")
	public JSONObject getUserRatings(@PathParam("login") String login) {

		List<RatesAssociation> media = ratingService.getRatingsForUser(login);
		Iterator<RatesAssociation> usit = media.iterator();

		Vector<JSONObject> vRatings = new Vector<JSONObject>();	

		try {
			while(usit.hasNext()){
				RatesAssociation m = usit.next();

				String mediaResourceUri = uriInfo.getBaseUri().toASCIIString() + "/media/" + m.getMedium();
				JSONObject rating = new JSONObject();
				rating.append("medium",mediaResourceUri);
				rating.append("rating", m.getRating());
				rating.append("time", m.getTime().toString());
				vRatings.add(rating);
			}

			JSONObject j = new JSONObject();
			j.append("ratings",vRatings);
			return j;
		} catch (JSONException e) {
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
}