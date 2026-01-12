package com.warranty.warranty.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.warranty.warranty.entities.LoopEntity;


//import com.warranty.warranty.entities.LoopEntity;

public interface LoopBlocksRepository extends JpaRepository<LoopEntity, Integer> {

	Optional<LoopEntity> findByIdAndStatus(Integer id, char status);

	@Query("SELECT DISTINCT l FROM LoopEntity l " + "LEFT JOIN FETCH l.blockOrders b "
			+ "WHERE l.status = 'A' AND l.userId = :userId")// AND b.status ='A'")
	List<LoopEntity> getLoops(@Param("userId") Integer userId);

	Optional<LoopEntity> findByLoopNameAndUserId(String loopName, Integer userId);

	Optional<LoopEntity> findByLoopNameAndUserIdAndStatus(String loopName, Integer userId,char status);

	Optional<LoopEntity> findByIdAndUserIdAndStatus(Integer id, Integer userId, char status);
	
	List<LoopEntity> findByUserIdAndStatus(Integer userId, char status);

	@Modifying
	@Query(value = "update  qloop set status='D' " + " where id= :id and status='A' ", nativeQuery = true)
	int udpateById(@Param("id") Integer id);

	
//	@Query(value = "update  blocks set status='D' "
//			+ " where qloop_id=:loopId AND id= :id", nativeQuery = true)
	@Modifying
	@Transactional
	@Query(value = "UPDATE blocks SET status = 'D', modified_on = NOW() WHERE qloop_id = :loopId AND id = :id AND status='A' ", nativeQuery = true)
	int updateByLoopIdAndIdAndUserId(@Param("loopId") Integer loopId, @Param("id") Integer id);

}