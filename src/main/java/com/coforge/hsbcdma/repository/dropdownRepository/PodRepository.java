package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.Pod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PodRepository extends JpaRepository<Pod,Long> {
    //    import
    Optional<Pod> findByPodIgnoreCase(String pod);
}
