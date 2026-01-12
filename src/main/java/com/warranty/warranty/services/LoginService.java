package com.warranty.warranty.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.warranty.warranty.components.MailComponent;
import com.warranty.warranty.entities.AuthTokenEntity;
import com.warranty.warranty.entities.TemplateEntity;
import com.warranty.warranty.entities.UserEntity;
import com.warranty.warranty.entities.VerifyOTPEntity;
import com.warranty.warranty.model.GmLoginRequest;
import com.warranty.warranty.model.GmloginResponse;
import com.warranty.warranty.model.LoginResponse;
import com.warranty.warranty.repositories.AuthTokenRepository;
import com.warranty.warranty.repositories.GenerateOTPRepository;
import com.warranty.warranty.repositories.TemplateRepository;
import com.warranty.warranty.repositories.UserRepository;

import com.warranty.warranty.util.UtilMethods;
import com.warranty.warranty.util.SendEmail;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;

@Service
public class LoginService {

	private final DeviceRegistrationService deviceRegistrationService;

//    private final AuthTokenRepository authTokenRepository;

	private static final Logger logger = LogManager.getLogger(VerifyOTPService.class);
	@Autowired
	UserRepository userRepository;
	@Autowired
	GenerateOTPRepository generateOTPRepository;
	@Autowired
	AuthTokenRepository authTokenRepository;

	@Autowired
	TemplateRepository templateRepository;
	@Autowired
	MailComponent mailComponent;
	@Autowired
	private SendEmail sendEmail;

	@Autowired
	private UtilMethods authCodeGenerator;
	@Value("${app.token_expiry}")
	private int TOKEN_EXPIRY;

	LoginService(DeviceRegistrationService deviceRegistrationService) {
		this.deviceRegistrationService = deviceRegistrationService;
	}

//    LoginService(AuthTokenRepository authTokenRepository) {
//        this.authTokenRepository = authTokenRepository;
//    }

// web client id
	String google_client_id = "828583701808-b1vtrb0t6dmmtf3iih7tunb5ot8tb9g6.apps.googleusercontent.com";

	public LoginResponse sendOTP(String userEmail, String requestType) {
		// check email in user table. if present then trigger otp, else save record in
		// user table and trigger OTP

		Optional<UserEntity> useOptional = userRepository.findByEmail(userEmail.toLowerCase());
		VerifyOTPEntity generateOTPEntity = new VerifyOTPEntity();

		Optional<TemplateEntity> templateOptional = null;
		UserEntity user = new UserEntity();
        logger.info("User optional id --" + useOptional.isEmpty());

		if (useOptional.isEmpty()) {
			if (requestType.equalsIgnoreCase("login")) {
				// insert records and get record id.
				user.setEmail(userEmail);
				user.setStatus('A');

                logger.info("User Email id --" + user.getEmail().toString() + "  " + requestType);
				user = userRepository.save(user);
			}

		} else {
			user = useOptional.get();
		}

		markInactivePendingOTPforGivenUser(user.getId());
		generateOTPEntity.setUserId(user.getId());
		generateOTPEntity.setStatus('A');
		generateOTPEntity.setOtp(authCodeGenerator.getSixDigitOTP());
		generateOTPEntity.setOtpKey(authCodeGenerator.getOTPKey());

		if (requestType.equalsIgnoreCase("login")) {
			generateOTPEntity.setOtpType("login");
			templateOptional = templateRepository.findByTemplateId(mailComponent.getOtplogintemplateid());

		} else if (requestType.equalsIgnoreCase("deleteuser")) {
			templateOptional = templateRepository.findByTemplateId(mailComponent.getDeleteUsertemplateid());
			generateOTPEntity.setOtpType("deleteuser");
		}
		generateOTPEntity = generateOTPRepository.save(generateOTPEntity);
		logger.info("sending email on user id " + generateOTPEntity.toString());
		// TODO send email with OTP.....
		if (templateOptional.isPresent()) {
			TemplateEntity templateEntity = templateOptional.get();
			String body = templateEntity.getTemplate().replace("#SixDigitOTP#", generateOTPEntity.getOtp());
			logger.info("sending email for template id --" + templateEntity.getTemplateId());
			logger.info("sending email on user id for otp...generate OTP Entiry" + generateOTPEntity);
			//sendEmailOTP(userEmail, body, mailComponent.getSubject());
		} else {
			logger.info("Email OTP Template id 1001 does not exist..............class" + getClass().getName());
			return new LoginResponse("failed", "OTP send request failed.", userEmail, null);
		}
		logger.info(".generate OTP Entiry id....." + generateOTPEntity);
		if (generateOTPEntity.getId()>-1) {
			logger.info("Email OTP Template id 1001 .............class" + getClass().getName());
			return new LoginResponse("success", "OTP Sent",userEmail, generateOTPEntity.getOtpKey());
		} else {
			logger.info(getClass().getName() + "Inside Else otp failed");
			return new LoginResponse("failed", "OTP send request failed.", userEmail, null);
		}
	}

