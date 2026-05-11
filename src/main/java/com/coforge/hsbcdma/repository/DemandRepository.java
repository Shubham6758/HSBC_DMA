package com.coforge.hsbcdma.repository;

import com.coforge.hsbcdma.entity.Demand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * DemandRepository.
 */
public interface DemandRepository extends JpaRepository<Demand, Long> {
    Optional<Demand> findByDemandBusinessId(String demandBusinessId);
}
