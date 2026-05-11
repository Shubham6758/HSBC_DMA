package com.coforge.hsbcdma.repository.ProfileTrackerRepositories;

import com.coforge.hsbcdma.entity.dropdownEntities.Profile.ProfileTrackerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileTrackerStatusRepository extends JpaRepository<ProfileTrackerStatus, Long> {
    Optional<ProfileTrackerStatus> findByNameIgnoreCase(String name); // helpful for defaulting "Attached"
}
