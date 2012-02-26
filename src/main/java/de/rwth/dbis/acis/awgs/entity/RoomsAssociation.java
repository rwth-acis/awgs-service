package de.rwth.dbis.acis.awgs.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="ROOMS")
@NamedQueries({ 
	@NamedQuery(name = "RoomsAssociation.findAll", query = "SELECT a FROM RoomsAssociation a ORDER BY a.id"),
	@NamedQuery(name = "RoomsAssociation.findById", query = "SELECT a FROM RoomsAssociation a WHERE a.id=:id"),
	@NamedQuery(name = "RoomsAssociation.findByUser", query = "SELECT a FROM RoomsAssociation a WHERE a.user=:user ORDER BY a.room"),
	@NamedQuery(name = "RoomsAssociation.findByRoom", query = "SELECT a FROM RoomsAssociation a WHERE a.room=:room"),
	@NamedQuery(name = "RoomsAssociation.find", query = "SELECT a FROM RoomsAssociation a WHERE a.room=:room AND a.user=:user")
})
@XmlRootElement
public class RoomsAssociation {
	@Id
	@Column(name="ID")
	@GeneratedValue
	private int id;
	
	@Column(name="USER",nullable = false)
	private String user;

	@Column(name="ROOM", nullable = false)
	private String room;

	@Column(name="NICK", nullable = false)
	private String nick;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}
	
}
