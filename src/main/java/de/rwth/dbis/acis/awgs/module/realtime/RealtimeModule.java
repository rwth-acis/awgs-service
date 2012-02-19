package de.rwth.dbis.acis.awgs.module.realtime;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.MessageEventManager;
import org.jivesoftware.smackx.MessageEventRequestListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.packet.XHTMLExtension;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.NodeType;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubElementType;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.jivesoftware.smackx.pubsub.packet.SyncPacketSend;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;

import de.rwth.dbis.acis.awgs.entity.AuthorsAssociation;
import de.rwth.dbis.acis.awgs.entity.User;
import de.rwth.dbis.acis.awgs.service.AuthorsService;
import de.rwth.dbis.acis.awgs.service.UserService;


/**
 * @author Dominik Renzel (renzel@dbis.rwth-aachen.de)
 * 
 *         The Realtime module provides management support for various real-time
 *         features of spaces. The core of this module consists of a connection
 *         to an XMPP server hosting two services we make use of:
 *         <ul>
 *         <li>Multi-User Chat Rooms - space-centered chat rooms</li>
 *         <li>Publish-Subscribe Nodes - space-centered channels for
 *         broadcasting events</li>
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

	// XMPP connection and pubsub manager
	private Connection xc;
	private PubSubManager pubsub;

	// XMPP connection parameters
	private String xmppHost;
	private int xmppPort;
	private String xmppMucServiceSubdomainNode;
	private String xmppUser;
	private String xmppPass;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthorsService authorsService;

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
		//bindConstant().annotatedWith(Names.named("xmpp.host")).to(xmppHost);

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

		// Set XMPP Multiuser Chat Service subdomain
		// Use system property "xmpp.mucs", if available; else, use standard
		// value
		if (System.getProperty("xmpp.mucs") != null) {
			xmppMucServiceSubdomainNode = System.getProperty("xmpp.mucs");
		} else {
			xmppMucServiceSubdomainNode = DEFAULT_XMPP_MUC_SUBDOMAINPREF;
		}
		//bindConstant()Constant().annotatedWith(Names.named("xmpp.mucs")).to(xmppMucServiceSubdomainNode);

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
		System.out.println("Realtime Service going down");
		xc.sendPacket(new Presence(Presence.Type.unavailable));
		xc.disconnect();
	}

	public void testSendMessageToRoom(String jid, String msg) {
		// post a message into space chat room.
		// this case already includes the other use cases of
		// creating/configuring/getting/joining a room for a given space
		try {
			MultiUserChat muc = getChatRoom(jid);
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

		testSendMessageToRoom("awgs-test@muc.role.dbis.rwth-aachen.de", "Something I ever wanted to say...");
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

	/**
	 * Returns the subdomain node identifier of the XMPP Multi-User chat service
	 * used by this service.
	 * 
	 * @return String an XMPP service subdomain node identifier
	 */
	private String getXmppMucServiceSubdomainNode() {
		return xmppMucServiceSubdomainNode;
	}

	/**
	 * Returns the Jabber ID (JID) of the XMPP Multi-User chat (MUC) service
	 * used by this service.
	 * 
	 * @return String a MUC service JID
	 */
	private String getXmppMucServiceJid() {
		return getXmppMucServiceSubdomainNode() + "." + getXmppHost();
	}

	/**
	 * Computes a Jabber ID (JID) for a given user identifier.
	 * 
	 * @param sid
	 *            String a user identifier
	 * @return String a user JID
	 */
	public String getUserJid(String uid) {
		return uid + "@" + getXmppHost();
	}

	/**
	 * Computes a Jabber ID (JID) of a chat room for a given space identifier.
	 * 
	 * @param sid
	 *            String a space identifier
	 * @return String a chat room JID
	 */
	public String getSpaceRoomJid(String sid) {
		return "space-" + sid + "@" + getXmppMucServiceJid();
	}

	/**
	 * Computes the PubSub node identifier for a given space identifier.
	 * 
	 * @param sid
	 *            String a space identifier
	 * @return
	 */
	public static String getSpaceNodeIdentifier(String sid) {
		return "space-" + sid;
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
	public MultiUserChat getChatRoom(String jid) throws XMPPException {

		RoomInfo r = null;

		r = MultiUserChat.getRoomInfo(xc, jid);
		// if the respective room already exists, return a MUC object to interact.
		MultiUserChat muc = new MultiUserChat(xc, jid);
		return muc;

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
				response += "<ul><li>Erster</li><li>Zweiter</li></ul>";
				response += "@awgs-bot get user <username> - get information about registered user\n";
				response += "@awgs-bot get medium ratings <mediaid> - get rating information about registered medium\n";
			}
			else if(body.startsWith("@awgs-bot get user ")){
				String[] tokens = body.split("@awgs-bot get user ");
				System.out.println("Token 2: " + tokens[1]);
				if(tokens.length != 2){
					response = "Syntax error!<br/></br>";
					response += "Syntax: @awgs-bot get user <username>";
					response += "<br/> get information about a registered user";
				}
				else{
					User u = userService.getByJid(tokens[1]);
					try {
						JSONObject jo = new JSONObject();
						jo.put("jid", u.getMail());
						jo.put("name", u.getName());
						jo.put("mail", u.getMail());
						
						response = jo.toString(2);
						//response = u.getLogin();
						
					} catch (JSONException e) {
						response = "JSON error while retrieving user information";
					}
				}
			}
			else if(body.startsWith("@awgs-bot get item authors ")){
				String[] tokens = body.split("@awgs-bot get item authors ");
				System.out.println("Token 2: " + tokens[1]);
				if(tokens.length != 2){
					response = "Syntax error!<br/></br>";
					response += "Syntax: @awgs-bot get item authors <awgsid>";
					response += "<br/> get rating information about a registered item";
				}
				else{
					String itemid = tokens[1];
					List<AuthorsAssociation> rs = authorsService.getAuthorshipsForItem(itemid);
					
					Iterator<AuthorsAssociation> rit = rs.iterator();

					JSONObject j = new JSONObject();

					try {
						while(rit.hasNext()){
							AuthorsAssociation r = rit.next();

							
							JSONObject rating = new JSONObject();
							
							rating.put("id",r.getId());
							rating.put("item",r.getItemInstance().getUrl());
							rating.put("user",r.getUserInstance().getJid());
							rating.put("time", r.getTime().toString());
							
							j.accumulate("ratings", rating);
						}
						response = "Authors for item " + itemid;
						response += j.toString(2);
						System.out.println("Response: " +response);
						
					} catch (JSONException e) {
						response = "JSON error while retrieving media ratings";
					}
				}
			}
			else{
				response = "Send '@awgs-bot help' for a list of commands.";
			}

			String to = null;

			System.out.println("Type: P" + m.getType().toString() + "P");
			Message.Type type = m.getType();
			if(m.getType().equals(Message.Type.groupchat)){
				System.out.println("Realtime Service: detected MUC message");
				to = from.split("/")[0];
			}
			else{
				System.out.println("Realtime Service: detected IM message");
				to = from;
			}
			
			
				System.out.println("Realtime Service: sending reply to " + to);
				sendMessage(to, type, response);
				System.out.println("Realtime Service: sent reply to " + to);
			
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

}
