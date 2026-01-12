package com.warranty.warranty.services;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.warranty.warranty.components.MailComponent;
import com.warranty.warranty.entities.AuthTokenEntity;
import com.warranty.warranty.entities.TemplateEntity;
import com.warranty.warranty.entities.UserEntity;
import com.warranty.warranty.entities.VerifyOTPEntity;
import com.warranty.warranty.model.AuthTokenRequest;
import com.warranty.warranty.model.LoginResponse;
import com.warranty.warranty.model.UserRequest;
import com.warranty.warranty.model.UserResponse;
import com.warranty.warranty.model.VerifyOTPRequest;
import com.warranty.warranty.model.VerifyOTPResponse;
import com.warranty.warranty.repositories.AuthTokenRepository;
import com.warranty.warranty.repositories.TemplateRepository;
import com.warranty.warranty.repositories.UserRepository;
import com.warranty.warranty.repositories.VerifyOTPRepository;
import com.warranty.warranty.util.SendEmail;
import com.warranty.warranty.util.UtilMethods;
import com.warranty.warranty.entities.VerifyOTPEntity;
@Service
public class VerifyOTPService {

	private static final Logger logger = LogManager.getLogger(VerifyOTPService.class);
	@Autowired
	private VerifyOTPRepository verifyOTPRepository;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	TemplateRepository templateRepository;
	@Autowired
	MailComponent mailComponent;
	@Autowired
	private SendEmail sendEmail;
	@Autowired
	PushNotificationService pushNotificationService;

	@Autowired
	private AuthTokenRequest authTokenRequest;

	@Autowired
	private AuthTokenRepository authTokenRepository;
	@Autowired
	private UtilMethods utilMethods;
	@Value("${app.otp_validity}")
	private int OTP_EXPIRY;
	@Value("${app.token_expiry}")
	private int TOKEN_EXPIRY;

