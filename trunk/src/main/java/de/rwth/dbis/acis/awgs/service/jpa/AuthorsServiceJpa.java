package de.rwth.dbis.acis.awgs.service.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.dbis.acis.awgs.entity.AuthorsAssociation;
import de.rwth.dbis.acis.awgs.service.AuthorsService;

@Service("authorsService")
public class AuthorsServiceJpa implements AuthorsService {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Transactional(readOnly = true)
	public AuthorsAssociation getById(String id){
		Query queryFindRating = entityManager.createNamedQuery("AuthorsAssociation.findById");
		queryFindRating.setParameter("id", id);

		List<AuthorsAssociation> ratings = queryFindRating.getResultList();
		AuthorsAssociation result = null;
		if(ratings.size() > 0) {
			result = ratings.get(0);
		}
		return result;
	}
	
	@Transactional(readOnly = true)
	public AuthorsAssociation get(String jid, String itemid, Date time){
		Query queryFindRating = entityManager.createNamedQuery("AuthorsAssociation.find");
		queryFindRating.setParameter("user", jid);
		queryFindRating.setParameter("item", itemid);
		queryFindRating.setParameter("time", time);

		List<AuthorsAssociation> ratings = queryFindRating.getResultList();
		AuthorsAssociation result = null;
		if(ratings.size() > 0) {
			result = ratings.get(0);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	
	@Transactional(readOnly = true)
	public List<AuthorsAssociation> getAll() {
		Query query = entityManager.createNamedQuery("AuthorsAssociation.findAll");
		List<AuthorsAssociation> ratings = null;
		ratings = query.getResultList();
		return ratings;
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean save(AuthorsAssociation rating) {
		
		entityManager.persist(rating);
		entityManager.flush();
		
		return true;
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean update(AuthorsAssociation rating) {
		entityManager.merge(rating);
		entityManager.flush();
		return true;
	}
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean delete(AuthorsAssociation rating) {
		rating = entityManager.getReference(AuthorsAssociation.class, rating.getId());
		if (rating == null)
			return false;
		entityManager.remove(rating);
		entityManager.flush();
		return true;
	}

	@Transactional(readOnly = true)
	public List<AuthorsAssociation> getAuthorshipsForUser(String login) {
		Query query = entityManager.createNamedQuery("AuthorsAssociation.findByUser");
		query.setParameter("user", login);

		List<AuthorsAssociation> ratings = null;
		ratings = query.getResultList();
		return ratings;
	}

	@Transactional(readOnly = true)
	public List<AuthorsAssociation> getAuthorshipsForItem(String id) {
		Query query = entityManager.createNamedQuery("AuthorsAssociation.findByItem");
		query.setParameter("item", id);

		List<AuthorsAssociation> ratings = null;
		ratings = query.getResultList();
		return ratings;
	}
}

