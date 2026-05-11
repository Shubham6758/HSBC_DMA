package com.coforge.hsbcdma.audit.core;


import com.coforge.hsbcdma.audit.AuditHistory;
import com.coforge.hsbcdma.audit.AuditHistoryRepository;
import com.coforge.hsbcdma.audit.DiffUtil;
import com.coforge.hsbcdma.audit.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GenericAuditService {

    private final AuditHistoryRepository auditRepo;
    private final JsonUtil jsonUtil;

    /**
     * Take snapshot safely (helper)
     */
    public <T> Map<String, Object> snapshot(T entity, SnapshotBuilder<T> builder) {
        return builder.snapshot(entity);
    }

    /**
     * Record ATTACH event. Stores {"new": snapshot}
     */
    public <T> AuditHistory recordAttach(
            AuditHistory.EntityType entityType,
            T after,
            SnapshotBuilder<T> snapshotBuilder,
            AuditMetaExtractor<T> meta,
            String userId
    ) {
        Map<String, Object> snap = snapshotBuilder.snapshot(after);

        AuditHistory h = AuditHistory.builder()
                .entityType(entityType)
                .entityId(meta.entityId(after))
                .demandId(meta.demandId(after))
                .profileId(meta.profileId(after))
                .profileTrackerId(meta.profileTrackerId(after))
                .onboardingId(meta.onboardingId(after))
                .action(AuditHistory.Action.ATTACH)
                .changedByUserId(meta.changedBy(after, userId))
                .diff(jsonUtil.toJson(Map.of("new", snap)))
                .build();

        return auditRepo.save(h);
    }

    /**
     * Record UPDATE event. Stores {field: {old,new}}
     * Returns null when no diff.
     */
    public <T> AuditHistory recordUpdate(
            AuditHistory.EntityType entityType,
            AuditHistory.Action action,
            T after,
            Map<String, Object> oldSnap,
            SnapshotBuilder<T> snapshotBuilder,
            AuditMetaExtractor<T> meta,
            String userId
    ) {
        Map<String, Object> newSnap = snapshotBuilder.snapshot(after);
        Map<String, Object> diff = DiffUtil.diff(oldSnap, newSnap);

        if (diff.isEmpty()) return null;

        AuditHistory h = AuditHistory.builder()
                .entityType(entityType)
                .entityId(meta.entityId(after))
                .demandId(meta.demandId(after))
                .profileId(meta.profileId(after))
                .profileTrackerId(meta.profileTrackerId(after))
                .onboardingId(meta.onboardingId(after))
                .action(action)
                .changedByUserId(meta.changedBy(after, userId))
                .diff(jsonUtil.toJson(diff))
                .build();

        return auditRepo.save(h);
    }
}
