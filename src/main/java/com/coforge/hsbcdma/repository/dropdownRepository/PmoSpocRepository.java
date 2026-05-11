package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.PmoSpoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PmoSpocRepository extends JpaRepository<PmoSpoc,Long> {
    //    import
    Optional<PmoSpoc> findByPmoSpocIgnoreCase(String pmoSpoc);
}
