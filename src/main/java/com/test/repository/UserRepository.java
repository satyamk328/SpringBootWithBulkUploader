package com.test.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.test.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Query(value = "FROM User r WHERE lower(r.email) =:email")
	Optional<User> findByEmail(String name);

}
