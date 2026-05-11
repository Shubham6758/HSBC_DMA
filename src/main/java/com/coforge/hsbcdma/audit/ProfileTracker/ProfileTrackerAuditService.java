package com.coforge.hsbcdma.audit.ProfileTracker;


import com.coforge.hsbcdma.audit.AuditHistoryRepository;
import com.coforge.hsbcdma.audit.JsonUtil;
import com.coforge.hsbcdma.entity.ProfileTracker;
import com.coforge.hsbcdma.entity.ProfileTrackerHistory;
import com.coforge.hsbcdma.repository.ProfileTrackerRepositories.ProfileTrackerHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProfileTrackerAuditService {

    private final ProfileTrackerHistoryRepository historyRepo;
    private final AuditHistoryRepository auditHistoryRepository;
    private final ProfileTracker1SnapshotBuilder snapshotBuilder;
    private final JsonUtil jsonUtil; // your helper

    /**
     * Call right after INSERT in profile_tracker
     */
    public void recordAttach(ProfileTracker tracker, String userId, Long addDemandHistoryId) {

        ProfileTrackerHistory h = new ProfileTrackerHistory();
        h.setProfileTrackerId(tracker.getId()); // must be saved first
        h.setDemandId(tracker.getDemand().getId());
        h.setProfileId(tracker.getProfile().getId());
        h.setAction(ProfileTrackerHistory.Action.ATTACH);
        h.setChangedByUserId(userId);
//        h.setAddDemandHistoryId(addDemandHistoryId);

        // On attach, store full snapshot (so you can see what was attached)
        h.setDiff(jsonUtil.toJson(Map.of("new", snapshotBuilder.snapshot(tracker))));

        historyRepo.save(h);
    }

    /**
     * Call right after UPDATE in profile_tracker
     */
//    public void recordUpdate(ProfileTracker after, Map<String, Object> oldSnap,
//                             String userId, Long addDemandHistoryId) {
//
//        Map<String, Object> newSnap = snapshotBuilder.snapshot(after);
//        Map<String, Object> diff = DiffUtil.diff(oldSnap, newSnap);
//
//        if (diff.isEmpty()) return;
//
//        ProfileTrackerHistory h = new ProfileTrackerHistory();
//        h.setProfileTrackerId(after.getId());
//        h.setDemandId(after.getDemand().getId());
//        h.setProfileId(after.getProfile().getId());
//        h.setAction(ProfileTrackerHistory.Action.UPDATE);
//        h.setChangedByUserId(userId);
////        h.setAddDemandHistoryId(addDemandHistoryId);
//        h.setDiff(jsonUtil.toJson(diff));
//
//        historyRepo.save(h);
//    }
//
//
//    public void recordAttach1(ProfileTracker tracker) {
//
//        Map<String, Object> snap = snapshotBuilder.snapshot(tracker);
//
//        AuditHistory h = new AuditHistory();
//        h.setEntityType(AuditHistory.EntityType.PROFILE_TRACKER); // ensure this enum exists
//        h.setEntityId(tracker.getId());
//        if (tracker.getDemand() != null) h.setDemandId(tracker.getDemand().getId());
//        if (tracker.getProfile() != null) h.setProfileId(tracker.getProfile().getId());
//        h.setAction(AuditHistory.Action.ATTACH);
//        h.setChangedByUserId(firstNonNull(tracker.getUpdatedByUserId(), tracker.getCreatedByUserId()));
//        h.setDiff(jsonUtil.toJson(Map.of("new", snap)));
//
//        AuditHistory saved = auditHistoryRepository.save(h);
//    }
//
//
//    /**
//     * Returns saved AuditHistory or null when there is no diff.
//     * Call this right AFTER you persist the 'after' entity (so IDs are present).
//     */
//    public void recordUpdate1(ProfileTracker after, Map<String, Object> oldSnap) {
//        Map<String, Object> newSnap = snapshotBuilder.snapshot(after);
//
//        Map<String, Object> diff = DiffUtil.diff(oldSnap, newSnap);
//        if (diff.isEmpty()) {
//            return;
//        }
//
//        AuditHistory h = new AuditHistory();
//        h.setEntityType(AuditHistory.EntityType.PROFILE_TRACKER); // <<< add this enum constant
//        h.setEntityId(after.getId());
//        // Populate convenience columns for querying:
//        if (after.getDemand() != null) {
//            h.setDemandId(after.getDemand().getId());
//        }
//        if (after.getProfile() != null) {
//            h.setProfileId(after.getProfile().getId());
//        }
//        h.setAction(AuditHistory.Action.UPDATE);
//        h.setChangedByUserId(firstNonNull(after.getUpdatedByUserId(), after.getCreatedByUserId()));
//        h.setDiff(jsonUtil.toJson(diff));
//
//        AuditHistory saved = auditHistoryRepository.save(h);
//
//    }
//    private String firstNonNull(String a, String b) {
//        return a != null ? a : b;
//    }
}
