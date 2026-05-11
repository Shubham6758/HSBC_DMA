package com.coforge.hsbcdma.audit.ProfileTracker;


import com.coforge.hsbcdma.entity.ProfileTracker;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ProfileTracker1SnapshotBuilder {

    public Map<String, Object> snapshot(ProfileTracker pt) {
        if (pt == null) return Collections.emptyMap();

        Map<String, Object> m = new LinkedHashMap<>();

        m.put("id", pt.getId());
        m.put("demandId", idOf(pt.getDemand()));
        m.put("profileId", idOf(pt.getProfile()));

        m.put("profileSharedDate", pt.getProfileSharedDate() != null ? pt.getProfileSharedDate().toString() : null);
        m.put("attachedDate", pt.getAttachedDate() != null ? pt.getAttachedDate().toString() : null);
        m.put("interviewDate", pt.getInterviewDate() != null ? pt.getInterviewDate().toString() : null);
        m.put("decisionDate", pt.getDecisionDate() != null ? pt.getDecisionDate().toString() : null);

        m.put("evaluationStatusId", idOf(pt.getEvaluationStatus()));
        m.put("profileTrackerStatusId", idOf(pt.getProfileTrackerStatus()));

        return m;
    }

    private Long idOf(Object ref) {
        if (ref == null) return null;
        try {
            return (Long) ref.getClass().getMethod("getId").invoke(ref);
        } catch (Exception e) {
            return null;
        }
    }
}
