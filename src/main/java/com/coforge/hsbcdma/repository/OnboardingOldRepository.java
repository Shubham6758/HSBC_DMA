package com.coforge.hsbcdma.repository;

import com.coforge.hsbcdma.entity.OnboardingOLD;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * OnboardingRepository.
 */
public interface OnboardingOldRepository extends JpaRepository<OnboardingOLD, Long> {
    Optional<OnboardingOLD> findByDemandIdAndProfileId(Long demandId , Long profileId);
}
