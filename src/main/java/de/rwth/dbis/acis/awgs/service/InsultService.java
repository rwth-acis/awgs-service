package de.rwth.dbis.acis.awgs.service;

import java.util.List;

import de.rwth.dbis.acis.awgs.entity.Insult;

public interface InsultService {
	public boolean save(Insult insult);
	public boolean update(Insult insult);
	public boolean delete(Insult insult);
	public List<Insult> getAll();
	public Insult get(int id);
	public Insult getLast();
	public Insult getRandom();
}
