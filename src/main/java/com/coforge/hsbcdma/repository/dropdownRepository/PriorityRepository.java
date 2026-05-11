package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.Priority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PriorityRepository extends JpaRepository<Priority,Long> {
//    import
    Optional<Priority> findByPriorityIgnoreCase(String priority);
}
