package com.coforge.hsbcdma.repository.OnboardingRepositories;

import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.WbsType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WbsTypeRepository extends JpaRepository<WbsType, Long> {
    Optional<WbsType> findByNameIgnoreCase(String s);
}
