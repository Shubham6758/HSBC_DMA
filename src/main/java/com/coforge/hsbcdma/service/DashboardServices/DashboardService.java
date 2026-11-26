//package com.coforge.hsbcdma.service.DashboardServices;
//
//import com.coforge.hsbcdma.dto.DashboardDTO.DashboardCountDto;
//import com.coforge.hsbcdma.dto.DashboardDTO.HbuSummaryDto;
//import com.coforge.hsbcdma.dto.DashboardDTO.LobSummaryDto;
//import com.coforge.hsbcdma.dto.DashboardDTO.PrioritySummaryDto;
//import com.coforge.hsbcdma.repository.DashboardRepositories.DashboardRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class DashboardService {
//
//    private final DashboardRepository dashboardRepository;
//
//    public List<LobSummaryDto> getLobSummary(LocalDate fromDate, LocalDate toDate) {
//
//        List<Object[]> lobRaw = dashboardRepository.getLobSummaryRaw(fromDate, toDate);
//
//        return lobRaw.stream()
//                .map(row -> new LobSummaryDto(
//                        (String) row[0],
//                        ((Number) row[1]).longValue(),
//                        ((Number) row[2]).longValue(),
//                        ((Number) row[3]).longValue()
//                ))
//                .toList();
//    }
//    public List<HbuSummaryDto> getHbuSummary(LocalDate fromDate, LocalDate toDate) {
//
//        List<Object[]> hbuRaw = dashboardRepository.getHbuSummaryRaw(fromDate, toDate);
//
//        return hbuRaw.stream()
//                .map(row -> new HbuSummaryDto(
//                        (String) row[0],
//                        ((Number) row[1]).longValue(),
//                        ((Number) row[2]).longValue(),
//                        ((Number) row[3]).longValue()
//                ))
//                .toList();
//    }
//
//    public PrioritySummaryDto getPrioritySummary(
//            LocalDate fromDate,
//            LocalDate toDate) {
//
//        List<Object[]> results =
//                dashboardRepository.getPriorityCounts(fromDate, toDate);
//
//        Object[] row = results.get(0);
//
//        return new PrioritySummaryDto(
//                row[0] != null ? ((Number) row[0]).longValue() : 0L,
//                row[1] != null ? ((Number) row[1]).longValue() : 0L,
//                row[2] != null ? ((Number) row[2]).longValue() : 0L
//        );
//    }
//
//    //Dashboard Count
//
//    public DashboardCountDto getDashboardCounts(LocalDate fromDate,
//                                                LocalDate toDate) {
//
//        List<Object[]> results =
//                dashboardRepository.getDashboardCounts(fromDate, toDate);
//
//        if (results.isEmpty()) {
//            return new DashboardCountDto(0L, 0L, 0L, 0L);
//        }
//
//        Object[] row = results.get(0);
//
//        return new DashboardCountDto(
//                row[0] != null ? ((Number) row[0]).longValue() : 0L,
//                row[1] != null ? ((Number) row[1]).longValue() : 0L,
//                row[2] != null ? ((Number) row[2]).longValue() : 0L,
//                row[3] != null ? ((Number) row[3]).longValue() : 0L
//        );
//    }
//}
//
package com.coforge.hsbcdma.service.DashboardServices;

