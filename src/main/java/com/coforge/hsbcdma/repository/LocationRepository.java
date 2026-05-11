package com.coforge.hsbcdma.repository;


import com.coforge.hsbcdma.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Created by Chetan

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByIdIn(List<Long> ids);

    Optional<Location> findByNameIgnoreCase(String name);
}
