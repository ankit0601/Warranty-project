package com.warranty.warranty.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.warranty.warranty.entities.GenerateOTPEntity;
import com.warranty.warranty.entities.VerifyOTPEntity;

@Repository
public interface GenerateOTPRepository extends JpaRepository<VerifyOTPEntity, Integer> {
	 Optional<GenerateOTPEntity> findByOtpAndOtpKey( String otp,String otokey);
	 @Modifying
	 @Transactional
	 @Query(value="update verify_otp set status='D' "+" where user_id= :userId and status= :status",nativeQuery = true)
	 int udpateByUserIdandStatus(@Param("userId") Integer userId,@Param("status") String status);
}
