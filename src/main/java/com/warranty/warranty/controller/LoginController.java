package com.warranty.warranty.controller;
import com.warranty.warranty.model.*;
import com.warranty.warranty.services.LoginService;
import com.warranty.warranty.services.VerifyOTPService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Validated
public class LoginController {

    private static final Logger logger = LogManager.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;

    @Autowired
    private VerifyOTPService verifyOTPService;

    @PostMapping("/sendOTP")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        // Validate Request -- Email Mandatory
        logger.info("fetched template object from db --" + loginRequest.toString());

        if (loginRequest != null)
            return new ResponseEntity<>(loginService.sendOTP(loginRequest.getEmail(),"login"), HttpStatus.OK);
        else
            return new ResponseEntity<>((HttpHeaders) null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/verifyOTP")
    public ResponseEntity<VerifyOTPResponse> verifyOtp(@RequestBody VerifyOTPRequest verifyOTPRequest) {
        // Validate Request -- Email, Otp, Otpkey Mandatory
        logger.info("Inside controller" + verifyOTPRequest.toString());
        if (verifyOTPRequest != null)
            return new ResponseEntity<>(verifyOTPService.VerifyOTP(verifyOTPRequest), HttpStatus.OK);
        else
            return new ResponseEntity<>((HttpHeaders) null, HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/gmlogin")
    public ResponseEntity<GmloginResponse> login(@RequestBody GmLoginRequest gmailrequestt) {

        // Validate Request -- Email, Otp, Otpkey Mandatory

        logger.info("Inside gmail controller" + gmailrequestt.toString());
        if (gmailrequestt != null)
            return new ResponseEntity<>(loginService.verifyGMToken(gmailrequestt), HttpStatus.OK);
        else
            return new ResponseEntity<>((HttpHeaders) null, HttpStatus.BAD_REQUEST);


    }

    @PostMapping("/ioslogin")
    public ResponseEntity<GmloginResponse> appleLogin(@RequestBody GmLoginRequest gmailrequestt) {

        // Validate Request -- Email, Otp, Otpkey Mandatory

        logger.info("Inside apple controller" + gmailrequestt.toString());
        if (gmailrequestt != null)
            return new ResponseEntity<>(loginService.verifyAppleToken(gmailrequestt), HttpStatus.OK);
        else
            return new ResponseEntity<>((HttpHeaders) null, HttpStatus.BAD_REQUEST);

    }

}
