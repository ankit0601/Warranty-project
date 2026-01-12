package com.warranty.warranty.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.warranty.warranty.entities.DeviceEntity;
import com.warranty.warranty.entities.TemplateEntity;

public interface TemplateRepository extends JpaRepository<TemplateEntity,Integer> {
    Optional<TemplateEntity> findByTemplateId(int id);
   
}
