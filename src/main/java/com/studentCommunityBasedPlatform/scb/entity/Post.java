package com.studentCommunityBasedPlatform.scb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, length = 255)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(length = 100)
	private String category;

	@Column(length = 500)
	private String tags;

	@Column(name = "user_id", nullable = false)
	private Integer userId;

	@Column(name = "image_url", length = 500)
	private String imageUrl;

	@Column(name = "likes_count")
	@Builder.Default  // 🔧 ADD THIS
	private Integer likesCount = 0;

	@Column(name = "saves_count")
	@Builder.Default  // 🔧 ADD THIS
	private Integer savesCount = 0;

	@Column(name = "is_public")
	@Builder.Default  // 🔧 ADD THIS
	private Boolean isPublic = true;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// Relationships
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User author;

	@OneToMany(mappedBy = "postId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Builder.Default  // 🔧 ADD THIS TOO
	private List<PostLike> likes = new ArrayList<>();

	@OneToMany(mappedBy = "postId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Builder.Default  // 🔧 ADD THIS TOO
	private List<PostSave> saves = new ArrayList<>();
}