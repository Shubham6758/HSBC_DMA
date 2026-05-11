package com.coforge.hsbcdma.repository;

import com.coforge.hsbcdma.entity.Roles;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long>{
   Optional<Roles>findByRole(String name);
   boolean existsByRoleIgnoreCase(String role);
   Optional<Roles> findByRoleIgnoreCase(String role);
   List<Roles> findByActiveTrue();
   @EntityGraph(attributePaths = {"createdBy"})
   List<Roles> findAll();


}

