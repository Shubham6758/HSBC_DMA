package com.coforge.hsbcdma.audit.ProfileTracker;

import com.coforge.hsbcdma.audit.core.AuditMetaExtractor;
import com.coforge.hsbcdma.entity.ProfileTracker;
import org.springframework.stereotype.Component;

@Component
public class ProfileTrackerAuditMeta implements AuditMetaExtractor<ProfileTracker> {

    @Override
    public Long entityId(ProfileTracker entity) {
        return entity.getId();
    }

    @Override
    public Long demandId(ProfileTracker entity) {
        return entity.getDemand() != null ? entity.getDemand().getId() : null;
    }

    @Override
    public Long profileId(ProfileTracker entity) {
        return entity.getProfile() != null ? entity.getProfile().getId() : null;
    }

    @Override
    public String changedBy(ProfileTracker entity, String fallbackUserId) {
        String u = firstNonNull(entity.getUpdatedByUserId(), entity.getCreatedByUserId());
        return u != null ? u : fallbackUserId;
    }

    @Override
    public Long profileTrackerId(ProfileTracker entity){
        return entity.getId();
    }

    private String firstNonNull(String a, String b) {
        return a != null ? a : b;
    }

}
