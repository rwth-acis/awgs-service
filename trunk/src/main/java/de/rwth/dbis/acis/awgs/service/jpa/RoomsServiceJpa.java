package de.rwth.dbis.acis.awgs.service.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.rwth.dbis.acis.awgs.entity.RoomsAssociation;
import de.rwth.dbis.acis.awgs.service.RoomsService;


	@Service("roomsService")
	public class RoomsServiceJpa implements RoomsService {

		private EntityManager entityManager;

		@PersistenceContext
		public void setEntityManager(EntityManager entityManager) {
			this.entityManager = entityManager;
		}

		public EntityManager getEntityManager() {
			return entityManager;
		}
		
		@Transactional(readOnly = true)
		public RoomsAssociation getById(String id){
			Query queryFindRoom = entityManager.createNamedQuery("RoomsAssociation.findById");
			queryFindRoom.setParameter("id", id);

			List<RoomsAssociation> rooms = queryFindRoom.getResultList();
			RoomsAssociation result = null;
			if(rooms.size() > 0) {
				result = rooms.get(0);
			}
			return result;
		}
		
		@Transactional(readOnly = true)
		public RoomsAssociation getByRoom(String jid){
			Query queryFindRoom = entityManager.createNamedQuery("RoomsAssociation.findByRoom");
			queryFindRoom.setParameter("room", jid);

			List<RoomsAssociation> rooms = queryFindRoom.getResultList();
			RoomsAssociation result = null;
			if(rooms.size() > 0) {
				result = rooms.get(0);
			}
			return result;
		}
		
		@Transactional(readOnly = true)
		public RoomsAssociation get(String jid, String room){
			Query queryFindRoom = entityManager.createNamedQuery("RoomsAssociation.find");
			queryFindRoom.setParameter("user", jid);
			queryFindRoom.setParameter("room", room);

			List<RoomsAssociation> rooms = queryFindRoom.getResultList();
			RoomsAssociation result = null;
			if(rooms.size() > 0) {
				result = rooms.get(0);
			}
			return result;
		}
		
		@SuppressWarnings("unchecked")
		
		@Transactional(readOnly = true)
		public List<RoomsAssociation> getAll() {
			Query query = entityManager.createNamedQuery("RoomsAssociation.findAll");
			List<RoomsAssociation> room = null;
			room = query.getResultList();
			return room;
		}

		@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
		public boolean save(RoomsAssociation room) {
			
			entityManager.persist(room);
			entityManager.flush();
			
			return true;
		}
		
		@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
		public boolean update(RoomsAssociation room) {
			entityManager.merge(room);
			entityManager.flush();
			return true;
		}
		@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
		public boolean delete(RoomsAssociation room) {
			room = entityManager.getReference(RoomsAssociation.class, room.getId());
			if (room == null)
				return false;
			entityManager.remove(room);
			entityManager.flush();
			return true;
		}

		@Transactional(readOnly = true)
		public List<RoomsAssociation> getRoomsForUser(String jid) {
			Query query = entityManager.createNamedQuery("RoomsAssociation.findByUser");
			query.setParameter("user", jid);

			List<RoomsAssociation> rooms = null;
			rooms = query.getResultList();
			return rooms;
		}

	}

