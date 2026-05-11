package com.coforge.hsbcdma.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuditHistoryRepository extends JpaRepository<AuditHistory,Long> {

    List<AuditHistory> findByEntityTypeAndEntityIdOrderByChangedAtDesc(AuditHistory.EntityType type, Long entityId);

    // Demand -> all events (demand updates + attach + tracker updates)
    List<AuditHistory> findByDemandIdOrderByChangedAtDesc(Long demandId);

    // Profile -> all attach/update tracker events (vice versa)
    List<AuditHistory> findByProfileIdOrderByChangedAtDesc(Long profileId);

    // Tracker -> history of that tracker row
    List<AuditHistory> findByProfileTrackerIdOrderByChangedAtDesc(Long trackerId);

    @Query("""
    select h
    from AuditHistory h
    where h.demandId = :demandId
      and (
            (h.entityType = com.coforge.hsbcdma.audit.AuditHistory$EntityType.ADD_DEMAND
                 and h.action in (
                     com.coforge.hsbcdma.audit.AuditHistory$Action.UPDATE_DEMAND
                 )
            )
         or (h.entityType = com.coforge.hsbcdma.audit.AuditHistory$EntityType.PROFILE_TRACKER
                 and h.action in (
                     com.coforge.hsbcdma.audit.AuditHistory$Action.ATTACH,
                     com.coforge.hsbcdma.audit.AuditHistory$Action.UPDATE,
                     com.coforge.hsbcdma.audit.AuditHistory$Action.UPDATE_PROFILE_TRACKER
                 )
            )
         or (h.entityType = com.coforge.hsbcdma.audit.AuditHistory$EntityType.ONBOARDING
                 and h.action in (
                     com.coforge.hsbcdma.audit.AuditHistory$Action.ATTACH,
                     com.coforge.hsbcdma.audit.AuditHistory$Action.UPDATE,
                     com.coforge.hsbcdma.audit.AuditHistory$Action.UPDATE_ONBOARDING
                 )
            )
      )
    order by h.id desc
    """)
    Page<AuditHistory> findDemandTimeline(@Param("demandId") Long demandId, Pageable pageable);


    @Query("""
    select h
    from AuditHistory h
    where h.profileId = :profileId
      and (
            (h.entityType = com.coforge.hsbcdma.audit.AuditHistory.EntityType.PROFILE
                 and h.action in (
                     com.coforge.hsbcdma.audit.AuditHistory.Action.UPDATE_PROFILE
                 )
            )
         or (h.entityType = com.coforge.hsbcdma.audit.AuditHistory.EntityType.PROFILE_TRACKER
                 and h.action in (
                     com.coforge.hsbcdma.audit.AuditHistory.Action.ATTACH,
                     com.coforge.hsbcdma.audit.AuditHistory.Action.UPDATE_PROFILE_TRACKER
                 )
            )
         or (h.entityType = com.coforge.hsbcdma.audit.AuditHistory.EntityType.ONBOARDING
                 and h.action in (
                     com.coforge.hsbcdma.audit.AuditHistory.Action.UPDATE_ONBOARDING
                 )
            )
      )
    order by h.id desc
    """)
    Page<AuditHistory> findProfileTimeline(@Param("profileId") Long profileId, Pageable pageable);
}
