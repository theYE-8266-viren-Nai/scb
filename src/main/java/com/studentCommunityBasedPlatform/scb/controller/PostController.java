package com.studentCommunityBasedPlatform.scb.controller;


import com.studentCommunityBasedPlatform.scb.dto.CreatePostRequest;
import com.studentCommunityBasedPlatform.scb.dto.PostResponse;
import com.studentCommunityBasedPlatform.scb.entity.User;
import com.studentCommunityBasedPlatform.scb.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@PostMapping
	public ResponseEntity<PostResponse> createPost(
			@RequestBody CreatePostRequest request,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		PostResponse post = postService.createPost(request, user.getId());
		return ResponseEntity.ok(post);
	}

	@GetMapping
	public ResponseEntity<List<PostResponse>> getAllPosts(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		List<PostResponse> posts = postService.getAllPosts(user.getId());
		return ResponseEntity.ok(posts);
	}

	@GetMapping("/category/{category}")
	public ResponseEntity<List<PostResponse>> getPostsByCategory(
			@PathVariable String category,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		List<PostResponse> posts = postService.getPostsByCategory(category, user.getId());
		return ResponseEntity.ok(posts);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<PostResponse>> getUserPosts(
			@PathVariable Integer userId,
			Authentication authentication) {
		User currentUser = (User) authentication.getPrincipal();
		List<PostResponse> posts = postService.getUserPosts(userId, currentUser.getId());
		return ResponseEntity.ok(posts);
	}

	@GetMapping("/search")
	public ResponseEntity<List<PostResponse>> searchPosts(
			@RequestParam String q,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		List<PostResponse> posts = postService.searchPosts(q, user.getId());
		return ResponseEntity.ok(posts);
	}

	@PostMapping("/{postId}/like")
	public ResponseEntity<PostResponse> toggleLike(
			@PathVariable Integer postId,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		PostResponse post = postService.toggleLike(postId, user.getId());
		return ResponseEntity.ok(post);
	}

	@PostMapping("/{postId}/save")
	public ResponseEntity<PostResponse> toggleSave(
			@PathVariable Integer postId,
			Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		PostResponse post = postService.toggleSave(postId, user.getId());
		return ResponseEntity.ok(post);
	}
}