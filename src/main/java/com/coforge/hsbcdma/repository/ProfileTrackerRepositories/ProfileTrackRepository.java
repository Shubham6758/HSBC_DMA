package com.coforge.hsbcdma.repository.ProfileTrackerRepositories;


import com.coforge.hsbcdma.entity.ProfileTracker;
import com.coforge.hsbcdma.entity.dropdownEntities.Profile.ProfileTrackerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
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


    @Modifying
    @Query("""
    UPDATE ProfileTracker pt
    SET pt.profileTrackerStatus = :status
    WHERE pt.demand.id = :demandId
""")
    int updateStatusByDemandId(@Param("demandId") Long demandId,
                               @Param("status") ProfileTrackerStatus status);

    @Query("""
    SELECT COUNT(pt)
    FROM ProfileTracker pt
    WHERE pt.demand.id = :demandId
      AND (
            pt.profileTrackerStatus IS NULL
            OR LOWER(pt.profileTrackerStatus.name) NOT IN (
                'Client Rejected',
                'Client Screen Reject',
                'Screen Reject',
                'Karat rejected',
                'Internal Reject - Low Margin',
                'Another Candidate Selected On This Demand',
                'Mapped to Other Demand',
                'Redeployed/Mapped to other demand',
                'Demand Abandoned',
                'Demand closed',
                'Backout Before Joining',
                'Backout before interview',
                'Red BGV',
                'Duplicate profile',
                'Duplicate entry',
                'Resigned'
            )
          )
""")
    long countActiveProfilesForDemand(@Param("demandId") Long demandId);

    @Query("""
    SELECT COUNT(pt)
    FROM ProfileTracker pt
    WHERE pt.profile.id = :profileId
      AND LOWER(pt.profileTrackerStatus.name) = 'client selected'
""")
    long countClientSelectedForProfile(@Param("profileId") Long profileId);

    @Query("""
    SELECT COUNT(pt)
    FROM ProfileTracker pt
    WHERE pt.profile.id = :profileId
      AND (
            pt.profileTrackerStatus IS NULL
            OR LOWER(pt.profileTrackerStatus.name) NOT IN (
                'client rejected',
                'client screen reject',
                'screen reject',
                'karat rejected',
                'internal reject - low margin',
                'another candidate selected on this demand',
                'mapped to other demand',
                'redeployed/mapped to other demand',
                'demand abandoned',
                'demand closed',
                'backout before joining',
                'backout before interview',
                'red bgv',
                'duplicate profile',
                'duplicate entry',
                'resigned',
                'removed from demand'
            )
          )
""")
    long countActiveMappingsForProfile(
            @Param("profileId") Long profileId);


    @Query("""
    SELECT COUNT(pt)
    FROM ProfileTracker pt
    WHERE pt.demand.id = :demandId
      AND (
          pt.profileTrackerStatus IS NULL
          OR LOWER(pt.profileTrackerStatus.name) <> 'removed from demand'
      )
    """)
    long countNonRemovedProfilesForDemand(
            @Param("demandId") Long demandId
    );

}
