package com.coforge.hsbcdma.rbac;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiPermissionMappingRepository
        extends JpaRepository<ApiPermissionMapping, Long> {

    List<ApiPermissionMapping> findAllByActiveTrue();

    boolean existsByHttpMethodIgnoreCaseAndPathPattern(
            String httpMethod,
            String pathPattern
    );
}