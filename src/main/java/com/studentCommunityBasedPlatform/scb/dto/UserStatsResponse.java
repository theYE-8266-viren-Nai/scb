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
public class UserStatsResponse {

	private Integer userId;
	private Integer totalPosts;
	private Integer totalStudyGroups;
	private Integer totalConnections;
	private Integer totalLikesReceived;
	private Integer totalCommentsReceived;
	private LocalDateTime memberSince;
	private LocalDateTime lastActive;
	private Integer loginStreak;
	private Integer profileViews;
}