package com.coforge.hsbcdma.audit.Demand;

import com.coforge.hsbcdma.audit.core.AuditMetaExtractor;
import com.coforge.hsbcdma.entity.AddDemand;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
@Component
public class AddDemandAuditMeta implements AuditMetaExtractor<AddDemand> {
    @Override
    public Long entityId(AddDemand entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    public Long demandId(AddDemand entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    public String changedBy(AddDemand entity, String fallbackUserId) {
        if (entity == null) return fallbackUserId;
        String u = entity.getUpdatedByUserId();
        return StringUtils.hasText(u) ? u : fallbackUserId;
    }
}
