package de.rwth.dbis.ugnm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "MEDIUM")
@NamedQueries({ 
	@NamedQuery(name = "Medium.findAll", query = "SELECT m FROM Medium m"),
	@NamedQuery(name = "Medium.findMedium", query = "SELECT m FROM Medium m where m.id=:id"),
	@NamedQuery(name = "Medium.findMediumUrl", query = "SELECT m FROM Medium m where m.url=:url")
})
@XmlRootElement
public class Medium {
	
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private int id;
	
	@Column(name = "URL", nullable = false)
	private String url;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	 /**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
