package com.warranty.warranty.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.warranty.warranty.entities.VersionManagementEntity;

@Repository
public interface CommonConfigRepository extends JpaRepository<VersionManagementEntity, Integer> {
	
//	@Query("SELECT id,version_no,mandatory,description FROM version_management v where v.version_no=(select MAX(b.version_no ) from version_management b)")
	         
//	@Query("SELECT v FROM VersionManagementEntity v WHERE v.versionNo = (SELECT MAX(v2.versionNo) FROM VersionManagementEntity v2)")
//	 Optional<VersionManagementEntity> findTopByOrderByVersionNoDesc();
	 Optional<VersionManagementEntity> findTopByOperatingSystemOrderByVersionNoDesc(String operatingSystem);
    
}

