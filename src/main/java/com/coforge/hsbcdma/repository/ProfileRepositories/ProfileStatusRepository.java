package com.coforge.hsbcdma.repository.ProfileRepositories;


import com.coforge.hsbcdma.entity.dropdownEntities.Profile.ProfileStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileStatusRepository extends JpaRepository<ProfileStatus, Long> {
    Optional<ProfileStatus> findByName(String name);

    Optional<ProfileStatus> findByNameIgnoreCase(String name);
}