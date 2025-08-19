package com.studentCommunityBasedPlatform.scb.service;

import com.studentCommunityBasedPlatform.scb.dto.CreateStudyGroupRequest;
import com.studentCommunityBasedPlatform.scb.dto.StudyGroupResponse;
import com.studentCommunityBasedPlatform.scb.entity.GroupMember;
import com.studentCommunityBasedPlatform.scb.entity.StudyGroup;
import com.studentCommunityBasedPlatform.scb.entity.User;
import com.studentCommunityBasedPlatform.scb.repository.StudyGroupRepository;
import com.studentCommunityBasedPlatform.scb.repository.GroupMemberRepository;
import com.studentCommunityBasedPlatform.scb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyGroupService {

	private final StudyGroupRepository studyGroupRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final UserRepository userRepository;

	@Transactional
	public StudyGroupResponse createStudyGroup(CreateStudyGroupRequest request, Integer creatorId) {
		User creator = userRepository.findById(creatorId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		StudyGroup studyGroup = StudyGroup.builder()
				.name(request.getName())
				.description(request.getDescription())
				.subject(request.getSubject())
				.maxMembers(request.getMaxMembers())
				.currentMembers(1)
				.creatorId(creatorId)
				.isPrivate(request.getIsPrivate())
				.meetingSchedule(request.getMeetingSchedule())
				.groupImageUrl(request.getGroupImageUrl())
				.build();

		StudyGroup savedGroup = studyGroupRepository.save(studyGroup);

		// Add creator as admin member
		GroupMember creatorMember = GroupMember.builder()
				.groupId(savedGroup.getId())
				.userId(creatorId)
				.role("admin")
				.isActive(true)
				.build();

		groupMemberRepository.save(creatorMember);

		return convertToStudyGroupResponse(savedGroup, creatorId);
	}

	public List<StudyGroupResponse> getAllPublicGroups(Integer userId) {
		Pageable pageable = PageRequest.of(0, 50);
		Page<StudyGroup> groups = studyGroupRepository.findByIsPrivateFalseOrderByCreatedAtDesc(pageable);

		return groups.getContent().stream()
				.map(group -> convertToStudyGroupResponse(group, userId))
				.collect(Collectors.toList());
	}

	public List<StudyGroupResponse> getGroupsBySubject(String subject, Integer userId) {
		List<StudyGroup> groups = studyGroupRepository.findBySubjectOrderByCreatedAtDesc(subject);

		return groups.stream()
				.filter(group -> !group.getIsPrivate())
				.map(group -> convertToStudyGroupResponse(group, userId))
				.collect(Collectors.toList());
	}

	public List<StudyGroupResponse> getUserGroups(Integer userId) {
		List<GroupMember> memberships = groupMemberRepository.findByUserIdAndIsActiveTrue(userId);

		return memberships.stream()
				.map(membership -> {
					StudyGroup group = studyGroupRepository.findById(membership.getGroupId())
							.orElse(null);
					return group != null ? convertToStudyGroupResponse(group, userId) : null;
				})
				.filter(group -> group != null)
				.collect(Collectors.toList());
	}

	public List<StudyGroupResponse> searchGroups(String searchTerm, Integer userId) {
		List<StudyGroup> groups = studyGroupRepository.searchGroups(searchTerm);

		return groups.stream()
				.map(group -> convertToStudyGroupResponse(group, userId))
				.collect(Collectors.toList());
	}

	public StudyGroupResponse getGroupById(Integer groupId, Integer userId) {
		StudyGroup group = studyGroupRepository.findById(groupId)
				.orElseThrow(() -> new RuntimeException("Study group not found"));

		return convertToStudyGroupResponse(group, userId);
	}

	@Transactional
	public StudyGroupResponse joinGroup(Integer groupId, Integer userId) {
		StudyGroup group = studyGroupRepository.findById(groupId)
				.orElseThrow(() -> new RuntimeException("Study group not found"));

		// Check if user is already a member
		Optional<GroupMember> existingMember = groupMemberRepository
				.findByGroupIdAndUserIdAndIsActiveTrue(groupId, userId);

		if (existingMember.isPresent()) {
			throw new RuntimeException("User is already a member of this group");
		}

		// Check if group is full
		if (group.getCurrentMembers() >= group.getMaxMembers()) {
			throw new RuntimeException("Group is full");
		}

		// Add user as member
		GroupMember newMember = GroupMember.builder()
				.groupId(groupId)
				.userId(userId)
				.role("member")
				.isActive(true)
				.build();

		groupMemberRepository.save(newMember);

		// Update current members count
		group.setCurrentMembers(group.getCurrentMembers() + 1);
		StudyGroup updatedGroup = studyGroupRepository.save(group);

		return convertToStudyGroupResponse(updatedGroup, userId);
	}

	@Transactional
	public StudyGroupResponse leaveGroup(Integer groupId, Integer userId) {
		StudyGroup group = studyGroupRepository.findById(groupId)
				.orElseThrow(() -> new RuntimeException("Study group not found"));

		GroupMember member = groupMemberRepository
				.findByGroupIdAndUserIdAndIsActiveTrue(groupId, userId)
				.orElseThrow(() -> new RuntimeException("User is not a member of this group"));

		// Don't allow creator to leave if there are other members
		if (group.getCreatorId().equals(userId) && group.getCurrentMembers() > 1) {
			throw new RuntimeException("Creator cannot leave group with other members. Transfer ownership or delete group.");
		}

		// Remove member
		member.setIsActive(false);
		groupMemberRepository.save(member);

		// Update current members count
		group.setCurrentMembers(group.getCurrentMembers() - 1);
		StudyGroup updatedGroup = studyGroupRepository.save(group);

		return convertToStudyGroupResponse(updatedGroup, userId);
	}

	@Transactional
	public void deleteGroup(Integer groupId, Integer userId) {
		StudyGroup group = studyGroupRepository.findById(groupId)
				.orElseThrow(() -> new RuntimeException("Study group not found"));

		// Only creator can delete group
		if (!group.getCreatorId().equals(userId)) {
			throw new RuntimeException("Only group creator can delete the group");
		}

		studyGroupRepository.delete(group);
	}

	public List<GroupMember> getGroupMembers(Integer groupId, Integer userId) {
		StudyGroup group = studyGroupRepository.findById(groupId)
				.orElseThrow(() -> new RuntimeException("Study group not found"));

		// Check if user has access to view members
		Optional<GroupMember> userMembership = groupMemberRepository
				.findByGroupIdAndUserIdAndIsActiveTrue(groupId, userId);

		if (group.getIsPrivate() && userMembership.isEmpty()) {
			throw new RuntimeException("Access denied: Private group");
		}

		return groupMemberRepository.findByGroupIdAndIsActiveTrue(groupId);
	}

	private StudyGroupResponse convertToStudyGroupResponse(StudyGroup group, Integer currentUserId) {
		User creator = userRepository.findById(group.getCreatorId()).orElse(null);

		Optional<GroupMember> userMembership = currentUserId != null ?
				groupMemberRepository.findByGroupIdAndUserIdAndIsActiveTrue(group.getId(), currentUserId) :
				Optional.empty();

		boolean isMember = userMembership.isPresent();
		boolean isCreator = group.getCreatorId().equals(currentUserId);
		String userRole = userMembership.map(GroupMember::getRole).orElse(null);

		return StudyGroupResponse.builder()
				.id(group.getId())
				.name(group.getName())
				.description(group.getDescription())
				.subject(group.getSubject())
				.maxMembers(group.getMaxMembers())
				.currentMembers(group.getCurrentMembers())
				.isPrivate(group.getIsPrivate())
				.meetingSchedule(group.getMeetingSchedule())
				.groupImageUrl(group.getGroupImageUrl())
				.createdAt(group.getCreatedAt())
				.creatorName(creator != null ? creator.getFirstName() + " " + creator.getLastName() : "Unknown")
				.creatorEmail(creator != null ? creator.getEmail() : "unknown")
				.isCurrentUserMember(isMember)
				.isCurrentUserCreator(isCreator)
				.currentUserRole(userRole)
				.build();
	}
}