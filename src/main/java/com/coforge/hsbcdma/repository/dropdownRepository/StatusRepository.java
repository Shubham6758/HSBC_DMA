package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status,Long> {

    Optional<Status> findByStatus(String status);

    Optional<Status> findByStatusIgnoreCase(String openPositions);
}

