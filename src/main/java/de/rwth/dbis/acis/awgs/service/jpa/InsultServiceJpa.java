package de.rwth.dbis.acis.awgs.service.jpa;

import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.dbis.acis.awgs.entity.Insult;
import de.rwth.dbis.acis.awgs.service.InsultService;

@Service("insultService")
public class InsultServiceJpa implements InsultService {

	private EntityManager entityManager;
	private static Random random = new Random();

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean save(Insult insult) {
		entityManager.persist(insult);
		entityManager.flush();
		return true;
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean update(Insult insult) {
		entityManager.merge(insult);
		entityManager.flush();
		return true;
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean delete(Insult insult) {
		insult = entityManager.getReference(Insult.class, insult.getId());
		if (insult == null)
			return false;
		entityManager.remove(insult);
		entityManager.flush();
		return true;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Insult> getAll() {
		Query query = entityManager.createNamedQuery("Insult.findAll");
		List<Insult> insults = null;
		insults = query.getResultList();
		return insults;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Insult get(int id){
		Query queryFindInsult = entityManager.createNamedQuery("Insult.find");
		queryFindInsult.setParameter("id", id);

		List<Insult> items = queryFindInsult.getResultList();
		Insult result = null;
		if(items.size() > 0) {
			result = items.get(0);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Insult getLast() {
		Query query = entityManager.createNamedQuery("Insult.findLast");
		List<Insult> insults = query.getResultList();
		return insults.get(0);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Insult getRandom() {
		int lid = this.getLast().getId();
		int rid = 1 + random.nextInt(lid);
		System.out.println("Next insult: " + rid);
		//int rid = 1 + (int) ( Math.random()*(lid - 1) );
		//System.out.println("Last ID: " + lid + " Random ID: " + rid);
		return get(rid);
		
	}

}
