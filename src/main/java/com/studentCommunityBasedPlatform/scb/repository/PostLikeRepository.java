package com.studentCommunityBasedPlatform.scb.repository;

import com.studentCommunityBasedPlatform.scb.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {
	Optional<PostLike> findByPostIdAndUserId(Integer postId, Integer userId);
	long countByPostId(Integer postId); // Optional: for like counts
}