import com.coforge.hsbcdma.dto.DashboardDTO.*;
import com.coforge.hsbcdma.dto.DashboardDTO.HbuSummaryDto;
import com.coforge.hsbcdma.dto.DashboardDTO.LobSummaryDto;
import com.coforge.hsbcdma.dto.DashboardDTO.PrioritySummaryDto;
import com.coforge.hsbcdma.repository.DashboardRepositories.DashboardRepository;
import com.coforge.hsbcdma.repository.DashboardRepositories.DashboardRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepositoryCustom dashboardRepositoryCustom;

    private final DashboardRepository dashboardRepository;

    public List<LobSummaryDto> getLobSummary(
            LocalDate fromDate,
            LocalDate toDate,
            List<Long> hbuIds,
            List<Long> lobIds,
            List<Long> hmIds,
            List<Long> skillIds,
            List<Long> priorityIds) {

//        List<Object[]> lobRaw = dashboardRepository.getLobSummaryRaw(fromDate, toDate);
        Map<String, Object> params = new HashMap<>();

        StringBuilder sql = new StringBuilder("""
    SELECT
        l.lob,

        COUNT(DISTINCT CASE
            WHEN s.status = 'Open'
            THEN ad.id
        END),

        COUNT(DISTINCT CASE
            WHEN s.status IN (
                'Demand Fulfilled',
                'Onboarded',
                'Selected',
                'Soft Select'
            )
            THEN ad.id
        END),

        COUNT(DISTINCT CASE
            WHEN s.status IN (
                'Profile Shared',
                'Profile Shared with IBU',
                'Profile Shared - No Response',
                'Profile Shared with IBU - No Response'
            )
            THEN ad.id
        END)

    FROM add_demands ad
    LEFT JOIN status s ON ad.status_id = s.id
    LEFT JOIN lob l ON ad.lob_id = l.id

    WHERE ad.lob_id IS NOT NULL
                    AND DATE(ad.created_at)
                    BETWEEN :fromDate AND :toDate
""");

        params.put("fromDate", fromDate);
        params.put("toDate", toDate);
        if (hbuIds != null && !hbuIds.isEmpty()) {
            sql.append(" AND ad.hbu_id IN (:hbuIds)");
            params.put("hbuIds", hbuIds);
        }

        if (lobIds != null && !lobIds.isEmpty()) {
            sql.append(" AND ad.lob_id IN (:lobIds)");
            params.put("lobIds", lobIds);
        }

        if (hmIds != null && !hmIds.isEmpty()) {
            sql.append(" AND ad.hiring_manager_id IN (:hmIds)");
            params.put("hmIds", hmIds);
        }

        if (skillIds != null && !skillIds.isEmpty()) {
            sql.append(" AND ad.skill_cluster_id IN (:skillIds)");
            params.put("skillIds", skillIds);
        }

        if (priorityIds != null && !priorityIds.isEmpty()) {
            sql.append(" AND ad.priority_id IN (:priorityIds)");
            params.put("priorityIds", priorityIds);
        }
        sql.append(" GROUP BY l.lob");

        List<Object[]> lobRaw =
                dashboardRepositoryCustom.executeDynamicQuery(
                        sql.toString(),
                        params
                );

        return lobRaw.stream()
                .map(row -> new LobSummaryDto(
                        (String) row[0],
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).longValue()
                ))
                .toList();
    }
    public List<HbuSummaryDto> getHbuSummary(
            LocalDate fromDate,
            LocalDate toDate,
            List<Long> hbuIds,
            List<Long> lobIds,
            List<Long> hmIds,
            List<Long> skillIds,
            List<Long> priorityIds) {

        Map<String, Object> params = new HashMap<>();

        StringBuilder sql = new StringBuilder("""
        SELECT
            h.hbu,

            COUNT(DISTINCT CASE
                WHEN s.status = 'Open'
                THEN ad.id
            END),

            COUNT(DISTINCT CASE
                WHEN s.status IN (
                    'Demand Fulfilled',
                    'Onboarded',
                    'Selected',
                    'Soft Select'
                )
                THEN ad.id
            END),

            COUNT(DISTINCT CASE
                WHEN s.status IN (
                    'Profile Shared',
                    'Profile Shared with IBU',
                    'Profile Shared - No Response',
                    'Profile Shared with IBU - No Response'
                )
                THEN ad.id
            END)

        FROM add_demands ad
        LEFT JOIN status s ON ad.status_id = s.id
        INNER JOIN hbu h ON ad.hbu_id = h.id

        WHERE DATE(ad.created_at)
        BETWEEN :fromDate AND :toDate
    """);

        params.put("fromDate", fromDate);
        params.put("toDate", toDate);

        if (hbuIds != null && !hbuIds.isEmpty()) {
            sql.append(" AND ad.hbu_id IN (:hbuIds)");
            params.put("hbuIds", hbuIds);
        }

        if (lobIds != null && !lobIds.isEmpty()) {
            sql.append(" AND ad.lob_id IN (:lobIds)");
            params.put("lobIds", lobIds);
        }

        if (hmIds != null && !hmIds.isEmpty()) {
            sql.append(" AND ad.hiring_manager_id IN (:hmIds)");
            params.put("hmIds", hmIds);
        }

        if (skillIds != null && !skillIds.isEmpty()) {
            sql.append(" AND ad.skill_cluster_id IN (:skillIds)");
            params.put("skillIds", skillIds);
        }

        if (priorityIds != null && !priorityIds.isEmpty()) {
            sql.append(" AND ad.priority_id IN (:priorityIds)");
            params.put("priorityIds", priorityIds);
        }

        sql.append(" GROUP BY h.hbu");

        List<Object[]> hbuRaw =
                dashboardRepositoryCustom.executeDynamicQuery(
                        sql.toString(),
                        params
                );

        return hbuRaw.stream()
                .map(row -> new HbuSummaryDto(
                        (String) row[0],
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).longValue()
                ))
                .toList();
    }



    public PrioritySummaryDto getPrioritySummary(
            LocalDate fromDate,
            LocalDate toDate,
            List<Long> hbuIds,
            List<Long> lobIds,
            List<Long> hmIds,
            List<Long> skillIds,
            List<Long> priorityIds) {

        Map<String, Object> params = new HashMap<>();

        StringBuilder sql = new StringBuilder("""
        SELECT

            COUNT(
                CASE
                WHEN p.priority = 'P0'
                THEN 1
                END
            ),

            COUNT(
                CASE
                WHEN p.priority = 'P1'
                THEN 1
                END
            ),

            COUNT(
                CASE
                WHEN p.priority = 'P2'
                THEN 1
                END
            )

        FROM add_demands ad
        LEFT JOIN priority p
            ON ad.priority_id = p.id

        WHERE DATE(ad.created_at)
        BETWEEN :fromDate AND :toDate
    """);

        params.put("fromDate", fromDate);
        params.put("toDate", toDate);

        if (hbuIds != null && !hbuIds.isEmpty()) {
            sql.append(" AND ad.hbu_id IN (:hbuIds)");
            params.put("hbuIds", hbuIds);
        }

        if (lobIds != null && !lobIds.isEmpty()) {
            sql.append(" AND ad.lob_id IN (:lobIds)");
            params.put("lobIds", lobIds);
        }

        if (hmIds != null && !hmIds.isEmpty()) {
            sql.append(" AND ad.hiring_manager_id IN (:hmIds)");
            params.put("hmIds", hmIds);
        }

        if (skillIds != null && !skillIds.isEmpty()) {
            sql.append(" AND ad.skill_cluster_id IN (:skillIds)");
            params.put("skillIds", skillIds);
        }

        if (priorityIds != null && !priorityIds.isEmpty()) {
            sql.append(" AND ad.priority_id IN (:priorityIds)");
            params.put("priorityIds", priorityIds);
        }

        List<Object[]> results =
                dashboardRepositoryCustom.executeDynamicQuery(
                        sql.toString(),
                        params
                );

        Object[] row = results.get(0);

        return new PrioritySummaryDto(
                row[0] != null ? ((Number) row[0]).longValue() : 0L,
                row[1] != null ? ((Number) row[1]).longValue() : 0L,
                row[2] != null ? ((Number) row[2]).longValue() : 0L
        );
    }

    //Dashboard Count

