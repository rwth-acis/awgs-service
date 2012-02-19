package de.rwth.dbis.ugnm.entity;

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
	@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u ORDER BY u.xp DESC"),
	@NamedQuery(name = "User.findUser", query = "SELECT u FROM User u where u.login=:login")
})
@XmlRootElement
public class User {
	
	@Id
	@Column(name = "LOGIN", nullable = false)
	private String login;
	
	@Column(name = "PASS", nullable = false)
	private String pass;
	
	@Column(name = "NAME", nullable = false)
	private String name;
	
	@Column(name = "XP", nullable = false)
	private int xp;

	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogin() {
		return login;
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
	
	public int getXp() {
		return xp;
	}

	public void setXp(int xp) {
		this.xp = xp;
	}
}
