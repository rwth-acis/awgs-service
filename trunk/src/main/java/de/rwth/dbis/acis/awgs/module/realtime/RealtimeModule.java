package de.rwth.dbis.acis.awgs.module.realtime;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.springframework.beans.factory.annotation.Autowired;

import de.rwth.dbis.acis.awgs.entity.Item;
import de.rwth.dbis.acis.awgs.service.ItemService;
import de.rwth.dbis.acis.awgs.util.Authentication;


/**
 * @author Dominik Renzel (renzel@dbis.rwth-aachen.de)
 * 
 *         The Realtime module provides management support for various real-time
 *         features of spaces. The core of this module consists of a connection
 *         to an XMPP server hosting two services we make use of:
 *         <ul>
 *         <li>Multi-User Chat Rooms: A multi user chat bot enables the usage of the AWGS services via conversations in XMPP Multiuser chat rooms.</li>
 *         </ul>
 * 
 *         This implementation has been tested successfully with ejabberd (v2.1.8).
 */
public class RealtimeModule implements PacketListener {

	// default values for XMPP connection parameters, if none provided as system
	// properties
	public static final String DEFAULT_XMPP_HOST = ""; // Empty string implies that XMPP should not be used
	public static final int DEFAULT_XMPP_PORT = 5222;
	public static final String DEFAULT_XMPP_USER = "awgs-bot";
	public static final String DEFAULT_XMPP_PASS = "1234567890";
	public static final String DEFAULT_XMPP_MUC_SUBDOMAINPREF = "conference";

	// XMPP connection
	private Connection xc;

	// XMPP connection parameters
	private String xmppHost;
	private int xmppPort;
	private String xmppUser;
	private String xmppPass;
	
	@Autowired
	ItemService itemService;

	Map<String,MultiUserChat> mucs = new HashMap();

	public void configure() {

		System.out.println("Configuring Realtime Module");
		// Set XMPP server host name.
		// Use system property "xmpp.host", if available; else, use standard
		// value.
		if (System.getProperty("xmpp.host") != null) {
			xmppHost = System.getProperty("xmpp.host");
		} else {
			xmppHost = DEFAULT_XMPP_HOST;
		}

		// Set XMPP server port.
		// Use system property "xmpp.mucs", if available and can be parsed as
		// int; else, use standard value.
		if (System.getProperty("xmpp.port") != null) {
			try {
				xmppPort = Integer.parseInt(System.getProperty("xmpp.port"));
			} catch (NumberFormatException e) {
				xmppPort = DEFAULT_XMPP_PORT;
			}
		} else {
			xmppPort = DEFAULT_XMPP_PORT;
		}

		// Set XMPP user name used by the service.
		// Use system property "xmpp.user", if available; else, use standard
		// value.
		if (System.getProperty("xmpp.user") != null) {
			xmppUser = System.getProperty("xmpp.user");
		} else {
			xmppUser = DEFAULT_XMPP_USER;
		}

		// Set XMPP password used by the service.
		// Use system property "xmpp.pass", if available; else, use standard
		// value.
		if (System.getProperty("xmpp.pass") != null) {
			xmppPass = System.getProperty("xmpp.pass");
		} else {
			xmppPass = DEFAULT_XMPP_PASS;
		}

		// If we don't have a proper XMPP configuration, then it is time to quit now
		if (!isConfigured()) {
			return;
		}

		try {
			connect();
		} catch (XMPPException e) {
			System.err.println(e.getMessage());
		}
	}

	public boolean isConfigured() {
		// If no XMPP host was specified, then assume that there is no XMPP server
		// to integrate with
		return xmppHost.length() > 0;
	}

	public void destroy(){
		System.out.println("Realtime Module going down");
		xc.sendPacket(new Presence(Presence.Type.unavailable));
		xc.disconnect();
	}

