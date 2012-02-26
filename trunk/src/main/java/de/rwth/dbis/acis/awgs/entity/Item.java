package de.rwth.dbis.acis.awgs.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "ITEM")
@NamedQueries({ 
	@NamedQuery(name = "Item.findAll", query = "SELECT m FROM Item m ORDER BY m.id DESC"),
	@NamedQuery(name = "Item.findItem", query = "SELECT m FROM Item m WHERE m.id=:id"),
	@NamedQuery(name = "Item.findLast", query = "SELECT m FROM Item m ORDER BY m.id DESC LIMIT 0,1"),
	@NamedQuery(name = "Item.findItemUrl", query = "SELECT m FROM Item m where m.url=:url")
})
@XmlRootElement
public class Item {
	
	@Id
	@Column(name = "ID", nullable = false)
	private String id;
	
	@Column(name = "URL", nullable = false)
	private String url;
	
	@Column(name = "NAME", nullable = false)
	private String name;
	
	@Column(name = "DESCRIPTION", nullable=false)
	private String description;
	
	@Column(name = "STATUS", nullable=false)
	private int status;
	
	@Column(name = "OWNER", nullable=false)
	private String owner;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

}
