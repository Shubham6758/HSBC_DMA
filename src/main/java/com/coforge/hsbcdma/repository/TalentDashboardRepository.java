package com.coforge.hsbcdma.repository;

import com.coforge.hsbcdma.entity.AddNewDemand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * pratish.b
 */
public interface TalentDashboardRepository extends JpaRepository<AddNewDemand,Long> {

    @Query("""
            SELECT  s.status AS status, count(*) as count  FROM AddNewDemand s 
                WHERE s.status IN (:statuses) GROUP BY s.status
        """)
    List<StatusCountView> countByStatuses(@Param("statuses") List<String> statuses);

    Integer countByStatus(String status);
}
