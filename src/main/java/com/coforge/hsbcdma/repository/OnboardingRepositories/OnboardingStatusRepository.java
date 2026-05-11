package com.coforge.hsbcdma.repository.OnboardingRepositories;


import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.OnboardingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OnboardingStatusRepository extends JpaRepository<OnboardingStatus, Long> {
    Optional<OnboardingStatus> findByNameIgnoreCase(String name);
}
