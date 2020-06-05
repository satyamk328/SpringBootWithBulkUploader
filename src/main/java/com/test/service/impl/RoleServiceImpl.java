package com.test.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.test.exception.ServiceException;
import com.test.model.Role;
import com.test.repository.RoleRepository;
import com.test.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleRepository roleRepo;
	
	@Override
	public List<Role> findAll() {
		return roleRepo.findAll();
	}

	@Override
	public Role findById(Long id) {
		Role book = roleRepo.findById(id).orElse(null);
		if (book == null)
			throw new ServiceException("Role Id is invalid. Please enter valid id");
		return book;
	}

	@Override
	@Transactional
	public Role save(Role role) {
		Role r = roleRepo.findByName(role.getName()).orElse(null);
		if(r != null){
			throw new ServiceException("Role Name is already exist. Please enter valid Role Name");
		}
		return roleRepo.save(role);
	}

	@Override
	@Transactional
	public Role update(Role role) {
		Role r = findById(role.getId());
		if(r != null && !r.getName().equalsIgnoreCase(role.getName())) {
			throw new ServiceException("Role Name is already exist. Please enter valid Role Name");
		}
		return roleRepo.save(role);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		findById(id);
		roleRepo.deleteById(id);
	}

}