	public GmloginResponse verifyGMToken(GmLoginRequest gmailrequest) {
		try {
			if (gmailrequest == null || gmailrequest.getIdtoken() == null
					|| gmailrequest.getIdtoken().equalsIgnoreCase("")) {
				GmloginResponse gmResponse = new GmloginResponse();
				gmResponse.setStatus("failed");
				gmResponse.setMessage("Invalid request");
				return gmResponse;
			}

			logger.info("gmail idtokenxxxx" + gmailrequest.getIdtoken().toString());

			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
					new JacksonFactory()).setAudience(Collections.singletonList(google_client_id)).build();
			logger.info("gmail idtoken verifier called..with token...");
			GoogleIdToken idToken;

			idToken = verifier.verify(gmailrequest.getIdtoken());
			logger.info("gmail idtoken verified....");
			if (idToken != null) {
				Payload tokenPayload = idToken.getPayload();
				String userId = tokenPayload.getSubject(); // Google user ID
				String email = tokenPayload.getEmail();
				String name = (String) tokenPayload.get("name");
				String pictureUrl = (String) tokenPayload.get("picture");

				Optional<UserEntity> useOptional = userRepository.findByEmail(email);
//				Optional<UserEntity> useOptional = userRepository.findByEmail("gmailuser@gmail.com");
				UserEntity user = new UserEntity();
				if (useOptional.isEmpty()) {
					// insert records and get record id.
					user.setUserGmAppleid(userId);
					user.setEmail(email);
					user.setName(email);
					user.setPhotoUrl(pictureUrl);
					user.setGmailOrAppleUser("gmail");
					user = userRepository.save(user);
				} else {
					user = useOptional.get();
					user.setUserGmAppleid(userId);
					user.setEmail(email);
					user.setName(name);
					user.setPhotoUrl(pictureUrl);
//					user.setName(email);
//					user.setPhotoUrl(pictureUrl);
					user.setGmailOrAppleUser("Gmail");
					user = userRepository.save(user);
				}
				AuthTokenEntity authTokenEntity = new AuthTokenEntity();

				LocalDateTime dateandtime = LocalDateTime.now();
				authTokenEntity.setUpdatedDate(dateandtime);

				authTokenEntity.setUserId(user.getId());
				authTokenEntity.setToken(new UtilMethods().generateCode());
				authTokenEntity.setTokenExpiry(TOKEN_EXPIRY);
				logger.info(authTokenEntity.toString());
				authTokenEntity = authTokenRepository.save(authTokenEntity);
				return new GmloginResponse("success", "login successful", authTokenEntity.getToken(),
						authTokenEntity.getTokenExpiry());
			} else {
				logger.info("inside else condition.");

				return new GmloginResponse("failed", "Invalid Token. Please relogin.", null, 0);
			}

		} catch (Exception e) {
			logger.info("exeption occurred  " + e);

			return new GmloginResponse(HttpStatus.INTERNAL_SERVER_ERROR + "", "Token verification failed", null, 0);

		}

	}

	public GmloginResponse verifyAppleToken(GmLoginRequest gmailrequest) {
		try {

			if (gmailrequest == null || gmailrequest.getIdtoken() == null
					|| gmailrequest.getIdtoken().equalsIgnoreCase("")) {
				GmloginResponse gmResponse = new GmloginResponse();
				gmResponse.setStatus("failed");
				gmResponse.setMessage("Request or Token is null.");
				return gmResponse;

			}

			String idToken = gmailrequest.getIdtoken();
			logger.info("apple idtokenxxxx" + idToken);
			String name = gmailrequest.getName();
			SignedJWT signedJWT = SignedJWT.parse(idToken);
			JWKSet jwkSet = JWKSet.load(new URL("https://appleid.apple.com/auth/keys"));
			JWSHeader header = signedJWT.getHeader();
			JWK jwk = jwkSet.getKeyByKeyId(header.getKeyID());
			RSAKey rsaKey = (RSAKey) jwk;
			RSAPublicKey publicKey = rsaKey.toRSAPublicKey();

			JWSVerifier verifier = new RSASSAVerifier(publicKey);

			if (!signedJWT.verify(verifier)) {
				throw new Exception("Token verification failed");
			}

			var claims = signedJWT.getJWTClaimsSet();
			if (claims != null) {

				String userId = claims.getSubject();
				String email = (String) claims.getClaim("email");
				Optional<UserEntity> useOptional = userRepository.findByEmail(email);
//				Optional<UserEntity> useOptional = userRepository.findByEmail("gmailuser@gmail.com");
				UserEntity user = new UserEntity();
				if (useOptional.isEmpty()) {
					user.setUserGmAppleid(userId);
					user.setEmail(email);
					user.setName(name);
					user.setGmailOrAppleUser("apple");
					user = userRepository.save(user);
				} else {
					user = useOptional.get();
					user.setUserGmAppleid(userId);
					user.setEmail(email);
					user.setName(name);
					user.setGmailOrAppleUser("apple");
					user = userRepository.save(user);
				}
				AuthTokenEntity authTokenEntity = new AuthTokenEntity();
				LocalDateTime dateandtime = LocalDateTime.now();
				authTokenEntity.setUpdatedDate(dateandtime);

				authTokenEntity.setUserId(user.getId());
				authTokenEntity.setToken(new UtilMethods().generateCode());
				authTokenEntity.setTokenExpiry(TOKEN_EXPIRY);
				logger.info(authTokenEntity.toString());
				authTokenEntity = authTokenRepository.save(authTokenEntity);
				return new GmloginResponse("success", "login successful", authTokenEntity.getToken(),
						authTokenEntity.getTokenExpiry());
			} else {
				logger.info("inside else condition.");

				return new GmloginResponse("failed", "Invalid Token. Please relogin.", null, 0);
			}
		} catch (Exception e) {
			logger.info("exeption occurred  " + e);

			return new GmloginResponse(HttpStatus.INTERNAL_SERVER_ERROR + "", "Token verification failed", null, 0);

		}

	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void markInactivePendingOTPforGivenUser(Integer id) {
		try {
			logger.info(getClass().getName() + "Updating status of unverified OTP records for user id " + id);
			int count = generateOTPRepository.udpateByUserIdandStatus(id, "A");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info(getClass().getName() + "Error occurred during updating status of unverified OTP records" + e);
		}

	}

	@Async("asyncTaskExecutor")
	private void sendEmailOTP(String userEmailid, String body, String subject) {
		logger.info("sending email by Executor thread..." + getClass().getName());
		sendEmail.sendEmailOTP(userEmailid, body, subject);
	}

	public AuthTokenEntity getAuthTokenEntity(String token) {
		Optional<AuthTokenEntity> authOptional = authTokenRepository.findByTokenAndStatus(token,'A');
		AuthTokenEntity authTokenEntity = null;
		if (authOptional.isPresent()) {
			authTokenEntity = authOptional.get();

		}
		return authTokenEntity;
	}

	public boolean validateToken(String token) {
		AuthTokenEntity authTokenEntity = getAuthTokenEntity(token);
		LocalDate myObj = LocalDate.now();
		if(null==authTokenEntity)
		{
			return false;
		}
		logger.info("days difference .......... " + (authCodeGenerator.daysDifferentDayCount(myObj,
				authTokenEntity.getUpdatedDate().now().toLocalDate())));
		if (0 >= (authCodeGenerator.daysDifferentDayCount(myObj,
				authTokenEntity.getUpdatedDate().now().toLocalDate()))) {

			return true;
		}

		return false;
	}
}
