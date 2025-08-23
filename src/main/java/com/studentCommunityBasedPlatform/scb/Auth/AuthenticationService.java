package com.studentCommunityBasedPlatform.scb.Auth;

import com.studentCommunityBasedPlatform.scb.config.JwtService;
import com.studentCommunityBasedPlatform.scb.entity.Role;
import com.studentCommunityBasedPlatform.scb.entity.User;
import com.studentCommunityBasedPlatform.scb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public AuthenticationResponse register(RegisterRequest request) {
		var user = User.builder()
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(Role.USER)
				// Set default values for new fields
				.isProfilePublic(true)
				.allowMessagesFromStrangers(true)
				.emailNotificationsEnabled(true)
				.pushNotificationsEnabled(true)
				.emailVerified(false)
				.phoneVerified(false)
				.isActive(true)
				.loginCount(1)
				.profileViews(0)
				.createdAt(LocalDateTime.now())
				.lastActiveAt(LocalDateTime.now())
				.lastLoginAt(LocalDateTime.now())
				.build();

		repository.save(user);
		var jwtToken = jwtService.generateToken(user);

		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getEmail(),
						request.getPassword()
				)
		);

		var user = repository.findByEmail(request.getEmail())
				.orElseThrow();

		// Update login tracking
		user.incrementLoginCount();
		repository.save(user);

		var jwtToken = jwtService.generateToken(user);

		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}
}