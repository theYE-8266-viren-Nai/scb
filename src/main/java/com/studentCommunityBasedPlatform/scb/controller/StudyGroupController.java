package com.studentCommunityBasedPlatform.scb.controller;


import com.studentCommunityBasedPlatform.scb.dto.CreateStudyGroupRequest;
import com.studentCommunityBasedPlatform.scb.dto.StudyGroupResponse;
import com.studentCommunityBasedPlatform.scb.entity.GroupMember;
import com.studentCommunityBasedPlatform.scb.entity.User;
import com.studentCommunityBasedPlatform.scb.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class StudyGroupController {

	private final StudyGroupService studyGroupService;

	@PostMapping
	public ResponseEntity<StudyGroupResponse> createGroup(
			@RequestBody CreateStudyGroupRequest request,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		StudyGroupResponse group = studyGroupService.createStudyGroup(request, user.getId());
		return ResponseEntity.ok(group);
	}

	@GetMapping
	public ResponseEntity<List<StudyGroupResponse>> getAllGroups(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		List<StudyGroupResponse> groups = studyGroupService.getAllPublicGroups(user.getId());
		return ResponseEntity.ok(groups);
	}

	@GetMapping("/subject/{subject}")
	public ResponseEntity<List<StudyGroupResponse>> getGroupsBySubject(
			@PathVariable String subject,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		List<StudyGroupResponse> groups = studyGroupService.getGroupsBySubject(subject, user.getId());
		return ResponseEntity.ok(groups);
	}

	@GetMapping("/my-groups")
	public ResponseEntity<List<StudyGroupResponse>> getUserGroups(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		List<StudyGroupResponse> groups = studyGroupService.getUserGroups(user.getId());
		return ResponseEntity.ok(groups);
	}

	@GetMapping("/search")
	public ResponseEntity<List<StudyGroupResponse>> searchGroups(
			@RequestParam String q,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		List<StudyGroupResponse> groups = studyGroupService.searchGroups(q, user.getId());
		return ResponseEntity.ok(groups);
	}

	@GetMapping("/{groupId}")
	public ResponseEntity<StudyGroupResponse> getGroupById(
			@PathVariable Integer groupId,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		StudyGroupResponse group = studyGroupService.getGroupById(groupId, user.getId());
		return ResponseEntity.ok(group);
	}

	@PostMapping("/{groupId}/join")
	public ResponseEntity<StudyGroupResponse> joinGroup(
			@PathVariable Integer groupId,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		StudyGroupResponse group = studyGroupService.joinGroup(groupId, user.getId());
		return ResponseEntity.ok(group);
	}

	@PostMapping("/{groupId}/leave")
	public ResponseEntity<StudyGroupResponse> leaveGroup(
			@PathVariable Integer groupId,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		StudyGroupResponse group = studyGroupService.leaveGroup(groupId, user.getId());
		return ResponseEntity.ok(group);
	}

	@DeleteMapping("/{groupId}")
	public ResponseEntity<Void> deleteGroup(
			@PathVariable Integer groupId,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		studyGroupService.deleteGroup(groupId, user.getId());
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{groupId}/members")
	public ResponseEntity<List<GroupMember>> getGroupMembers(
			@PathVariable Integer groupId,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		List<GroupMember> members = studyGroupService.getGroupMembers(groupId, user.getId());
		return ResponseEntity.ok(members);
	}
}
