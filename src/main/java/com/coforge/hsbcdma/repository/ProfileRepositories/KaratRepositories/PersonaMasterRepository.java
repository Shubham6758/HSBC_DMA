package com.coforge.hsbcdma.repository.ProfileRepositories.KaratRepositories;


import com.coforge.hsbcdma.entity.dropdownEntities.Profile.Karat.PersonaMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaMasterRepository extends JpaRepository<PersonaMaster, Long> {

    Optional<PersonaMaster> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
