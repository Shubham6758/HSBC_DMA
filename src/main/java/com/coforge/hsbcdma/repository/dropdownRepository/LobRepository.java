package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.Lob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LobRepository extends JpaRepository<Lob,Long> {

    Optional<Lob> findByLob(String lob);

    Optional<Lob> findByNameIgnoreCase(String trim);
}
