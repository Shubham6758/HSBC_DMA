package com.coforge.hsbcdma.service.ProfileTrackerServices;

import com.coforge.hsbcdma.dto.ProfileTrackDTO.ProfileTrackerFilterRequest;
import com.coforge.hsbcdma.entity.AddDemand;
import com.coforge.hsbcdma.entity.Location;
import com.coforge.hsbcdma.entity.Profile;
import com.coforge.hsbcdma.entity.ProfileTracker;
import com.coforge.hsbcdma.entity.dropdownEntities.PrimarySkills;
import com.coforge.hsbcdma.entity.dropdownEntities.SecondarySkills;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class ProfileTrackerSpecifications {

    private ProfileTrackerSpecifications() {}

    /* ========= Attribute names on master entities ========= */
    private static final String ATTR_PRIORITY               = "priority";        // Priority.priority
    private static final String ATTR_SKILL_CLUSTER          = "skillCluster";     // SkillCluster.skillCluster
    private static final String ATTR_LOB                    = "lob";              // Lob.lob
    private static final String ATTR_HBU                    = "hbu";              // Hbu.hbu
    private static final String ATTR_EXTERNAL_INTERNAL      = "externalInternal"; // ExternalInternal.externalInternal
    private static final String ATTR_HIRING_MANAGER         = "hiringManager";    // HiringManager.hiringManager

    private static final String ATTR_EVALUATION_STATUS      = "name";             // EvaluationStatus.name
    private static final String ATTR_PROFILE_TRACKER_STATUS = "name";             // ProfileTrackerStatus.name

    // =====================================================
    // ENTRY
    // =====================================================
    public static Specification<ProfileTracker> byFilter(ProfileTrackerFilterRequest f) {
        return (root, query, cb) -> {

            // distinct only for entity query (not count query)
            if (query.getResultType() != Long.class) {
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();
            if (f == null) return cb.conjunction();

            // --------------------------------------------------
            // Decide joins only if needed
            // --------------------------------------------------
            boolean needsDemand =
                    f.getDemandPkId() != null ||
                            f.getDemandNumber() != null ||
                            hasList(f.getPriorityNames()) ||
                            hasList(f.getSkillClusterNames()) ||
                            hasList(f.getLobNames()) ||
                            hasList(f.getHbuNames()) ||
                            hasList(f.getHiringManagerNames()) ||
                            hasList(f.getDemandPrimarySkillNames()) ||
                            hasList(f.getDemandSecondarySkillNames()) ||
                            hasList(f.getDemandLocationNames()) ||
                            hasList(f.getDemandLocationIds());

            boolean needsProfile =
                    f.getProfileId() != null ||
                            hasText(f.getCandidateName()) ||
                            hasText(f.getEmpId()) ||
                            hasText(f.getEmailId()) ||
                            hasList(f.getExternalInternalNames());

            final Join<ProfileTracker, AddDemand> d =
                    needsDemand ? root.join("demand", JoinType.INNER) : null;

            final Join<ProfileTracker, Profile> p =
                    needsProfile ? root.join("profile", JoinType.INNER) : null;

            // --------------------------------------------------
            // Direct keys
            // --------------------------------------------------
            if (f.getDemandPkId() != null) {
                predicates.add(cb.equal(
                        (d != null ? d : root.join("demand")).get("id"),
                        f.getDemandPkId()));
            }

            if (f.getDemandNumber() != null) {
                predicates.add(cb.equal(
                        (d != null ? d : root.join("demand")).get("demandId"),
                        f.getDemandNumber()));
            }

            if (f.getProfileId() != null) {
                predicates.add(cb.equal(
                        (p != null ? p : root.join("profile")).get("id"),
                        f.getProfileId()));
            }

            // --------------------------------------------------
            // Demand-side MULTI‑SELECT filters
            // --------------------------------------------------
            if (hasList(f.getPriorityNames())) {
                predicates.add(likeAnyIgnoreCase(cb,
                        (d != null ? d : root.join("demand"))
                                .join("priority", JoinType.LEFT)
                                .get(ATTR_PRIORITY),
                        f.getPriorityNames()));
            }

            if (hasList(f.getSkillClusterNames())) {
                predicates.add(likeAnyIgnoreCase(cb,
                        (d != null ? d : root.join("demand"))
                                .join("skillCluster", JoinType.LEFT)
                                .get(ATTR_SKILL_CLUSTER),
                        f.getSkillClusterNames()));
            }

            if (hasList(f.getLobNames())) {
                predicates.add(likeAnyIgnoreCase(cb,
                        (d != null ? d : root.join("demand"))
                                .join("lob", JoinType.LEFT)
                                .get(ATTR_LOB),
                        f.getLobNames()));
            }

            if (hasList(f.getHbuNames())) {
                predicates.add(likeAnyIgnoreCase(cb,
                        (d != null ? d : root.join("demand"))
                                .join("hbu", JoinType.LEFT)
                                .get(ATTR_HBU),
                        f.getHbuNames()));
            }

            if (hasList(f.getHiringManagerNames())) {
                predicates.add(likeAnyIgnoreCase(cb,
                        (d != null ? d : root.join("demand"))
                                .join("hiringManager", JoinType.LEFT)
                                .get(ATTR_HIRING_MANAGER),
                        f.getHiringManagerNames()));
            }

            // --------------------------------------------------
            // Profile-side filters
            // --------------------------------------------------
            if (hasText(f.getCandidateName())) {
                predicates.add(cb.like(
                        cb.lower((p != null ? p : root.join("profile"))
                                .get("candidateName")),
                        like(f.getCandidateName())));
            }

            if (hasText(f.getEmpId())) {
                predicates.add(cb.equal(
                        cb.lower((p != null ? p : root.join("profile")).get("empId")),
                        f.getEmpId().trim().toLowerCase()));
            }

            if (hasText(f.getEmailId())) {
                predicates.add(cb.equal(
                        cb.lower((p != null ? p : root.join("profile")).get("emailId")),
                        f.getEmailId().trim().toLowerCase()));
            }

            if (hasList(f.getExternalInternalNames())) {
                predicates.add(likeAnyIgnoreCase(cb,
                        (p != null ? p : root.join("profile"))
                                .join("externalInternal", JoinType.LEFT)
                                .get(ATTR_EXTERNAL_INTERNAL),
                        f.getExternalInternalNames()));
            }

            // --------------------------------------------------
            // Tracker-side MULTI‑SELECT masters
            // --------------------------------------------------
            if (hasList(f.getEvaluationStatusNames())) {
                predicates.add(likeAnyIgnoreCase(cb,
                        root.join("evaluationStatus", JoinType.LEFT)
                                .get(ATTR_EVALUATION_STATUS),
                        f.getEvaluationStatusNames()));
            }

            if (hasList(f.getProfileTrackerStatusNames())) {
                predicates.add(likeAnyIgnoreCase(cb,
                        root.join("profileTrackerStatus", JoinType.LEFT)
                                .get(ATTR_PROFILE_TRACKER_STATUS),
                        f.getProfileTrackerStatusNames()));
            }

            // --------------------------------------------------
            // Dates (inclusive)
            // --------------------------------------------------
            addDateRange(cb, predicates, root.get("profileSharedDate"),
                    f.getProfileSharedDateFrom(), f.getProfileSharedDateTo());

            addDateRange(cb, predicates, root.get("interviewDate"),
                    f.getInterviewDateFrom(), f.getInterviewDateTo());

            addDateRange(cb, predicates, root.get("attachedDate"),
                    f.getAttachedDateFrom(), f.getAttachedDateTo());

            addDateRange(cb, predicates, root.get("decisionDate"),
                    f.getDecisionDateFrom(), f.getDecisionDateTo());

            // --------------------------------------------------
            // Demand skills & locations (ANY match via EXISTS)
            // --------------------------------------------------
            if (hasList(f.getDemandPrimarySkillNames())) {
                predicates.add(anyDemandPrimarySkillExists(
                        root, query, cb, f.getDemandPrimarySkillNames()));
            }

            if (hasList(f.getDemandSecondarySkillNames())) {
                predicates.add(anyDemandSecondarySkillExists(
                        root, query, cb, f.getDemandSecondarySkillNames()));
            }

            if (hasList(f.getDemandLocationNames())) {
                predicates.add(anyDemandLocationNameExists(
                        root, query, cb, f.getDemandLocationNames()));
            }

            if (hasList(f.getDemandLocationIds())) {
                predicates.add(anyDemandLocationIdExists(
                        root, query, cb, f.getDemandLocationIds()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /* =====================================================
       EXISTS helpers for Demand M:N (ANY semantics)
       ===================================================== */

    private static Predicate anyDemandPrimarySkillExists(
            Root<ProfileTracker> root, CriteriaQuery<?> outer,
            CriteriaBuilder cb, List<String> names) {

        Subquery<Long> sq = outer.subquery(Long.class);
        Root<AddDemand> d = sq.from(AddDemand.class);
        Join<AddDemand, PrimarySkills> ps =
                d.join("primarySkills", JoinType.INNER);

        sq.select(d.get("id")).where(
                cb.equal(d, root.get("demand")),
                cb.or(names.stream()
                        .filter(ProfileTrackerSpecifications::hasText)
                        .map(s -> cb.like(
                                cb.lower(ps.get("primarySkills")),
                                like(s)))
                        .toArray(Predicate[]::new))
        );
        return cb.exists(sq);
    }

    private static Predicate anyDemandSecondarySkillExists(
            Root<ProfileTracker> root, CriteriaQuery<?> outer,
            CriteriaBuilder cb, List<String> names) {

        Subquery<Long> sq = outer.subquery(Long.class);
        Root<AddDemand> d = sq.from(AddDemand.class);
        Join<AddDemand, SecondarySkills> ss =
                d.join("secondarySkills", JoinType.INNER);

        sq.select(d.get("id")).where(
                cb.equal(d, root.get("demand")),
                cb.or(names.stream()
                        .filter(ProfileTrackerSpecifications::hasText)
                        .map(s -> cb.like(
                                cb.lower(ss.get("secondarySkills")),
                                like(s)))
                        .toArray(Predicate[]::new))
        );
        return cb.exists(sq);
    }

    private static Predicate anyDemandLocationNameExists(
            Root<ProfileTracker> root, CriteriaQuery<?> outer,
            CriteriaBuilder cb, List<String> names) {

        Subquery<Long> sq = outer.subquery(Long.class);
        Root<AddDemand> d = sq.from(AddDemand.class);
        Join<AddDemand, Location> loc =
                d.join("demandLocations", JoinType.INNER);

        sq.select(d.get("id")).where(
                cb.equal(d, root.get("demand")),
                cb.or(names.stream()
                        .filter(ProfileTrackerSpecifications::hasText)
                        .map(s -> cb.like(
                                cb.lower(loc.get("name")),
                                like(s)))
                        .toArray(Predicate[]::new))
        );
        return cb.exists(sq);
    }

    private static Predicate anyDemandLocationIdExists(
            Root<ProfileTracker> root, CriteriaQuery<?> outer,
            CriteriaBuilder cb, List<Long> ids) {

        if (ids == null || ids.isEmpty()) return cb.conjunction();

        Subquery<Long> sq = outer.subquery(Long.class);
        Root<AddDemand> d = sq.from(AddDemand.class);
        Join<AddDemand, Location> loc =
                d.join("demandLocations", JoinType.INNER);

        sq.select(d.get("id")).where(
                cb.equal(d, root.get("demand")),
                loc.get("id").in(ids)
        );
        return cb.exists(sq);
    }

    /* =====================================================
       Helpers
       ===================================================== */

    private static Predicate likeAnyIgnoreCase(
            CriteriaBuilder cb, Path<String> path, List<String> values) {

        List<Predicate> ors = new ArrayList<>();
        for (String v : values) {
            if (hasText(v)) {
                ors.add(cb.like(cb.lower(path), like(v)));
            }
        }
        return ors.isEmpty() ? cb.conjunction() : cb.or(ors.toArray(new Predicate[0]));
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static boolean hasList(List<?> l) {
        return l != null && !l.isEmpty();
    }

    private static String like(String v) {
        return "%" + v.trim().toLowerCase() + "%";
    }

    private static void addDateRange(
            CriteriaBuilder cb,
            List<Predicate> preds,
            Path<LocalDate> path,
            LocalDate from,
            LocalDate to) {

        if (from != null && to != null) {
            preds.add(cb.between(path, from, to));
        } else if (from != null) {
            preds.add(cb.greaterThanOrEqualTo(path, from));
        } else if (to != null) {
            preds.add(cb.lessThanOrEqualTo(path, to));
        }
    }
}