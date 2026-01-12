package com.warranty.warranty.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.warranty.warranty.entities.DeviceEntity;


@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, Integer> {
    Optional<DeviceEntity> findByUserIdAndDeviceId(String userId, String deviceId);
}
