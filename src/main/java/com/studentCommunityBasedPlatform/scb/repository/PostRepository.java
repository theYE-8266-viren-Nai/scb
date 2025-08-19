package com.studentCommunityBasedPlatform.scb.repository;


import com.studentCommunityBasedPlatform.scb.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

	// Find posts by category
	List<Post> findByCategoryOrderByCreatedAtDesc(String category);

	// Find posts by user
	List<Post> findByUserIdOrderByCreatedAtDesc(Integer userId);

	// Find public posts
	Page<Post> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);

	// Search posts by title or content
	@Query("SELECT p FROM Post p WHERE " +
			"(LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
			"LOWER(p.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
			"LOWER(p.tags) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
			"AND p.isPublic = true " +
			"ORDER BY p.createdAt DESC")
	List<Post> searchPosts(@Param("searchTerm") String searchTerm);

	// Get trending posts (most liked in last 7 days)
//	@Query("SELECT p FROM Post p WHERE " +
//			"p.createdAt >= CURRENT_DATE - 7 " +
//			"AND p.isPublic = true " +
//			"ORDER BY p.likesCount DESC")
//	List<Post> findTrendingPosts(Pageable pageable);

}