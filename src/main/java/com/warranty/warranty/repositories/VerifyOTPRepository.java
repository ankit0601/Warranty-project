package com.warranty.warranty.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.warranty.warranty.entities.AuthTokenEntity;
import com.warranty.warranty.entities.VerifyOTPEntity;

@Repository
public interface VerifyOTPRepository extends JpaRepository<VerifyOTPEntity, Integer> {
	
	
//	Optional<VerifyOTPEntity> findByOtpAndOtpKeyAndStatus( String otp,String otpkey, char status);
	Optional<VerifyOTPEntity> findByOtpAndOtpKeyAndStatusAndOtpType( String otp,String otpkey, char status,String otpType);
	

}
