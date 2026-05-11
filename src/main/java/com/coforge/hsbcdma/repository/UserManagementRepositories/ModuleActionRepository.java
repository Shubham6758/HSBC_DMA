package com.coforge.hsbcdma.repository.UserManagementRepositories;

import com.coforge.hsbcdma.entity.UserManagementEntities.ModuleAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleActionRepository extends JpaRepository<ModuleAction, Long> {

    List<ModuleAction> findByModuleId(Long moduleId);

    List<ModuleAction> findByActionId(Long actionId);

    boolean existsByModuleIdAndActionId(Long moduleId, Long actionId);
}
