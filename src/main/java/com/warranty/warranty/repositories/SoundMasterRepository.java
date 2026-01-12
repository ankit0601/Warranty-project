package com.warranty.warranty.repositories;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.warranty.warranty.entities.CountryMasterEntity;
import com.warranty.warranty.entities.SoundMasterEntity;
import com.warranty.warranty.entities.UserEntity;


@Repository
public interface SoundMasterRepository extends JpaRepository<SoundMasterEntity, Integer> {
    List<SoundMasterEntity> findAllByStatus(char status );
//    Optional<CountryMasterEntity> findByName(String name);
   
}
