package com.coforge.hsbcdma.audit.ProfileTracker;

import com.coforge.hsbcdma.audit.core.ReflectionIdUtil;
import com.coforge.hsbcdma.audit.core.SnapshotBuilder;
import com.coforge.hsbcdma.entity.ProfileTracker;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
@Component
public class ProfileTrackerSnapshotBuilder implements SnapshotBuilder<ProfileTracker> {
    @Override
    public Map<String, Object> snapshot(ProfileTracker pt) {

        if (pt == null) return Collections.emptyMap();

        Map<String, Object> m = new LinkedHashMap<>();

        m.put("id", pt.getId());
        m.put("demandId", pt.getDemand() != null ? pt.getDemand().getId() : null);
        m.put("profileId", pt.getProfile() != null ? pt.getProfile().getId() : null);

        m.put("profileSharedDate", pt.getProfileSharedDate() != null ? pt.getProfileSharedDate().toString() : null);
        m.put("attachedDate", pt.getAttachedDate() != null ? pt.getAttachedDate().toString() : null);
        m.put("interviewDate", pt.getInterviewDate() != null ? pt.getInterviewDate().toString() : null);
        m.put("decisionDate", pt.getDecisionDate() != null ? pt.getDecisionDate().toString() : null);

        m.put("evaluationStatusId", ReflectionIdUtil.idOf(pt.getEvaluationStatus()));
        m.put("profileTrackerStatusId", ReflectionIdUtil.idOf(pt.getProfileTrackerStatus()));

//        m.put("evaluationStatus", RefSnapshotUtil.ref(pt.getEvaluationStatus()));
//        m.put("profileTrackerStatus", RefSnapshotUtil.ref(pt.getProfileTrackerStatus()));

        return m;
    }
}
