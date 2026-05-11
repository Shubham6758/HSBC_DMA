package com.coforge.hsbcdma.audit.core;

public interface AuditMetaExtractor<T> {
    Long entityId(T entity);

    default Long demandId(T entity) { return null; }

    default Long profileId(T entity) { return null; }

    default Long profileTrackerId(T entity){return null;}

    default Long onboardingId(T entity){return null;}

    /**
     * If caller passes userId explicitly, you can use it as fallback.
     */
    String changedBy(T entity, String fallbackUserId);
}