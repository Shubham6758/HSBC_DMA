package com.coforge.hsbcdma.repository.UserManagementRepositories;

import com.coforge.hsbcdma.entity.UserManagementEntities.ChildModuleAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildModuleActionRepository extends JpaRepository<ChildModuleAction, Long> {

    List<ChildModuleAction> findByChildModuleId(Long childModuleId);

    List<ChildModuleAction> findByActionId(Long actionId);

    boolean existsByChildModuleIdAndActionId(Long moduleId, Long actionId);

}
