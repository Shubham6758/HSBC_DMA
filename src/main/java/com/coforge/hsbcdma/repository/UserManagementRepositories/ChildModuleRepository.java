package com.coforge.hsbcdma.repository.UserManagementRepositories;

import com.coforge.hsbcdma.entity.UserManagementEntities.ChildModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildModuleRepository extends JpaRepository<ChildModule, Long> {

    List<ChildModule> findByModuleId(Long moduleId);
}
