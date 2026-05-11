package com.coforge.hsbcdma.repository;


import com.coforge.hsbcdma.entity.SubDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Created by Chetan

public interface SubDepartmentRepository extends JpaRepository<SubDepartment, Long> {
    List<SubDepartment> findByDepartmentId(Long departmentId);
}
