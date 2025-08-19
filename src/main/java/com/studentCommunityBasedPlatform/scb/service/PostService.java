package com.studentCommunityBasedPlatform.scb.service;


import com.studentCommunityBasedPlatform.scb.dto.CreatePostRequest;
import com.studentCommunityBasedPlatform.scb.dto.PostResponse;
import com.studentCommunityBasedPlatform.scb.entity.Post;
import com.studentCommunityBasedPlatform.scb.entity.PostLike;
import com.studentCommunityBasedPlatform.scb.entity.PostSave;
import com.studentCommunityBasedPlatform.scb.entity.User;
import com.studentCommunityBasedPlatform.scb.repository.PostRepository;
import com.studentCommunityBasedPlatform.scb.repository.PostLikeRepository;
import com.studentCommunityBasedPlatform.scb.repository.PostSaveRepository;
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
public class PostService {

	private final PostRepository postRepository;
	private final PostLikeRepository postLikeRepository;
	private final PostSaveRepository postSaveRepository;
	private final UserRepository userRepository;

	@Transactional
	public PostResponse createPost(CreatePostRequest request, Integer userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		Post post = Post.builder()
				.title(request.getTitle())
				.description(request.getDescription())
				.content(request.getContent())
				.category(request.getCategory())
				.tags(request.getTags())
				.imageUrl(request.getImageUrl())
				.isPublic(request.getIsPublic())
				.userId(userId)
				.build();

		Post savedPost = postRepository.save(post);
		return convertToPostResponse(savedPost, userId);
	}

	public List<PostResponse> getAllPosts(Integer userId) {
		Pageable pageable = PageRequest.of(0, 50);
		Page<Post> posts = postRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);

		return posts.getContent().stream()
				.map(post -> convertToPostResponse(post, userId))
				.collect(Collectors.toList());
	}

	public List<PostResponse> getPostsByCategory(String category, Integer userId) {
		List<Post> posts = postRepository.findByCategoryOrderByCreatedAtDesc(category);

		return posts.stream()
				.map(post -> convertToPostResponse(post, userId))
				.collect(Collectors.toList());
	}

	public List<PostResponse> getUserPosts(Integer targetUserId, Integer currentUserId) {
		List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(targetUserId);

		return posts.stream()
				.map(post -> convertToPostResponse(post, currentUserId))
				.collect(Collectors.toList());
	}

	public List<PostResponse> searchPosts(String searchTerm, Integer userId) {
		List<Post> posts = postRepository.searchPosts(searchTerm);

		return posts.stream()
				.map(post -> convertToPostResponse(post, userId))
				.collect(Collectors.toList());
	}

	@Transactional
	public PostResponse toggleLike(Integer postId, Integer userId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new RuntimeException("Post not found"));

		Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId);

		if (existingLike.isPresent()) {
			// Unlike
			postLikeRepository.delete(existingLike.get());
			// 🔧 FIX: Handle null likesCount
			int currentLikes = post.getLikesCount() != null ? post.getLikesCount() : 0;
			post.setLikesCount(Math.max(0, currentLikes - 1));
		} else {
			// Like
			PostLike newLike = PostLike.builder()
					.postId(postId)
					.userId(userId)
					.build();
			postLikeRepository.save(newLike);

			// 🔧 FIX: Handle null likesCount
			int currentLikes = post.getLikesCount() != null ? post.getLikesCount() : 0;
			post.setLikesCount(currentLikes + 1);
		}

		Post updatedPost = postRepository.save(post);
		return convertToPostResponse(updatedPost, userId);
	}
	@Transactional
	public PostResponse toggleSave(Integer postId, Integer userId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new RuntimeException("Post not found"));

		Optional<PostSave> existingSave = postSaveRepository.findByPostIdAndUserId(postId, userId);

		if (existingSave.isPresent()) {
			// Unsave
			postSaveRepository.delete(existingSave.get());
			// 🔧 FIX: Handle null savesCount
			int currentSaves = post.getSavesCount() != null ? post.getSavesCount() : 0;
			post.setSavesCount(Math.max(0, currentSaves - 1));
		} else {
			// Save
			PostSave newSave = PostSave.builder()
					.postId(postId)
					.userId(userId)
					.build();
			postSaveRepository.save(newSave);
			// 🔧 FIX: Handle null savesCount
			int currentSaves = post.getSavesCount() != null ? post.getSavesCount() : 0;
			post.setSavesCount(currentSaves + 1);
		}

		Post updatedPost = postRepository.save(post);
		return convertToPostResponse(updatedPost, userId);
	}
	private PostResponse convertToPostResponse(Post post, Integer currentUserId) {
		User author = userRepository.findById(post.getUserId()).orElse(null);

		boolean isLiked = currentUserId != null &&
				postLikeRepository.findByPostIdAndUserId(post.getId(), currentUserId).isPresent();
		boolean isSaved = currentUserId != null &&
				postSaveRepository.findByPostIdAndUserId(post.getId(), currentUserId).isPresent();

		return PostResponse.builder()
				.id(post.getId())
				.title(post.getTitle())
				.description(post.getDescription())
				.content(post.getContent())
				.category(post.getCategory())
				.tags(post.getTags())
				.imageUrl(post.getImageUrl())
				.likesCount(post.getLikesCount())
				.savesCount(post.getSavesCount())
				.isPublic(post.getIsPublic())
				.createdAt(post.getCreatedAt())
				.updatedAt(post.getUpdatedAt())
				.authorName(author != null ? author.getFirstName() + " " + author.getLastName() : "Unknown")
				.authorEmail(author != null ? author.getEmail() : "unknown")
				.isLikedByCurrentUser(isLiked)
				.isSavedByCurrentUser(isSaved)
				.build();
	}
}