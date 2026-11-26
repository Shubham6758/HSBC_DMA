package com.coforge.hsbcdma.revenuemargin.repository;

import com.coforge.hsbcdma.revenuemargin.entity.HsbcCentralResourceTrackerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HsbcCentralResourceTrackerRepository
        extends JpaRepository<
        HsbcCentralResourceTrackerEntity,
        Long> {

    HsbcCentralResourceTrackerEntity
    findByCoforgeIdAndPsCode(
            String coforgeId,
            String psCode
    );
    HsbcCentralResourceTrackerEntity findByKey(String key);
    List<HsbcCentralResourceTrackerEntity> findAllByPsCode(String psCode);
    List<HsbcCentralResourceTrackerEntity> findAllByCtool(String ctool);
}