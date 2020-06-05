package com.test.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.exception.ServiceException;
import com.test.model.User;
import com.test.repository.UserRepository;
import com.test.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public User findById(Long id) {
		User library = userRepository.findById(id).orElse(null);
		if (library == null)
			throw new ServiceException("User Id is invalid. Please enter valid id");
		return library;
	}

	@Override
	@Transactional
	public User save(User user) {
		User r = userRepository.findByEmail(user.getEmail()).orElse(null);
		if(r != null){
			throw new ServiceException("Email is already exist. Please enter valid Email");
		}
		return userRepository.save(user);
	}

	@Override
	@Transactional
	public User update(User user) {
		findById(user.getId());
		User r = userRepository.findByEmail(user.getEmail()).orElse(null);
		if(r != null && !r.getId().equals(user.getId())) {
			throw new ServiceException("Email is already exist. Please enter valid Email");
		}
		return userRepository.save(user);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		User user = findById(id);
		userRepository.delete(user);
	}

}
