package com.coforge.hsbcdma.repository;

import com.coforge.hsbcdma.entity.CountryCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryCodeRepository extends JpaRepository<CountryCode,Long> {
    Optional<CountryCode> findByCallingCode(String callingCode);
    Optional<CountryCode> findByName(String name);


    Optional<CountryCode> findByNameIgnoreCase(String name);

}
