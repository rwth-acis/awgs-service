package de.rwth.dbis.acis.awgs.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="AUTHORS")
@NamedQueries({ 
	@NamedQuery(name = "AuthorsAssociation.findAll", query = "SELECT a FROM AuthorsAssociation r ORDER BY a.time"),
	@NamedQuery(name = "AuthorsAssociation.findById", query = "SELECT a FROM AuthorsAssociation r WHERE a.id=:id"),
	@NamedQuery(name = "AuthorsAssociation.findByUser", query = "SELECT a FROM AuthorsAssociation a WHERE a.user=:user ORDER BY a.time"),
	@NamedQuery(name = "AuthorsAssociation.findByItem", query = "SELECT a FROM AuthorsAssociation a where a.item=:item ORDER BY a.time"),
	@NamedQuery(name = "AuthorsAssociation.find", query = "SELECT a FROM AuthorsAssociation a where a.item=:item and a.user=:user and a.time=:time")
})
@XmlRootElement
public class AuthorsAssociation {
	@Id
	@Column(name="ID")
	@GeneratedValue
	private int id;
	
	@Column(name="USER",nullable = false)
	private String user;

	@Column(name="ITEM", nullable = false)
	private String item;

	@Column(name="TIME", nullable = false)
	private Date time;

	@ManyToOne
	@JoinColumn(name="USER", referencedColumnName="JID", insertable = false, updatable = false)
	private User userInstance;

	@ManyToOne
	@JoinColumn(name="ITEM", referencedColumnName="ID", insertable = false, updatable = false)
	private Item itemInstance;

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

	public User getUserInstance() {
		return userInstance;
	}
	
	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public Date getTime() {
		return time;
	}

	public Item getItemInstance() {
		return itemInstance;
	}
	
	public void setTime(Date time) {
		this.time = time;
	}
}