package de.rwth.dbis.acis.awgs.resource;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.dbis.acis.awgs.entity.Item;
import de.rwth.dbis.acis.awgs.entity.ItemType;
import de.rwth.dbis.acis.awgs.module.realtime.RealtimeModule;
import de.rwth.dbis.acis.awgs.service.ItemService;
import de.rwth.dbis.acis.awgs.service.ItemTypeService;
import de.rwth.dbis.acis.awgs.util.Authentication;
import de.rwth.dbis.acis.awgs.util.CORS;
import de.rwth.dbis.acis.awgs.util.HTMLHelper;


@Path("/items/{id}")
@Component
public class ItemResource extends URIAwareResource{

	@Autowired
	ItemService itemService;

	@Autowired
	ItemTypeService itemTypeService;

	@Autowired
	RealtimeModule realtimeModule;

	@GET
	@Produces("application/json")
	public Response getItem(@PathParam("id") String id) {

		Item i = itemService.getById(id);

		if (i == null){
			Response.ResponseBuilder r = Response.status(Status.NOT_FOUND);
			return CORS.makeCORS(r,_corsHeaders);
		}
		Response.ResponseBuilder r = Response.ok(i);
		return CORS.makeCORS(r,_corsHeaders);
	}

	@GET
	@Produces("text/html")
	public Response getItemHtml(@PathParam("id") String id) {

		Item i = itemService.getById(id);
		if (i == null){
			Response.ResponseBuilder r = Response.status(Status.NOT_FOUND);
			return CORS.makeCORS(r,_corsHeaders);
		}

		String title = "ACIS Working Group Series " + id;
		String body = "<h1>ACIS Working Group Series " + id + "</h1>";

		body += "<label for='name' style='font-weight:bold;'>Name:</label><div id='name'>" + i.getName() + "</div>";
		body += "<label for='owner' style='font-weight:bold;'>Owner:</label><div id='owner'><a href='xmpp:" + i.getOwner() + "'>" + i.getOwner() + "</a></div>";
		body += "<label for='description' style='font-weight:bold;'>Description:</label><div id='description'>" + i.getDescription() + "</div>";
		body += "<label for='url' style='font-weight:bold;'>URL:</label><div id='url'><a href='" + i.getUrl() + "'>" + i.getUrl() + "</a></div>";
		body += "<label for='status' style='font-weight:bold;'>Type:</label><div id='type'>" + i.getTypeInstance().getName() + "</div>";
		body += "<label for='lastup' style='font-weight:bold;'>Last Update:</label><div id='lastup'>" + i.getLastUpdate().toGMTString() + "</div>";

		String html = HTMLHelper.getHtmlDoc(title,body);

		Response.ResponseBuilder r = Response.ok(html).header("Content-Type","text/html; charset=utf-8");
		return CORS.makeCORS(r,_corsHeaders);

	}

	@PUT
	public Response updateItem(@HeaderParam("authorization") String auth, @PathParam("id") String id, JSONObject o) throws JSONException {


		if(o == null || o.length() == 0){
			Response.ResponseBuilder r = Response.status(Status.NOT_MODIFIED);
			return CORS.makeCORS(r,_corsHeaders);
		}

		Item i = itemService.getById(id);

		if(null == i){
			Response.ResponseBuilder r = Response.status(Status.NOT_FOUND);
			return CORS.makeCORS(r,_corsHeaders);
		}

		if(!realtimeModule.isAuthenticated(auth)){
			Response.ResponseBuilder r = Response.status(Status.UNAUTHORIZED);
			return CORS.makeCORS(r,_corsHeaders);
		}

		String[] authorize = Authentication.parseAuthHeader(auth);
		String jid = authorize[0];

		if(!i.getOwner().equals(jid)){
			Response.ResponseBuilder r = Response.status(Status.FORBIDDEN);
			return CORS.makeCORS(r,_corsHeaders);	
		}

		// from here on we can accept the update to happen.

		boolean modified = false;

		if(o.has("name")){
			i.setName(o.getString("name"));
			modified = true;
		}

		if(o.has("description")){
			i.setDescription(o.getString("description"));
			modified = true;
		}

		if(o.has("url")){
			System.out.println("Found URL: " + o.getString("url"));
			try {
				URL u = new URL(o.getString("url"));
				i.setUrl(o.getString("url"));
				modified = true;
			} catch (MalformedURLException e1) {
				Response.ResponseBuilder r = Response.status(Status.BAD_REQUEST);
				return CORS.makeCORS(r,_corsHeaders);
			}
		}

		if(o.has("type")){
			ItemType itype = itemTypeService.get(o.getInt("type"));

			if(itype == null){
				Response.ResponseBuilder r = Response.status(Status.BAD_REQUEST);
				return CORS.makeCORS(r,_corsHeaders);
			}
			i.setType(itype.getId());
			modified = true;
		}

		if(modified){
			i.setLastUpdate(new Date());
			itemService.update(i);

			
			try {
				URI location = new URI(getEndpointUri().toASCIIString() + "/" + i.getId());
				String msg = i.getOwner() + " updated " + i.getId() + ": " + i.getName() + " ("  + location + ").";

				realtimeModule.broadcastToRooms(msg,null);

				Response.ResponseBuilder r = Response.ok();
				return CORS.makeCORS(r,_corsHeaders);

			} catch (URISyntaxException e) {
				Response.ResponseBuilder r = Response.status(Status.INTERNAL_SERVER_ERROR);
				return CORS.makeCORS(r,_corsHeaders);
			}


		} else {
			Response.ResponseBuilder r = Response.status(Status.NOT_MODIFIED);
			return CORS.makeCORS(r,_corsHeaders);
		}


	}

	@DELETE
	public Response deleteItem(@HeaderParam("authorization") String auth, @PathParam("id") String id){

		Item i = itemService.getById(id);

		if(null == i){
			Response.ResponseBuilder r = Response.status(Status.NOT_FOUND);
			return CORS.makeCORS(r,_corsHeaders);
		}

		if(!realtimeModule.isAuthenticated(auth)){
			Response.ResponseBuilder r = Response.status(Status.UNAUTHORIZED);
			return CORS.makeCORS(r,_corsHeaders);
		}

		String[] authorize = Authentication.parseAuthHeader(auth);
		String jid = authorize[0];

		if(!i.getOwner().equals(jid)){
			Response.ResponseBuilder r = Response.status(Status.FORBIDDEN);
			return CORS.makeCORS(r,_corsHeaders);	
		}

		itemService.delete(i);

		String msg = i.getOwner() + " deleted " + i.getId() + ": " + i.getName() + ".";
		realtimeModule.broadcastToRooms(msg,null);

		Response.ResponseBuilder r = Response.ok();
		return CORS.makeCORS(r,_corsHeaders);

	}

}