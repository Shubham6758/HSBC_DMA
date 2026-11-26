package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.BusinessFunction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessFunctionRepository
        extends JpaRepository<BusinessFunction, Long> {

    Optional<BusinessFunction> findByBusinessFunctionIgnoreCase(
            String businessFunction
    );
}
