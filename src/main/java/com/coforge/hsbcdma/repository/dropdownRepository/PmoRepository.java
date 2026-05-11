package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.Pmo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PmoRepository extends JpaRepository<Pmo,Long> {
    //    import
    Optional<Pmo> findByPmoIgnoreCase(String pmo);
}
