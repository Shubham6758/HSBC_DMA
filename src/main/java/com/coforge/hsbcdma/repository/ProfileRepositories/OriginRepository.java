package com.coforge.hsbcdma.repository.ProfileRepositories;

import com.coforge.hsbcdma.entity.dropdownEntities.Profile.Origin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OriginRepository extends JpaRepository<Origin,Long> {

    Optional<Origin> findByNameIgnoreCase(String s);
}
