package com.test.service;

import java.util.List;

import com.test.model.User;

public interface UserService {

	public List<User> findAll();

	public User findById(Long id);

	public User save(User book);

	public User update(User book);

	public void delete(Long id);
	
}
