package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.HiringManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HiringManagerRepository extends JpaRepository<HiringManager,Long> {
//    import
    Optional<HiringManager> findByHiringManagerIgnoreCase(String hiringManger);
}
