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
@Table(name = "study_groups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, length = 255)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false, length = 100)
	private String subject;

	@Column(name = "max_members")
	private Integer maxMembers = 20;

	@Column(name = "current_members")
	private Integer currentMembers = 1;

	@Column(name = "creator_id", nullable = false)
	private Integer creatorId;

	@Column(name = "is_private")
	private Boolean isPrivate = false;

	@Column(name = "meeting_schedule", length = 255)
	private String meetingSchedule;

	@Column(name = "group_image_url", length = 500)
	private String groupImageUrl;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// Relationships
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator_id", insertable = false, updatable = false)
	private User creator;

	@OneToMany(mappedBy = "groupId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<GroupMember> members = new ArrayList<>();
}
