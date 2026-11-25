package com.coforge.hsbcdma.repository.ProfileRepositories.KaratRepositories;

import com.coforge.hsbcdma.entity.dropdownEntities.Profile.Karat.CoderpadResultMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoderpadResultMasterRepository extends JpaRepository<CoderpadResultMaster, Long> {

    Optional<CoderpadResultMaster> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}