package de.rwth.dbis.ugnm.service.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.dbis.ugnm.entity.Medium;
import de.rwth.dbis.ugnm.service.MediaService;

@Service("mediaService")
public class MediaServiceJpa implements MediaService {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Transactional(readOnly = true)
	public Medium getById(int id){
		Query queryFindMedium = entityManager.createNamedQuery("Medium.findMedium");
		queryFindMedium.setParameter("id", id);

		List<Medium> media = queryFindMedium.getResultList();
		Medium result = null;
		if(media.size() > 0) {
			result = media.get(0);
		}
		return result;
	}
	
	@Transactional(readOnly = true)
	public Medium getByUrl(String url){
		Query queryFindMedium = entityManager.createNamedQuery("Medium.findMediumUrl");
		queryFindMedium.setParameter("url", url);

		List<Medium> media = queryFindMedium.getResultList();
		Medium result = null;
		if(media.size() > 0) {
			result = media.get(0);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Medium> getAll() {
		Query query = entityManager.createNamedQuery("Medium.findAll");
		List<Medium> media = null;
		media = query.getResultList();
		return media;
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean save(Medium medium) {
		
		entityManager.persist(medium);
		entityManager.flush();
		
		return true;
	}
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean update(Medium medium) {
		entityManager.merge(medium);
		entityManager.flush();
		return true;
	}
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean delete(Medium medium) {
		medium = entityManager.getReference(Medium.class, medium.getId());
		if (medium == null)
			return false;
		entityManager.remove(medium);
		entityManager.flush();
		return true;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Medium findMedium(Medium medium) {
		Medium result = null;
		Query queryFindMedium = entityManager.createNamedQuery("Medium.findMedium");
		queryFindMedium.setParameter("id", medium.getId());

		List<Medium> media = queryFindMedium.getResultList();
		if(media.size() > 0) {
			result = media.get(0);
		}
		return result;
	}
}

