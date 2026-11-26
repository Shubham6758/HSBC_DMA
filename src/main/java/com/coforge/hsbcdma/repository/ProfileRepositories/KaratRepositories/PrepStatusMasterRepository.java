package com.coforge.hsbcdma.repository.ProfileRepositories.KaratRepositories;

import com.coforge.hsbcdma.entity.dropdownEntities.Profile.Karat.PrepStatusMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrepStatusMasterRepository extends JpaRepository<PrepStatusMaster, Long> {

    Optional<PrepStatusMaster> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}