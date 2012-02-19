package de.rwth.dbis.ugnm;

import static org.junit.Assert.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

public class UsersResourceTest extends JerseyTest{
    
	@Autowired
	ApplicationContext context;
	
	/*
	 * Testkonstruktor; sollte für alle von Euch geschriebenen Testklassen gleich sein.
	 **/
    public UsersResourceTest() throws Exception {
		super(new WebAppDescriptor.Builder("de.rwth.dbis.ugnm")
        .contextPath("")
        .contextParam("contextConfigLocation", "classpath:applicationContext.xml")
        .servletClass(SpringServlet.class)
        .contextListenerClass(ContextLoaderListener.class)
        .build());
    }
    
    // ------------------- Test Methoden -------------------
    
    // hier haben wir Euch ein paar unfertige Beispiele angegeben, die aber das Testen Eurer Ressourcen
    // ganz gut demonstrieren. Das generelle Vorgehen ist:
    //
    //   - sende Request an Ressource
    //	 - empfange Response von Ressource
    //   - vergleiche Response gegen spezifiziertes Verhalten der Ressource
    //
    // Alle Testmethoden sind mit der Annotation @Test versehen.
    // Zusätzliche Hilfsmethoden sollten nicht mit @Test annotiert werden, können aber auch Asserts enthalten.
    // Bitte vergesst nicht, dass Euer Test vor und nach der Ausführung den gleichen Zustand in der Datenbank
    // hinterlassen sollte. Das ist bereits für das angegebene kleine Beispiel der Fall. Es wird ein User "mia"
    // erzeugt, und in einem weiteren Testfall wieder entfernt.
	
    
    @Test
	/*
	 * sendet einen GET Request an die Ressource /users. 
	 * 
	 * deckt folgende spezifizierte Fälle ab:
	 * 
	 *   - /users			GET		200	(Liste aller User erfolgreich geholt)
	 **/
	public void testGetSuccess() {
		// sende GET Request an Ressource /users und erhalte Antwort als Instanz der Klasse ClientResponse
		ClientResponse response = resource().path("users").accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		
		// teste, ob die gelieferten Daten den entsprechenden MIME Typ für JSON aufweisen.
        assertEquals(response.getType().toString(),MediaType.APPLICATION_JSON);
        
        // verarbeite die zurückgelieferten Daten als JSON Objekt.
        JSONObject o = response.getEntity(JSONObject.class);
        
        // teste, ob das gelieferte JSON Object ein Feld "users" besitzt.
        assertTrue(o.has("users"));
        
	}
    
    
	@Test
	/*
	 * führt zuerst für einen nicht existierenden User ein DELETE aus. Dies sollte mit 404 fehlschlagen. 
	 * Danach wird dieser User mit Post und unter Angabe aller nötigen Parameter auf die Collection Ressource angelegt. 
	 * Dies sollte erfolgreich sein. Danach wird der gleiche User wieder gelöscht.
	 * 
	 * deckt folgende spezifizierte Fälle ab:
	 * 
	 *   - /users/{login}	DELETE	404	(zu entfernender User existiert nicht)
	 *   - /users/			POST	201 (neuer User wurde erfolgreich angelegt)
	 *   - /users/{login}	DELETE	200 (bestehender User erfolgreich entfernt)	
	 **/
	public void testDeletePostDelete() {
		
		// ---------- Delete auf nicht existierenden User ------------
		WebResource r = resource(); 
		
		// auf diese Art und Weise kann man eine HTTP Basic Authentifizierung durchführen.
        r.addFilter(new HTTPBasicAuthFilter("mia", "aaaaa")); 
		
        // sende DELETE Request an nicht existierende Ressource /users/mia (sollte vor dem Test nicht existieren)
		ClientResponse response = r.path("users/mia").delete(ClientResponse.class);
		
		// teste, ob der spezifizierte HTTP Status 404 (Not Found) zurückgeliefert wurde. 
        assertEquals(response.getStatus(), Status.NOT_FOUND.getStatusCode());
	
        // ----------- Erfolgreiches Anlegen eines Users ---------------
        
		// gebe JSON Content als String an.
		String content = "{'name':'Mia','login':'mia','pass':'aaaaa'}";
		
		// sende POST Request inkl. validem Content und unter Angabe des MIME Type application/json an Ressource /users.
		ClientResponse response2 = resource().path("users").type(MediaType.APPLICATION_JSON).post(ClientResponse.class,content);
		
		// teste, ob der spezifizierte HTTP Status 201 (Created) zurückgeliefert wurde.
		assertEquals(response2.getStatus(), Status.CREATED.getStatusCode());
		
		WebResource r2 = resource(); 

        r2.addFilter(new HTTPBasicAuthFilter("mia", "aaaaa")); 
		
		ClientResponse response3 = r.path("users/mia").delete(ClientResponse.class);
        assertEquals(response3.getStatus(), Status.OK.getStatusCode());
	}
}