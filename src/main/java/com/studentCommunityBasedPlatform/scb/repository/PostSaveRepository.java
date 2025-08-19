package com.studentCommunityBasedPlatform.scb.repository;

import com.studentCommunityBasedPlatform.scb.entity.PostSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostSaveRepository extends JpaRepository<PostSave, Integer> {
	Optional<PostSave> findByPostIdAndUserId(Integer postId, Integer userId);
	long countByPostId(Integer postId); // Optional: for save counts
}