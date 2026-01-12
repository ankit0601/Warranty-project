package com.warranty.warranty.services;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;
import com.warranty.warranty.configuration.FirebaseConfig;
import com.warranty.warranty.entities.AuthTokenEntity;
import com.warranty.warranty.entities.DeviceEntity;
import com.warranty.warranty.entities.UserEntity;
import com.warranty.warranty.model.LoginResponse;
import com.warranty.warranty.model.RegisterDeviceRequest;
import com.warranty.warranty.model.RegisterDeviceResponse;
import com.warranty.warranty.model.UserRequest;
import com.warranty.warranty.model.UserResponse;
import com.warranty.warranty.repositories.DeviceRepository;
import com.warranty.warranty.repositories.UserRepository;
import com.warranty.warranty.util.UtilMethods;

@Service
public class UserService {

	private final UtilMethods utilMethods;

	private final AsyncTaskExecutor asyncTaskExecutor;
	private static final Logger logger = LogManager.getLogger(VerifyOTPService.class);
	private final FirebaseConfig firebaseConfig;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private LoginService loginService;

	UserService(FirebaseConfig firebaseConfig, AsyncTaskExecutor asyncTaskExecutor, UtilMethods utilMethods) {
		this.firebaseConfig = firebaseConfig;
		this.asyncTaskExecutor = asyncTaskExecutor;
		this.utilMethods = utilMethods;
	}

	public UserResponse updateUser(UserRequest user, String token) {
		// Check if device already exists for this user
		UserResponse userResponse = new UserResponse();
		try {
			AuthTokenEntity authTokenEntity = loginService.getAuthTokenEntity(token);
			if (authTokenEntity == null) {
				userResponse.setStatus("failed");
				userResponse.setMessage("Invalid User.");
			}
			Optional<UserEntity> userinfoOptional = userRepository.findById(authTokenEntity.getUserId());
			if (userinfoOptional.isPresent() == true) {
				// Update existing device
				UserEntity userInfo = userinfoOptional.get();
				userInfo.setName(user.getName());
				// userInfo.setEmail(userInfo.getEmail());
				userInfo.setMobile(user.getMobile());
				userInfo.setPhotoUrl(userInfo.getPhotoUrl());
				userInfo.setDob(user.getDob());
				userInfo.setCountry(user.getCountry());
				userInfo.setGender(user.getGender());
				userInfo.setCity(user.getCity());
				userInfo.setLat(user.getLat());
				userInfo.setLongitude(user.getLongitude());
				userRepository.save(userInfo);

				userResponse.setId(user.getId());
				userResponse.setName(user.getName());
				userResponse.setEmail(userInfo.getEmail());
				userResponse.setMobile(user.getMobile());
				userResponse.setPhotoUrl(userInfo.getPhotoUrl());
				userResponse.setDob(user.getDob());
				userResponse.setCountry(user.getCountry());
				userResponse.setGender(user.getGender());
				userResponse.setCity(user.getCity());
				userResponse.setLat(user.getLat());
				userResponse.setLongitude(user.getLongitude());
				userResponse.setStatus("success");
				userResponse.setMessage("User details are updated.");

				return userResponse;

			} else {
				UserEntity userInfo = new UserEntity();
				userInfo.setName(user.getName());
				// userInfo.setEmail(user.getEmail());
				userInfo.setMobile(user.getMobile());
				// userInfo.setPhotoUrl(user.getPhotoUrl());
				userInfo.setDob(user.getDob());
				userInfo.setCountry(user.getCountry());
				userInfo.setGender(user.getGender());
				userInfo.setCity(user.getCity());
				userInfo.setLat(user.getLat());
				userInfo.setLongitude(user.getLongitude());
				userRepository.save(userInfo);
				Optional<UserEntity> optionalUserEntity = userRepository.findByEmail(user.getEmail());

				if (optionalUserEntity.isPresent()) {
					userInfo = optionalUserEntity.get();
					userResponse.setId(userInfo.getId());
					userResponse.setName(userInfo.getName());
					userResponse.setEmail(userInfo.getEmail());
					userResponse.setMobile(userInfo.getMobile());
					userResponse.setPhotoUrl(userInfo.getPhotoUrl());
					userResponse.setDob(userInfo.getDob());
					userResponse.setCountry(userInfo.getCountry());
					userResponse.setGender(userInfo.getGender());
					userResponse.setCity(userInfo.getCity());
					userResponse.setLat(userInfo.getLat());
					userResponse.setLongitude(userInfo.getLongitude());

				}
				userResponse.setStatus("success");
				userResponse.setMessage("User details are updated.");

				return userResponse;

			}

		} catch (Exception e) {

			userResponse.setMessage("Request failed.");
			userResponse.setStatus("failed");

			return userResponse;
		}

	}

	public UserResponse getUser(String token) {
		// Check if device already exists for this user
		UserResponse userResponse = new UserResponse();
		try {
			AuthTokenEntity authTokenEntity = loginService.getAuthTokenEntity(token);
			if (authTokenEntity == null) {
				return null;
			}
			logger.info("User ID from token.." + authTokenEntity.getUserId());
			Optional<UserEntity> userinfoOptional = userRepository.findById(authTokenEntity.getUserId());
			logger.info("user Information, is present?.." + userinfoOptional.isPresent());
			if (userinfoOptional.isPresent() == true) {
				UserEntity userInfo = userinfoOptional.get();
				// userResponse.setId(userInfo.getId());
				userResponse.setName(userInfo.getName());
				userResponse.setEmail(userInfo.getEmail());
				userResponse.setMobile(userInfo.getMobile());
				userResponse.setPhotoUrl(userInfo.getPhotoUrl());
				userResponse.setDob(userInfo.getDob());
				userResponse.setCountry(userInfo.getCountry());
				userResponse.setGender(userInfo.getGender());
				userResponse.setCity(userInfo.getCity());

//				userResponse.setLat(userInfo.getLat());
//				userResponse.setLongitude(userInfo.getLongitude());
				userResponse.setStatus("success");
				userResponse.setMessage("User Details");
				return userResponse;

			} else {
				userResponse.setMessage("User not found.");
				userResponse.setStatus("failed");
				return userResponse;
			}
		} catch (Exception e) {
			userResponse.setMessage("Request failed.");
			userResponse.setStatus("failed");
			return userResponse;
		}
	}

	public UserResponse deleteUser(String token) {
		// Check if device already exists for this user
		UserResponse userResponse = new UserResponse();
		try {
			AuthTokenEntity authTokenEntity = loginService.getAuthTokenEntity(token);
			logger.info("User ID from token.." + authTokenEntity.getUserId());
			Optional<UserEntity> userinfoOptional = userRepository.findById(authTokenEntity.getUserId());
			logger.info("User Information, is present?.." + userinfoOptional.isPresent());
			if (userinfoOptional.isPresent() == true) {
				UserEntity userInfo = userinfoOptional.get();

				LoginResponse loginResponse = loginService.sendOTP(userInfo.getEmail(), "deleteuser");
				userResponse.setStatus(loginResponse.getStatus());
				userResponse.setMessage(loginResponse.getMessage());
				userResponse.setOtpKey(loginResponse.getOtpKey());
				userResponse.setEmail(utilMethods.maskEmail(loginResponse.getEmail()));
				return userResponse;
			} else
				userResponse.setMessage("User not found.");
			userResponse.setStatus("failed");
			return userResponse;

		} catch (Exception e) {
			userResponse.setMessage("Request failed." + e.getLocalizedMessage());
			userResponse.setStatus("failed");
			return userResponse;
		}

	}
}
