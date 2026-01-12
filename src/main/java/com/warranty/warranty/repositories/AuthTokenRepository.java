package com.warranty.warranty.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.warranty.warranty.entities.AuthTokenEntity;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthTokenEntity, Integer> {
	Optional<AuthTokenEntity> findByTokenAndStatus(String token, char status);

	Optional<AuthTokenEntity> findByUserId(Integer userid);

	@Modifying
	@Query(value = "update  auth_token set status='D' " + " where user_id=:userId ", nativeQuery = true)
	int updateStatusByUserId(@Param("userId") Integer userId);

	Optional<AuthTokenEntity> findByUserIdAndStatus(Integer userId, char status);
}
