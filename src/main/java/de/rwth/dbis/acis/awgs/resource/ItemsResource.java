package de.rwth.dbis.acis.awgs.resource;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
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

@Path("/items")
@Component
public class ItemsResource extends URIAwareResource{

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
	@Produces("text/html")
	public Response getItemsHTML(@HeaderParam("authorization") String auth, @QueryParam(value="q") String query) {
		try {
			List<Item> items;
			if(null == query || query.equals("")){
				items = itemService.getAll();
			}
			else {
				items = itemService.search(query);
			}
			
			StringTemplateGroup tg = new StringTemplateGroup(
					new FileReader(context.getRealPath("WEB-INF/template/items.stg")), DefaultTemplateLexer.class);
			
			StringTemplate t = tg.getInstanceOf("list");
			
			t.setAttribute("items", items);
			t.setAttribute("root",this.getRootUri().toASCIIString());
			t.setAttribute("epuri", this.getEndpointUri().toASCIIString());
			
			Response.ResponseBuilder r = Response.ok(t.toString());
			return CORS.makeCORS(r,_corsHeaders);
		} catch (FileNotFoundException e) {
			Response.ResponseBuilder r = Response.serverError();
			return CORS.makeCORS(r,_corsHeaders);
		}
	}
	
	@GET
	@Produces("application/json")
	public Response getItems(@QueryParam(value="q") String query) {
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
			
			//System.out.println("Passed URI: " + System.getProperty("service.uri"));
			
			while(itemit.hasNext()){
				Item i = itemit.next();
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

		if(o == null || !(o.has("name")) || !(o.has("description")) || !(o.has("url")) || !(o.has("type"))){
			Response.ResponseBuilder r = Response.status(Status.BAD_REQUEST);
			return CORS.makeCORS(r,_corsHeaders);
		}
		
		try {
			@SuppressWarnings("unused")
			URL u = new URL(o.getString("url"));
		} catch (MalformedURLException e1) {
			Response.ResponseBuilder r = Response.status(Status.BAD_REQUEST);
			return CORS.makeCORS(r,_corsHeaders);
		}
		
		ItemType itype = itemTypeService.get(o.getInt("type"));
		
		if(itype == null){
			Response.ResponseBuilder r = Response.status(Status.BAD_REQUEST);
			return CORS.makeCORS(r,_corsHeaders);
		}
		
		Item newItem = new Item();

		String newid = itemService.getNextItemId();

		newItem.setId(newid);
		
		String escapedName = StringEscapeUtils.escapeXml(o.getString("name"));
		String escapedDesc = StringEscapeUtils.escapeXml(o.getString("description"));
		newItem.setName(escapedName);
		newItem.setDescription(escapedDesc);
		
		if(!o.getString("name").equals(escapedName)){
			System.err.println("Warning: New item name had to be escaped! \nOriginal Item Name: " + o.getString("name"));
		}
		if(!o.getString("description").equals(escapedDesc)){
			System.err.println("Warning: New item description had to be escaped! \nOriginal Item Description: " + o.getString("description"));
		}
		
		newItem.setUrl(o.getString("url"));

		String jid = Authentication.parseAuthHeader(auth)[0];

		newItem.setOwner(jid);
		newItem.setType(itype.getId());
		newItem.setLastUpdate(new Date());

		if(itemService.getByUrl(newItem.getUrl()) == null) {
			itemService.save(newItem);

			URI location;
			try {
				location = new URI(getEndpointUri().toASCIIString() + "/" + newItem.getId());

				String msg = newItem.getOwner() + " registered AWGS ID " + newItem.getId() + ": " + newItem.getName() + " ("  + location + ").";
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

