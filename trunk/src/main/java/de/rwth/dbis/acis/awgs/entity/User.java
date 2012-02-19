package de.rwth.dbis.acis.awgs.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "USER")
@NamedQueries({ 
	@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u ORDER BY u.jid DESC"),
	@NamedQuery(name = "User.findUser", query = "SELECT u FROM User u where u.jid=:jid")
})
@XmlRootElement
public class User {
	
	@Id
	@Column(name = "JID", nullable = false)
	private String jid;
	
	@Column(name = "PASS", nullable = false)
	private String pass;
	
	@Column(name = "NAME", nullable = false)
	private String name;
	
	@Column(name = "MAIL", nullable = false)
	private String mail;

	public void setJid(String jid) {
		this.jid = jid;
	}

	public String getJid() {
		return jid;
	}
	
	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getPass() {
		return pass;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
	
}
