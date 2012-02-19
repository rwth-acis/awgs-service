package de.rwth.dbis.acis.awgs.service;

import java.util.List;

import de.rwth.dbis.acis.awgs.entity.User;

public interface UserService {
	public boolean save(User user);
	public List<User> getAll();
	public User getByJid(String jid);
	public boolean delete(User user);
	public boolean update(User user);
	public User findUser(User user);
}

