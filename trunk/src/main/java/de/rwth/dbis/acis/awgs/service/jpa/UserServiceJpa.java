package de.rwth.dbis.ugnm.service.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.dbis.ugnm.entity.User;
import de.rwth.dbis.ugnm.service.UserService;

@Service("userService")
public class UserServiceJpa implements UserService {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Transactional(readOnly = true)
	public User getByLogin(String login){
		Query queryFindUser = entityManager.createNamedQuery("User.findUser");
		queryFindUser.setParameter("login", login);

		List<User> users = queryFindUser.getResultList();
		User result = null;
		if(users.size() > 0) {
			result = users.get(0);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<User> getAll() {
		Query query = entityManager.createNamedQuery("User.findAll");
		List<User> users = null;
		users = query.getResultList();
		return users;
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean save(User user) {
		
		entityManager.persist(user);
		entityManager.flush();
		
		return true;
	}
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean update(User user) {
		entityManager.merge(user);
		entityManager.flush();
		return true;
	}
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean delete(User user) {
		user = entityManager.getReference(User.class, user.getLogin());
		if (user == null)
			return false;
		entityManager.remove(user);
		entityManager.flush();
		return true;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public User findUser(User user) {
		User result = null;
		Query queryFindUser = entityManager.createNamedQuery("User.findUser");
		queryFindUser.setParameter("login", user.getLogin());

		List<User> users = queryFindUser.getResultList();
		if(users.size() > 0) {
			result = users.get(0);
		}
		return result;
	}
	
}

