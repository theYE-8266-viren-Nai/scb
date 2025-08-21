package com.studentCommunityBasedPlatform.scb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileRequest {

	@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
	private String firstName;

	@Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
	private String lastName;

	@Email(message = "Please provide a valid email address")
	private String email;

	@Size(max = 500, message = "Bio cannot exceed 500 characters")
	private String bio;

	@Size(max = 100, message = "Location cannot exceed 100 characters")
	private String location;

	@Size(max = 100, message = "University cannot exceed 100 characters")
	private String university;

	@Size(max = 100, message = "Major cannot exceed 100 characters")
	private String major;

	private Integer graduationYear;

	@Size(max = 15, message = "Phone number cannot exceed 15 characters")
	private String phoneNumber;

	@Size(max = 100, message = "Website URL cannot exceed 100 characters")
	private String website;

	private Boolean isProfilePublic;
	private Boolean allowMessagesFromStrangers;
	private Boolean emailNotificationsEnabled;
	private Boolean pushNotificationsEnabled;
}
