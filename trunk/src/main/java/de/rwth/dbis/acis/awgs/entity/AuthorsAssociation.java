package de.rwth.dbis.ugnm.entity;

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
@Table(name="RATES")
@NamedQueries({ 
	@NamedQuery(name = "RatesAssociation.findAll", query = "SELECT r FROM RatesAssociation r ORDER BY r.time"),
	@NamedQuery(name = "RatesAssociation.findById", query = "SELECT r FROM RatesAssociation r WHERE r.id=:id"),
	@NamedQuery(name = "RatesAssociation.findByUser", query = "SELECT r FROM RatesAssociation r WHERE r.user=:user ORDER BY r.time"),
	@NamedQuery(name = "RatesAssociation.findByMedium", query = "SELECT r FROM RatesAssociation r where r.medium=:medium ORDER BY r.time"),
	@NamedQuery(name = "RatesAssociation.find", query = "SELECT r FROM RatesAssociation r where r.medium=:medium and r.user=:user and r.time=:time")
})
@XmlRootElement
public class RatesAssociation {
	@Id
	@Column(name="ID")
	@GeneratedValue
	private int id;
	
	@Column(name="USER",nullable = false)
	private String user;
	
	@Column(name="MEDIUM", nullable = false)
	private int medium;

	@Column(name="RATING", nullable = false)
	private int rating;

	@Column(name="TIME", nullable = false)
	private Date time;

	@ManyToOne
	@JoinColumn(name="USER", referencedColumnName="LOGIN", insertable = false, updatable = false)
	private User userInstance;

	@ManyToOne
	@JoinColumn(name="MEDIUM", referencedColumnName="ID", insertable = false, updatable = false)
	private Medium mediumInstance;

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
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the medium
	 */
	public int getMedium() {
		return medium;
	}

	/**
	 * @param medium the medium to set
	 */
	public void setMedium(int medium) {
		this.medium = medium;
	}

	/**
	 * @return the rating
	 */
	public int getRating() {
		return rating;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(int rating) {
		this.rating = rating;
	}

	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	/**
	 * @return the userInstance
	 */
	public User getUserInstance() {
		return userInstance;
	}

	/**
	 * @return the mediumInstance
	 */
	public Medium getMediumInstance() {
		return mediumInstance;
	}

}