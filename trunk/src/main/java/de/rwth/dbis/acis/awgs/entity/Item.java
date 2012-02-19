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
@Table(name = "ITEM")
@NamedQueries({ 
	@NamedQuery(name = "Item.findAll", query = "SELECT m FROM Item m"),
	@NamedQuery(name = "Item.findItem", query = "SELECT m FROM Item m where m.id=:id"),
	@NamedQuery(name = "Item.findItemUrl", query = "SELECT m FROM Item m where m.url=:url")
})
@XmlRootElement
public class Item {
	
	@Id
	@Column(name = "ID", nullable = false)
	private int id;
	
	@Column(name = "URL", nullable = false)
	private String url;
	
	@Column(name = "NAME", nullable = false)
	private String name;
	
	@Column(name = "DESCRIPTION", nullable=false)
	private String description;
	
	@Column(name = "STATUS", nullable=false)
	private int status;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
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

}
