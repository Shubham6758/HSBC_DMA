package com.coforge.hsbcdma.repository;

import com.coforge.hsbcdma.entity.TaManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaManagementRepository extends JpaRepository<TaManagement,Long> {

    List<TaManagement> findByDemandId(String demandId);

    boolean existsByDemandId(String demandId);

    void deleteByDemandId(String demandId);
}