//    public DashboardCountDto getDashboardCounts(
//            LocalDate fromDate,
//            LocalDate toDate,
//            List<Long> hbuIds,
//            List<Long> lobIds,
//            List<Long> hmIds,
//            List<Long> skillIds,
//            List<Long> priorityIds) {
//
//        Map<String, Object> params = new HashMap<>();
//
//        StringBuilder sql = new StringBuilder("""
//    SELECT
//        COUNT(ad.id),
//
//        COUNT(CASE
//            WHEN s.status IN (
//                'Open',
//                'Open Positions',
//                'Profile Shared',
//                'Profile Sent to Delivery',
//                'Profile Available - Ctool Awaited',
//                'Profile Shared with IBU',
//                'Profile Shared - No Response',
//                'Profile Shared with IBU - No Response',
//                'Delayed demand - profile not available',
//                'Awaiting SOW',
//                'Awaiting CSG Clarification'
//            )
//            THEN 1 END),
//
//        COUNT(CASE
//            WHEN s.status IN (
//                'Demand Fulfilled',
//                'Candidate Shortlisted',
//                'Selected',
//                'Onboarded',
//                'Soft Select'
//            )
//            THEN 1 END),
//
//        COUNT(CASE
//            WHEN s.status IN (
//                'Demand Abandoned',
//                'Abandoned by Client',
//                'Lost to competition',
//                'On Hold'
//            )
//            OR s.status IS NULL
//            THEN 1 END)
//
//    FROM add_demands ad
//    LEFT JOIN status s
//        ON ad.status_id = s.id
//
//    WHERE DATE(ad.created_at)
//    BETWEEN :fromDate AND :toDate
//""");
//
//        params.put("fromDate", fromDate);
//        params.put("toDate", toDate);
//
//        if (hbuIds != null && !hbuIds.isEmpty()) {
//            sql.append(" AND ad.hbu_id IN (:hbuIds)");
//            params.put("hbuIds", hbuIds);
//        }
//
//        if (lobIds != null && !lobIds.isEmpty()) {
//            sql.append(" AND ad.lob_id IN (:lobIds)");
//            params.put("lobIds", lobIds);
//        }
//
//        if (hmIds != null && !hmIds.isEmpty()) {
//            sql.append(" AND ad.hiring_manager_id IN (:hmIds)");
//            params.put("hmIds", hmIds);
//        }
//
//        if (skillIds != null && !skillIds.isEmpty()) {
//            sql.append(" AND ad.skill_cluster_id IN (:skillIds)");
//            params.put("skillIds", skillIds);
//        }
//
//        if (priorityIds != null && !priorityIds.isEmpty()) {
//            sql.append(" AND ad.priority_id IN (:priorityIds)");
//            params.put("priorityIds", priorityIds);
//        }
//        List<Object[]> results =
//                dashboardRepositoryCustom.executeDynamicQuery(
//                        sql.toString(),
//                        params
//                );
//
//        if (results.isEmpty()) {
//            return new DashboardCountDto(0L, 0L, 0L, 0L);
//        }
//
//        Object[] row = results.get(0);
//
//        return new DashboardCountDto(
//                row[0] != null ? ((Number) row[0]).longValue() : 0L,
//                row[1] != null ? ((Number) row[1]).longValue() : 0L,
//                row[2] != null ? ((Number) row[2]).longValue() : 0L,
//                row[3] != null ? ((Number) row[3]).longValue() : 0L
//        );
//    }
public DashboardCountDto getDashboardCounts(
        LocalDate fromDate,
        LocalDate toDate,
        List<Long> hbuIds,
        List<Long> lobIds,
        List<Long> hmIds,
        List<Long> skillIds,
        List<Long> priorityIds) {

    Map<String, Object> demandParams = new HashMap<>();

    StringBuilder demandSql = new StringBuilder("""
        SELECT
            COUNT(ad.id),

            COUNT(CASE
                WHEN s.status IN (
                    'Open',
                    'Open Positions',
                    'Profile Shared',
                    'Profile Sent to Delivery',
                    'Profile Available - Ctool Awaited',
                    'Profile Shared with IBU',
                    'Profile Shared - No Response',
                    'Profile Shared with IBU - No Response',
                    'Delayed demand - profile not available',
                    'Awaiting SOW',
                    'Awaiting CSG Clarification'
                )
                THEN 1
            END),

            COUNT(CASE
                WHEN s.status IN (
                    'Demand Fulfilled',
                    'Candidate Shortlisted',
                    'Selected',
                    'Soft Select'
                )
                THEN 1
            END),

            COUNT(CASE
                WHEN s.status IN (
                    'Onboarded',
                    'Demand Abandoned',
                    'Abandoned by Client',
                    'Lost to competition',
                    'On Hold'
                )
                OR s.status IS NULL
                THEN 1
            END)

        FROM add_demands ad
        LEFT JOIN status s
            ON ad.status_id = s.id

        WHERE DATE(ad.created_at)
            BETWEEN :fromDate AND :toDate
        """);

    demandParams.put("fromDate", fromDate);
    demandParams.put("toDate", toDate);

    if (hbuIds != null && !hbuIds.isEmpty()) {
        demandSql.append(" AND ad.hbu_id IN (:hbuIds)");
        demandParams.put("hbuIds", hbuIds);
    }

    if (lobIds != null && !lobIds.isEmpty()) {
        demandSql.append(" AND ad.lob_id IN (:lobIds)");
        demandParams.put("lobIds", lobIds);
    }

    if (hmIds != null && !hmIds.isEmpty()) {
        demandSql.append(" AND ad.hiring_manager_id IN (:hmIds)");
        demandParams.put("hmIds", hmIds);
    }

    if (skillIds != null && !skillIds.isEmpty()) {
        demandSql.append(" AND ad.skill_cluster_id IN (:skillIds)");
        demandParams.put("skillIds", skillIds);
    }

    if (priorityIds != null && !priorityIds.isEmpty()) {
        demandSql.append(" AND ad.priority_id IN (:priorityIds)");
        demandParams.put("priorityIds", priorityIds);
    }

    List<Object[]> demandResults =
            dashboardRepositoryCustom.executeDynamicQuery(
                    demandSql.toString(),
                    demandParams
            );

    Object[] demandRow = demandResults != null && !demandResults.isEmpty()
            ? demandResults.get(0)
            : null;

    /*
     * This query is independent because the profile table has
     * no relationship with add_demands.
     */
    Map<String, Object> profileParams = new HashMap<>();

    StringBuilder profileSql = new StringBuilder("""
    SELECT
        COUNT(p.id) AS totalProfileCount,

        COUNT(CASE
            WHEN LOWER(TRIM(pt.external_internal)) = 'internal'
            THEN 1
        END) AS internalProfileCount,

        COUNT(CASE
            WHEN LOWER(TRIM(pt.external_internal)) = 'external'
            THEN 1
        END) AS externalProfileCount,

        COUNT(CASE
            WHEN LOWER(TRIM(ps.name))
                 = 'selection/rejection pending'
            THEN 1
        END) AS selectionRejectionPendingCount
    
        FROM profiles p

        LEFT JOIN external_internal pt
            ON p.external_internal_id = pt.id
    
        LEFT JOIN profile_status ps
            ON p.profile_status_id = ps.id

        WHERE DATE(p.created_at)
            BETWEEN :fromDate AND :toDate
        """);

    profileParams.put("fromDate", fromDate);
    profileParams.put("toDate", toDate);

    if (hbuIds != null && !hbuIds.isEmpty()) {
        profileSql.append(" AND p.hbu_id IN (:hbuIds) ");

        profileParams.put(
                "hbuIds",
                hbuIds
        );
    }

    if (lobIds != null && !lobIds.isEmpty()) {
        profileSql.append("""
        AND LOWER(TRIM(p.lob_shared)) IN (
            SELECT LOWER(TRIM(l.lob))
            FROM lob l
            WHERE l.id IN (:lobIds)
        )
        """);

        profileParams.put("lobIds", lobIds);
    }

    List<Object[]> profileResults =
            dashboardRepositoryCustom.executeDynamicQuery(
                    profileSql.toString(),
                    profileParams
            );

    Object[] profileRow = profileResults != null && !profileResults.isEmpty()
            ? profileResults.get(0)
            : null;

    return new DashboardCountDto(
            valueAt(demandRow, 0),  // Total demands
            valueAt(demandRow, 1),  // Open demands
            valueAt(demandRow, 2),  // Fulfilled demands
            valueAt(demandRow, 3),  // Abandoned demands

            valueAt(profileRow, 0), // Total profiles
            valueAt(profileRow, 1), // Internal profiles
            valueAt(profileRow, 2), // External profiles
            valueAt(profileRow, 3)  // Selection/Rejection Pending
    );
}

    private long valueAt(Object[] row, int index) {

        if (row == null || index >= row.length || row[index] == null) {
            return 0L;
        }

        return ((Number) row[index]).longValue();
    }


    public DemandFilterCountDTO getDemandCount(
            List<Long> hbuIds,
            List<Long> lobIds,
            List<Long> hmIds,
            List<Long> skillIds,
            List<Long>priorityIds
    ) {

        Map<String, Object> params = new HashMap<>();

        StringBuilder sql = new StringBuilder("""
        SELECT
            COUNT(ad.id),

            COUNT(CASE
                WHEN s.status IN (
                    'Open',
                    'Open Positions',
                    'Profile Shared',
                    'Profile Sent to Delivery',
                    'Profile Available - Ctool Awaited',
                    'Profile Shared with IBU',
                    'Profile Shared - No Response',
                    'Profile Shared with IBU - No Response',
                    'Delayed demand - profile not available',
                    'Awaiting SOW',
                    'Awaiting CSG Clarification'
                )
                THEN 1 END),

            COUNT(CASE
                WHEN s.status IN (
                    'Demand Fulfilled',
                    'Candidate Shortlisted',
                    'Selected',
                    'Soft Select'
                )
                THEN 1 END),

            COUNT(CASE
                WHEN s.status IN (
                    'Onboarded',
                    'Demand Abandoned',
                    'Abandoned by Client',
                    'Lost to competition',
                    'On Hold'
                )
                OR s.status IS NULL
                THEN 1 END)

        FROM add_demands ad
        LEFT JOIN status s
            ON ad.status_id = s.id

        WHERE 1 = 1
    """);

        if (hbuIds != null && !hbuIds.isEmpty()) {
            sql.append(" AND ad.hbu_id IN (:hbuIds)");
            params.put("hbuIds", hbuIds);
        }

        if (lobIds != null && !lobIds.isEmpty()) {
            sql.append(" AND ad.lob_id IN (:lobIds)");
            params.put("lobIds", lobIds);
        }

        if (hmIds != null && !hmIds.isEmpty()) {
            sql.append(" AND ad.hiring_manager_id IN (:hmIds)");
            params.put("hmIds", hmIds);
        }

        if (skillIds != null && !skillIds.isEmpty()) {
            sql.append(" AND ad.skill_cluster_id IN (:skillIds)");
            params.put("skillIds", skillIds);
        }

        if (priorityIds != null && !priorityIds.isEmpty()) {
            sql.append(" AND ad.priority_id IN (:priorityIds)");
            params.put("priorityIds", priorityIds);
        }


        List<Object[]> rows =
                dashboardRepositoryCustom.executeDynamicQuery(
                        sql.toString(),
                        params
                );

        Object[] row = rows.get(0);

        return new DemandFilterCountDTO(
                row[0] != null ? ((Number) row[0]).longValue() : 0L,
                row[1] != null ? ((Number) row[1]).longValue() : 0L,
                row[2] != null ? ((Number) row[2]).longValue() : 0L,
                row[3] != null ? ((Number) row[3]).longValue() : 0L
        );
    }
}

