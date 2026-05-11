package com.coforge.hsbcdma.service.ProfileServices;

import com.coforge.hsbcdma.dto.ProfilesDTO.ProfilesFilterRequest;
import com.coforge.hsbcdma.entity.Profile;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ProfilesSpecifications {

    private ProfilesSpecifications() {}

    private static final String ID_FIELD = "id";
    private static final String NAME_FIELD = "name";
    private static final String REL_PRIMARY_SKILLS = "primarySkills";
    private static final String REL_SECONDARY_SKILLS = "secondarySkills";

    private static final Logger logger = LoggerFactory.getLogger(ProfilesSpecifications.class);

    /** Combine both ID-based and Name-based filters */
    public static Specification<Profile> build(ProfilesFilterRequest f) {
        return Specification.where(byIds(f)).and(byNames(f));
    }

    /** Filters based on scalars, ranges, and IDs (ManyToOne & ManyToMany) */
    public static Specification<Profile> byIds(ProfilesFilterRequest f) {
        return (root, query, cb) -> {
            query.distinct(true); // avoid duplicates due to M:N joins
            if (f == null) return cb.conjunction();

            List<Predicate> predicates = new ArrayList<>();
            logger.info("ProfilesSpecifications.byNames -> filter received: {}", f);
            // ---- Scalars ----
            if (f.getId() != null) {
                predicates.add(cb.equal(root.get(ID_FIELD), f.getId()));
            }
            if (f.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), f.getIsActive()));
            }
            if (hasText(f.getCandidateName())) { // case-insensitive contains
                String like = "%" + f.getCandidateName().trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("candidateName")), like));
            }
            if (hasText(f.getEmailId())) { // exact, case-insensitive
                predicates.add(cb.equal(cb.lower(root.get("emailId")), f.getEmailId().trim().toLowerCase()));
            }
            if (hasText(f.getEmpId())) { // exact, case-insensitive
                predicates.add(cb.equal(cb.lower(root.get("empId")), f.getEmpId().trim().toLowerCase()));
            }
            if (hasText(f.getSapId())) { // exact, case-insensitive
                predicates.add(cb.equal(cb.lower(root.get("sapId")), f.getSapId().trim().toLowerCase()));
            }
            if (f.getPhoneNumber() != null) { // exact
                predicates.add(cb.equal(root.get("phoneNumber"), f.getPhoneNumber()));
            }

            // ---- Experience (min/max or "a-b") ----
            addExperiencePredicates(predicates, cb, root, f);

            // ---- ManyToOne by ID ----
            addEqId(predicates, cb, root, "skillCluster", f.getSkillClusterId());
            addEqId(predicates, cb, root, "location", f.getLocationId());
            addEqId(predicates, cb, root, "hbu", f.getHbuId());
            addEqId(predicates, cb, root, "externalInternal", f.getExternalInternalId());

            // ---- ManyToMany by ID (ANY) ----
            if (hasLongList(f.getPrimarySkillIds())) {
                Join<Object, Object> j = root.join(REL_PRIMARY_SKILLS, JoinType.INNER);
                predicates.add(j.get(ID_FIELD).in(f.getPrimarySkillIds()));
            }
            if (hasLongList(f.getSecondarySkillIds())) {
                Join<Object, Object> j = root.join(REL_SECONDARY_SKILLS, JoinType.INNER);
                predicates.add(j.get(ID_FIELD).in(f.getSecondarySkillIds()));
            }

            addEqId(predicates, cb, root, "origin", f.getOriginId());
            addEqId(predicates, cb, root, "karatStatus", f.getKaratStatusId());
            addEqId(predicates, cb, root, "source", f.getSourceId());
            addEqId(predicates, cb, root, "overallStatusRdg", f.getOverallStatusRdgId());

            if (f.getMinAgeing() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ageing"), f.getMinAgeing()));
            }
            if (f.getMaxAgeing() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("ageing"), f.getMaxAgeing()));
            }

            if (f.getMinBillingRate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("minBillingRate"), f.getMinBillingRate()));
            }
            if (f.getMaxBillingRate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("minBillingRate"), f.getMaxBillingRate()));
            }

            // ---- TA exact matches ----
            if (hasText(f.getPanNumber())) {
                predicates.add(cb.equal(
                        cb.lower(root.get("panNumber")),
                        f.getPanNumber().trim().toLowerCase()
                ));
            }

            if (f.getL1InterviewDate() != null) {
                predicates.add(cb.equal(
                        root.get("l1InterviewDate"),
                        f.getL1InterviewDate()
                ));
            }

            if (f.getNegotiableNpLwd() != null) {
                predicates.add(cb.equal(
                        root.get("negotiableNpLwd"),
                        f.getNegotiableNpLwd()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /** Filters based on ManyToOne & ManyToMany names (equals/in, case-insensitive) */
    public static Specification<Profile> byNames(ProfilesFilterRequest f) {
        return (root, query, cb) -> {
            query.distinct(true);
            if (f == null) return cb.conjunction();

            List<Predicate> predicates = new ArrayList<>();
            logger.info("ProfilesSpecifications.byNames -> filter received: {}", f);
            // ---- ManyToOne by NAME (equals-ignore-case on "name") ----
            addLikeIgnoreCase(predicates, cb, root, "skillCluster","skillCluster",f.getSkillClusterName());
            addLikeIgnoreCase(predicates, cb, root, "location", "name",f.getLocationName());
            addLikeIgnoreCase(predicates, cb, root, "hbu", "hbu",f.getHbuName());
            addLikeIgnoreCase(predicates, cb, root, "externalInternal","externalInternal", f.getExternalInternalName());

            // ---- ManyToMany by NAME (ANY, case-insensitive) ----
            if (hasTextList(f.getPrimarySkillNames())) {
                Join<Object, Object> j = root.join("primarySkills", JoinType.INNER);
                predicates.add(likeAnyIgnoreCase(cb, j.get("primarySkills"), f.getPrimarySkillNames()));
            }
            if (hasTextList(f.getSecondarySkillNames())) {
                Join<Object, Object> j = root.join(REL_SECONDARY_SKILLS, JoinType.INNER);
                predicates.add(likeAnyIgnoreCase(cb, j.get(REL_SECONDARY_SKILLS), f.getSecondarySkillNames()));
            }

            // ---------- New Masters (NAME-based search) ----------
            addLikeIgnoreCase(
                    predicates, cb, root,
                    "origin", "name",
                    f.getOriginName()
            );

            addLikeIgnoreCase(
                    predicates, cb, root,
                    "karatStatus", "name",
                    f.getKaratStatusName()
            );

            addLikeIgnoreCase(
                    predicates, cb, root,
                    "source", "name",
                    f.getSourceName()
            );

            addLikeIgnoreCase(
                    predicates, cb, root,
                    "overallStatusRdg", "name",
                    f.getOverallStatusRdgName()
            );

            if (hasText(f.getRecruiter())) {
                predicates.add(cb.like(
                        cb.lower(root.get("recruiter")),
                        "%" + f.getRecruiter().trim().toLowerCase() + "%"
                ));
            }

            if (hasText(f.getCurrentLocation())) {
                predicates.add(cb.like(
                        cb.lower(root.get("currentLocation")),
                        "%" + f.getCurrentLocation().trim().toLowerCase() + "%"
                ));
            }

            if (hasText(f.getOfficialNP())) {
                predicates.add(cb.like(
                        cb.lower(root.get("officialNP")),
                        "%" + f.getOfficialNP().trim().toLowerCase() + "%"
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ================= Helpers =================
    private static void addExperiencePredicates(List<Predicate> predicates, CriteriaBuilder cb, Root<?> root, ProfilesFilterRequest f) {
        if (f.getMinExperience() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("experience"), f.getMinExperience()));
        }
        if (f.getMaxExperience() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("experience"), f.getMaxExperience()));
        }
        if (hasText(f.getExperienceRange())) {
            float[] range = parseRange(f.getExperienceRange().trim());
            if (range != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("experience"), range[0]));
                predicates.add(cb.lessThanOrEqualTo(root.get("experience"), range[1]));
            }
        }
    }

    private static float[] parseRange(String s) {
        // Accepts "5-7", "5.0-7.5" with optional spaces
        Pattern p = Pattern.compile("\\s*([0-9]+(?:\\.[0-9]+)?)\\s*-\\s*([0-9]+(?:\\.[0-9]+)?)\\s*");
        Matcher m = p.matcher(s);
        if (!m.matches()) return null;
        float a = Float.parseFloat(m.group(1));
        float b = Float.parseFloat(m.group(2));
        return new float[]{Math.min(a, b), Math.max(a, b)};
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static boolean hasTextList(List<String> list) {
        if (list == null || list.isEmpty()) return false;
        return list.stream().anyMatch(ProfilesSpecifications::hasText);
    }

    private static boolean hasLongList(List<Long> list) {
        return list != null && !list.isEmpty() && list.stream().anyMatch(Objects::nonNull);
    }

    /** equals-ignore-case on ManyToOne's "name" field */
//    private static void addEqIgnoreCase(List<Predicate> p, CriteriaBuilder cb, Root<?> root,
//                                        String joinAttr, String value) {
//        if (!hasText(value)) return;
//        p.add(cb.equal(cb.lower(root.get(joinAttr).get(NAME_FIELD)), value.trim().toLowerCase()));
//    }

    private static void addEqIgnoreCase(List<Predicate> p, CriteriaBuilder cb, Root<?> root,
                                        String joinAttr, String field, String value) {
        if (!hasText(value)) return;
        p.add(cb.equal(cb.lower(root.get(joinAttr).get(field)), value.trim().toLowerCase()));
    }

    /** IN (lowercase) for names list */
    private static Predicate inIgnoreCase(CriteriaBuilder cb, Path<String> field, List<String> values) {
        CriteriaBuilder.In<String> in = cb.in(cb.lower(field));
        for (String v : values) {
            if (hasText(v)) in.value(v.trim().toLowerCase());
        }
        return in;
    }

    /** root.get(joinAttr).get("id") = value */
    private static void addEqId(List<Predicate> p, CriteriaBuilder cb, Root<?> root,
                                String joinAttr, Long idValue) {
        if (idValue == null) return;
        p.add(cb.equal(root.get(joinAttr).get(ID_FIELD), idValue));
    }



    private static String likePattern(String s) {
        return "%" + s.trim().toLowerCase() + "%";
    }

    /**
     * ManyToOne partial match: root.get(relation).get(field) LIKE %value% (ignore-case)
     */
    private static void addLikeIgnoreCase(List<Predicate> predicates,
                                          CriteriaBuilder cb,
                                          Root<Profile> root,
                                          String relation,
                                          String field,
                                          String value) {
        if (!hasText(value)) return;

        Path<String> p = root.get(relation).get(field);
        predicates.add(cb.like(cb.lower(p), likePattern(value)));
    }

    /**
     * ManyToMany partial match for list: OR( field LIKE %v1%, field LIKE %v2% ...)
     */
    private static Predicate likeAnyIgnoreCase(CriteriaBuilder cb,
                                               Path<String> field,
                                               List<String> values) {

        List<String> cleaned = values == null ? List.of() :
                values.stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();

        if (cleaned.isEmpty()) {
            return cb.conjunction();
        }

        List<Predicate> likes = new ArrayList<>();
        for (String v : cleaned) {
            likes.add(cb.like(cb.lower(field), likePattern(v)));
        }

        return cb.or(likes.toArray(new Predicate[0]));
    }

}
