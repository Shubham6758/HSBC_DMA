package com.coforge.hsbcdma.resource.Repository_Resource_sheet;


import com.coforge.hsbcdma.resource.Entity_Resource_Sheet.ResourceData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResourceDataRepository extends JpaRepository<ResourceData, Long> {

    List<ResourceData> findByPsidAndIsActiveTrueOrderByCreatedAtDesc(String psid);

    Optional<ResourceData> findTopByPsidAndIsActiveTrueOrderByUpdatedAtDescCreatedAtDesc(String psid);

    List<ResourceData> findAllByOrderByIsActiveDescUpdatedAtDescCreatedAtDesc();
}