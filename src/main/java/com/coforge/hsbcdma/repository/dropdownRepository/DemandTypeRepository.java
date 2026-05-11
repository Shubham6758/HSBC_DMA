package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.DemandType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DemandTypeRepository extends JpaRepository<DemandType,Long> {
//    import
    Optional<DemandType> findByDemandTypeIgnoreCase(String demandType);
}
