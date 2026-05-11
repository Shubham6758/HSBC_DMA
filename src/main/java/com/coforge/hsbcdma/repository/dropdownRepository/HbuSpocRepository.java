package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.HbuSpoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HbuSpocRepository extends JpaRepository<HbuSpoc,Long> {
    //    import
    Optional<HbuSpoc> findByHbuSpocIgnoreCase(String hbuSpoc);
}
