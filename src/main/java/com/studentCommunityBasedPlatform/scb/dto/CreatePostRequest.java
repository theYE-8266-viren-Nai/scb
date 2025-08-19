package com.studentCommunityBasedPlatform.scb.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostRequest {
	private String title;
	private String description;
	private String content;
	private String category;
	private String tags;
	private String imageUrl;
	private Boolean isPublic = true;
}