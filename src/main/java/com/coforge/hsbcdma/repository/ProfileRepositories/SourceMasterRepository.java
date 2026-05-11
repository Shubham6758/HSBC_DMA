package com.coforge.hsbcdma.repository.ProfileRepositories;

import com.coforge.hsbcdma.entity.dropdownEntities.Profile.SourceMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SourceMasterRepository extends JpaRepository<SourceMaster,Long> {

    Optional<SourceMaster> findByNameIgnoreCase(String s);
}
