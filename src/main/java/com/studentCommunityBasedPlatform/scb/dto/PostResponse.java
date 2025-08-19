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
public class PostResponse {
	private Integer id;
	private String title;
	private String description;
	private String content;
	private String category;
	private String tags;
	private String imageUrl;
	private Integer likesCount;
	private Integer savesCount;
	private Boolean isPublic;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	// Author info
	private String authorName;
	private String authorEmail;

	// User interaction flags
	private Boolean isLikedByCurrentUser = false;
	private Boolean isSavedByCurrentUser = false;
}
