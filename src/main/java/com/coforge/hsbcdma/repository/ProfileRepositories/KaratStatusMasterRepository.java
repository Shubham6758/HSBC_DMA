package com.coforge.hsbcdma.repository.ProfileRepositories;

import com.coforge.hsbcdma.entity.dropdownEntities.Profile.KaratStatusMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KaratStatusMasterRepository extends JpaRepository<KaratStatusMaster,Long> {
    Optional<KaratStatusMaster> findByNameIgnoreCase(String s);
}
