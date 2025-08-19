package com.studentCommunityBasedPlatform.scb.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupResponse {
	private Integer id;
	private String name;
	private String description;
	private String subject;
	private Integer maxMembers;
	private Integer currentMembers;
	private Boolean isPrivate;
	private String meetingSchedule;
	private String groupImageUrl;
	private LocalDateTime createdAt;

	// Creator info
	private String creatorName;
	private String creatorEmail;

	// User status
	private Boolean isCurrentUserMember = false;
	private Boolean isCurrentUserCreator = false;
	private String currentUserRole;
}