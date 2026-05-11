package com.coforge.hsbcdma.repository;

import com.coforge.hsbcdma.entity.AddNewDemand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddNewDemandRepository extends JpaRepository<AddNewDemand,Long> {
    Optional<Long> findByRrNumber(Long rrNumber);
    Optional<String> findByDemandId(String demandId);
}
