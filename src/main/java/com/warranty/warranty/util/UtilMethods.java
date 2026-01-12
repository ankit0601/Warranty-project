package com.warranty.warranty.util;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

import com.warranty.warranty.controller.LoginController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class UtilMethods {

	private static final Logger logger = LogManager.getLogger(UtilMethods.class);

	public String generateCode() {
		return UUID.randomUUID().toString();
	}

	public String getSixDigitOTP() {
		String otp = new DecimalFormat("000000").format(new Random().nextInt(999999));
		return "222222";
	}

	public String getOTPKey() {

		String uuid = UUID.randomUUID().toString();
		return uuid;

	}

	public long daysDifferentDayCount(LocalDate currentDate, LocalDate oldDate) {
		try {
			long daysBetween = ChronoUnit.DAYS.between(oldDate, currentDate);

			return daysBetween;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception Occurred.....in Date formating  .." + e.getMessage());
			return -1;
		}
	}

	public String dismentleEmail(String email) {
		if (email == null || !email.contains("@")) {
			return email; // invalid or non-email
		}

		String[] parts = email.split("@");
		String local = parts[0].charAt(0) + UUID.randomUUID().toString() + parts[0].charAt(parts.length - 1)+"@"+parts[1];
		return local;
	}

	public String maskEmail(String email) {
		if (email == null || !email.contains("@")) {
			return email; // invalid or non-email
		}

		String[] parts = email.split("@");
		String local = parts[0];
		String domain = parts[1];

		// Mask local part
		StringBuilder maskedLocal = new StringBuilder();

		for (int i = 0; i < local.length(); i++) {
			char ch = local.charAt(i);
			if (i < 1 || ch == '.' || ch == '_' || ch == '-') {
				maskedLocal.append(ch); // keep first letter and separators
			} else if (i < local.length() - 2) {
				maskedLocal.append("*"); // mask middle letters
			} else {
				maskedLocal.append(ch); // keep last letter
			}
		}

		return maskedLocal + "@" + domain;
	}

}
