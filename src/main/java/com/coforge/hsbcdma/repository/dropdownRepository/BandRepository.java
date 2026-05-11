package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.Band;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BandRepository extends JpaRepository<Band,Long> {
//    import
    Optional<Band> findByBandIgnoreCase(String band);
}
