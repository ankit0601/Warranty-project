package com.warranty.warranty.model;

import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.NotBlank;


@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GmLoginRequest {
	
	
		@NotBlank(message = "Token is mandatory")
		 private String idtoken;
		private String name;
		
	

}
