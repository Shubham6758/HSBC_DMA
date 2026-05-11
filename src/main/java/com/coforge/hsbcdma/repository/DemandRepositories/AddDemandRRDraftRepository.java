package com.coforge.hsbcdma.repository.DemandRepositories;

import com.coforge.hsbcdma.entity.AddDemandRRDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddDemandRRDraftRepository extends JpaRepository<AddDemandRRDraft, Long> {

//    List<AddDemandRRDraft> findAllByDemandIdIn(List<Long> demandIds);
      List<AddDemandRRDraft> findByFileName(String fileName);

    List<AddDemandRRDraft> findByDraftId_Id(Long draftId);
}

//public interface AddDemandRRDraftRepository extends JpaRepository<AddDemandRRDraft, Long> {
//
//    List<AddDemandRRDraft> findByDraftIdOrderByDemandIdAsc(Long draftId);
//
//    Optional<AddDemandRRDraft> findByDraftIdAndDemandId(Long draftId, Long demandId);
//
//    @Modifying(clearAutomatically = true, flushAutomatically = true)
//    @Transactional
//    @Query("""
//        update AddDemandRRDraft r
//           set r.rrNumber = :rr
//         where r.draftId = :draftId
//           and r.demandId = :demandId
//    """)
//    int updateRR(@Param("draftId") Long draftId,
//                 @Param("demandId") Long demandId,
//                 @Param("rr") Long rr);
//}