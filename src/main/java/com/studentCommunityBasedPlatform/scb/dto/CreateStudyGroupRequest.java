package com.studentCommunityBasedPlatform.scb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateStudyGroupRequest {
	private String name;
	private String description;
	private String subject;
	private Integer maxMembers = 20;
	private Boolean isPrivate = false;
	private String meetingSchedule;
	private String groupImageUrl;
}