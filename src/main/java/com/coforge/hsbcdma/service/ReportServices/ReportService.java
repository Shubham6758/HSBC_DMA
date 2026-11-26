package com.coforge.hsbcdma.service.ReportServices;

import com.coforge.hsbcdma.dto.ReportDTO.CombinationCountDTO;
import com.coforge.hsbcdma.dto.ReportDTO.GenericStatusProjection;
import com.coforge.hsbcdma.dto.ReportDTO.HmSkillReportDTO;
import com.coforge.hsbcdma.repository.ReportRepositories.ReportRepository;
import com.coforge.hsbcdma.repository.ReportRepositories.ReportRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportRepositoryCustom repo;
    private static final Set<String> ALLOWED_PARAMETERS =
            Set.of("hbu", "lob", "hm", "priority");
    public List<HmSkillReportDTO> getHmSkillReport(
            List<Long> hmIds,
            List<Long> skillIds,
            List<Long> hbuIds,
            List<Long> lobIds) {

        System.out.println("HM ID = " + hmIds);
        System.out.println("SKILL ID = " + skillIds);

//        List<Object[]> result =
//                reportRepository.getHmSkillReport(
//                        hmIds,
//                        skillIds,
//                        hbuIds,
//                        lobIds
//                );
//
//        return result.stream()
//                .map(HmSkillReportDTO::new)
//                .toList();


        Map<String, Object> params = new HashMap<>();

        StringBuilder sql = new StringBuilder("""
    SELECT
        COALESCE(hm.hiring_manager, 'NA') AS hiring_manager,
        COALESCE(sc.skill_cluster, 'NA') AS skill,
        COALESCE(h.hbu,'NA') AS hbu,
        COALESCE(l.lob,'NA') AS lob,

        COUNT(DISTINCT CASE
                            WHEN LOWER(TRIM(s.status))
                                 IN ('open','open positions')
                            THEN d.id
                        END) AS open_positions,

        COUNT(DISTINCT pt.id) AS profiles_shared,
        
        COUNT(DISTINCT CASE
                            WHEN LOWER(TRIM(s.status))
                                 IN ('open','open positions')
                            AND pt.profile_shared_date IS NOT NULL
                            AND pt.interview_date IS NULL
                            THEN pt.id
                        END) AS awaiting_slots,
                
                 COUNT(DISTINCT CASE
                                     WHEN LOWER(TRIM(s.status))
                                          IN ('open','open positions')
                                     AND pt.interview_date IS NOT NULL
                                     AND o.id IS NULL
                                     THEN pt.id
                                 END) AS awaiting_feedback,
                                 

        COUNT(DISTINCT CASE
                            WHEN EXISTS (
                SELECT 1
                FROM onboarding o2
                JOIN onboarding_status obs2
                    ON o2.onboarding_status_id = obs2.id
                WHERE o2.profile_tracker_id = pt.id
                AND obs2.name = 'Onboarded'
            )
            THEN pt.id
        END) AS selected,

       COUNT(DISTINCT CASE
                            WHEN pts.name = 'Client Rejected'
                            THEN pt.id
                        END) AS rejected

    FROM add_demands d

    LEFT JOIN status s ON d.status_id = s.id
    LEFT JOIN hiring_manager hm ON d.hiring_manager_id = hm.id
    LEFT JOIN skill_cluster sc ON d.skill_cluster_id = sc.id
    LEFT JOIN hbu h ON d.hbu_id = h.id
    LEFT JOIN lob l ON d.lob_id = l.id
    LEFT JOIN profile_tracker pt ON pt.demand_id = d.id
    LEFT JOIN onboarding o ON o.profile_tracker_id = pt.id
    LEFT JOIN profile_tracker_status pts
        ON pt.profile_tracker_status_id = pts.id

    WHERE 1=1
""");



        if (hmIds != null && !hmIds.isEmpty()) {
            sql.append(" AND d.hiring_manager_id IN (:hmIds)");
            params.put("hmIds", hmIds);
        }

        if (skillIds != null && !skillIds.isEmpty()) {
            sql.append(" AND d.skill_cluster_id IN (:skillIds)");
            params.put("skillIds", skillIds);
        }

        if (hbuIds != null && !hbuIds.isEmpty()) {
            sql.append(" AND d.hbu_id IN (:hbuIds)");
            params.put("hbuIds", hbuIds);
        }

        if (lobIds != null && !lobIds.isEmpty()) {
            sql.append(" AND d.lob_id IN (:lobIds)");
            params.put("lobIds", lobIds);
        }


        sql.append("""
    GROUP BY hm.hiring_manager,
             sc.skill_cluster,
             h.hbu,
             l.lob
    ORDER BY hm.hiring_manager,
             sc.skill_cluster
""");
        List<Object[]> result =
                repo.executeDynamicQuery(sql.toString(), params);

        return result.stream()
                .map(HmSkillReportDTO::new)
                .toList();
    }









