package com.studentCommunityBasedPlatform.scb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${file.upload-dir:uploads}")
	private String uploadDir;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// Create absolute path to uploads directory
		File uploadPath = new File(uploadDir);
		String absolutePath = "file:" + uploadPath.getAbsolutePath() + "/";

		System.out.println("🔧 Configuring static file serving...");
		System.out.println("📁 Upload directory: " + uploadPath.getAbsolutePath());
		System.out.println("🌐 Serving from: " + absolutePath);
		System.out.println("🔗 URL pattern: /uploads/**");

		registry.addResourceHandler("/uploads/**")
				.addResourceLocations(absolutePath);

		// Verify directory exists
		if (!uploadPath.exists()) {
			System.out.println("⚠️ Upload directory does not exist, creating: " + uploadPath.getAbsolutePath());
			uploadPath.mkdirs();
		} else {
			System.out.println("✅ Upload directory exists and ready");
		}

		// List some files for verification
		File[] avatars = new File(uploadPath, "avatars").listFiles();
		File[] covers = new File(uploadPath, "covers").listFiles();

		if (avatars != null && avatars.length > 0) {
			System.out.println("📸 Found " + avatars.length + " avatar files");
		}
		if (covers != null && covers.length > 0) {
			System.out.println("🖼️ Found " + covers.length + " cover files");
		}
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:3000", "http://localhost:5173")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true);
	}
}