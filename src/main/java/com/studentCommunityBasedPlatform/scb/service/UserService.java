package com.studentCommunityBasedPlatform.scb.service;

import com.studentCommunityBasedPlatform.scb.dto.*;
import com.studentCommunityBasedPlatform.scb.entity.User;
import com.studentCommunityBasedPlatform.scb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	// ==================== PROFILE MANAGEMENT ====================

	public UserProfileResponse getUserProfile(Integer userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		// Increment profile views if not the same user
		user.incrementProfileViews();
		userRepository.save(user);

		return mapToUserProfileResponse(user);
	}

	public UserProfileResponse updateUserProfile(Integer userId, UpdateUserProfileRequest request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		// Update all fields
		if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
		if (request.getLastName() != null) user.setLastName(request.getLastName());
		if (request.getEmail() != null) user.setEmail(request.getEmail());
		if (request.getBio() != null) user.setBio(request.getBio());
		if (request.getLocation() != null) user.setLocation(request.getLocation());
		if (request.getUniversity() != null) user.setUniversity(request.getUniversity());
		if (request.getMajor() != null) user.setMajor(request.getMajor());
		if (request.getGraduationYear() != null) user.setGraduationYear(request.getGraduationYear());
		if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
		if (request.getWebsite() != null) user.setWebsite(request.getWebsite());
		if (request.getIsProfilePublic() != null) user.setIsProfilePublic(request.getIsProfilePublic());
		if (request.getAllowMessagesFromStrangers() != null) user.setAllowMessagesFromStrangers(request.getAllowMessagesFromStrangers());
		if (request.getEmailNotificationsEnabled() != null) user.setEmailNotificationsEnabled(request.getEmailNotificationsEnabled());
		if (request.getPushNotificationsEnabled() != null) user.setPushNotificationsEnabled(request.getPushNotificationsEnabled());

		User savedUser = userRepository.save(user);
		return mapToUserProfileResponse(savedUser);
	}

	public UserStatsResponse getUserStats(Integer userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		return UserStatsResponse.builder()
				.userId(user.getId())
				.totalPosts(0) // TODO: Calculate from posts
				.totalStudyGroups(0) // TODO: Calculate from study groups
				.totalConnections(0) // TODO: Calculate from connections
				.totalLikesReceived(0) // TODO: Calculate from post likes
				.totalCommentsReceived(0) // TODO: Calculate from comments
				.memberSince(user.getCreatedAt())
				.lastActive(user.getLastActiveAt())
				.loginStreak(user.getLoginCount())
				.profileViews(user.getProfileViews())
				.build();
	}

	// ==================== MEDIA MANAGEMENT ====================

	public UserProfileResponse uploadAvatar(Integer userId, MultipartFile file) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		// TODO: Implement actual file upload logic
		String filename = "avatar_" + userId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
		user.setAvatarUrl("/uploads/avatars/" + filename);

		User savedUser = userRepository.save(user);
		return mapToUserProfileResponse(savedUser);
	}

	public UserProfileResponse deleteAvatar(Integer userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		user.setAvatarUrl(null);
		User savedUser = userRepository.save(user);
		return mapToUserProfileResponse(savedUser);
	}

	public UserProfileResponse uploadCoverPhoto(Integer userId, MultipartFile file) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		// TODO: Implement actual file upload logic
		String filename = "cover_" + userId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
		user.setCoverPhotoUrl("/uploads/covers/" + filename);

		User savedUser = userRepository.save(user);
		return mapToUserProfileResponse(savedUser);
	}

	public UserProfileResponse deleteCoverPhoto(Integer userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		user.setCoverPhotoUrl(null);
		User savedUser = userRepository.save(user);
		return mapToUserProfileResponse(savedUser);
	}

	// ==================== SECURITY ====================

	public void changePassword(Integer userId, ChangePasswordRequest request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		// Verify current password
		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
			throw new RuntimeException("Current password is incorrect");
		}

		// Verify new password confirmation
		if (!request.getNewPassword().equals(request.getConfirmPassword())) {
			throw new RuntimeException("New password and confirmation do not match");
		}

		// Update password
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userRepository.save(user);
	}

	public void sendEmailVerification(Integer userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		// Generate verification token
		String token = UUID.randomUUID().toString();
		user.setEmailVerificationToken(token);
		user.setVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));

		userRepository.save(user);

		// TODO: Send actual email
		System.out.println("Email verification token for " + user.getEmail() + ": " + token);
	}

	public void verifyEmail(String token) {
		User user = userRepository.findByEmailVerificationToken(token)
				.orElseThrow(() -> new RuntimeException("Invalid verification token"));

		if (user.getVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Verification token has expired");
		}

		user.setEmailVerified(true);
		user.setEmailVerificationToken(null);
		user.setVerificationTokenExpiresAt(null);

		userRepository.save(user);
	}

	// ==================== PREFERENCES ====================

	public UserProfileResponse updatePrivacySettings(Integer userId, Map<String, Boolean> privacySettings) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (privacySettings.containsKey("isProfilePublic")) {
			user.setIsProfilePublic(privacySettings.get("isProfilePublic"));
		}
		if (privacySettings.containsKey("allowMessagesFromStrangers")) {
			user.setAllowMessagesFromStrangers(privacySettings.get("allowMessagesFromStrangers"));
		}

		User savedUser = userRepository.save(user);
		return mapToUserProfileResponse(savedUser);
	}

	public UserProfileResponse updateNotificationSettings(Integer userId, Map<String, Boolean> notificationSettings) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (notificationSettings.containsKey("emailNotificationsEnabled")) {
			user.setEmailNotificationsEnabled(notificationSettings.get("emailNotificationsEnabled"));
		}
		if (notificationSettings.containsKey("pushNotificationsEnabled")) {
			user.setPushNotificationsEnabled(notificationSettings.get("pushNotificationsEnabled"));
		}

		User savedUser = userRepository.save(user);
		return mapToUserProfileResponse(savedUser);
	}

	// ==================== SEARCH & DISCOVERY ====================

	public List<UserProfileResponse> searchUsers(String query, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);

		List<User> users = userRepository.findAll().stream()
				.filter(user -> user.getIsProfilePublic() && user.getIsActive())
				.filter(user ->
						(user.getFirstName() != null && user.getFirstName().toLowerCase().contains(query.toLowerCase())) ||
								(user.getLastName() != null && user.getLastName().toLowerCase().contains(query.toLowerCase())) ||
								(user.getEmail() != null && user.getEmail().toLowerCase().contains(query.toLowerCase())) ||
								(user.getUniversity() != null && user.getUniversity().toLowerCase().contains(query.toLowerCase())) ||
								(user.getMajor() != null && user.getMajor().toLowerCase().contains(query.toLowerCase())))
				.skip(page * size)
				.limit(size)
				.collect(Collectors.toList());

		return users.stream()
				.map(this::mapToUserProfileResponse)
				.collect(Collectors.toList());
	}

	public List<UserProfileResponse> advancedSearchUsers(String name, String university, String major,
														 Integer graduationYear, String location, int page, int size) {
		List<User> users = userRepository.findAll().stream()
				.filter(user -> user.getIsProfilePublic() && user.getIsActive())
				.filter(user -> {
					boolean matches = true;

					if (name != null && !name.trim().isEmpty()) {
						matches = matches && ((user.getFirstName() != null && user.getFirstName().toLowerCase().contains(name.toLowerCase())) ||
								(user.getLastName() != null && user.getLastName().toLowerCase().contains(name.toLowerCase())));
					}

					if (university != null && !university.trim().isEmpty()) {
						matches = matches && (user.getUniversity() != null &&
								user.getUniversity().toLowerCase().contains(university.toLowerCase()));
					}

					if (major != null && !major.trim().isEmpty()) {
						matches = matches && (user.getMajor() != null &&
								user.getMajor().toLowerCase().contains(major.toLowerCase()));
					}

					if (graduationYear != null) {
						matches = matches && Objects.equals(user.getGraduationYear(), graduationYear);
					}

					if (location != null && !location.trim().isEmpty()) {
						matches = matches && (user.getLocation() != null &&
								user.getLocation().toLowerCase().contains(location.toLowerCase()));
					}

					return matches;
				})
				.skip(page * size)
				.limit(size)
				.collect(Collectors.toList());

		return users.stream()
				.map(this::mapToUserProfileResponse)
				.collect(Collectors.toList());
	}

	public List<UserProfileResponse> getUserSuggestions(Integer userId, int limit) {
		User currentUser = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		// Simple suggestion algorithm: users with same university or major
		List<User> suggestions = userRepository.findAll().stream()
				.filter(user -> !user.getId().equals(userId))
				.filter(user -> user.getIsProfilePublic() && user.getIsActive())
				.filter(user ->
						(currentUser.getUniversity() != null &&
								Objects.equals(user.getUniversity(), currentUser.getUniversity())) ||
								(currentUser.getMajor() != null &&
										Objects.equals(user.getMajor(), currentUser.getMajor())))
				.limit(limit)
				.collect(Collectors.toList());

		return suggestions.stream()
				.map(this::mapToUserProfileResponse)
				.collect(Collectors.toList());
	}

	// ==================== ACTIVITY ====================

	public List<Map<String, Object>> getUserActivity(Integer userId, int page, int size) {
		// TODO: Implement actual activity tracking
		// For now, return placeholder data
		List<Map<String, Object>> activities = new ArrayList<>();

		Map<String, Object> activity1 = new HashMap<>();
		activity1.put("type", "profile_updated");
		activity1.put("description", "Updated profile information");
		activity1.put("timestamp", LocalDateTime.now().minusDays(1));
		activities.add(activity1);

		Map<String, Object> activity2 = new HashMap<>();
		activity2.put("type", "login");
		activity2.put("description", "Logged into the platform");
		activity2.put("timestamp", LocalDateTime.now().minusHours(2));
		activities.add(activity2);

		return activities.stream()
				.skip(page * size)
				.limit(size)
				.collect(Collectors.toList());
	}

	public List<Map<String, Object>> getPublicUserActivity(Integer userId, int page, int size) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (!user.getIsProfilePublic()) {
			return new ArrayList<>(); // Return empty if profile is private
		}

		// TODO: Implement actual public activity tracking
		List<Map<String, Object>> activities = new ArrayList<>();

		Map<String, Object> activity = new HashMap<>();
		activity.put("type", "joined");
		activity.put("description", "Joined the platform");
		activity.put("timestamp", user.getCreatedAt());
		activities.add(activity);

		return activities.stream()
				.skip(page * size)
				.limit(size)
				.collect(Collectors.toList());
	}

	// ==================== ACCOUNT MANAGEMENT ====================

	public void deactivateAccount(Integer userId, String reason) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		user.setIsActive(false);
		user.setDeactivationReason(reason);
		user.setDeactivatedAt(LocalDateTime.now());

		userRepository.save(user);
	}

	public void reactivateAccount(Integer userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		user.setIsActive(true);
		user.setDeactivationReason(null);
		user.setDeactivatedAt(null);
		user.setLastActiveAt(LocalDateTime.now());

		userRepository.save(user);
	}

	public void deleteAccount(Integer userId, String confirmation) {
		if (!"DELETE".equals(confirmation)) {
			throw new RuntimeException("Account deletion requires confirmation");
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		// TODO: Implement soft delete or cleanup related data
		userRepository.delete(user);
	}

	// ==================== HELPER METHODS ====================

	private UserProfileResponse mapToUserProfileResponse(User user) {
		return UserProfileResponse.builder()
				.id(user.getId())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.email(user.getEmail())
				.bio(user.getBio())
				.location(user.getLocation())
				.university(user.getUniversity())
				.major(user.getMajor())
				.graduationYear(user.getGraduationYear())
				.phoneNumber(user.getPhoneNumber())
				.website(user.getWebsite())
				.avatarUrl(user.getAvatarUrl())
				.coverPhotoUrl(user.getCoverPhotoUrl())
				.isProfilePublic(user.getIsProfilePublic())
				.allowMessagesFromStrangers(user.getAllowMessagesFromStrangers())
				.emailNotificationsEnabled(user.getEmailNotificationsEnabled())
				.pushNotificationsEnabled(user.getPushNotificationsEnabled())
				.emailVerified(user.getEmailVerified())
				.phoneVerified(user.getPhoneVerified())
				.createdAt(user.getCreatedAt())
				.lastActiveAt(user.getLastActiveAt())
				.isActive(user.getIsActive())
				.role(user.getRole().name())
				.postsCount(0) // TODO: Calculate actual count
				.studyGroupsCount(0) // TODO: Calculate actual count
				.connectionsCount(0) // TODO: Calculate actual count
				.build();
	}
}