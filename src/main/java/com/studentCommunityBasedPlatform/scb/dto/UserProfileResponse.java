package com.studentCommunityBasedPlatform.scb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

	private Integer id;
	private String firstName;
	private String lastName;
	private String email;
	private String bio;
	private String location;
	private String university;
	private String major;
	private Integer graduationYear;
	private String phoneNumber;
	private String website;
	private String avatarUrl;
	private String coverPhotoUrl;
	private Boolean isProfilePublic;
	private Boolean allowMessagesFromStrangers;
	private Boolean emailNotificationsEnabled;
	private Boolean pushNotificationsEnabled;
	private Boolean emailVerified;
	private Boolean phoneVerified;
	private LocalDateTime createdAt;
	private LocalDateTime lastActiveAt;
	private Boolean isActive;
	private String role;

	// Stats
	private Integer postsCount;
	private Integer studyGroupsCount;
	private Integer connectionsCount;
}
