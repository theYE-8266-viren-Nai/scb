package com.studentCommunityBasedPlatform.scb.repository;


import com.studentCommunityBasedPlatform.scb.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {

	// Find members of a group
	List<GroupMember> findByGroupIdAndIsActiveTrue(Integer groupId);

	// Find groups user is member of
	List<GroupMember> findByUserIdAndIsActiveTrue(Integer userId);

	// Check if user is member of group
	Optional<GroupMember> findByGroupIdAndUserIdAndIsActiveTrue(Integer groupId, Integer userId);

	// Count active members in group
	long countByGroupIdAndIsActiveTrue(Integer groupId);

	// Find group admins
	List<GroupMember> findByGroupIdAndRoleAndIsActiveTrue(Integer groupId, String role);
}
