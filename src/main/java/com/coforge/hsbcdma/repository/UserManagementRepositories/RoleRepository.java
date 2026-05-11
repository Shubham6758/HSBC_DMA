package com.coforge.hsbcdma.repository.UserManagementRepositories;

import com.coforge.hsbcdma.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Roles,Long> {
    Optional<Roles> findByRole(String name);
    boolean existsByRole(String name);
}
