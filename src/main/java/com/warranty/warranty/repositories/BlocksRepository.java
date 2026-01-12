package com.warranty.warranty.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//import com.meditation.my_sequence.dto.MySequenceProjection;
import com.warranty.warranty.entities.LoopEntity;
import com.warranty.warranty.entities.BlockEntity;
 
public interface BlocksRepository  extends JpaRepository<BlockEntity, Integer> {

//	 @Query(value = "SELECT s.sequence_name AS sequenceName," +
//             "so.step_order AS stepOrder, so.duration AS duration, so.status AS stepStatus " +
//             "FROM my_sequence s " +
//             "JOIN my_step_order so ON s.id = so.fk_sequence_id " +
//             "WHERE s.id = :sequenceId and s.status='A'", nativeQuery = true)
//	 Optional<List<MySequenceEntity>> findSequenceSteps();//@Param("sequenceId") Integer sequenceId);
	 
	 Optional<BlockEntity> findByIdAndStatus(Integer id, char status);
	 Optional<BlockEntity> findByNameAndStatus(String name,char status);
	 List<BlockEntity> findByQloopId(LoopEntity loopEntity);
	 
//	 List<BlockEntity> findByQloopId(Integer qloopId);
	 
//	 @Query(value = "SELECT b.qloop_id AS loopId, b.name AS Name, b.duration AS duration, " +
//             " b.id AS blockId, b.block_order AS blockOrder, b.sound, b.color, b.auto_start_next_block as autoStartNextBlock " +
//             " FROM blocks b " +            
//             " WHERE b.status = 'A'  AND b.qloop_id = :loopId",
//     nativeQuery = true)
//	 List<BlockEntity> getBlocks(@Param("loopId") Integer loopId); 
	
	 @Query(value = "SELECT s.id AS loopId, s.loop_name AS loopName, s.repeat_count AS repeatCount, " +
             "o.id AS id, o.block_id AS blockId, o.step_order AS stepOrder, o.duration AS duration " +
             "FROM qloop s " +
             "JOIN blocks o ON s.id = o.loop_id " +
             "WHERE s.status = 'A' AND o.status = 'A' AND s.fk_user_id = :userId",
     nativeQuery = true)
	 List<BlockEntity> findByUserIdAndStatus(@Param("userId") Integer userId, char status);
	 
	 
	 @Query("SELECT COALESCE(MAX(b.blockOrder), 0) " +
	           "FROM BlockEntity b " +
	           "WHERE b.qloopId.id = :loopId AND b.status ='A'")
	  Integer findMaxBlockOrderByLoopIdAndStatus(@Param("loopId") Integer loopId);
	 
	 
	 List<BlockEntity> findByQloopId_IdAndStatusOrderByBlockOrderAsc(Integer loopId,char status);
	 
//	 List<BlockEntity> findByIdAndStatus(Integer loopId,char status);
	 //Optional<BlockEntity> findByNameAndUserId(String name, Integer userId);
	 @Modifying
	 @Query(value="update  qloop set status='D' "+" where id= :id",nativeQuery = true)
	 int udpateById(@Param("id") Integer id );
	 @Modifying
	 @Query(value="update  blocks set status='D' "+" where fk_sequence_id=:loopId AND id= :id AND user_id = :userId ",nativeQuery = true)
	 int updateByLoopIdAndIdAndUserId(@Param("loopId") Integer loopId,@Param("id") Integer id,@Param("userId") Integer userId);
	 
	 

} 