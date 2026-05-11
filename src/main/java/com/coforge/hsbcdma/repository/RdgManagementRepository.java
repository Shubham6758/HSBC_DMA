package com.coforge.hsbcdma.repository;

import com.coforge.hsbcdma.entity.RdgManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RdgManagementRepository extends JpaRepository<RdgManagement,Long> {

    List<RdgManagement> findByDemandId(String demandId);

    boolean existsByDemandIdAndEmployeeId(String demandId, Long employeeId);

    boolean existsByDemandId(String demandId);

    void deleteByDemandId(String demandId);
}