	@Transactional
	public VerifyOTPResponse VerifyOTP(VerifyOTPRequest verifyOTPRequest) {

		logger.info("inside service class" + verifyOTPRequest.getOtp());
		logger.info("inside service class token expiry count " + TOKEN_EXPIRY);
		String otpType=verifyOTPRequest.getOtpType();
		Optional<VerifyOTPEntity> verifyOTPOptional = verifyOTPRepository
				.findByOtpAndOtpKeyAndStatusAndOtpType(verifyOTPRequest.getOtp(), verifyOTPRequest.getOtpKey(), 'A',otpType);

		logger.info("inside service class" + verifyOTPOptional.isPresent());

		if (verifyOTPOptional.isPresent()) {

			logger.info("otp and kay found " + verifyOTPOptional.isPresent());
			AuthTokenEntity authTokenEntity = new AuthTokenEntity();
			VerifyOTPEntity verifyOTP = verifyOTPOptional.get();
			if (verifyOTP != null) {
				LocalDateTime localDateTime = verifyOTP.getCreatedOn();
				logger.info(" verifyOTP......... " + verifyOTP + " ..." + localDateTime);
				if (verifyOTP.getVerifiedOn() != null || timeDifference(verifyOTP.getCreatedOn()) >= OTP_EXPIRY) {
					return new VerifyOTPResponse("failed", "OTP Expiried.", null, -1);
				}

				logger.info("inside insert verify OTP date and time...." + verifyOTP.toString());
				LocalDateTime dateandtime = LocalDateTime.now();
				verifyOTP.setVerifiedOn(dateandtime);
				verifyOTP.setStatus('V');
				if (otpType.equals("login")) {
					verifyOTPRepository.save(verifyOTP);
					int userId = verifyOTP.getUserId();
					authTokenRepository.updateStatusByUserId(userId);

					authTokenEntity.setUserId(userId);
					authTokenEntity.setStatus('A');
					authTokenEntity.setToken(new UtilMethods().generateCode());
					authTokenEntity.setTokenExpiry(TOKEN_EXPIRY);
					logger.info("Saving auth token in table" + authTokenEntity.toString());

					authTokenEntity = authTokenRepository.save(authTokenEntity);
					return new VerifyOTPResponse("success", "Logged in successfully.", authTokenEntity.getToken(),
							authTokenEntity.getTokenExpiry());
				} else if (otpType.equals("deleteuser")) {

					Optional<UserEntity> userEntityOptional = userRepository.findById(verifyOTP.getUserId());

					logger.info("userEntityOptional is " + userEntityOptional);
					if (userEntityOptional.isPresent()) {
						UserEntity userEntity = userEntityOptional.get();
						logger.info("User entity to mark deleted..... " + userEntity);
						if (userEntity != null) {
							logger.info("User entity to mark deleted.inside if availble condition.... " + userEntity);
							String emailId = userEntity.getEmail();
							String dismentleEmailId = utilMethods.dismentleEmail(userEntity.getEmail());
						
							//logger.info("masked email id... " + maskedEmailId);
							verifyOTPRepository.save(verifyOTP);
							userEntity.setEmail(dismentleEmailId);
							userEntity.setName(null);
							userEntity.setMobile(null);
							userEntity.setDob(null);
							userEntity.setStatus('D');

							logger.info("saving updated userEntity.... " + userEntity);

							Optional<AuthTokenEntity> authOptional = authTokenRepository
									.findByUserIdAndStatus(verifyOTP.getUserId(),'A');
							if (authOptional.isPresent()) {
								authTokenEntity = authOptional.get();
								authTokenEntity.setUserId(verifyOTP.getUserId());
								authTokenEntity.setTokenExpiry(0);
								authTokenEntity.setStatus('D');
								authTokenEntity.setToken(null);
								logger.info("Saving auth token in table" + authTokenEntity.toString());
								userRepository.save(userEntity);
								authTokenEntity = authTokenRepository.save(authTokenEntity);
							} else {
								return new VerifyOTPResponse("failed", "Invalid User.", null, -1);

							}
							try {
								Optional<TemplateEntity> templateOptional = null;
								templateOptional = templateRepository.findByTemplateId(mailComponent.getDeleteUsertemplateid());
								if (templateOptional.isPresent()) {
									TemplateEntity templateEntity = templateOptional.get();
									String body = templateEntity.getTemplate();
									logger.info("sending email for template id --" + templateEntity.getTemplateId());

									// sendEmailOTP(emailId, body, mailComponent.getSubject());
								}
							} catch (Exception e) {
								logger.error("Exception occurred.while sending email..." + e.getMessage());
							}
							logger.info("Returning the succcss response....");

							return new VerifyOTPResponse("success", "User deleted successfully.", null, -1);
						}
					}

					return new VerifyOTPResponse("failed", "User not found.", null, -1);
				}
				else
				{
				return new VerifyOTPResponse("failed", "Please confirm otp type.", null, -1);
				}
				// if()
				// sendPushNotification(null, null, null);
			} else
				return new VerifyOTPResponse("failed", "OTP can not be blank.", null, -1);
		} else {

			return new VerifyOTPResponse("failed", "Otp is not found. Please initaite new OTP and try again.", null,
					-1);
		}
	}

	@Async("asyncTaskExecutor")
	private void sendPushNotification(String token, String title, String body) {

		logger.info("sending push notification..." + getClass().getName());

//		try {
////			pushNotificationService.sendNotification(token, title, body);
//		} catch (FirebaseMessagingException e) {
//			e.printStackTrace();
//		}

	}

	@Async("asyncTaskExecutor")
	private void sendEmailOTP(String userEmailid, String body, String subject) {
		logger.info("sending email by Executor thread..." + getClass().getName());
		sendEmail.sendEmailOTP(userEmailid, body, subject);
	}

	private long timeDifference(LocalDateTime dateTime) {

		try {

//			String timestampStr = dateTime.toString();
			logger.info(" token generated time in string format " + dateTime);
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ss.SSSSSS"); //2025-07-06T20:38:19.236525
//			LocalDateTime timestamp = LocalDateTime.parse(timestampStr, formatter);
//			logger.info(" token generated time  " +timestamp);
			// Current time
			LocalDateTime now = LocalDateTime.now();
			// Calculate difference
			Duration duration = Duration.between(dateTime, now);
			long minutesDiff = duration.toSeconds();
			logger.info("token generated time difference " + minutesDiff);
			return minutesDiff;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info(" Exception in token generated time difference " + e.getMessage());
			return -1;
		}

	}

}
