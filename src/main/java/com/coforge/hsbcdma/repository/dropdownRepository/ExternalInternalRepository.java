package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.ExternalInternal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExternalInternalRepository extends JpaRepository<ExternalInternal,Long> {
//    import
    Optional<ExternalInternal> findByExternalInternalIgnoreCase(String externalInternal);
}
