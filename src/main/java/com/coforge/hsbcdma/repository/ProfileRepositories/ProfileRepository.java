package com.coforge.hsbcdma.repository.ProfileRepositories;

import com.coforge.hsbcdma.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long>, JpaSpecificationExecutor<Profile> {
    boolean existsByEmailId(String emailId);
    boolean existsByEmpId(String empId);
    List<Profile> findByFileName(String fileName);

    // New checks
    boolean existsByPhoneNumber(Long phoneNumber);
    boolean existsByPanNumberIgnoreCase(String panNumber);
    boolean existsByEmailIdIgnoreCase(String emailId);

    List<Profile> findByIsActiveTrue();


    @EntityGraph(attributePaths = {
            "skillCluster",
            "location",
            "hbu",
            "externalInternal"
    })
    @Query("select p from Profile p")
    Page<Profile> findAllWithRefs(Pageable pageable);

    @EntityGraph(attributePaths = {
            "skillCluster",
            "location",
            "hbu",
            "externalInternal"
    })

    @Query("""
        SELECT p
        FROM Profile p
        WHERE (:hbuId IS NULL OR p.hbu.id = :hbuId)
    """)

    Page<Profile> findAllWithRefsByHbu(@Param("hbuId") Long hbuId, Pageable pageable);
    
    
//    Import
    Optional<Profile>findByEmailIdIgnoreCase(String emailId);

    Optional<Profile> findByPanNumberIgnoreCase(String panNumber);

    Optional<Profile> findByPhoneNumber(Long phoneNumber);

    List<Profile> findAllByCandidateNameIgnoreCase(String raw);

    boolean existsBySapId(String sapId);

    Optional<Profile> findByEmpId(String trim);

    Optional<Profile> findByEmpIdAndCandidateNameIgnoreCase(String trim, String trim1);

    @Query("""
    SELECT COUNT(p)
    FROM Profile p
    WHERE LOWER(p.profileStatus.name) = LOWER(:status)
    AND (:hbuId IS NULL OR p.hbu.id = :hbuId)
""")
    long countByStatusAndHbu(@Param("status") String status,
                             @Param("hbuId") Long hbuId);

    List<Profile> findByTechIrsFileName(String techIrsFileName);

    List<Profile> findByIsDraftTrue();

    Page<Profile> findByIsDraftTrue(Pageable pageable);

    Optional<Profile> findByIdAndIsDraftTrue(Long id);
}