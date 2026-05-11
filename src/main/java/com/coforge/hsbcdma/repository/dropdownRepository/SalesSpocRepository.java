package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.SalesSpoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalesSpocRepository extends JpaRepository<SalesSpoc,Long> {
    //    import
    Optional<SalesSpoc> findBySalesSpocIgnoreCase(String salesSpoc);
}
