package com.coforge.hsbcdma.revenuemargin.repository;

import com.coforge.hsbcdma.revenuemargin.entity.RdgMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RdgMasterRepository
        extends JpaRepository<RdgMasterEntity, Long> {
    RdgMasterEntity findByEmployeeNo(
            String employeeNo
    );
}