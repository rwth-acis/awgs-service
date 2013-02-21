package de.rwth.dbis.acis.awgs.resource;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.apache.commons.lang3.StringEscapeUtils;
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


@Path("/items/{id}")
@Component
public class ItemResource extends URIAwareResource{

	@Context
	ServletContext context;
	
	@Context
	UriInfo info;
	
	@Autowired
	ItemService itemService;

	@Autowired
	ItemTypeService itemTypeService;

	@Autowired
	RealtimeModule realtimeModule;

	@GET
	@Produces("application/json")
	public Response getItem(@PathParam("id") String id) throws JSONException {
		
		Item i = itemService.getById(id);
		if (i == null){
			Response.ResponseBuilder r = Response.status(Status.NOT_FOUND);
			return CORS.makeCORS(r,_corsHeaders);
		}
		
		ItemType it = i.getTypeInstance();
		
		JSONObject tjom = new JSONObject();
		tjom.put("id", it.getId());
		tjom.put("name", it.getName());
		tjom.put("description",it.getDescription());
		
		JSONObject jom = new JSONObject();
		jom.put("resource", getEndpointUri() + "/" +  i.getId());
		jom.put("id", i.getId());
		jom.put("name",i.getName());
		jom.put("description", i.getDescription());
		jom.put("url", i.getUrl());
		jom.put("type", tjom);
		jom.put("owner" , i.getOwner());
		jom.put("lastupdate",i.getLastUpdate());
		
		Response.ResponseBuilder r = Response.ok(jom);
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
		
		StringTemplateGroup tg = new StringTemplateGroup("html",context.getRealPath("WEB-INF/template"));
		StringTemplate t = tg.getInstanceOf("item");
		
		String rootUri = "http://" + info.getBaseUri().getHost();
		if(info.getBaseUri().getPort()>0){
			rootUri += ":" + info.getBaseUri().getPort();
		}
		
		t.setAttribute("root", rootUri);
		t.setAttribute("i", i);
		/*
		t.setAttribute("item_title",i.getName());
		t.setAttribute("item_description", i.getDescription());
		t.setAttribute("item_url", i.getUrl());
		t.setAttribute("item_owner", i.getOwner());
		t.setAttribute("item_lastupdate", i.getLastUpdate()); 
		*/
		t.setAttribute("item_type",i.getTypeInstance().getName());
		
		Response.ResponseBuilder r = Response.ok(t.toString()).header("Content-Type","text/html; charset=utf-8");
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
			String escapedName = StringEscapeUtils.escapeXml(o.getString("name"));
			if(!o.getString("name").equals(escapedName)){
				System.err.println("Warning: New item name had to be escaped! \nOriginal Item Name: " + o.getString("name"));
			}
			i.setName(StringEscapeUtils.escapeXml(o.getString("name")));
			modified = true;
		}

		if(o.has("description")){
			String escapedDesc = StringEscapeUtils.escapeXml(o.getString("description"));
			if(!o.getString("description").equals(escapedDesc)){
				System.err.println("Warning: New item description had to be escaped! \nOriginal Item Description: " + o.getString("description"));
			}
			i.setDescription(escapedDesc);
			modified = true;
		}

		if(o.has("url")){
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
				URI location = new URI(getEndpointUri().toASCIIString());
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