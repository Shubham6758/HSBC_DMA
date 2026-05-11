package com.coforge.hsbcdma.repository.DashboardRepositories;

import com.coforge.hsbcdma.entity.AddDemand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DashboardRepository extends JpaRepository<AddDemand, Long> {
    // ============================================================
    // LOB Wise Summary
    // ============================================================

    @Query(value = """
    SELECT
        l.lob AS lob,

        COUNT(DISTINCT CASE
            WHEN s.status = 'Open'
            THEN ad.id
        END) AS openDemand,

        COUNT(DISTINCT CASE
            WHEN s.status IN (
                'Demand Fulfilled',
                'Onboarded',
                'Selected',
                'Soft Select'
            )
            THEN ad.id
        END) AS fulfilledDemand,

        COUNT(DISTINCT CASE
            WHEN s.status IN (
                'Profile Shared',
                'Profile Shared with IBU',
                'Profile Shared - No Response',
                'Profile Shared with IBU - No Response'
            )
            THEN ad.id
        END) AS profileSharedCount

    FROM add_demands ad
    LEFT JOIN status s ON ad.status_id = s.id
    LEFT JOIN lob l ON ad.lob_id = l.id

    WHERE ad.lob_id IS NOT NULL   -- ✅ THIS FIX
      AND DATE(ad.created_at) BETWEEN :fromDate AND :toDate

    GROUP BY l.lob
""", nativeQuery = true)
    List<Object[]> getLobSummaryRaw(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );


    // ============================================================
    // HBU Wise Summary
    // ============================================================

    @Query(value = """
    SELECT
        h.hbu AS hbu,

        COUNT(DISTINCT CASE
            WHEN s.status = 'Open'
            THEN ad.id
        END) AS openDemand,

        COUNT(DISTINCT CASE
            WHEN s.status IN (
                'Demand Fulfilled',
                'Onboarded',
                'Selected',
                'Soft Select'
            )
            THEN ad.id
        END) AS fulfilledDemand,

        COUNT(DISTINCT CASE
            WHEN s.status IN (
                'Profile Shared',
                'Profile Shared with IBU',
                'Profile Shared - No Response',
                'Profile Shared with IBU - No Response'
            )
            THEN ad.id
        END) AS profileSharedCount

    FROM add_demands ad
    LEFT JOIN status s ON ad.status_id = s.id

    -- ✅ IMPORTANT: remove null HBU
    INNER JOIN hbu h ON ad.hbu_id = h.id  

    WHERE DATE(ad.created_at) BETWEEN :fromDate AND :toDate

    GROUP BY h.hbu
""", nativeQuery = true)
    List<Object[]> getHbuSummaryRaw(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );


    // ============================================================
    // Priority Wise Summary
    // ============================================================
    @Query(value = """
    SELECT
        COUNT(CASE WHEN p.priority = 'P0' THEN 1 END) AS p0Count,
        COUNT(CASE WHEN p.priority = 'P1' THEN 1 END) AS p1Count,
        COUNT(CASE WHEN p.priority = 'P2' THEN 1 END) AS p2Count
    FROM add_demands ad
    LEFT JOIN priority p ON ad.priority_id = p.id
    WHERE DATE(ad.created_at) BETWEEN :fromDate AND :toDate
    """, nativeQuery = true)
    List<Object[]> getPriorityCounts(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );


    @Query(value = """
SELECT
    COUNT(ad.id) AS totalDemands,

    -- ✅ OPEN
    COUNT(CASE
        WHEN s.status IN (
            'Open',
            'Profile Shared',
            'Profile Sent to Delivery',
            'Profile Shared with IBU',
            'Profile Shared - No Response',
            'Profile Shared with IBU - No Response'
        )
        THEN 1 END) AS openCount,

    -- ✅ CLOSED
    COUNT(CASE
        WHEN s.status IN (
            'Demand Fulfilled',
            'Candidate Shortlisted',
            'Selected',
            'Onboarded',
            'Soft Select'
        )
        THEN 1 END) AS closedCount,

    -- ✅ REJECTED + NULL INCLUDED
    COUNT(CASE
        WHEN s.status IN (
            'Demand Abandoned',
            'Abandoned by Client',
            'Lost to competition',
            'On Hold',
            'Awaiting SOW',
            'Delayed demand - profile not available'
        )
        OR s.status IS NULL   -- ✅ THIS IS THE FIX
        THEN 1 END) AS rejectedCount

FROM add_demands ad
LEFT JOIN status s ON ad.status_id = s.id
WHERE DATE(ad.created_at) BETWEEN :fromDate AND :toDate
""", nativeQuery = true)
    List<Object[]> getDashboardCounts(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );
}
