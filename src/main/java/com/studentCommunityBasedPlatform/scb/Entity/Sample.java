//package com.studentCommunityBasedPlatform.scb.Entity;
//
//import jakarta.persistence.*;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "Sample")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class Sample {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long id;
//
//	@Column(name = "username", nullable = false, unique = true)
//	private String username;
//
//	@Column(name = "email", nullable = false, unique = true)
//	private String email;
//
//	@Column(name = "password", nullable = false)
//	private String password;
//
//	@Column(name = "first_name")
//	private String firstName;
//
//	@Column(name = "last_name")
//	private String lastName;
//
//	@Column(name = "phone_number")
//	private String phoneNumber;
//
//	@Enumerated(EnumType.STRING)
//	@Column(name = "role", nullable = false)
//	private Role role = Role.USER;
//
//	@Column(name = "is_active", nullable = false)
//	private Boolean isActive = true;
//
//	@Column(name = "is_email_verified", nullable = false)
//	private Boolean isEmailVerified = false;
//
//	@CreationTimestamp
//	@Column(name = "created_at", nullable = false, updatable = false)
//	private LocalDateTime createdAt;
//
//	@UpdateTimestamp
//	@Column(name = "updated_at")
//	private LocalDateTime updatedAt;
//
//	// Enum for user roles
//	public enum Role {
//		USER, ADMIN, MODERATOR
//	}
//}