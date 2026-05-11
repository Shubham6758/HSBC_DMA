package com.coforge.hsbcdma.audit.Profile;

import com.coforge.hsbcdma.audit.core.AuditMetaExtractor;
import com.coforge.hsbcdma.entity.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileAuditMeta implements AuditMetaExtractor<Profile> {

    @Override
    public Long entityId(Profile entity) {
        return entity.getId();
    }

    @Override
    public Long profileId(Profile entity) {
        return entity.getId();
    }

    @Override
    public String changedBy(Profile entity, String fallbackUserId) {
        if (entity.getUpdatedByUserId() != null && !entity.getUpdatedByUserId().isBlank())
            return entity.getUpdatedByUserId();
        if (entity.getCreatedByUserId() != null && !entity.getCreatedByUserId().isBlank())
            return entity.getCreatedByUserId();
        return fallbackUserId;
    }

}
