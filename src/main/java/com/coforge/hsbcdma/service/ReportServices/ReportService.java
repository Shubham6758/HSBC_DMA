package com.coforge.hsbcdma.service.ReportServices;

import com.coforge.hsbcdma.dto.ReportDTO.CombinationCountDTO;
import com.coforge.hsbcdma.dto.ReportDTO.GenericStatusProjection;
import com.coforge.hsbcdma.dto.ReportDTO.HmSkillReportDTO;
import com.coforge.hsbcdma.repository.ReportRepositories.ReportRepository;
import com.coforge.hsbcdma.repository.ReportRepositories.ReportRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportRepositoryCustom repo;

    public List<HmSkillReportDTO> getHmSkillReport(Long hmId, Long skillId) {

        List<Object[]> result = reportRepository.getHmSkillReport(hmId, skillId);

        return result.stream()
                .map(HmSkillReportDTO::new)
                .toList();
    }









    public List<CombinationCountDTO> getOpenCount(
            Long hbuId,
            Long lobId,
            Long hmId,
            Long skillId
    ) {

        boolean hasFilter = (hbuId != null || lobId != null || hmId != null || skillId != null);

        // ✅ NO FILTER
        if (!hasFilter) {

            String sql = """
        SELECT

            COUNT(DISTINCT CASE
                WHEN LOWER(TRIM(s.status)) IN ('open','open positions')
                THEN d.id
            END),

            COUNT(DISTINCT pt.id),

            COUNT(DISTINCT CASE
                WHEN LOWER(os.name) = 'onboarded'
                THEN pt.id
            END),

            COUNT(DISTINCT CASE
                WHEN LOWER(pts.name) = 'client rejected'
                THEN pt.id
            END)

        FROM add_demands d
        JOIN status s ON d.status_id = s.id

        LEFT JOIN profile_tracker pt ON pt.demand_id = d.id
        LEFT JOIN profile_tracker_status pts ON pt.profile_tracker_status_id = pts.id

        LEFT JOIN onboarding o ON o.profile_tracker_id = pt.id
        LEFT JOIN onboarding_status os ON o.onboarding_status_id = os.id
        """;

            List<?> rows = repo.executeDynamicQuery(sql);
            Object[] r = (Object[]) rows.get(0);

            return List.of(new CombinationCountDTO(
                    "NA","NA","NA","NA",
                    ((Number) r[0]).longValue(),
                    ((Number) r[1]).longValue(),
                    ((Number) r[2]).longValue(),
                    ((Number) r[3]).longValue()
            ));
        }

        // ✅ DYNAMIC QUERY
        StringBuilder sql = new StringBuilder("SELECT ");

        List<String> selectCols = new ArrayList<>();
        List<String> groupCols = new ArrayList<>();
        List<String> whereConditions = new ArrayList<>();

        if (hbuId != null) {
            selectCols.add("COALESCE(hbu.hbu, 'NA')");
            groupCols.add("hbu.hbu");
            whereConditions.add("d.hbu_id = " + hbuId);
        }

        if (lobId != null) {
            selectCols.add("COALESCE(l.lob, 'NA')");
            groupCols.add("l.lob");
            whereConditions.add("d.lob_id = " + lobId);
        }

        if (hmId != null) {
            selectCols.add("COALESCE(hm.hiring_manager, 'NA')");
            groupCols.add("hm.hiring_manager");
            whereConditions.add("d.hiring_manager_id = " + hmId);
        }

        if (skillId != null) {
            selectCols.add("COALESCE(sc.skill_cluster, 'NA')");
            groupCols.add("sc.skill_cluster");
            whereConditions.add("d.skill_cluster_id = " + skillId);
        }

        sql.append(String.join(", ", selectCols)).append(",");

        sql.append("""
        COUNT(DISTINCT CASE
            WHEN LOWER(TRIM(s.status)) IN ('open','open positions')
            THEN d.id
        END),

        COUNT(DISTINCT pt.id),

        COUNT(DISTINCT CASE
            WHEN LOWER(os.name) = 'onboarded'
            THEN pt.id
        END),

        COUNT(DISTINCT CASE
            WHEN LOWER(pts.name) = 'client rejected'
            THEN pt.id
        END)
    """);

        sql.append("""
        FROM add_demands d
        JOIN status s ON d.status_id = s.id

        LEFT JOIN profile_tracker pt ON pt.demand_id = d.id
        LEFT JOIN profile_tracker_status pts ON pt.profile_tracker_status_id = pts.id

        LEFT JOIN onboarding o ON o.profile_tracker_id = pt.id
        LEFT JOIN onboarding_status os ON o.onboarding_status_id = os.id

        LEFT JOIN hbu hbu ON d.hbu_id = hbu.id
        LEFT JOIN lob l ON d.lob_id = l.id
        LEFT JOIN hiring_manager hm ON d.hiring_manager_id = hm.id
        LEFT JOIN skill_cluster sc ON d.skill_cluster_id = sc.id
    """);

        // ✅ APPLY FILTERS
        if (!whereConditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", whereConditions));
        }

        sql.append(" GROUP BY ").append(String.join(", ", groupCols));

        List<?> rawRows = repo.executeDynamicQuery(sql.toString());

        List<CombinationCountDTO> result = new ArrayList<>();

        for (Object obj : rawRows) {
            Object[] row = (Object[]) obj;
            result.add(mapRow(row, hbuId, lobId, hmId, skillId));
        }

        return result;
    }

    // ✅ FINAL MAPPING
    private CombinationCountDTO mapRow(
            Object[] row,
            Long hbuId,
            Long lobId,
            Long hmId,
            Long skillId
    ) {

        int index = 0;

        String hbu = "NA";
        String lob = "NA";
        String hm = "NA";
        String skill = "NA";

        if (hbuId != null) hbu = (String) row[index++];
        if (lobId != null) lob = (String) row[index++];
        if (hmId != null) hm = (String) row[index++];
        if (skillId != null) skill = (String) row[index++];

        Long open     = ((Number) row[index++]).longValue();
        Long profile  = ((Number) row[index++]).longValue();
        Long selected = ((Number) row[index++]).longValue();
        Long rejected = ((Number) row[index]).longValue();

        return new CombinationCountDTO(hbu, lob, hm, skill, open, profile, selected, rejected);
    }





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
