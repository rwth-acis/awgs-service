package de.rwth.dbis.acis.awgs.resource;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.rwth.dbis.acis.awgs.entity.Item;
import de.rwth.dbis.acis.awgs.module.realtime.RealtimeModule;
import de.rwth.dbis.acis.awgs.service.ItemService;
import de.rwth.dbis.acis.awgs.util.Authentication;
import de.rwth.dbis.acis.awgs.util.CORS;
import de.rwth.dbis.acis.awgs.util.HTMLHelper;


@Path("/items/{id}")
@Component
public class ItemResource extends URIAwareResource{

	@Autowired
	ItemService itemService;
	
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
		body += "<label for='url' style='font-weight:bold;'>Document URL:</label><div id='url'><a href='" + i.getUrl() + "'>" + i.getUrl() + "</a></div>";
		body += "<label for='status' style='font-weight:bold;'>Status:</label><div id='status'>" + i.getStatus()+ "</div>";
		body += "<label for='lastup' style='font-weight:bold;'>Last Update:</label><div id='lastup'>" + i.getLastUpdate().toGMTString() + "</div>";
		
		String html = HTMLHelper.getHtmlDoc(title,body);
		
		Response.ResponseBuilder r = Response.ok(html);
		return CORS.makeCORS(r,_corsHeaders);
		
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
		
		String msg = i.getOwner() + " deleted item '" + i.getName() + "' (" + i.getId() + ").";
		realtimeModule.broadcastToRooms(msg,null);
		
		Response.ResponseBuilder r = Response.ok();
		return CORS.makeCORS(r,_corsHeaders);
	}

}