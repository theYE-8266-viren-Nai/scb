package com.studentCommunityBasedPlatform.scb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLike {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "post_id", nullable = false)
	private Integer postId;

	@Column(name = "user_id", nullable = false)
	private Integer userId;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
}
