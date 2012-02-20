package de.rwth.dbis.acis.awgs.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.rwth.dbis.acis.awgs.entity.Item;
import de.rwth.dbis.acis.awgs.service.ItemService;


@Path("/items/{id}")
@Component
public class ItemResource {

	@Autowired
	ItemService itemService;

	@GET
	@Produces("application/json")
	public Item getUser(@PathParam("id") String id) {
		
		Item i = itemService.getById(id);
		
		if (i == null){
			throw new WebApplicationException(404);
		}
		return i;
	}

}