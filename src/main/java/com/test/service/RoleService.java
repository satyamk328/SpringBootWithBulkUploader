package com.test.service;

import java.util.List;

import com.test.model.Role;

public interface RoleService {

	public List<Role> findAll();

	public Role findById(Long id);

	public Role save(Role role);

	public Role update(Role role);

	public void delete(Long id);
	
}
