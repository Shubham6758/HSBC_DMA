package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.Hbu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HbuRepository extends JpaRepository<Hbu,Long> {
//    import
    Optional<Hbu> findByHbuIgnoreCase(String hbu);
}
