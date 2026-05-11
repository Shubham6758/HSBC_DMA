package com.coforge.hsbcdma.repository.DemandRepositories;


import com.coforge.hsbcdma.entity.AddDemand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AddDemandRepository extends JpaRepository<AddDemand, Long> {

    Optional<AddDemand> findByDemandId(Long demandId);

    List<AddDemand> findAllByDemandIdIn(List<Long> demandIds);

    boolean existsByDemandId(Long demandId);

    @EntityGraph(attributePaths = {
            "hbu", "hbuSpoc",
            "band", "priority", "lob",
            "demandType", "demandTimeline",
            "externalInternal", "status", "pod",
            "pmo", "pmoSpoc", "salesSpoc",
            "hiringManager", "deliveryManager",
            "skillCluster","primarySkills","secondarySkills","demandLocations","projectManager","profileTrackers.profile"
    })
    @Query("select d from AddDemand d")
    Page<AddDemand> findAllWithRefs(Pageable pageable);



    @EntityGraph(attributePaths = {
            "hbu", "lob", "band", "priority", "demandType", "demandTimeline",
            "externalInternal", "status", "pod", "pmo", "pmoSpoc", "salesSpoc",
            "hiringManager", "deliveryManager", "skillCluster", "projectManager",
            "hbuSpoc",
            "primarySkills", "secondarySkills", "demandLocations"
    })
    @Query("""
        SELECT d
        FROM AddDemand d
        WHERE (:hbuId IS NULL OR d.hbu.id = :hbuId)
        ORDER BY d.status.priority
    """)
    Page<AddDemand> findAllWithRefsByHbu(@Param("hbuId") Long hbuId, Pageable pageable);



    @EntityGraph(attributePaths = {
            "hbu", "hbuSpoc",
            "band", "priority", "lob",
            "demandType", "demandTimeline",
            "externalInternal", "status", "pod",
            "pmo", "pmoSpoc", "salesSpoc",
            "hiringManager", "deliveryManager",
            "skillCluster","primarySkills","secondarySkills","demandLocations"
    })
    Page<AddDemand> findAll(Specification<AddDemand> spec, Pageable pageable);

    List<AddDemand> findByFileName(String fileName);

    @Query("select d.rrNumber from AddDemand d where d.rrNumber in :rrs")
    List<Long> findExistingRrNumbers(@Param("rrs") Collection<Long> rrs);

    boolean existsByRrNumberAndIdNot(Long rrNumber, Long id);


    //for history
    @Query(value = "SELECT primary_skill_id FROM add_demand_primary_skills_map WHERE add_demand_id = :demandPkId", nativeQuery = true)
    List<Long> listPrimarySkillIds(Long demandPkId);

    @Query(value = "SELECT secondary_skill_id FROM add_demand_secondary_skills_map WHERE add_demand_id = :demandPkId", nativeQuery = true)
    List<Long> listSecondarySkillIds(Long demandPkId);

    @Query(value = "SELECT location_id FROM add_demand_locations_map WHERE add_demand_id = :demandPkId", nativeQuery = true)
    List<Long> listLocationIds(Long demandPkId);

    Optional<AddDemand> findByRrNumber(Long rrNumber);

    Optional<AddDemand> findByDemandIdAndLobId(Long demandId, Long lobId);

    Optional<AddDemand> findByDemandIdAndLobIdAndSubLobId(Long demandId, Long id, Long id1);


//    Optional<AddDemand> findByDemandIdentifierIgnoreCase(Long raw);

}



