package com.coforge.hsbcdma.repository.ReportRepositories;

import com.coforge.hsbcdma.dto.ReportDTO.GenericStatusProjection;
import com.coforge.hsbcdma.entity.AddDemand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<AddDemand, Long> {

//    @Query(value = """
//        SELECT
//            COALESCE(hm.hiring_manager, 'NA') AS hiring_manager,
//            COALESCE(sc.skill_cluster, 'NA') AS skill,
//            COALESCE(h.hbu,'NA') AS hbu,
//            COALESCE(l.lob,'NA') AS lob,
//
//            COUNT(DISTINCT CASE
//                WHEN s.status = 'Open'
//                THEN d.id
//            END) AS open_positions,
//
//            COUNT(DISTINCT CASE
//                WHEN s.status = 'Open'
//                THEN pt.id
//            END) AS profiles_shared,
//
//            COUNT(DISTINCT CASE
//                WHEN s.status = 'Open'
//                        AND pt.profile_shared_date IS NOT NULL
//                        AND pt.interview_date IS NULL
//                        THEN pt.id
//            END) AS awaiting_slots,
//
//            COUNT(DISTINCT CASE
//                WHEN s.status = 'Open'
//                AND pt.interview_date IS NOT NULL
//                AND o.id IS NULL
//                THEN pt.id
//            END) AS awaiting_feedback,
//
//            COUNT(DISTINCT CASE
//                WHEN s.status = 'Open'
//                AND EXISTS (
//                    SELECT 1
//                    FROM onboarding o2
//                    JOIN onboarding_status obs2
//                        ON o2.onboarding_status_id = obs2.id
//                    WHERE o2.profile_tracker_id = pt.id
//                      AND obs2.name = 'Onboarded'
//                )
//                THEN pt.id
//            END) AS selected,
//
//            COUNT(DISTINCT CASE
//                WHEN s.status = 'Open'
//                AND pts.name = 'Client Rejected'
//                THEN pt.id
//            END) AS rejected
//
//        FROM add_demands d
//
//        LEFT JOIN status s ON d.status_id = s.id
//        LEFT JOIN hiring_manager hm ON d.hiring_manager_id = hm.id
//        LEFT JOIN skill_cluster sc ON d.skill_cluster_id = sc.id
//        LEFT JOIN hbu h ON d.hbu_id = h.id
//        LEFT JOIN lob l ON d.lob_id = l.id
//        LEFT JOIN profile_tracker pt ON pt.demand_id = d.id
//        LEFT JOIN onboarding o ON o.profile_tracker_id = pt.id
//        LEFT JOIN profile_tracker_status pts ON pt.profile_tracker_status_id = pts.id
//
//            WHERE (:hmIds IS NULL OR d.hiring_manager_id IN (:hmIds))
//
//             AND (:skillIds IS NULL OR d.skill_cluster_id IN (:skillIds))
//
//             AND (:hbuIds IS NULL OR d.hbu_id IN (:hbuIds))
//
//             AND (:lobIds IS NULL OR d.lob_id IN (:lobIds))
//
//        GROUP BY hm.hiring_manager, sc.skill_cluster,h.hbu,l.lob
//        ORDER BY hm.hiring_manager, sc.skill_cluster
//    """, nativeQuery = true)
//    List<Object[]> getHmSkillReport(
//            @Param("hmIds") List<Long> hmIds,
//            @Param("skillIds") List<Long> skillIds,
//            @Param("hbuIds") List<Long> hbuIds,
//            @Param("lobIds") List<Long> lobIds
//    );






    @Query(value = """
        SELECT 
            CASE 
                WHEN :groupBy = 'lob' THEN COALESCE(l.lob, 'NA')
                WHEN :groupBy = 'hbu' THEN COALESCE(h.hbu, 'NA')
            END AS groupKey,

            COALESCE(s.status, 'NA') AS status,
            COUNT(DISTINCT d.id) AS count

        FROM add_demands d
        LEFT JOIN lob l ON d.lob_id = l.id
        LEFT JOIN hbu h ON d.hbu_id = h.id
        LEFT JOIN status s ON d.status_id = s.id

        WHERE 
            (:id IS NULL OR 
             (:groupBy = 'lob' AND d.lob_id = :id) OR
             (:groupBy = 'hbu' AND d.hbu_id = :id)
            )

        GROUP BY groupKey, s.status
    """, nativeQuery = true)
    List<GenericStatusProjection> getStatus(
            @Param("groupBy") String groupBy,
            @Param("id") Long id);


}
