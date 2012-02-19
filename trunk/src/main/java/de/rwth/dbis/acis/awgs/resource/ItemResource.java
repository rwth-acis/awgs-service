package de.rwth.dbis.ugnm.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.rwth.dbis.ugnm.entity.Medium;
import de.rwth.dbis.ugnm.service.MediaService;

@Path("/media/{id}")
@Component
public class MediumResource {

	@Autowired
	MediaService mediaService;

	@GET
	@Produces("application/json")
	public Medium getUser(@PathParam("id") int id) {
		
		Medium u = mediaService.getById(id);
		
		if (u == null){
			throw new WebApplicationException(404);
		}
		
		return u;
	}

}