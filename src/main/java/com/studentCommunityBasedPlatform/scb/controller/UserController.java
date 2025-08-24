package com.studentCommunityBasedPlatform.scb.controller;

import com.studentCommunityBasedPlatform.scb.dto.UpdateUserProfileRequest;
import com.studentCommunityBasedPlatform.scb.dto.UserProfileResponse;
import com.studentCommunityBasedPlatform.scb.dto.ChangePasswordRequest;
import com.studentCommunityBasedPlatform.scb.dto.UserStatsResponse;
import com.studentCommunityBasedPlatform.scb.entity.User;
import com.studentCommunityBasedPlatform.scb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	// ==================== PROFILE MANAGEMENT ====================

	@GetMapping("/profile")
	public ResponseEntity<UserProfileResponse> getCurrentUserProfile(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		UserProfileResponse profile = userService.getUserProfile(user.getId());
		return ResponseEntity.ok(profile);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Integer userId) {
		UserProfileResponse profile = userService.getUserProfile(userId);
		return ResponseEntity.ok(profile);
	}

	@PutMapping("/profile")
	public ResponseEntity<UserProfileResponse> updateProfile(
			@Valid @RequestBody UpdateUserProfileRequest request,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		UserProfileResponse updatedProfile = userService.updateUserProfile(user.getId(), request);
		return ResponseEntity.ok(updatedProfile);
	}

	@GetMapping("/profile/stats")
	public ResponseEntity<UserStatsResponse> getCurrentUserStats(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		UserStatsResponse stats = userService.getUserStats(user.getId());
		return ResponseEntity.ok(stats);
	}

	@GetMapping("/{userId}/stats")
	public ResponseEntity<UserStatsResponse> getUserStats(@PathVariable Integer userId) {
		UserStatsResponse stats = userService.getUserStats(userId);
		return ResponseEntity.ok(stats);
	}

	// ==================== AVATAR & MEDIA MANAGEMENT ====================

	@PostMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UserProfileResponse> uploadAvatar(
			@RequestParam("file") MultipartFile file,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		UserProfileResponse updatedProfile = userService.uploadAvatar(user.getId(), file);
		return ResponseEntity.ok(updatedProfile);
	}

	@DeleteMapping("/profile/avatar")
	public ResponseEntity<UserProfileResponse> deleteAvatar(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		UserProfileResponse updatedProfile = userService.deleteAvatar(user.getId());
		return ResponseEntity.ok(updatedProfile);
	}

	@PostMapping(value = "/profile/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UserProfileResponse> uploadCoverPhoto(
			@RequestParam("file") MultipartFile file,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		UserProfileResponse updatedProfile = userService.uploadCoverPhoto(user.getId(), file);
		return ResponseEntity.ok(updatedProfile);
	}

	@DeleteMapping("/profile/cover")
	public ResponseEntity<UserProfileResponse> deleteCoverPhoto(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		UserProfileResponse updatedProfile = userService.deleteCoverPhoto(user.getId());
		return ResponseEntity.ok(updatedProfile);
	}

	// ==================== ACCOUNT SECURITY ====================

	@PostMapping("/change-password")
	public ResponseEntity<Map<String, String>> changePassword(
			@Valid @RequestBody ChangePasswordRequest request,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		userService.changePassword(user.getId(), request);
		return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
	}

	@PostMapping("/verify-email")
	public ResponseEntity<Map<String, String>> sendEmailVerification(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		userService.sendEmailVerification(user.getId());
		return ResponseEntity.ok(Map.of("message", "Verification email sent"));
	}

	@PostMapping("/verify-email/{token}")
	public ResponseEntity<Map<String, String>> verifyEmail(@PathVariable String token) {
		userService.verifyEmail(token);
		return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
	}

	// ==================== USER PREFERENCES & SETTINGS ====================

	@PutMapping("/preferences/privacy")
	public ResponseEntity<UserProfileResponse> updatePrivacySettings(
			@RequestBody Map<String, Boolean> privacySettings,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		UserProfileResponse updatedProfile = userService.updatePrivacySettings(user.getId(), privacySettings);
		return ResponseEntity.ok(updatedProfile);
	}

	@PutMapping("/preferences/notifications")
	public ResponseEntity<UserProfileResponse> updateNotificationSettings(
			@RequestBody Map<String, Boolean> notificationSettings,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		UserProfileResponse updatedProfile = userService.updateNotificationSettings(user.getId(), notificationSettings);
		return ResponseEntity.ok(updatedProfile);
	}

	// ==================== SEARCH & DISCOVERY ====================

	@GetMapping("/search")
	public ResponseEntity<List<UserProfileResponse>> searchUsers(
			@RequestParam String q,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		List<UserProfileResponse> users = userService.searchUsers(q, page, size);
		return ResponseEntity.ok(users);
	}

	@GetMapping("/search/advanced")
	public ResponseEntity<List<UserProfileResponse>> advancedSearchUsers(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String university,
			@RequestParam(required = false) String major,
			@RequestParam(required = false) Integer graduationYear,
			@RequestParam(required = false) String location,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		List<UserProfileResponse> users = userService.advancedSearchUsers(
				name, university, major, graduationYear, location, page, size);
		return ResponseEntity.ok(users);
	}

	@GetMapping("/suggestions")
	public ResponseEntity<List<UserProfileResponse>> getUserSuggestions(
			Authentication authentication,
			@RequestParam(defaultValue = "10") int limit) {
		User user = (User) authentication.getPrincipal();
		List<UserProfileResponse> suggestions = userService.getUserSuggestions(user.getId(), limit);
		return ResponseEntity.ok(suggestions);
	}

	// ==================== ACTIVITY & HISTORY ====================

	@GetMapping("/activity")
	public ResponseEntity<List<Map<String, Object>>> getUserActivity(
			Authentication authentication,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		User user = (User) authentication.getPrincipal();
		List<Map<String, Object>> activity = userService.getUserActivity(user.getId(), page, size);
		return ResponseEntity.ok(activity);
	}

	@GetMapping("/{userId}/activity/public")
	public ResponseEntity<List<Map<String, Object>>> getPublicUserActivity(
			@PathVariable Integer userId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		List<Map<String, Object>> activity = userService.getPublicUserActivity(userId, page, size);
		return ResponseEntity.ok(activity);
	}

	// ==================== PROFILE VIEW TRACKING ====================

	@PostMapping("/{userId}/view")
	public ResponseEntity<Map<String, String>> incrementProfileView(
			@PathVariable Integer userId,
			Authentication authentication) {
		User currentUser = (User) authentication.getPrincipal();

		// Don't increment views for own profile
		if (currentUser.getId().equals(userId)) {
			return ResponseEntity.ok(Map.of("message", "Cannot view own profile"));
		}

		userService.incrementProfileView(userId, currentUser.getId());
		return ResponseEntity.ok(Map.of("message", "Profile view recorded"));
	}

	// ==================== ACCOUNT MANAGEMENT ====================

	@PostMapping("/account/deactivate")
	public ResponseEntity<Map<String, String>> deactivateAccount(
			@RequestBody Map<String, String> reason,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		userService.deactivateAccount(user.getId(), reason.get("reason"));
		return ResponseEntity.ok(Map.of("message", "Account deactivated successfully"));
	}

	@PostMapping("/account/reactivate")
	public ResponseEntity<Map<String, String>> reactivateAccount(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		userService.reactivateAccount(user.getId());
		return ResponseEntity.ok(Map.of("message", "Account reactivated successfully"));
	}

	@DeleteMapping("/account")
	public ResponseEntity<Map<String, String>> deleteAccount(
			@RequestBody Map<String, String> confirmation,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		userService.deleteAccount(user.getId(), confirmation.get("confirmation"));
		return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
	}
}