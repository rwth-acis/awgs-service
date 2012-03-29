package de.rwth.dbis.acis.awgs.service;

import java.util.List;

import de.rwth.dbis.acis.awgs.entity.Item;

public interface ItemService {
	public boolean save(Item item);
	public List<Item> getAll();
	public List<Item> search(String query);
	public Item getLast();
	public Item getById(String id);
	public Item getByUrl(String url);
	public boolean delete(Item item);
	public boolean update(Item item);
	public Item findItem(Item item);
	public String getNextItemId();
}