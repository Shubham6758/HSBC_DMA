package com.coforge.hsbcdma.repository.OnboardingRepositories;

import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.BgvStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BgvStatusRepository extends JpaRepository<BgvStatus, Long> {
    Optional<BgvStatus> findByNameIgnoreCase(String s);
}