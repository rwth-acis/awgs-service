package de.rwth.dbis.acis.awgs.service.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.dbis.acis.awgs.entity.ItemType;
import de.rwth.dbis.acis.awgs.service.ItemTypeService;

@Service("itemTypeService")
public class ItemTypeServiceJpa implements ItemTypeService {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public ItemType get(int id){
		Query queryFindItem = entityManager.createNamedQuery("ItemType.get");
		queryFindItem.setParameter("id", id);

		List<ItemType> items = queryFindItem.getResultList();
		ItemType result = null;
		if(items.size() > 0) {
			result = items.get(0);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<ItemType> search(String query){
		Query q = entityManager.createNamedQuery("ItemType.search");
		q.setParameter("query","%" + query + "%");
		List<ItemType> itemTypes = q.getResultList();
		return itemTypes;
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean save(ItemType itemType) {
		entityManager.persist(itemType);
		entityManager.flush();
		return true;
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean update(ItemType itemType) {
		entityManager.merge(itemType);
		entityManager.flush();
		return true;
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean delete(ItemType itemType) {
		itemType = entityManager.getReference(ItemType.class, itemType.getId());
		if (itemType == null)
			return false;
		entityManager.remove(itemType);
		entityManager.flush();
		return true;
	}
	
}