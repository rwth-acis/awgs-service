package de.rwth.dbis.ugnm.service;

import java.util.List;
import de.rwth.dbis.ugnm.entity.Medium;

public interface MediaService {
	public boolean save(Medium medium);
	public List<Medium> getAll();
	public Medium getById(int id);
	public Medium getByUrl(String url);
	public boolean delete(Medium medium);
	public boolean update(Medium medium);
	public Medium findMedium(Medium medium);
}