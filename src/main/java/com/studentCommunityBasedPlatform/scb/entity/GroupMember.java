package com.studentCommunityBasedPlatform.scb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_members")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "group_id", nullable = false)
	private Integer groupId;

	@Column(name = "user_id", nullable = false)
	private Integer userId;

	@Column(length = 50)
	private String role = "member";

	@CreationTimestamp
	@Column(name = "joined_at", nullable = false, updatable = false)
	private LocalDateTime joinedAt;

	@Column(name = "is_active")
	private Boolean isActive = true;

	// Relationships
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id", insertable = false, updatable = false)
	private StudyGroup group;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;
}
