package com.coforge.hsbcdma.repository;

import com.coforge.hsbcdma.entity.ProfileTrackerOLD;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ProfileRepository.
 */
public interface ProfileTrackerRepository extends JpaRepository<ProfileTrackerOLD, Long> {
    Optional<ProfileTrackerOLD> findByDemandId(String demandId);
    List<ProfileTrackerOLD> findByProfileSharedDateBetween(LocalDate startDate, LocalDate endDate);
}
