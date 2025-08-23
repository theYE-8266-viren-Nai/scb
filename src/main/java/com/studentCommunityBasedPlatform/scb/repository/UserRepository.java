package com.studentCommunityBasedPlatform.scb.repository;

import com.studentCommunityBasedPlatform.scb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	Optional<User> findByEmailVerificationToken(String token);

	@Query("SELECT u FROM User u WHERE " +
			"u.isProfilePublic = true AND u.isActive = true AND (" +
			"LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
			"LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
			"LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
			"LOWER(u.university) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
			"LOWER(u.major) LIKE LOWER(CONCAT('%', :query, '%')))")
	List<User> searchUsers(@Param("query") String query);

	@Query("SELECT u FROM User u WHERE " +
			"u.isProfilePublic = true AND u.isActive = true AND " +
			"u.university = :university")
	List<User> findByUniversity(@Param("university") String university);

	@Query("SELECT u FROM User u WHERE " +
			"u.isProfilePublic = true AND u.isActive = true AND " +
			"u.major = :major")
	List<User> findByMajor(@Param("major") String major);

	@Query("SELECT u FROM User u WHERE " +
			"u.isProfilePublic = true AND u.isActive = true AND " +
			"u.graduationYear = :year")
	List<User> findByGraduationYear(@Param("year") Integer year);

	List<User> findByIsActiveTrue();

	List<User> findByEmailVerifiedTrue();
}