package com.coforge.hsbcdma.audit.Onboarding;

import com.coforge.hsbcdma.audit.core.AuditMetaExtractor;
import com.coforge.hsbcdma.entity.Onboarding;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
@Component
public class OnboardingAuditMeta implements AuditMetaExtractor<Onboarding> {

    @Override
    public Long entityId(Onboarding entity) {
        return entity.getId();
    }

    @Override
    public Long onboardingId(Onboarding entity) {
        return entity.getId();
    }


    @Override
    public Long demandId(Onboarding entity){
        return entity.getDemand() != null ? entity.getDemand().getId() : null;
    }

    @Override
    public Long profileId(Onboarding entity) {
        return entity.getProfile() != null ? entity.getProfile().getId() : null;
    }

    @Override
    public Long profileTrackerId(Onboarding entity){
        return entity.getProfileTracker() != null ? entity.getProfileTracker().getId() : null;
    }

    @Override
    public String changedBy(Onboarding entity, String fallbackUserId) {
        if (entity == null) return fallbackUserId;
        String u = entity.getProfileTracker() != null ? entity.getProfileTracker().getUpdatedByUserId():null;
        return StringUtils.hasText(u) ? u : fallbackUserId;
    }
}
