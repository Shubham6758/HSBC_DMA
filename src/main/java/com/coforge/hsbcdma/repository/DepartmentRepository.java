package com.coforge.hsbcdma.repository;


import com.coforge.hsbcdma.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Created by Chetan

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Object> findByNameIgnoreCase(String name);
}
