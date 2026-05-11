package com.coforge.hsbcdma.repository.dropdownRepository;

import com.coforge.hsbcdma.entity.dropdownEntities.SubLob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubLobRepository extends JpaRepository<SubLob,Long> {
    List<SubLob> findByLob_Id(Long lobId);
//    import
    Optional<SubLob> findBySubLobIgnoreCase(String subLob);
}