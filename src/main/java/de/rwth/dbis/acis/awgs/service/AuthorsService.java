package de.rwth.dbis.acis.awgs.service;

import java.util.Date;
import java.util.List;

import de.rwth.dbis.acis.awgs.entity.AuthorsAssociation;

public interface AuthorsService {
	public boolean save(AuthorsAssociation rating);
	public AuthorsAssociation get(String login, String mediaId, Date time);
	public List<AuthorsAssociation> getAll();
	public AuthorsAssociation getById(String id);
	public List<AuthorsAssociation> getAuthorshipsForUser(String login);
	public List<AuthorsAssociation> getAuthorshipsForItem (String id);
	public boolean delete(AuthorsAssociation rating);
	public boolean update(AuthorsAssociation rating);
}