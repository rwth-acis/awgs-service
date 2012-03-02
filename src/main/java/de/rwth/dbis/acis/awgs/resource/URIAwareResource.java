package de.rwth.dbis.acis.awgs.resource;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.rwth.dbis.acis.awgs.util.CORS;

public class URIAwareResource {
	@Context UriInfo uriInfo;
	
	protected String _corsHeaders;

	@OPTIONS
	public Response corsResource(@HeaderParam("Access-Control-Request-Headers") String requestH) {
		_corsHeaders = requestH;
		return CORS.makeCORS(Response.ok(), requestH);
	}
	
	public URI getEndpointUri(){
		if(System.getProperty("service.uri") != null){
			String resourcePath = uriInfo.getAbsolutePath().toASCIIString().split("/resources/")[1];
			String returnUri = System.getProperty("service.uri") + "/resources/" + resourcePath;
			try {
				return new URI(returnUri);
			} catch (URISyntaxException e) {
				System.err.println("Value " + returnUri + " for parameter service.uri is not a valid URI. Falling back to original URI.");
				return uriInfo.getAbsolutePath();
			}
		}
		else {
			return uriInfo.getAbsolutePath();
		}
	}
}
