package com.coforge.hsbcdma.repository.ProfileRepositories.KaratRepositories;

import com.coforge.hsbcdma.entity.dropdownEntities.Profile.Karat.KaratReadyStatusMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KaratReadyStatusMasterRepository extends JpaRepository<KaratReadyStatusMaster, Long> {

    Optional<KaratReadyStatusMaster> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}