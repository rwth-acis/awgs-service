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
@Table(name="ITEMTYPE")
@NamedQueries({
	@NamedQuery(name = "ItemType.get", query = "SELECT t FROM ItemType t WHERE t.id=:id"),
	@NamedQuery(name = "ItemType.search", query = "SELECT t FROM ItemType t where t.name like :query OR t.description LIKE :query ORDER BY t.id DESC LIMIT 0,1")
})
@XmlRootElement
public class ItemType {
	@Id
	@Column(name="ID")
	@GeneratedValue
	private int id;
	
	@Column(name="NAME",nullable = false)
	private String name;
	
	@Column(name="DESCRIPTION",nullable = false)
	private String description;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

}