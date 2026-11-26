package com.coforge.hsbcdma.revenuemargin.repository;

import com.coforge.hsbcdma.revenuemargin.entity.VmDumpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VmDumpRepository
        extends JpaRepository<
        VmDumpEntity,
        Long> {
    List<VmDumpEntity> findByResourceId(String resourceId);
    List<VmDumpEntity> findByCtoolId(String ctoolId);
}