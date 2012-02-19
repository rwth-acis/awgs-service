package de.rwth.dbis.ugnm.service;

import java.util.Date;
import java.util.List;

import de.rwth.dbis.ugnm.entity.RatesAssociation;

public interface RatingService {
	public boolean save(RatesAssociation rating);
	public RatesAssociation get(String login, int mediaId, Date time);
	public List<RatesAssociation> getAll();
	public RatesAssociation getById(String id);
	public List<RatesAssociation> getRatingsForUser(String login);
	public List<RatesAssociation> getRatingsForMedium (int id);
	public boolean delete(RatesAssociation rating);
	public boolean update(RatesAssociation rating);
}