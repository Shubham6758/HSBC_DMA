package com.coforge.hsbcdma.revenuemargin.repository;

import com.coforge.hsbcdma.revenuemargin.entity.M4RateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface M4RateRepository
        extends JpaRepository<
        M4RateEntity,
        Long> {
    M4RateEntity findByRoleTitle(
            String roleTitle
    );
}