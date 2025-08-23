package com.studentCommunityBasedPlatform.scb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_table")
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// Basic authentication fields
	private String firstName;
	private String lastName;
	private String email;
	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;

	// Extended profile fields
	@Column(name = "bio", length = 500)
	private String bio;

	@Column(name = "location", length = 100)
	private String location;

	@Column(name = "university", length = 100)
	private String university;

	@Column(name = "major", length = 100)
	private String major;

	@Column(name = "graduation_year")
	private Integer graduationYear;

	@Column(name = "phone_number", length = 15)
	private String phoneNumber;

	@Column(name = "website", length = 100)
	private String website;

	// Media fields
	@Column(name = "avatar_url")
	private String avatarUrl;

	@Column(name = "cover_photo_url")
	private String coverPhotoUrl;

	// Privacy settings
	@Column(name = "is_profile_public")
	@Builder.Default
	private Boolean isProfilePublic = true;

	@Column(name = "allow_messages_from_strangers")
	@Builder.Default
	private Boolean allowMessagesFromStrangers = true;

	// Notification settings
	@Column(name = "email_notifications_enabled")
	@Builder.Default
	private Boolean emailNotificationsEnabled = true;

	@Column(name = "push_notifications_enabled")
	@Builder.Default
	private Boolean pushNotificationsEnabled = true;

	// Verification status
	@Column(name = "email_verified")
	@Builder.Default
	private Boolean emailVerified = false;

	@Column(name = "phone_verified")
	@Builder.Default
	private Boolean phoneVerified = false;

	@Column(name = "email_verification_token")
	private String emailVerificationToken;

	@Column(name = "phone_verification_code")
	private String phoneVerificationCode;

	@Column(name = "verification_token_expires_at")
	private LocalDateTime verificationTokenExpiresAt;

	// Account status and timestamps
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "last_active_at")
	private LocalDateTime lastActiveAt;

	@Column(name = "is_active")
	@Builder.Default
	private Boolean isActive = true;

	@Column(name = "deactivation_reason")
	private String deactivationReason;

	@Column(name = "deactivated_at")
	private LocalDateTime deactivatedAt;

	// Activity tracking
	@Column(name = "login_count")
	@Builder.Default
	private Integer loginCount = 0;

	@Column(name = "profile_views")
	@Builder.Default
	private Integer profileViews = 0;

	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;

	// Lifecycle methods
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		lastActiveAt = LocalDateTime.now();
		if (isActive == null) isActive = true;
		if (isProfilePublic == null) isProfilePublic = true;
		if (allowMessagesFromStrangers == null) allowMessagesFromStrangers = true;
		if (emailNotificationsEnabled == null) emailNotificationsEnabled = true;
		if (pushNotificationsEnabled == null) pushNotificationsEnabled = true;
		if (emailVerified == null) emailVerified = false;
		if (phoneVerified == null) phoneVerified = false;
		if (loginCount == null) loginCount = 0;
		if (profileViews == null) profileViews = 0;
	}

	@PreUpdate
	protected void onUpdate() {
		lastActiveAt = LocalDateTime.now();
	}

	// UserDetails implementation
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return isActive;
	}

	@Override
	public boolean isAccountNonLocked() {
		return isActive;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return isActive;
	}

	@Override
	public boolean isEnabled() {
		return isActive ;
	}

	// Helper methods
	public void incrementLoginCount() {
		this.loginCount = (this.loginCount == null ? 0 : this.loginCount) + 1;
		this.lastLoginAt = LocalDateTime.now();
		this.lastActiveAt = LocalDateTime.now();
	}

	public void incrementProfileViews() {
		this.profileViews = (this.profileViews == null ? 0 : this.profileViews) + 1;
	}
}