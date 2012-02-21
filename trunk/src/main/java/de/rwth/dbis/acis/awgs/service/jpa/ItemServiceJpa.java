package de.rwth.dbis.acis.awgs.service.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.dbis.acis.awgs.entity.Item;
import de.rwth.dbis.acis.awgs.service.ItemService;

@Service("itemService")
public class ItemServiceJpa implements ItemService {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Transactional(readOnly = true)
	public Item getById(String id){
		Query queryFindItem = entityManager.createNamedQuery("Item.findItem");
		queryFindItem.setParameter("id", id);

		List<Item> items = queryFindItem.getResultList();
		Item result = null;
		if(items.size() > 0) {
			result = items.get(0);
		}
		return result;
	}
	
	@Transactional(readOnly = true)
	public Item getByUrl(String url){
		Query queryFindItem = entityManager.createNamedQuery("Item.findItemUrl");
		queryFindItem.setParameter("url", url);

		List<Item> items = queryFindItem.getResultList();
		Item result = null;
		if(items.size() > 0) {
			result = items.get(0);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Item> getAll() {
		Query query = entityManager.createNamedQuery("Item.findAll");
		List<Item> items = null;
		items = query.getResultList();
		return items;
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean save(Item item) {
		
		entityManager.persist(item);
		entityManager.flush();
		
		return true;
	}
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean update(Item item) {
		entityManager.merge(item);
		entityManager.flush();
		return true;
	}
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean delete(Item item) {
		item = entityManager.getReference(Item.class, item.getId());
		if (item == null)
			return false;
		entityManager.remove(item);
		entityManager.flush();
		return true;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Item findItem(Item item) {
		Item result = null;
		Query queryFindItem = entityManager.createNamedQuery("Item.findItem");
		queryFindItem.setParameter("id", item.getId());

		List<Item> items = queryFindItem.getResultList();
		if(items.size() > 0) {
			result = items.get(0);
		}
		return result;
	}
}
