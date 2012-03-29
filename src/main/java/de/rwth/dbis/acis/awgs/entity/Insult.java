package de.rwth.dbis.acis.awgs.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "INSULT")
@NamedQueries({ 
	@NamedQuery(name = "Insult.findAll", query = "SELECT i FROM Insult i ORDER BY i.id DESC"),
	@NamedQuery(name = "Insult.find", query = "SELECT i FROM Insult i WHERE i.id=:id"),
	@NamedQuery(name = "Insult.findLast", query = "SELECT i FROM Insult i ORDER BY i.id DESC LIMIT 0,1")
})
@XmlRootElement
public class Insult {
	
	@Id
	@Column(name="ID")
	@GeneratedValue
	private int id;
	
	@Column(name = "INSULT", nullable = false)
	private String insult;
	
	@Column(name = "CONTRIBUTOR", nullable=false)
	private String contributor;
	
	@Column(name = "DATE", nullable=false)
	private Date date;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the insult
	 */
	public String getInsult() {
		return insult;
	}

	/**
	 * @param insult the insult to set
	 */
	public void setInsult(String insult) {
		this.insult = insult;
	}

	/**
	 * @return the contributor
	 */
	public String getContributor() {
		return contributor;
	}

	/**
	 * @param contributor the contributor to set
	 */
	public void setContributor(String contributor) {
		this.contributor = contributor;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

}