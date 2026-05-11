package com.coforge.hsbcdma.repository.ProfileTrackerRepositories;


import com.coforge.hsbcdma.entity.ProfileTracker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfileTrackRepository extends JpaRepository<ProfileTracker, Long> , JpaSpecificationExecutor<ProfileTracker> {

    // For Demand -> Profiles bulk pre-check
    List<ProfileTracker> findAllByDemand_IdAndProfile_IdIn(Long demandPkId, List<Long> profileIds);

    // For Profile -> Demands bulk pre-check
    List<ProfileTracker> findAllByProfile_IdAndDemand_IdIn(Long profileId, List<Long> demandPkIds);



    @EntityGraph(attributePaths = {
            "evaluationStatus",
            "profileTrackerStatus",
            "demand",
            "profile"
    })
    Page<ProfileTracker> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"profile"})
    List<ProfileTracker> findByDemand_Id(Long demandId);

    @EntityGraph(attributePaths = {"demand"})
    List<ProfileTracker> findByProfile_Id(Long profileId);
// Import
    Optional<ProfileTracker> findByDemandIdAndProfileId(Long demandId, Long profileId);

    Optional<ProfileTracker> findByDemand_IdAndProfile_Id(Long id, Long id1);

    @Query("""
    SELECT COUNT(pt)
    FROM ProfileTracker pt
    WHERE pt.profile.id = :profileId
      AND pt.demand.id <> :demandId
      AND LOWER(pt.profileTrackerStatus.name) = 'client selected'
""")
    long countClientSelectedForOtherDemand(@Param("profileId") Long profileId,
                                           @Param("demandId") Long demandId);
}
