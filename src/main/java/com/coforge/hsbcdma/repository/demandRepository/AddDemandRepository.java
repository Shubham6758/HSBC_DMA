//package com.coforge.hsbcdma.repository.demandRepository;
//
//import com.coforge.hsbcdma.entity.AddDemand;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.data.jpa.repository.EntityGraph;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//import org.springframework.data.jpa.repository.Query;
//
//public interface AddDemandRepository extends JpaRepository<AddDemand,Long>,JpaSpecificationExecutor<AddDemand> {
//
//
//    @EntityGraph(attributePaths = {
//            "hbu", "hbuSpoc",
//            "band", "priority", "lob",
//            "demandType", "demandTimeline",
//            "externalInternal", "status", "pod",
//            "pmo", "pmoSpoc", "salesSpoc",
//            "hiringManager", "deliveryManager",
//            "skillCluster","primarySkills","secondarySkills","demandLocations","projectManager"
//    })
//    @Query("select d from AddDemand d")
//    Page<AddDemand> findAllWithRefs(Pageable pageable);
//
//
//    @Override
//    @EntityGraph(attributePaths = {
//            "hbu", "hbuSpoc",
//            "band", "priority", "lob",
//            "demandType", "demandTimeline",
//            "externalInternal", "status", "pod",
//            "pmo", "pmoSpoc", "salesSpoc",
//            "hiringManager", "deliveryManager",
//            "skillCluster","primarySkills","secondarySkills","demandLocations"
//    })
//    Page<AddDemand> findAll(Specification<AddDemand> spec, Pageable pageable);
//
//
//
//}
