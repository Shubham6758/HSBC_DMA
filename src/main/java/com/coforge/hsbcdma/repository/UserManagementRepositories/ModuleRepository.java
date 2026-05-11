package com.coforge.hsbcdma.repository.UserManagementRepositories;

import com.coforge.hsbcdma.entity.UserManagementEntities.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module, Long> {

    Optional<Module> findByModule(String module);
}
