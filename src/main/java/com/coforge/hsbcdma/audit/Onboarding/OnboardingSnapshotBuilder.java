package com.coforge.hsbcdma.audit.Onboarding;

import com.coforge.hsbcdma.audit.core.ReflectionIdUtil;
import com.coforge.hsbcdma.audit.core.SnapshotBuilder;
import com.coforge.hsbcdma.entity.Onboarding;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
@Component
public class OnboardingSnapshotBuilder implements SnapshotBuilder<Onboarding> {

    @Override
    public Map<String, Object> snapshot(Onboarding o) {
        if (o == null) return Collections.emptyMap();

        Map<String, Object> m = new LinkedHashMap<>();

        // ---- identity ----
        m.put("id", o.getId());

        // ---- relational fields -> IDs only (stable diffs) ----
        m.put("profileTrackerId", ReflectionIdUtil.idOf(o.getProfileTracker()));
        m.put("wbsTypeId", ReflectionIdUtil.idOf(o.getWbsType()));
        m.put("bgvStatusId", ReflectionIdUtil.idOf(o.getBgvStatus()));
        m.put("onboardingStatusId", ReflectionIdUtil.idOf(o.getOnboardingStatus()));

        // NEW refs you added
        m.put("demandId", ReflectionIdUtil.idOf(o.getDemand()));
        m.put("profileId", ReflectionIdUtil.idOf(o.getProfile()));

        // ---- scalar fields ----
        m.put("offerDate", o.getOfferDate() != null ? o.getOfferDate().toString(): null);
        m.put("dateOfJoining", o.getDateOfJoining() != null ?o.getDateOfJoining().toString(): null);
        m.put("ctoolId", o.getCtoolId());

        m.put("pevUploadDate",  o.getPevUploadDate() != null ? o.getPevUploadDate().toString(): null);
        m.put("vpTagging", o.getVpTagging() != null ? o.getVpTagging().toString(): null);
        m.put("techSelectDate", o.getTechSelectDate() != null ? o.getTechSelectDate().toString(): null);
        m.put("hsbcOnboardingDate", o.getHsbcOnboardingDate() != null ? o.getHsbcOnboardingDate().toString(): null);

        return m;
    }

}