	public void testSendMessageToRoom(String jid, String msg) {
		// post a message into space chat room.
		// this case already includes the other use cases of
		// creating/configuring/getting/joining a room for a given space
		try {
			MultiUserChat muc = getRoom(jid);
			muc.join("awgs-bot");
			System.out.println("Realtime Service: Entered room " + jid);
			muc.sendMessage(msg);

			//muc.addMessageListener(this);

			//muc.leave();
			//System.out.println("Realtime Service: Left room " + jid);

			//System.out.println("XMPP Info: Successfully sent message to room '" + jid + "'.");
		} catch (XMPPException e) {
			System.err.println("XMPP Error: Could not send message to room '" + jid + "': "
					+ e.getMessage());
		}
	}

	// ***************************************

	/**
	 * Connects and authenticates this service to its related XMPP server as
	 * part of the configuration process.
	 * 
	 * @throws XMPPException
	 *             in case connection or authentication fail.
	 */
	private void connect() throws XMPPException {

		// setup connection to XMPP server
		ConnectionConfiguration xcc = new ConnectionConfiguration(
				getXmppHost(), getXmppPort());
		xcc.setCompressionEnabled(true);
		xcc.setSASLAuthenticationEnabled(true);

		xc = new XMPPConnection(xcc);

		try {
			xc.connect();
			System.out.println("XMPP Info: Connected to XMPP host " + getXmppHost() + ":"
					+ getXmppPort());
		} catch (XMPPException e) {
			System.err.println("XMPP Error: Connection to XMPP host failed!");
			throw e;
		}

		// authenticate service at XMPP server
		try {
			xc.login(getXmppUser(), getXmppPass());
			System.out.println("XMPP Info: Authenticated service at XMPP host");

		} catch (XMPPException e) {
			System.err.println("XMPP Error: Authentication at XMPP host failed!");
			throw e;
		}

		PacketFilter filter = new PacketTypeFilter(Message.class);
		xc.addPacketListener(this,filter);


		//testSendMessageToRoom("awgs-test@muc.role.dbis.rwth-aachen.de", "Something I ever wanted to say...");
	}

	public String getUserVCard(String jid) throws XMPPException{
		VCard c = new VCard();
		c.load(xc,jid);
		return c.toString();
	}

	/**
	 * Returns the host name of the XMPP server to be used by this service.
	 * 
	 * @return String a host name
	 */
	private String getXmppHost() {
		return this.xmppHost;
	}

	/**
	 * Returns the port number of the XMPP server to be used by this service.
	 * 
	 * @return int a port number
	 */
	private int getXmppPort() {
		return this.xmppPort;
	}

	/**
	 * Returns the username for XMPP authentication used by this service.
	 * 
	 * @return String a username
	 */
	private String getXmppUser() {
		return xmppUser;
	}

	/**
	 * Returns the password for XMPP authentication used by this service.
	 * 
	 * @return String a password
	 */
	private String getXmppPass() {
		return xmppPass;
	}

	public void initRooms(){

	}

	/**
	 * Provides access to an XMPP Multi-User Chat room for a given space. If a
	 * respective room does not exist yet, it is automatically created with a
	 * standard configuration.
	 * 
	 * @param jid
	 *            String a space identifier
	 * @return MultiUserChat an object allowing interaction in space chat room
	 * @throws XMPPException
	 *             in case room creation failed or connection problems occurred
	 */
	public MultiUserChat getRoom(String jid) throws XMPPException {

		if(mucs.containsKey(jid)){
			return mucs.get(jid);
		}
		else{

			RoomInfo r = null;

			r = MultiUserChat.getRoomInfo(xc, jid);
			// if the respective room already exists, return a MUC object to interact.
			MultiUserChat muc = new MultiUserChat(xc, jid);
			mucs.put(jid, muc);
			return muc;
		}
	}

