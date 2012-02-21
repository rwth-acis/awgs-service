package de.rwth.dbis.acis.awgs.service;

import java.util.List;

import de.rwth.dbis.acis.awgs.entity.Item;

public interface ItemService {
	public boolean save(Item medium);
	public List<Item> getAll();
	public Item getById(String id);
	public Item getByUrl(String url);
	public boolean delete(Item medium);
	public boolean update(Item medium);
	public Item findItem(Item medium);
}