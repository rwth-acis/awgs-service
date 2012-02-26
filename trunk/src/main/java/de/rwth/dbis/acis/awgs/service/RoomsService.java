package de.rwth.dbis.acis.awgs.service;

import java.util.List;

import de.rwth.dbis.acis.awgs.entity.RoomsAssociation;

public interface RoomsService {
	public boolean save(RoomsAssociation ra);
	public RoomsAssociation get(String jid, String room);
	public List<RoomsAssociation> getAll();
	public RoomsAssociation getById(String id);
	public RoomsAssociation getByRoom(String id);
	public List<RoomsAssociation> getRoomsForUser(String jid);
	
	public boolean delete(RoomsAssociation ra);
	public boolean update(RoomsAssociation ra);
}

