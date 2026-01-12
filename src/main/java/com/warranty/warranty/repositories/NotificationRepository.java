package com.warranty.warranty.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.warranty.warranty.entities.NotificationEntity;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Integer>  {
//	@Query("SELECT r FROM notifications r WHERE r.sent = false order by r.reminderTime desc")
//	List<NotificationEntity> findDueReminders();
}
