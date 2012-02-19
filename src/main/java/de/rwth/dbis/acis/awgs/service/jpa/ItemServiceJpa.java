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
	public Item getById(int id){
		Query queryFindItem = entityManager.createNamedQuery("Item.findItem");
		queryFindItem.setParameter("id", id);

		List<Item> media = queryFindItem.getResultList();
		Item result = null;
		if(media.size() > 0) {
			result = media.get(0);
		}
		return result;
	}
	
	@Transactional(readOnly = true)
	public Item getByUrl(String url){
		Query queryFindItem = entityManager.createNamedQuery("Item.findItemUrl");
		queryFindItem.setParameter("url", url);

		List<Item> media = queryFindItem.getResultList();
		Item result = null;
		if(media.size() > 0) {
			result = media.get(0);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Item> getAll() {
		Query query = entityManager.createNamedQuery("Item.findAll");
		List<Item> media = null;
		media = query.getResultList();
		return media;
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean save(Item medium) {
		
		entityManager.persist(medium);
		entityManager.flush();
		
		return true;
	}
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean update(Item medium) {
		entityManager.merge(medium);
		entityManager.flush();
		return true;
	}
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean delete(Item medium) {
		medium = entityManager.getReference(Item.class, medium.getId());
		if (medium == null)
			return false;
		entityManager.remove(medium);
		entityManager.flush();
		return true;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Item findItem(Item medium) {
		Item result = null;
		Query queryFindItem = entityManager.createNamedQuery("Item.findItem");
		queryFindItem.setParameter("id", medium.getId());

		List<Item> media = queryFindItem.getResultList();
		if(media.size() > 0) {
			result = media.get(0);
		}
		return result;
	}
}

