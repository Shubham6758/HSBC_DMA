package com.coforge.hsbcdma.repository.ProfileRepositories;

import com.coforge.hsbcdma.entity.dropdownEntities.Profile.OverallStatusMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OverallStatusMasterRepository extends JpaRepository<OverallStatusMaster,Long> {

    Optional<OverallStatusMaster> findByNameIgnoreCase(String s);
}
