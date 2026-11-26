package com.coforge.hsbcdma.repository.OnboardingRepositories;


import com.coforge.hsbcdma.entity.Onboarding;
import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.OnboardingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OnboardingRepository extends JpaRepository<Onboarding, Long> ,JpaSpecificationExecutor<Onboarding> {
    boolean existsByProfileTracker_Id(Long profileTrackerId);
    Optional<Onboarding> findByProfileTracker_Id(Long profileTrackerId);

    @EntityGraph(attributePaths = {
            "profileTracker",
            "profileTracker.demand",
            "profileTracker.profile",
            "bgvStatus",
            "wbsType",
            "onboardingStatus"
    })
    Page<Onboarding> findAll(Pageable pageable);

    Optional<Onboarding> findByProfileTrackerId(Long id);


    @Modifying
    @Query("""
    UPDATE Onboarding o
    SET o.onboardingStatus = :status
    WHERE o.demand.id = :demandId
    """)
    int updateStatusByDemandId(
            @Param("demandId") Long demandId,
            @Param("status") OnboardingStatus status
    );

}
