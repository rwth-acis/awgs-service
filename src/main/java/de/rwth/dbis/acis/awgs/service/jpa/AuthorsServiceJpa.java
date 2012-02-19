package de.rwth.dbis.ugnm.service.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.dbis.ugnm.entity.RatesAssociation;
import de.rwth.dbis.ugnm.service.RatingService;

@Service("ratingService")
public class RatingServiceJpa implements RatingService {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Transactional(readOnly = true)
	public RatesAssociation getById(String id){
		Query queryFindRating = entityManager.createNamedQuery("RatesAssociation.findById");
		queryFindRating.setParameter("id", id);

		List<RatesAssociation> ratings = queryFindRating.getResultList();
		RatesAssociation result = null;
		if(ratings.size() > 0) {
			result = ratings.get(0);
		}
		return result;
	}
	
	@Transactional(readOnly = true)
	public RatesAssociation get(String login, int mediaId, Date time){
		Query queryFindRating = entityManager.createNamedQuery("RatesAssociation.find");
		queryFindRating.setParameter("user", login);
		queryFindRating.setParameter("medium", mediaId);
		queryFindRating.setParameter("time", time);

		List<RatesAssociation> ratings = queryFindRating.getResultList();
		RatesAssociation result = null;
		if(ratings.size() > 0) {
			result = ratings.get(0);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	
	@Transactional(readOnly = true)
	public List<RatesAssociation> getAll() {
		Query query = entityManager.createNamedQuery("RatesAssociation.findAll");
		List<RatesAssociation> ratings = null;
		ratings = query.getResultList();
		return ratings;
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean save(RatesAssociation rating) {
		
		entityManager.persist(rating);
		entityManager.flush();
		
		return true;
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean update(RatesAssociation rating) {
		entityManager.merge(rating);
		entityManager.flush();
		return true;
	}
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean delete(RatesAssociation rating) {
		rating = entityManager.getReference(RatesAssociation.class, rating.getId());
		if (rating == null)
			return false;
		entityManager.remove(rating);
		entityManager.flush();
		return true;
	}

	@Transactional(readOnly = true)
	public List<RatesAssociation> getRatingsForUser(String login) {
		Query query = entityManager.createNamedQuery("RatesAssociation.findByUser");
		query.setParameter("user", login);

		List<RatesAssociation> ratings = null;
		ratings = query.getResultList();
		return ratings;
	}

	@Transactional(readOnly = true)
	public List<RatesAssociation> getRatingsForMedium(int id) {
		Query query = entityManager.createNamedQuery("RatesAssociation.findByMedium");
		query.setParameter("medium", id);

		List<RatesAssociation> ratings = null;
		ratings = query.getResultList();
		return ratings;
	}
}

