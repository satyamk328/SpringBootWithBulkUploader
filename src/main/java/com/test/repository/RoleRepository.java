package com.test.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.test.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
	@Query("From Role r where lower(r.name)=:name")
	Optional<Role> findByName(String email);

}

