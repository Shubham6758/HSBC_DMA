
package com.coforge.hsbcdma.chatbot.serviceimpl;


import com.coforge.hsbcdma.chatbot.Service.Chatbot_QueryService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class Chatbot_QueryServiceImpl implements Chatbot_QueryService {

    private final JdbcTemplate jdbcTemplate;

    public Chatbot_QueryServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =====================================================
    // MAIN ENTRY METHOD
    // =====================================================
    @Override
    public long countByEntity(String entity, Map<String, Object> filters) {

        if (entity == null || entity.trim().isEmpty()) {
            return 0;
        }

        if ("demands".equalsIgnoreCase(entity)) {
            return countDemands(filters);
        }

        if ("profiles".equalsIgnoreCase(entity)) {
            return countProfiles(filters);
        }

        if ("resources".equalsIgnoreCase(entity)) {
            return countResources(filters);
        }

        if ("tracker".equalsIgnoreCase(entity)) {
            return countTracker(filters);
        }

        return 0;
    }

    // =====================================================
    // DEMANDS COUNT
    // =====================================================
    private long countDemands(Map<String, Object> filters) {

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT COUNT(*) FROM demands d WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (filters != null) {

            addLike(sql, params, "d.primary_skill", filters.get("primarySkills"));
            addLike(sql, params, "d.secondary_skill", filters.get("secondarySkills"));
            addLike(sql, params, "d.hiring_manager", filters.get("hiringManager"));
            addLike(sql, params, "d.lob", filters.get("lob"));
            addLike(sql, params, "d.rr", filters.get("rr"));
            addLike(sql, params, "d.demand_business_id", filters.get("demandBusinessId"));

            addEqualIgnoreCase(sql, params, "d.status", filters.get("status"));
            addEqualIgnoreCase(sql, params, "d.externality", filters.get("externality"));

            addDateRange(sql, params, "d.date_profile_shared", filters.get("dateProfileSharedRange"));
        }

        return executeCount(sql, params);
    }

    // =====================================================
    // PROFILES COUNT
    // =====================================================
    private long countProfiles(Map<String, Object> filters) {

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT COUNT(*) FROM profiles p WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (filters != null) {

            addLike(sql, params, "p.candidate_name", filters.get("candidateName"));
            addLike(sql, params, "p.email_id", filters.get("email"));
            addLike(sql, params, "p.emp_id", filters.get("empId"));
            addLike(sql, params, "p.sap_id", filters.get("sapId"));
            addLike(sql, params, "p.current_location", filters.get("location"));
            addLike(sql, params, "p.band", filters.get("band"));
            addLike(sql, params, "p.practice", filters.get("practice"));
            addLike(sql, params, "p.recruiter", filters.get("recruiter"));
            addLike(sql, params, "p.action_status", filters.get("actionStatus"));

            addBoolean01(sql, params, "p.is_active", filters.get("isActive"));

            addRange(sql, params, "p.experience",
                    filters.get("experience"),
                    filters.get("experienceMin"),
                    filters.get("experienceMax"));

            // Skill workaround
            Object primarySkill = filters.get("primarySkills");
            if (hasValue(primarySkill)) {
                sql.append(" AND (LOWER(p.summary) LIKE LOWER(?) OR LOWER(p.practice) LIKE LOWER(?)) ");
                params.add("%" + primarySkill.toString().trim() + "%");
                params.add("%" + primarySkill.toString().trim() + "%");
            }

            addDateRange(sql, params, "p.created_at", filters.get("createdAtRange"));
        }

        return executeCount(sql, params);
    }

    // =====================================================
    // RESOURCES COUNT
    // =====================================================
    private long countResources(Map<String, Object> filters) {

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT COUNT(*) FROM resource_data r WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (filters != null) {

            addLike(sql, params, "r.resource_name", filters.get("resourceName"));
            addLike(sql, params, "r.emp_id", filters.get("empId"));
            addLike(sql, params, "r.psid", filters.get("psid"));
            addLike(sql, params, "r.primary_skill", filters.get("primarySkills"));
            addLike(sql, params, "r.resource_status", filters.get("resourceStatus"));

            addLike(sql, params, "r.hbu", filters.get("hbu"));
            addLike(sql, params, "r.lob", filters.get("lob"));
            addLike(sql, params, "r.coforge_location", filters.get("coforgeLocation"));
            addLike(sql, params, "r.functional_mgr_hsbc", filters.get("functionalMgrHsbc"));
            addLike(sql, params, "r.replacement_required", filters.get("replacementRequired"));
            addLike(sql, params, "r.replacement_given", filters.get("replacementGiven"));
            addLike(sql, params, "r.onshore_offshore", filters.get("onshoreOffshore"));

            addBoolean01(sql, params, "r.is_active", filters.get("isActive"));

            addRange(
                    sql,
                    params,
                    "r.experience",
                    filters.get("experience"),
                    filters.get("experienceMin"),
                    filters.get("experienceMax")
            );
        }

        return executeCount(sql, params);
    }

    // =====================================================
    // TRACKER COUNT
    // =====================================================
    private long countTracker(Map<String, Object> filters) {

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT COUNT(*) FROM profile_tracker pt ");
        sql.append(" LEFT JOIN profiles p ON pt.profile_id = p.id ");
        sql.append(" LEFT JOIN demands d ON pt.demand_id = d.id ");
        sql.append(" WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (filters != null) {

            addLike(sql, params, "p.candidate_name", filters.get("candidateName"));
            addLike(sql, params, "d.primary_skill", filters.get("primarySkills"));

            Object demandId = filters.get("demandId");
            if (hasValue(demandId)) {
                sql.append(" AND pt.demand_id = ? ");
                params.add(Long.parseLong(demandId.toString()));
            }
        }

        return executeCount(sql, params);
    }

    // =====================================================
    // COMMON HELPERS
    // =====================================================
    private long executeCount(StringBuilder sql, List<Object> params) {

        System.out.println("SQL: " + sql);
        System.out.println("PARAMS: " + params);

        Long result = jdbcTemplate.queryForObject(
                sql.toString(),
                params.toArray(),
                Long.class
        );

        return result == null ? 0 : result;
    }

    private void addLike(StringBuilder sql, List<Object> params, String column, Object value) {

        if (!hasValue(value)) return;

        sql.append(" AND LOWER(").append(column).append(") LIKE LOWER(?) ");
        params.add("%" + value.toString().trim() + "%");
    }

    private void addEqualIgnoreCase(StringBuilder sql, List<Object> params, String column, Object value) {

        if (!hasValue(value)) return;

        sql.append(" AND LOWER(").append(column).append(") = LOWER(?) ");
        params.add(value.toString().trim());
    }

    private void addBoolean01(StringBuilder sql, List<Object> params, String column, Object value) {

        if (value == null) return;

        boolean boolValue = Boolean.parseBoolean(value.toString());
        sql.append(" AND ").append(column).append(" = ? ");
        params.add(boolValue ? 1 : 0);
    }

    private void addRange(StringBuilder sql, List<Object> params,
                          String column, Object exact, Object min, Object max) {

        if (hasValue(exact)) {
            sql.append(" AND ").append(column).append(" = ? ");
            params.add(Double.parseDouble(exact.toString()));
        }

        if (hasValue(min)) {
            sql.append(" AND ").append(column).append(" >= ? ");
            params.add(Double.parseDouble(min.toString()));
        }

        if (hasValue(max)) {
            sql.append(" AND ").append(column).append(" <= ? ");
            params.add(Double.parseDouble(max.toString()));
        }
    }

    private void addDateRange(StringBuilder sql, List<Object> params,
                              String column, Object rangeObj) {

        if (!hasValue(rangeObj)) return;

        LocalDate today = LocalDate.now();
        LocalDate start = null;
        LocalDate end = null;

        String range = rangeObj.toString().trim().toUpperCase();

        if ("TODAY".equals(range)) {
            start = today;
            end = today.plusDays(1);

        } else if ("YESTERDAY".equals(range)) {
            start = today.minusDays(1);
            end = today;

        } else if ("THIS_WEEK".equals(range)) {
            start = today.with(DayOfWeek.MONDAY);
            end = start.plusDays(7);

        } else if ("LAST_WEEK".equals(range)) {
            LocalDate thisWeekStart = today.with(DayOfWeek.MONDAY);
            start = thisWeekStart.minusDays(7);
            end = thisWeekStart;

        } else if ("THIS_MONTH".equals(range)) {
            start = today.withDayOfMonth(1);
            end = start.plusMonths(1);

        } else if ("LAST_MONTH".equals(range)) {
            LocalDate thisMonthStart = today.withDayOfMonth(1);
            start = thisMonthStart.minusMonths(1);
            end = thisMonthStart;

        } else if ("THIS_YEAR".equals(range)) {
            start = today.withDayOfYear(1);
            end = start.plusYears(1);
        }

        if (start == null || end == null) return;

        sql.append(" AND ").append(column).append(" >= ? ");
        sql.append(" AND ").append(column).append(" < ? ");

        params.add(Date.valueOf(start));
        params.add(Date.valueOf(end));
    }

    private boolean hasValue(Object value) {
        return value != null && !value.toString().trim().isEmpty();
    }
}