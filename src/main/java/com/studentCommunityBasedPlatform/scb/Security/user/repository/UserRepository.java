package com.studentCommunityBasedPlatform.scb.Security.user.repository;

import com.studentCommunityBasedPlatform.scb.Security.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

	Optional<User> findByEmail(String mail);
}
