package com.coforge.hsbcdma.revenuemargin.repository;

import com.coforge.hsbcdma.revenuemargin.entity.RefreshAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshAuditRepository
        extends JpaRepository<
        RefreshAuditEntity,
        Long> {

    RefreshAuditEntity
    findTopBySourceNameOrderByRefreshTimeDesc(
            String sourceName
    );
    java.util.List<RefreshAuditEntity>
    findBySourceNameOrderByRefreshTimeDesc(String sourceName);
}