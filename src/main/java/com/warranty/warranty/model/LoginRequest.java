package com.warranty.warranty.model;



import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
	
	@NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
	 private String email;
	 private String otp;
}
