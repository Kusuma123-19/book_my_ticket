package com.jsp.book.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Component
public class CloudinaryHelper {

	private static final String MOVIE_FOLDER = "BMT-Movies";
	private static final String THEATER_FOLDER = "BMT-Theater";
	private static final String QR_FOLDER = "BMT-Theater-QR";

	private static final String FALLBACK_IMAGE = "https://placehold.co/600x400/EEE/31343C";

	private final Cloudinary cloudinary;
	private final boolean isDummy;
	private final Path uploadDir;

	public CloudinaryHelper(@Value("${cloudinary.url}") String cloudinaryUrl) {
		this.isDummy = cloudinaryUrl.contains("dummy_secret");
		if (!this.isDummy) {
			this.cloudinary = new Cloudinary(cloudinaryUrl);
		} else {
			this.cloudinary = null;
		}
		
		this.uploadDir = Paths.get("uploads");
		if (this.isDummy && !Files.exists(this.uploadDir)) {
			try {
				Files.createDirectories(this.uploadDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String generateImageLink(MultipartFile file) {
		return uploadFile(file, MOVIE_FOLDER);
	}

	public String getTheaterImageLink(MultipartFile file) {
		return uploadFile(file, THEATER_FOLDER);
	}

	public String saveTicketQr(byte[] qr) {
		if (isDummy) {
			try {
				String filename = "qr_" + UUID.randomUUID().toString() + ".png";
				Path filePath = uploadDir.resolve(filename);
				Files.write(filePath, qr);
				return "/uploads/" + filename;
			} catch (IOException e) {
				return FALLBACK_IMAGE;
			}
		}
		return uploadBytes(qr, QR_FOLDER);
	}

	/* ---------- Private helpers ---------- */

	private String uploadFile(MultipartFile file, String folder) {
		if (isDummy) {
			try {
				if (file.isEmpty()) return FALLBACK_IMAGE;
				String originalFilename = file.getOriginalFilename();
				String extension = originalFilename != null && originalFilename.contains(".") 
					? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
				String filename = folder + "_" + UUID.randomUUID().toString() + extension;
				Path filePath = uploadDir.resolve(filename);
				Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				return "/uploads/" + filename;
			} catch (IOException e) {
				e.printStackTrace();
				return FALLBACK_IMAGE;
			}
		} else {
			try {
				return uploadBytes(file.getBytes(), folder);
			} catch (IOException e) {
				return FALLBACK_IMAGE;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private String uploadBytes(byte[] data, String folder) {
		try {
			Map<String, Object> params = ObjectUtils.asMap("folder", folder, "use_filename", true);
			return (String) cloudinary.uploader().upload(data, params).get("url");
		} catch (Exception e) {
			e.printStackTrace();
			return FALLBACK_IMAGE;
		}
	}
}