//    public List<CombinationCountDTO> getOpenCount(
//            List<Long> hbuIds,
//            List<Long> lobIds,
//            List<Long> hmIds,
//            List<Long> skillIds,
//            List<Long> priorityIds
//
//    ) {
//
//        Map<String, Object> params = new HashMap<>();
//
//        StringBuilder sql = new StringBuilder("""
//        SELECT
//            COUNT(DISTINCT CASE
//                WHEN LOWER(TRIM(s.status)) IN ('open','open positions')
//                THEN d.id END),
//
//            COUNT(DISTINCT pt.id),
//
//            COUNT(DISTINCT CASE
//                WHEN LOWER(os.name) = 'onboarded'
//                THEN pt.id END),
//
//            COUNT(DISTINCT CASE
//                WHEN LOWER(pts.name) = 'client rejected'
//                THEN pt.id END),
//
//            AVG(
//                CASE
//                    WHEN LOWER(TRIM(s.status)) IN ('open','open positions')
//                    THEN DATEDIFF(CURDATE(), d.demand_received_date)
//                END
//            ) AS aging
//
//        FROM add_demands d
//        JOIN status s ON d.status_id = s.id
//        LEFT JOIN profile_tracker pt ON pt.demand_id = d.id
//        LEFT JOIN profile_tracker_status pts ON pt.profile_tracker_status_id = pts.id
//        LEFT JOIN onboarding o ON o.profile_tracker_id = pt.id
//        LEFT JOIN onboarding_status os ON o.onboarding_status_id = os.id
//        WHERE 1=1
//    """);
//
//        // ✅ APPLY FILTERS (NO GROUP BY)
//        if (hbuIds != null && !hbuIds.isEmpty()) {
//            sql.append(" AND d.hbu_id IN (:hbuIds)");
//            params.put("hbuIds", hbuIds);
//        }
//
//        if (lobIds != null && !lobIds.isEmpty()) {
//            sql.append(" AND d.lob_id IN (:lobIds)");
//            params.put("lobIds", lobIds);
//        }
//
//        if (hmIds != null && !hmIds.isEmpty()) {
//            sql.append(" AND d.hiring_manager_id IN (:hmIds)");
//            params.put("hmIds", hmIds);
//        }
//
//        if (skillIds != null && !skillIds.isEmpty()) {
//            sql.append(" AND d.skill_cluster_id IN (:skillIds)");
//            params.put("skillIds", skillIds);
//        }
//
//        if (priorityIds != null && !priorityIds.isEmpty()) {
//            sql.append(" AND d.priority_id IN (:priorityIds)");
//            params.put("priorityIds", priorityIds);
//        }
//
//        List<Object[]> rows = repo.executeDynamicQuery(sql.toString(), params);
//
//        Object[] row = rows.get(0);
//
//        // ✅ Fetch names
//        String hbuNames = getNames("hbu", "hbu", "id", hbuIds);
//        String lobNames = getNames("lob", "lob", "id", lobIds);
//        String hmNames = getNames("hiring_manager", "hiring_manager", "id", hmIds);
//        String skillNames = getNames("skill_cluster", "skill_cluster", "id", skillIds);
//        String priorityNames = getNames(
//                "priority",
//                "priority",
//                "id",
//                priorityIds
//        );
////        return List.of(new CombinationCountDTO(
////                hbuNames,
////                lobNames,
////                hmNames,
////                skillNames,
////                ((Number) row[0]).longValue(),
////                ((Number) row[1]).longValue(),
////                ((Number) row[2]).longValue(),
////                ((Number) row[3]).longValue()
////        ));
//
//        Long openCount = ((Number) row[0]).longValue();
//        Long profileShared = ((Number) row[1]).longValue();
//        Long selected = ((Number) row[2]).longValue();
//        Long rejected = ((Number) row[3]).longValue();
//
//        Double selectionRate = profileShared == 0
//                ? 0.0
//                : Math.round((selected * 10000.0 / profileShared)) / 100.0;
//        Long aging = row[4] == null
//                ? 0L
//                : ((Number) row[4]).longValue();
//
//        String agingBand;
//
//        if (aging <= 30) {
//            agingBand = "0-30 Days";
//        } else if (aging <= 60) {
//            agingBand = "31-60 Days";
//        } else if (aging <= 90) {
//            agingBand = "61-90 Days";
//        } else {
//            agingBand = "90+ Days";
//        }
//        return List.of(new CombinationCountDTO(
//                hbuNames,
//                lobNames,
//                hmNames,
//                skillNames,
//                priorityNames,
//                openCount,
//                profileShared,
//                selected,
//                rejected,
//                selectionRate,
//                aging,
//                agingBand
//
//        ));
//    }

    public List<CombinationCountDTO> getOpenCount(
            List<Long> hbuIds,
            List<Long> lobIds,
            List<Long> hmIds,
            List<Long> skillIds,
            List<Long> priorityIds
    ) {

        Map<String, Object> params = new HashMap<>();

        boolean hbuSelected =
                hbuIds != null && !hbuIds.isEmpty();

        boolean lobSelected =
                lobIds != null && !lobIds.isEmpty();

        boolean hmSelected =
                hmIds != null && !hmIds.isEmpty();

        boolean skillSelected =
                skillIds != null && !skillIds.isEmpty();

        boolean prioritySelected =
                priorityIds != null && !priorityIds.isEmpty();

        /*
         * No filter selected:
         * Return one combined count across all dimensions and skills.
         *
         * Any filter selected:
         * Return skill-wise counts.
         */
        boolean anyFilterSelected =
                hbuSelected
                        || lobSelected
                        || hmSelected
                        || skillSelected
                        || prioritySelected;

        String hbuSelect = hbuSelected
                ? "COALESCE(h.hbu, 'NA')"
                : "'All'";

        String lobSelect = lobSelected
                ? "COALESCE(l.lob, 'NA')"
                : "'All'";

        String hmSelect = hmSelected
                ? "COALESCE(hm.hiring_manager, 'NA')"
                : "'All'";

        /*
         * No filters: show All and combine skills.
         * Any filter: show actual skill and group by skill.
         */
        String skillSelect = anyFilterSelected
                ? "COALESCE(sc.skill_cluster, 'NA')"
                : "'All'";

        String prioritySelect = prioritySelected
                ? "COALESCE(p.priority, 'NA')"
                : "'All'";

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ")
                .append(hbuSelect)
                .append(" AS hbu_name, ")
                .append(lobSelect)
                .append(" AS lob_name, ")
                .append(hmSelect)
                .append(" AS hiring_manager_name, ")
                .append(skillSelect)
                .append(" AS skill_name, ")
                .append(prioritySelect)
                .append(" AS priority_name, ");

        sql.append("""
                COUNT(DISTINCT CASE
                    WHEN LOWER(TRIM(s.status))
                         IN ('open', 'open positions')
                    THEN d.id
                END) AS open_count,
    
                COUNT(DISTINCT pt.id) AS profile_shared,
    
                COUNT(DISTINCT CASE
                    WHEN LOWER(TRIM(os.name)) = 'onboarded'
                    THEN pt.id
                END) AS selected_count,
    
                COUNT(DISTINCT CASE
                    WHEN LOWER(TRIM(pts.name)) = 'client rejected'
                    THEN pt.id
                END) AS rejected_count,
    
                AVG(
                    CASE
                        WHEN LOWER(TRIM(s.status))
                             IN ('open', 'open positions')
                             AND d.demand_received_date IS NOT NULL
                        THEN DATEDIFF(
                            CURDATE(),
                            d.demand_received_date
                        )
                    END
                ) AS aging
    
            FROM add_demands d
    
            LEFT JOIN status s
                ON d.status_id = s.id
    
            LEFT JOIN hbu h
                ON d.hbu_id = h.id
    
            LEFT JOIN lob l
                ON d.lob_id = l.id
    
            LEFT JOIN hiring_manager hm
                ON d.hiring_manager_id = hm.id
    
            LEFT JOIN skill_cluster sc
                ON d.skill_cluster_id = sc.id
    
            LEFT JOIN priority p
                ON d.priority_id = p.id
    
            LEFT JOIN profile_tracker pt
                ON pt.demand_id = d.id
    
            LEFT JOIN profile_tracker_status pts
                ON pt.profile_tracker_status_id = pts.id
    
            LEFT JOIN onboarding o
                ON o.profile_tracker_id = pt.id
    
            LEFT JOIN onboarding_status os
                ON o.onboarding_status_id = os.id
    
            WHERE 1 = 1
            """);

        if (hbuSelected) {
            sql.append(" AND d.hbu_id IN (:hbuIds)");
            params.put("hbuIds", hbuIds);
        }

        if (lobSelected) {
            sql.append(" AND d.lob_id IN (:lobIds)");
            params.put("lobIds", lobIds);
        }

        if (hmSelected) {
            sql.append(" AND d.hiring_manager_id IN (:hmIds)");
            params.put("hmIds", hmIds);
        }

        /*
         * If skill is not selected, no skill filtering is applied.
         * But when another filter is selected, results are still
         * grouped skill-wise.
         */
        if (skillSelected) {
            sql.append(" AND d.skill_cluster_id IN (:skillIds)");
            params.put("skillIds", skillIds);
        }

        if (prioritySelected) {
            sql.append(" AND d.priority_id IN (:priorityIds)");
            params.put("priorityIds", priorityIds);
        }

        List<String> groupByColumns = new ArrayList<>();
        List<String> orderByColumns = new ArrayList<>();

        if (hbuSelected) {
            groupByColumns.add("d.hbu_id");
            groupByColumns.add("h.hbu");
            orderByColumns.add("h.hbu");
        }

        if (lobSelected) {
            groupByColumns.add("d.lob_id");
            groupByColumns.add("l.lob");
            orderByColumns.add("l.lob");
        }

        if (hmSelected) {
            groupByColumns.add("d.hiring_manager_id");
            groupByColumns.add("hm.hiring_manager");
            orderByColumns.add("hm.hiring_manager");
        }

        /*
         * Critical behavior:
         *
         * No filters:
         * No skill grouping, so one combined row.
         *
         * Any filter:
         * Always add skill grouping, so counts are skill-wise.
         */
        if (anyFilterSelected) {
            groupByColumns.add("d.skill_cluster_id");
            groupByColumns.add("sc.skill_cluster");
            orderByColumns.add("sc.skill_cluster");
        }

        if (prioritySelected) {
            groupByColumns.add("d.priority_id");
            groupByColumns.add("p.priority");
            orderByColumns.add("p.priority");
        }

        /*
         * No filters means no GROUP BY.
         */
        if (!groupByColumns.isEmpty()) {
            sql.append(" GROUP BY ");
            sql.append(String.join(", ", groupByColumns));
        }

        if (!orderByColumns.isEmpty()) {
            sql.append(" ORDER BY ");
            sql.append(String.join(", ", orderByColumns));
        }

        List<Object[]> rows = repo.executeDynamicQuery(
                sql.toString(),
                params
        );

        List<CombinationCountDTO> result = new ArrayList<>();

        for (Object[] row : rows) {

            String hbuName = getStringValue(row[0]);
            String lobName = getStringValue(row[1]);
            String hmName = getStringValue(row[2]);
            String skillName = getStringValue(row[3]);
            String priorityName = getStringValue(row[4]);

            Long openCount = getLongValue(row[5]);
            Long profileShared = getLongValue(row[6]);
            Long selected = getLongValue(row[7]);
            Long rejected = getLongValue(row[8]);

            Double selectionRate = profileShared == 0L
                    ? 0.0
                    : Math.round(
                    selected * 10000.0 / profileShared
            ) / 100.0;

            Long aging = row[9] == null
                    ? 0L
                    : Math.round(
                    ((Number) row[9]).doubleValue()
            );

            String agingBand;

            if (aging <= 30) {
                agingBand = "0-30 Days";
            } else if (aging <= 60) {
                agingBand = "31-60 Days";
            } else if (aging <= 90) {
                agingBand = "61-90 Days";
            } else {
                agingBand = "90+ Days";
            }

            result.add(
                    new CombinationCountDTO(
                            hbuName,
                            lobName,
                            hmName,
                            skillName,
                            priorityName,
                            openCount,
                            profileShared,
                            selected,
                            rejected,
                            selectionRate,
                            aging,
                            agingBand
                    )
            );
        }

        return result;
    }
    public List<CombinationCountDTO> getallhmhbu(
            List<String> parameters
    ) {

        Set<String> selectedParameters =
                normalizeParameters(parameters);

        validateParameters(selectedParameters);

        boolean hbuSelected =
                selectedParameters.contains("hbu");

        boolean lobSelected =
                selectedParameters.contains("lob");

        boolean hmSelected =
                selectedParameters.contains("hm");

        boolean prioritySelected =
                selectedParameters.contains("priority");

        /*
         * Selected dimensions are displayed separately,
         * but all skills are combined into one "All" value.
         */
        String hbuSelect = hbuSelected
                ? "COALESCE(h.hbu, 'NA')"
                : "'All'";

        String lobSelect = lobSelected
                ? "COALESCE(l.lob, 'NA')"
                : "'All'";

        String hmSelect = hmSelected
                ? "COALESCE(hm.hiring_manager, 'NA')"
                : "'All'";

        /*
         * Always return All for skill.
         * Do not group by skill_cluster.
         */
        String skillSelect = "'All'";

        String prioritySelect = prioritySelected
                ? "COALESCE(p.priority, 'NA')"
                : "'All'";

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ")
                .append(hbuSelect)
                .append(" AS hbu_name, ")
                .append(lobSelect)
                .append(" AS lob_name, ")
                .append(hmSelect)
                .append(" AS hiring_manager_name, ")
                .append(skillSelect)
                .append(" AS skill_name, ")
                .append(prioritySelect)
                .append(" AS priority_name, ");

        sql.append("""
            COUNT(DISTINCT CASE
                WHEN LOWER(TRIM(s.status))
                     IN ('open', 'open positions')
                THEN d.id
            END) AS open_count,

            COUNT(DISTINCT pt.id) AS profile_shared,

            COUNT(DISTINCT CASE
                WHEN LOWER(TRIM(os.name)) = 'onboarded'
                THEN pt.id
            END) AS selected_count,

            COUNT(DISTINCT CASE
                WHEN LOWER(TRIM(pts.name)) = 'client rejected'
                THEN pt.id
            END) AS rejected_count,

            AVG(
                CASE
                    WHEN LOWER(TRIM(s.status))
                         IN ('open', 'open positions')
                         AND d.demand_received_date IS NOT NULL
                    THEN DATEDIFF(
                        CURDATE(),
                        d.demand_received_date
                    )
                END
            ) AS aging

            FROM add_demands d

            LEFT JOIN status s
                ON d.status_id = s.id

            LEFT JOIN hbu h
                ON d.hbu_id = h.id

            LEFT JOIN lob l
                ON d.lob_id = l.id

            LEFT JOIN hiring_manager hm
                ON d.hiring_manager_id = hm.id

            LEFT JOIN priority p
                ON d.priority_id = p.id

            LEFT JOIN profile_tracker pt
                ON pt.demand_id = d.id

            LEFT JOIN profile_tracker_status pts
                ON pt.profile_tracker_status_id = pts.id

            LEFT JOIN onboarding o
                ON o.profile_tracker_id = pt.id

            LEFT JOIN onboarding_status os
                ON o.onboarding_status_id = os.id

            WHERE 1 = 1
            """);

        /*
         * Exclude rows where a selected dimension has no value.
         */
        if (hbuSelected) {
            sql.append(" AND d.hbu_id IS NOT NULL ");
        }

        if (lobSelected) {
            sql.append(" AND d.lob_id IS NOT NULL ");
        }

        if (hmSelected) {
            sql.append(" AND d.hiring_manager_id IS NOT NULL ");
        }

        if (prioritySelected) {
            sql.append(" AND d.priority_id IS NOT NULL ");
        }

        List<String> groupByColumns = new ArrayList<>();
        List<String> orderByColumns = new ArrayList<>();

        if (hbuSelected) {
            groupByColumns.add("d.hbu_id");
            groupByColumns.add("h.hbu");

            orderByColumns.add("h.hbu");
        }

        if (lobSelected) {
            groupByColumns.add("d.lob_id");
            groupByColumns.add("l.lob");

            orderByColumns.add("l.lob");
        }

        if (hmSelected) {
            groupByColumns.add("d.hiring_manager_id");
            groupByColumns.add("hm.hiring_manager");

            orderByColumns.add("hm.hiring_manager");
        }

        if (prioritySelected) {
            groupByColumns.add("d.priority_id");
            groupByColumns.add("p.priority");

            orderByColumns.add("p.priority");
        }

        /*
         * Important:
         * Do not add d.skill_cluster_id or sc.skill_cluster
         * to GROUP BY because all skills must be combined.
         */
        if (!groupByColumns.isEmpty()) {
            sql.append(" GROUP BY ")
                    .append(String.join(", ", groupByColumns));
        }

        if (!orderByColumns.isEmpty()) {
            sql.append(" ORDER BY ")
                    .append(String.join(", ", orderByColumns));
        }

        List<Object[]> rows =
                repo.executeDynamicQuery(
                        sql.toString(),
                        Collections.emptyMap()
                );

        return mapToDTO(rows);
    }

    private Set<String> normalizeParameters(
            List<String> parameters
    ) {

        if (parameters == null || parameters.isEmpty()) {
            return Collections.emptySet();
        }

        return parameters.stream()
                .filter(Objects::nonNull)

                /*
                 * Converts ["hm,lob"] into "hm" and "lob".
                 */
                .flatMap(parameter ->
                        Arrays.stream(parameter.split(","))
                )

                .map(String::trim)
                .map(parameter ->
                        parameter.toLowerCase(Locale.ROOT)
                )
                .filter(parameter -> !parameter.isBlank())

                /*
                 * Removes duplicate parameters while
                 * preserving request order.
                 */
                .collect(Collectors.toCollection(
                        LinkedHashSet::new
                ));
    }

    private void validateParameters(
            Set<String> selectedParameters
    ) {

        Set<String> invalidParameters =
                selectedParameters.stream()
                        .filter(parameter ->
                                !ALLOWED_PARAMETERS.contains(parameter)
                        )
                        .collect(Collectors.toSet());

        if (!invalidParameters.isEmpty()) {
            throw new IllegalArgumentException(
                    "Invalid parameters: "
                            + invalidParameters
                            + ". Allowed parameters are: "
                            + ALLOWED_PARAMETERS
            );
        }
    }

    private List<CombinationCountDTO> mapToDTO(
            List<Object[]> rows
    ) {

        List<CombinationCountDTO> result =
                new ArrayList<>();

        for (Object[] row : rows) {

            String hbuName =
                    getStringValue(row[0]);

            String lobName =
                    getStringValue(row[1]);

            String hiringManagerName =
                    getStringValue(row[2]);

            String skillName =
                    getStringValue(row[3]);

            String priorityName =
                    getStringValue(row[4]);

            Long openCount =
                    getLongValue(row[5]);

            Long profileShared =
                    getLongValue(row[6]);

            Long selectedCount =
                    getLongValue(row[7]);

            Long rejectedCount =
                    getLongValue(row[8]);

            Double selectionRate =
                    calculateSelectionRate(
                            selectedCount,
                            profileShared
                    );

            Long aging = row[9] == null
                    ? 0L
                    : Math.round(
                    ((Number) row[9]).doubleValue()
            );

            String agingBand =
                    calculateAgingBand(aging);

            result.add(
                    new CombinationCountDTO(
                            hbuName,
                            lobName,
                            hiringManagerName,
                            skillName,
                            priorityName,
                            openCount,
                            profileShared,
                            selectedCount,
                            rejectedCount,
                            selectionRate,
                            aging,
                            agingBand
                    )
            );
        }

        return result;
    }

    private Double calculateSelectionRate(
            Long selectedCount,
            Long profileShared
    ) {

        if (profileShared == null || profileShared == 0L) {
            return 0.0;
        }

        long selected = selectedCount == null
                ? 0L
                : selectedCount;

        return Math.round(
                selected * 10000.0 / profileShared
        ) / 100.0;
    }

    private String calculateAgingBand(Long aging) {

        if (aging == null || aging <= 30) {
            return "0-30 Days";
        }

        if (aging <= 60) {
            return "31-60 Days";
        }

        if (aging <= 90) {
            return "61-90 Days";
        }

        return "90+ Days";
    }
    private Long getLongValue(Object value) {

        if (value == null) {
            return 0L;
        }

        return ((Number) value).longValue();
    }

    private String getStringValue(Object value) {

        if (value == null) {
            return "NA";
        }

        return value.toString();
    }
    private String getNames(String table, String column, String idColumn, List<Long> ids) {

        if (ids == null || ids.isEmpty()) return "";

        String sql = "SELECT " + column + " FROM " + table +
                " WHERE " + idColumn + " IN (:ids)";

        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);

        List<Object[]> rows = repo.executeDynamicQuery(sql, params);

        List<String> names = new ArrayList<>();

        for (Object obj : rows) {
            if (obj != null) {
                names.add(obj.toString());
            }
        }

        return names.isEmpty() ? "" : String.join(", ", names);
    }

