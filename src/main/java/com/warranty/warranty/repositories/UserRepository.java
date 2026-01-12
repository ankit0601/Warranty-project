package com.warranty.warranty.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.warranty.warranty.entities.UserEntity;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findById(Integer id );
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email); // Optional validation helper
}
