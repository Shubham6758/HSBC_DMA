package com.coforge.hsbcdma.revenuemargin.repository;

import com.coforge.hsbcdma.revenuemargin.entity.OtherDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtherDataRepository
        extends JpaRepository<
        OtherDataEntity,
        Long> {

    OtherDataEntity findByPsid(
            String psid
    );

}