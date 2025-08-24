package com.studentCommunityBasedPlatform.scb.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

	@Value("${file.upload-dir:uploads}")
	private String uploadDir;

	@Value("${server.port:8080}")
	private String serverPort;

	private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

	public String saveFile(MultipartFile file, String subDirectory, String prefix) throws IOException {
		// Validate file
		validateFile(file);

		// Create directories if they don't exist
		Path uploadPath = Paths.get(uploadDir).resolve(subDirectory);
		Files.createDirectories(uploadPath);

		// Generate unique filename
		String originalFilename = file.getOriginalFilename();
		String extension = getFileExtension(originalFilename);
		String filename = prefix + "_" + UUID.randomUUID().toString() + "." + extension;

		// Save file
		Path filePath = uploadPath.resolve(filename);
		Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		// Return the URL path (not absolute path)
		String fileUrl = "/uploads/" + subDirectory + "/" + filename;

		System.out.println("📁 File saved: " + filePath.toAbsolutePath());
		System.out.println("🌐 File URL: http://localhost:" + serverPort + fileUrl);

		return fileUrl;
	}

	public void deleteFile(String fileUrl) {
		if (fileUrl == null || !fileUrl.startsWith("/uploads/")) {
			return;
		}

		try {
			// Convert URL to file path
			String relativePath = fileUrl.substring("/uploads/".length());
			Path filePath = Paths.get(uploadDir).resolve(relativePath);

			if (Files.exists(filePath)) {
				Files.delete(filePath);
				System.out.println("🗑️ File deleted: " + filePath.toAbsolutePath());
			} else {
				System.out.println("⚠️ File not found for deletion: " + filePath.toAbsolutePath());
			}
		} catch (IOException e) {
			System.err.println("❌ Error deleting file: " + fileUrl);
			e.printStackTrace();
		}
	}

	private void validateFile(MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new IOException("File is empty");
		}

		if (file.getSize() > MAX_FILE_SIZE) {
			throw new IOException("File size exceeds maximum limit of 5MB");
		}

		String filename = file.getOriginalFilename();
		if (filename == null) {
			throw new IOException("Invalid filename");
		}

		String extension = getFileExtension(filename).toLowerCase();
		if (!ALLOWED_EXTENSIONS.contains(extension)) {
			throw new IOException("File type not allowed. Supported types: " + ALLOWED_EXTENSIONS);
		}

		// Check if it's actually an image by reading file header
		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new IOException("File must be an image");
		}
	}

	private String getFileExtension(String filename) {
		if (filename == null || filename.lastIndexOf('.') == -1) {
			return "";
		}
		return filename.substring(filename.lastIndexOf('.') + 1);
	}
}