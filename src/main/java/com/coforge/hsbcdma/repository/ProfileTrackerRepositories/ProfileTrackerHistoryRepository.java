package com.coforge.hsbcdma.repository.ProfileTrackerRepositories;

import com.coforge.hsbcdma.entity.ProfileTrackerHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileTrackerHistoryRepository extends JpaRepository<ProfileTrackerHistory,Long> {

    List<ProfileTrackerHistory> findByProfileTrackerId(Long trackerId);
    List<ProfileTrackerHistory> findByDemandId(Long demandId);
    List<ProfileTrackerHistory> findByProfileId(Long profileId);
}