package com.warranty.warranty.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.warranty.warranty.entities.AuthTokenEntity;
import com.warranty.warranty.entities.UserEntity;
import com.warranty.warranty.model.UploadPhotoResponse;
import com.warranty.warranty.repositories.UserRepository;

import com.warranty.warranty.entities.AuthTokenEntity;
import com.warranty.warranty.entities.UserEntity;
import com.warranty.warranty.model.UploadPhotoResponse;
import com.warranty.warranty.repositories.UserRepository;


import com.warranty.warranty.model.UserResponse;

@Service
public class UploadPhotoService {

	private static final Logger logger = LogManager.getLogger(UploadPhotoService.class);
	@Value("${app.uploadpath}")
	private String UPLOAD_DIR;
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private LoginService loginService;

	public UploadPhotoResponse UploadPhoto(String authtoken, MultipartFile file) {
		UploadPhotoResponse photoResponse=new UploadPhotoResponse();
		try {
			String fileOriginalName =file.getOriginalFilename();
			//UserResponse userResponse = new UserResponse();
			logger.info("getOriginalFilename..." + fileOriginalName);
			if (!isValidFileName(fileOriginalName)) {
				logger.info("file name validation.....");
				photoResponse.setMessage("Invalid file name or extension. Only JPEG, JPG, GIF or PNG file extensions allowed");
				photoResponse.setStatus("failed");
				photoResponse.setPhoto_url(null);
				return photoResponse;
			}
			
			if (fileOriginalName.length() > 100) {
				logger.info("getOriginalFilename length..." + fileOriginalName.length());
				photoResponse.setMessage("File name is greater than limit 100 char.");
				photoResponse.setStatus("failed");
				photoResponse.setPhoto_url(null);
				return photoResponse;
			}

			// Ensure directory exists
			Path uploadPath = Paths.get(UPLOAD_DIR);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			logger.info("file path.." + uploadPath.getFileName());
			// Generate unique file name
			String filename = UUID.randomUUID() + "_" + fileOriginalName;
			Path filePath = uploadPath.resolve(filename);
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			String fileUrl =  filename;
			AuthTokenEntity authTokenEntity = loginService.getAuthTokenEntity(authtoken);
			if (authTokenEntity == null) {
				photoResponse.setMessage("Invalid user.");
				photoResponse.setStatus("failed");
				photoResponse.setPhoto_url(null);
				return photoResponse;
			}
			Optional<UserEntity> userinfoOptional = userRepository.findById(authTokenEntity.getUserId());

		

			if (userinfoOptional.isPresent() == true) {
				// Update existing device
				logger.info("userinfoOptional is present" + userinfoOptional.isPresent());
				UserEntity userInfo = userinfoOptional.get();
				userInfo.setPhotoUrl(fileUrl);
				userRepository.save(userInfo);

				photoResponse.setMessage("Uploaded successfully");
				photoResponse.setStatus("success");
				photoResponse.setPhoto_url(fileUrl);

				return photoResponse;
			}
			photoResponse.setMessage("Invalid User");
			photoResponse.setStatus("failed");
			photoResponse.setPhoto_url(fileUrl);

			return photoResponse;
		} catch (IOException e) {
			photoResponse.setMessage("Photo upload failed");
			photoResponse.setStatus("failed");
			photoResponse.setPhoto_url(null);
			return photoResponse;

		}

	}
	public boolean isValidFileName(String fileName) {
	    String regex = "^[a-zA-Z0-9._-]+\\.(jpg|jpeg|png|gif)$";
	    return fileName.toLowerCase().matches(regex);
	}
}
