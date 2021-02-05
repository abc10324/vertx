package com.sam.vertx.model.service;

import java.util.Optional;

import com.sam.vertx.model.User;
import com.sam.vertx.model.dao.UserDao;

import io.vertx.core.Vertx;

public class UserService {
	
	private Vertx vertx;
	
	private UserDao userDao;
	
	public UserService(Vertx vertx) {
		this.vertx = vertx;
		this.userDao = new UserDao(vertx);
	}
	
	public void addUser(User newUser) {
		userDao.addUser(newUser);
	}
	
	public User getUser(String userId) {
		Optional<User> result = userDao.getUser(userId);
		System.out.println("Find " + result.get().getName());
		return result.orElse(new User());
	}
	
}
