package com.warranty.warranty.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OtpTriggeredResponse {
	
	private String status;
	private String message;
	private String otp;
	private String otpKey;
	private int id;

}