//    private CombinationCountDTO mapRow(
//            Object[] row,
//            List<Long> hbuIds,
//            List<Long> lobIds,
//            List<Long> hmIds,
//            List<Long> skillIds
//    ) {
//        int index = 0;
//
//        String hbu = "NA";
//        String lob = "NA";
//        String hm = "NA";
//        String skill = "NA";
//
//        // ✅ Map dynamic columns (only if selected)
//        if (hbuIds != null && !hbuIds.isEmpty()) {
//            hbu = row[index] != null ? row[index].toString() : "NA";
//            index++;
//        }
//
//        if (lobIds != null && !lobIds.isEmpty()) {
//            lob = row[index] != null ? row[index].toString() : "NA";
//            index++;
//        }
//
//        if (hmIds != null && !hmIds.isEmpty()) {
//            hm = row[index] != null ? row[index].toString() : "NA";
//            index++;
//        }
//
//        if (skillIds != null && !skillIds.isEmpty()) {
//            skill = row[index] != null ? row[index].toString() : "NA";
//            index++;
//        }
//
//        // ✅ Fixed metric columns
//        Long open = row[index] != null ? ((Number) row[index]).longValue() : 0L;
//        index++;
//
//        Long profile = row[index] != null ? ((Number) row[index]).longValue() : 0L;
//        index++;
//
//        Long selected = row[index] != null ? ((Number) row[index]).longValue() : 0L;
//        index++;
//
//        Long rejected = row[index] != null ? ((Number) row[index]).longValue() : 0L;
//
//        return new CombinationCountDTO(hbu, lob, hm, skill, open, profile, selected, rejected);
//    }





    public List<Map<String, Object>> getStatus(String groupBy, Long id) {

        List<GenericStatusProjection> rows = reportRepository.getStatus(groupBy, id);

        Map<String, Map<String, Long>> grouped = new LinkedHashMap<>();

        for (GenericStatusProjection row : rows) {

            grouped
                    .computeIfAbsent(row.getGroupKey(), k -> new LinkedHashMap<>())
                    .put(row.getStatus(), row.getCount());
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, Map<String, Long>> entry : grouped.entrySet()) {

            Map<String, Object> obj = new LinkedHashMap<>();
            obj.put(groupBy, entry.getKey());
            obj.put("statusCounts", entry.getValue());

            result.add(obj);
        }

        return result;
    }



}

