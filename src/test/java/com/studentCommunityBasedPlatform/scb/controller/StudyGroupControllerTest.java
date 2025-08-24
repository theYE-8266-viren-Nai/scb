package com.studentCommunityBasedPlatform.scb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentCommunityBasedPlatform.scb.dto.CreateStudyGroupRequest;
import com.studentCommunityBasedPlatform.scb.dto.StudyGroupResponse;
import com.studentCommunityBasedPlatform.scb.service.StudyGroupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudyGroupController.class)
@DisplayName("StudyGroup Controller Tests")
class StudyGroupControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StudyGroupService studyGroupService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@WithMockUser(username = "test@example.com", roles = "USER")
	@DisplayName("Should get all public groups")
	void getAllGroups_ShouldReturnListOfGroups() throws Exception {
		// Given
		StudyGroupResponse testGroup = StudyGroupResponse.builder()
				.id(1)
				.name("Test Study Group")
				.description("Test Description")
				.subject("Computer Science")
				.maxMembers(15)
				.currentMembers(5)
				.isPrivate(false)
				.createdAt(LocalDateTime.now())
				.creatorName("Test User")
				.creatorEmail("test@example.com")
				.isCurrentUserMember(false)
				.isCurrentUserCreator(false)
				.build();

		List<StudyGroupResponse> groups = Arrays.asList(testGroup);
		when(studyGroupService.getAllPublicGroups(anyInt())).thenReturn(groups);

		// When & Then
		mockMvc.perform(get("/api/groups"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].name").value("Test Study Group"))
				.andExpect(jsonPath("$[0].subject").value("Computer Science"));
	}

	@Test
	@WithMockUser(username = "test@example.com", roles = "USER")
	@DisplayName("Should create study group successfully")
	void createGroup_ShouldReturnCreatedGroup() throws Exception {
		// Given
		CreateStudyGroupRequest request = CreateStudyGroupRequest.builder()
				.name("New Study Group")
				.description("New Description")
				.subject("Mathematics")
				.maxMembers(10)
				.isPrivate(false)
				.build();

		StudyGroupResponse response = StudyGroupResponse.builder()
				.id(2)
				.name("New Study Group")
				.description("New Description")
				.subject("Mathematics")
				.maxMembers(10)
				.currentMembers(1)
				.isPrivate(false)
				.createdAt(LocalDateTime.now())
				.creatorName("Test User")
				.creatorEmail("test@example.com")
				.isCurrentUserMember(true)
				.isCurrentUserCreator(true)
				.build();

		when(studyGroupService.createStudyGroup(any(CreateStudyGroupRequest.class), anyInt()))
				.thenReturn(response);

		// When & Then
		mockMvc.perform(post("/api/groups")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("New Study Group"))
				.andExpect(jsonPath("$.subject").value("Mathematics"))
				.andExpect(jsonPath("$.isCurrentUserCreator").value(true));
	}

	@Test
	@WithMockUser(username = "test@example.com", roles = "USER")
	@DisplayName("Should search groups")
	void searchGroups_ShouldReturnMatchingGroups() throws Exception {
		// Given
		StudyGroupResponse testGroup = StudyGroupResponse.builder()
				.id(1)
				.name("Java Programming Group")
				.subject("Programming")
				.build();

		when(studyGroupService.searchGroups(eq("Java"), anyInt()))
				.thenReturn(Arrays.asList(testGroup));

		// When & Then
		mockMvc.perform(get("/api/groups/search")
						.param("q", "Java"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].name").value("Java Programming Group"));
	}

	@Test
	@DisplayName("Should return 401 for unauthenticated requests")
	void createGroup_WithoutAuthentication_ShouldReturn401() throws Exception {
		// Given
		CreateStudyGroupRequest request = CreateStudyGroupRequest.builder()
				.name("Test Group")
				.build();

		// When & Then
		mockMvc.perform(post("/api/groups")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isUnauthorized());
	}
}