package de.rwth.dbis.ugnm.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

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
import org.springframework.stereotype.Component;

import de.rwth.dbis.ugnm.entity.User;
import de.rwth.dbis.ugnm.service.UserService;
import de.rwth.dbis.ugnm.util.CORS;

@Path("/users")
@Component
public class UsersResource {

	@Autowired
	UserService userService;

	@Context UriInfo uriInfo;

	private String _corsHeaders;

	@OPTIONS
	public Response corsResource(@HeaderParam("Access-Control-Request-Headers") String requestH) {
		_corsHeaders = requestH;
		return CORS.makeCORS(Response.ok(), requestH);
	}

	@GET
	@Produces("application/json")
	public Response getUsers() {
		List<User> users = userService.getAll();
		Iterator<User> usit = users.iterator();

		JSONObject jo = new JSONObject();

		try {
			while(usit.hasNext()){
				User u = usit.next();
				JSONObject jou = new JSONObject();
				jou.put("login", u.getLogin());
				jou.put("name", u.getName());
				jou.put("xp", u.getXp());
				String uUri = uriInfo.getAbsolutePath().toASCIIString() + "/" + u.getLogin();
				jou.put("resource", uUri);
				jo.accumulate("users", jou);
			}
		} catch (JSONException e) {
			Response.ResponseBuilder r = Response.serverError();
			return CORS.makeCORS(r, _corsHeaders);
		}

		Response.ResponseBuilder r = Response.ok(jo);
		return CORS.makeCORS(r,_corsHeaders);
	}

	@POST
	@Consumes("application/json")
	public Response putUser(JSONObject o) throws JSONException {

		if(o == null || !(o.has("login") && o.has("name") && o.has("pass"))){
			throw new WebApplicationException(Status.BAD_REQUEST);
		}
		else{
			User nu = new User();
			nu.setLogin((String) o.get("login"));
			nu.setPass((String) o.get("pass"));
			nu.setName((String) o.get("name"));
			nu.setXp(0);

			if(userService.findUser(nu) == null) {
				userService.save(nu);
				URI location;
				try {
					location = new URI(uriInfo.getAbsolutePath().toASCIIString() + "/" + o.get("login"));
					
					Response.ResponseBuilder r = Response.created(location);
					return CORS.makeCORS(r, _corsHeaders);
					
				} catch (URISyntaxException e) {
					Response.ResponseBuilder r = Response.status(Status.INTERNAL_SERVER_ERROR);
					return CORS.makeCORS(r, _corsHeaders);
				}
			}
			else{
				Response.ResponseBuilder r = Response.status(Status.CONFLICT);
				return CORS.makeCORS(r, _corsHeaders);
			}
		}
	}

}

