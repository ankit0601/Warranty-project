package com.warranty.warranty.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.warranty.warranty.entities.CountryMasterEntity;
import com.warranty.warranty.entities.UserEntity;


@Repository
public interface CountryMasterRepository extends JpaRepository<CountryMasterEntity, Integer> {
    Optional<CountryMasterEntity> findById(Integer id );
    Optional<CountryMasterEntity> findByName(String name);
   
}
