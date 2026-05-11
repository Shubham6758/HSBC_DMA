package com.coforge.hsbcdma.repository.NotificationRepositories;

import com.coforge.hsbcdma.entity.Notification2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Notification2Repository extends JpaRepository<Notification2, Long> {

    long countByUserIdAndIsReadFalse(String userId);

    List<Notification2> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(String userId);
}