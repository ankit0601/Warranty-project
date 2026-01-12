package com.warranty.warranty.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TNCPrivacyPolicy {

	 private String status;
	    private String message;
		private String privacyPolcy;
		private String termsCondition;
		private String path;

}
