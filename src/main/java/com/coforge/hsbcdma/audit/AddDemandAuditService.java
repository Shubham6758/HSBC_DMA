package com.coforge.hsbcdma.audit;


import com.coforge.hsbcdma.entity.AddDemand;
import com.coforge.hsbcdma.entity.AddDemandHistory;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AddDemandAuditService {

    private static final Logger logger = LoggerFactory.getLogger(AddDemandAuditService.class);

    private final AddDemandHistoryRepository historyRepo;
    private final AddDemandSnapshot1Builder snapshotBuilder;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    private final AuditHistoryRepository auditRepo;

    public void recordCreate(AddDemand after) {
        Map<String, Object> newSnap = snapshotBuilder.snapshot(after);
        AddDemandHistory h = new AddDemandHistory();
        h.setDemandId(after.getId());
        h.setAction(AddDemandHistory.Action.CREATE);
        h.setChangedByUserId(firstNonNull(after.getUpdatedByUserId(), after.getCreatedByUserId()));
//        h.setOldData(null);
//        h.setNewData(toJson(newSnap));
        h.setDiff(null);
//        h.setSummary("Created");

        historyRepo.save(h);
        logger.info("History recorded: CREATE demandPkId={}", after.getId());
    }

    public void recordUpdate(AddDemand after, Map<String, Object> oldSnap) {
        Map<String, Object> newSnap = snapshotBuilder.snapshot(after);

        Map<String, Object> diff = DiffUtil.diff(oldSnap, newSnap);
        if (diff.isEmpty()) {
            logger.info("No changes detected, skipping history. demandPkId={}", after.getId());
            return;
        }

        AddDemandHistory h = new AddDemandHistory();
        h.setDemandId(after.getId());
        h.setAction(AddDemandHistory.Action.UPDATE);
        h.setChangedByUserId(firstNonNull(after.getUpdatedByUserId(), after.getCreatedByUserId()));
//        h.setOldData(toJson(oldSnap));
//        h.setNewData(toJson(newSnap));
        h.setDiff(toJson(diff));
//        h.setSummary(buildSummary(diff));

        historyRepo.save(h);
        logger.info("History recorded: UPDATE demandPkId={} fieldsChanged={}", after.getId(), diff.keySet());
    }



    /** Returns saved event or null when no diff */
//    public void recordUpdate1(AddDemand after, Map<String, Object> oldSnap) {
//        Map<String, Object> newSnap = snapshotBuilder.snapshot(after);
//
//        Map<String, Object> diff = DiffUtil.diff(oldSnap, newSnap);
//        if (diff.isEmpty()) {
//            logger.info("No changes detected, skipping audit. demandPkId={}", after.getId());
//            return;
//        }
//
//        AuditHistory h = new AuditHistory();
//        h.setEntityType(AuditHistory.EntityType.ADD_DEMAND);
//        h.setEntityId(after.getId());
//        h.setDemandId(after.getId());
//        h.setAction(AuditHistory.Action.UPDATE);
//        h.setChangedByUserId(firstNonNull(after.getUpdatedByUserId(), after.getCreatedByUserId()));
//        h.setDiff(toJson(diff));
//
//        AuditHistory saved = auditRepo.save(h);
//        logger.info("Audit recorded: UPDATE demandPkId={} auditId={} fieldsChanged={}",
//                after.getId(), saved.getId(), diff.keySet());
//    }

    private String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new IllegalStateException("JSON serialization failed", e);
        }
    }

    private String firstNonNull(String a, String b) {
        return a != null ? a : b;
    }

//    private String buildSummary(Map<String, Object> diff) {
//        // Example: "priorityId: 2→3; statusId: 4→5"
//        return diff.entrySet().stream()
//                .limit(5)
//                .map(e -> {
//                    @SuppressWarnings("unchecked")
//                    Map<String, Object> v = (Map<String, Object>) e.getValue();
//                    Object oldV = v.get("old");
//                    Object newV = v.get("new");
//                    return e.getKey() + ": " + safe(oldV) + "→" + safe(newV);
//                })
//                .collect(Collectors.joining("; "));
//    }

    private String safe(Object o) {
        return o == null ? "∅" : o.toString();
    }
}