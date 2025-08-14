// =============================================================================
// UPDATE YOUR SecurityConfig.java - CORS Configuration
// =============================================================================

package com.studentCommunityBasedPlatform.scb.Security.user.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthFilter;
	private final AuthenticationProvider authenticationProvider;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.cors()  // 🎯 Enable CORS
				.and()
				.csrf()
				.disable()
				.authorizeHttpRequests()
				.requestMatchers("/api/auth/**")
				.permitAll()
				.anyRequest()
				.authenticated()
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	// 🌐 UPDATED CORS Configuration for Vite
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// 🎯 ALLOW VITE DEV SERVER (PORT 5173)
		configuration.setAllowedOriginPatterns(Arrays.asList(
				"http://localhost:5173",    // 🎯 Vite default port
				"http://127.0.0.1:5173",    // Alternative localhost
				"http://localhost:3000",    // Create React App (backup)
				"http://localhost:3001"     // Alternative ports
		));

		// Allow these HTTP methods
		configuration.setAllowedMethods(Arrays.asList(
				"GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
		));

		// Allow all headers
		configuration.setAllowedHeaders(Arrays.asList("*"));

		// Allow credentials (for JWT tokens)
		configuration.setAllowCredentials(true);

		// Cache preflight response for 1 hour
		configuration.setMaxAge(3600L);

		// Apply to all API endpoints
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", configuration);

		return source;
	}
}