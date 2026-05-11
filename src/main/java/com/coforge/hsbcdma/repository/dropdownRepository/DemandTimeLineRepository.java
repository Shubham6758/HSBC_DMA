package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.DemandTimeLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DemandTimeLineRepository extends JpaRepository<DemandTimeLine,Long> {
//    import
    Optional<DemandTimeLine> findByDemandTimeLineIgnoreCase(String demandTimeLine);
}
