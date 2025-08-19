package com.studentCommunityBasedPlatform.scb.repository;

import com.studentCommunityBasedPlatform.scb.entity.StudyGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Integer> {

	// Find groups by subject
	List<StudyGroup> findBySubjectOrderByCreatedAtDesc(String subject);

	// Find groups created by user
	List<StudyGroup> findByCreatorIdOrderByCreatedAtDesc(Integer creatorId);

	// Find public groups
	Page<StudyGroup> findByIsPrivateFalseOrderByCreatedAtDesc(Pageable pageable);

	// Search groups
	@Query("SELECT sg FROM StudyGroup sg WHERE " +
			"(LOWER(sg.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
			"LOWER(sg.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
			"LOWER(sg.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
			"AND sg.isPrivate = false " +
			"ORDER BY sg.createdAt DESC")
	List<StudyGroup> searchGroups(@Param("searchTerm") String searchTerm);

	// Find groups with available spots
	@Query("SELECT sg FROM StudyGroup sg WHERE " +
			"sg.currentMembers < sg.maxMembers " +
			"AND sg.isPrivate = false " +
			"ORDER BY sg.createdAt DESC")
	List<StudyGroup> findAvailableGroups();
}
