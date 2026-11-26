package com.coforge.hsbcdma.repository.PoolRRRepositories;

import com.coforge.hsbcdma.entity.PoolRR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PoolRRRepository extends JpaRepository<PoolRR, Long> {
    boolean existsByRrIgnoreCase(String rr);
    Optional<PoolRR> findByRr(String rr);
}