	@Override
	public void processPacket(Packet arg0) {
		//System.out.println(" Package Class: " + arg0.getClass().getCanonicalName());
		Message m = (Message) arg0;
		//System.out.println(m.getBody());
		String body = m.getBody();
		String from = m.getFrom();

		System.out.println("Realtime Service: message from " + m.getFrom());
		if(body.startsWith("@awgs-bot")){

			String response;

			if(body.trim().equals("@awgs-bot help")){
				response = "Command list:\n";
				response += "@awgs-bot get items - get list of registered items\n";
				response += "@awgs-bot get item <id> - get information about registered item\n";
			}
			else if(body.trim().equals("@awgs-bot get items")){
				
				List<Item> items = itemService.getAll();
				Iterator<Item>itemsit = items.iterator();
				response = "Found " + items.size() + " AWGS items:";
				while(itemsit.hasNext()){
					Item i = itemsit.next();
					response += "\n  - " + i.getId() + ": " + i.getName();
				}
			}
			else if(body.trim().startsWith("@awgs-bot get item ")){
				String[] tokens = body.trim().split("@awgs-bot get item ");
				if(tokens.length != 2){
					response = "Syntax error";
					response += "Command Syntax: @awgs-bot get item <id>";
				}
				else{
					String id = tokens[1];
					response = "AWGS Item " + id;
					Item i = itemService.getById(id);
					response += "\nName: " + i.getName();
					response += "\nDescription: " + i.getDescription();
					response += "\nOwner: " + i.getOwner();
					response += "\nDocument URL: " + i.getUrl();
					
					String status;
					
					if(i.getStatus() == 0){
						status = "draft";
					}
					else if(i.getStatus() == 1){
						status = "submitted";
					}
					else{
						status = "unknown";
					}
					
					response += "\nStatus: " + status;
				}
			}
			
			else{
				response = "Send '@awgs-bot help' for a list of commands.";
			}

			String to = null;
			Message.Type type = m.getType();
			
			if(m.getType().equals(Message.Type.groupchat)){
				//System.out.println("Realtime Service: detected MUC message");
				to = from.split("/")[0];
			}
			else{
				//System.out.println("Realtime Service: detected IM message");
				to = from;
			}

			//System.out.println("Realtime Service: sending reply to " + to);
			sendMessage(to, type, response);
			//System.out.println("Realtime Service: sent reply to " + to);

		}
	}

	public void sendMessage(String to, Message.Type t, String body){
		Message resp = new Message();
		resp.setTo(to);
		resp.setType(t);
		resp.setBody(body);

		/*
		XHTMLExtension e = new XHTMLExtension();
		String xbody = "<body xmlns='http://www.w3.org/1999/xhtml'>";
		xbody +="<a href='http://nillenposse.de'>Nillenposse</a>";
		xbody += body;
		xbody +="</body>";
		e.addBody(xbody);
		resp.addExtension(e);
		 */
		xc.sendPacket(resp);
	}

	public boolean isAuthenticated(String authHeader){

		Connection xc = null;

		try{
			String[] dauth = Authentication.parseAuthHeader(authHeader);

			String jid = dauth[0];
			String pass = dauth[1];

			String[] jidts = jid.split("@");
			String node = jidts[0];
			String domain = jidts[1];

			System.out.println("Authenticate XMPP - Node: " + node);
			System.out.println("Authenticate XMPP - Domain: " + domain);
			System.out.println("Authenticate XMPP - Pass: " + pass);

			ConnectionConfiguration xcc = new ConnectionConfiguration(domain, 5222);
			xcc.setCompressionEnabled(true);
			xcc.setSASLAuthenticationEnabled(true);

			xc = new XMPPConnection(xcc);
			xc.connect();
			System.out.println("XMPP Authentication Test: connected to XMPP host");

			xc.login(node, pass);
			System.out.println("XMPP Authentication Test: authenticated user at XMPP host");
			return true;
		}
		catch(IllegalArgumentException e){
			System.err.println(e.getMessage());
			return false;
		}
		catch(XMPPException e){
			System.err.println(e.getMessage());
			return false;
		}
		finally{
			if(xc != null && xc.isConnected()){
				xc.disconnect();
			}
		}
	}

}
