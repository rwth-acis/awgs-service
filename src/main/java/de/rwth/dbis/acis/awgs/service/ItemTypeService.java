package de.rwth.dbis.acis.awgs.service;

import java.util.List;

import de.rwth.dbis.acis.awgs.entity.ItemType;

public interface ItemTypeService {
	public List<ItemType> getAll();
	public ItemType get(int id);
	public ItemType getByName(String name);
	public List<ItemType> search(String query);
	public boolean save(ItemType type);
	public boolean delete(ItemType type);
	public boolean update(ItemType type);
}
