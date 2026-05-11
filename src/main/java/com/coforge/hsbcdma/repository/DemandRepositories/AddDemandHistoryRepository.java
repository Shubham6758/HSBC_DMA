package com.coforge.hsbcdma.repository.DemandRepositories;


import com.coforge.hsbcdma.entity.AddDemandHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddDemandHistoryRepository extends JpaRepository<AddDemandHistory, Long> {
    List<AddDemandHistory> findByDemandId(Long demandId);
}