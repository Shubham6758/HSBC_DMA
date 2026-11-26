package com.coforge.hsbcdma.service.ProfileServices;

import com.coforge.hsbcdma.dto.ProfilesDTO.ProfilesFilterRequest;
import com.coforge.hsbcdma.entity.Profile;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ProfilesSpecifications {

    private ProfilesSpecifications() {}

    private static final String ID_FIELD = "id";
    private static final String NAME_FIELD = "name";
    private static final String REL_PRIMARY_SKILLS = "primarySkills";
    private static final String REL_SECONDARY_SKILLS = "secondarySkills";

    // ================== ENTRY ==================
    public static Specification<Profile> build(ProfilesFilterRequest f) {
        return Specification
                .where(byIds(f))
                .and(byNames(f));
    }

    // ================== ID / SCALAR FILTERS ==================
    public static Specification<Profile> byIds(ProfilesFilterRequest f) {
        return (root, query, cb) -> {
            query.distinct(true);
            if (f == null) return cb.conjunction();

            List<Predicate> predicates = new ArrayList<>();

            if (f.getId() != null)
                predicates.add(cb.equal(root.get("id"), f.getId()));

            if (f.getIsActive() != null)
                predicates.add(cb.equal(root.get("isActive"), f.getIsActive()));

            if (hasText(f.getCandidateName()))
                predicates.add(cb.like(
                        cb.lower(root.get("candidateName")),
                        likePattern(f.getCandidateName())
                ));

            if (hasText(f.getEmailId()))
                predicates.add(cb.equal(
                        cb.lower(root.get("emailId")),
                        f.getEmailId().trim().toLowerCase()
                ));

            if (hasText(f.getEmpId()))
                predicates.add(cb.equal(
                        cb.lower(root.get("empId")),
                        f.getEmpId().trim().toLowerCase()
                ));

            if (hasText(f.getSapId()))
                predicates.add(cb.equal(
                        cb.lower(root.get("sapId")),
                        f.getSapId().trim().toLowerCase()
                ));

            if (f.getPhoneNumber() != null)
                predicates.add(cb.equal(root.get("phoneNumber"), f.getPhoneNumber()));

            // ---- Experience ----
            addExperience(predicates, cb, root, f);

            // ---- ManyToOne IDs ----
            addEqId(predicates, cb, root, "skillCluster", f.getSkillClusterId());
            addEqId(predicates, cb, root, "location", f.getLocationId());
            addEqId(predicates, cb, root, "hbu", f.getHbuId());
            addEqId(predicates, cb, root, "externalInternal", f.getExternalInternalId());

            // ---- ManyToMany IDs ----
            if (hasLongList(f.getPrimarySkillIds())) {
                Join<Object, Object> j = root.join(REL_PRIMARY_SKILLS, JoinType.INNER);
                predicates.add(j.get("id").in(f.getPrimarySkillIds()));
            }

            if (hasLongList(f.getSecondarySkillIds())) {
                Join<Object, Object> j = root.join(REL_SECONDARY_SKILLS, JoinType.INNER);
                predicates.add(j.get("id").in(f.getSecondarySkillIds()));
            }

            // ---- Ageing / Billing ----
            if (f.getMinAgeing() != null)
                predicates.add(cb.ge(root.get("ageing"), f.getMinAgeing()));

            if (f.getMaxAgeing() != null)
                predicates.add(cb.le(root.get("ageing"), f.getMaxAgeing()));

            if (f.getMinBillingRate() != null)
                predicates.add(cb.ge(root.get("minBillingRate"), f.getMinBillingRate()));

            if (f.getMaxBillingRate() != null)
                predicates.add(cb.le(root.get("minBillingRate"), f.getMaxBillingRate()));

            // ✅ TEXT FIELDS
            addLike(predicates, cb, root.get("karatReadiness"), f.getKaratReadiness());
            addLike(predicates, cb, root.get("lobShared"), f.getLobShared());
            addLike(predicates, cb, root.get("practice"), f.getPractice());
//            addLike(predicates, cb, root.get("band"), f.getBand());
            addLike(predicates, cb, root.get("ageingRange"), f.getAgeingRange());
            addLike(predicates, cb, root.get("codes"), f.getCodes());
            addLike(predicates, cb, root.get("codeType"), f.getCodeType());

            //karat
            addEqId(predicates, cb, root, "persona", f.getPersonaId());
            addEqId(predicates, cb, root, "prepStatus", f.getPrepStatusId());
            addEqId(predicates, cb, root, "coderpadResult1", f.getCoderpadResult1Id());
            addEqId(predicates, cb, root, "coderpadResult2", f.getCoderpadResult2Id());
            addEqId(predicates, cb, root, "karatReadyStatus", f.getKaratReadyStatusId());
            addEqId(predicates, cb, root, "karatTestResult", f.getKaratTestResultId());

            if (f.getMinKaratAgeing() != null) {
                predicates.add(cb.ge(
                        cb.function("DATEDIFF", Integer.class,
                                root.get("karatTestDate"),
                                root.get("karatReadyYesDate")
                        ),
                        f.getMinKaratAgeing()
                ));
            }

            if (f.getL1InterviewDate() != null) {
                predicates.add(cb.equal(
                        root.get("l1InterviewDate"),
                        f.getL1InterviewDate()
                ));
            }

            addLike(predicates, cb, root.get("negotiableNpLwd"), f.getNegotiableNpLwd());

            // ---- Mentor Buddy ----
            addLike(predicates, cb, root.get("mentorBuddy"), f.getMentorBuddy());

            // ---- Start Date ----
            if (f.getStartDate() != null) {
                predicates.add(
                        cb.equal(
                                root.get("startDate"),
                                f.getStartDate()
                        )
                );
            }

            // ---- Planned End Date ----
            if (f.getPlannedEndDate() != null) {
                predicates.add(
                        cb.equal(
                                root.get("plannedEndDate"),
                                f.getPlannedEndDate()
                        )
                );
            }

            // ---- Karat Test Date ----
            if (f.getKaratTestDate() != null) {
                predicates.add(
                        cb.equal(
                                root.get("karatTestDate"),
                                f.getKaratTestDate()
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ================== NAME‑BASED (DROPDOWN) ==================
    public static Specification<Profile> byNames(ProfilesFilterRequest f) {
        return (root, query, cb) -> {
            query.distinct(true);
            if (f == null) return cb.conjunction();

            List<Predicate> predicates = new ArrayList<>();

            // ---- ManyToOne dropdowns (MULTI ✅) ----
            addLikeAnyIgnoreCase(predicates, cb, root, "skillCluster", "skillCluster", f.getSkillClusterNames());
            addLikeAnyIgnoreCase(predicates, cb, root, "location", "name", f.getLocationNames());
            addLikeAnyIgnoreCase(predicates, cb, root, "hbu", "hbu", f.getHbuNames());
            addLikeAnyIgnoreCase(predicates, cb, root, "externalInternal", "externalInternal", f.getExternalInternalNames());
            addLikeAnyIgnoreCase(predicates,cb,root,"profileStatus","name",f.getProfileStatusNames());
            // ---- ManyToMany dropdowns ----
            if (hasTextList(f.getPrimarySkillNames())) {
                Join<Object, Object> j = root.join(REL_PRIMARY_SKILLS, JoinType.INNER);
                predicates.add(likeAnyIgnoreCase(cb, j.get("primarySkills"), f.getPrimarySkillNames()));
            }

            if (hasTextList(f.getSecondarySkillNames())) {
                Join<Object, Object> j = root.join(REL_SECONDARY_SKILLS, JoinType.INNER);
                predicates.add(likeAnyIgnoreCase(cb, j.get("secondarySkills"), f.getSecondarySkillNames()));
            }

            // ---- RDG masters dropdowns ----
            addLikeAnyIgnoreCase(predicates, cb, root, "origin", "name", f.getOriginNames());
            addLikeAnyIgnoreCase(predicates, cb, root, "karatStatus", "name", f.getKaratStatusNames());
            addLikeAnyIgnoreCase(predicates, cb, root, "source", "name", f.getSourceNames());
            addLikeAnyIgnoreCase(predicates, cb, root, "overallStatusRdg", "name", f.getOverallStatusRdgNames());

            // ---- Text filters ----
            addLike(predicates, cb, root.get("recruiter"), f.getRecruiter());
            addLike(predicates, cb, root.get("currentLocation"), f.getCurrentLocation());
            addLike(predicates, cb, root.get("officialNP"), f.getOfficialNP());

            //karat

            addLikeAnyIgnoreCase(predicates, cb, root, "persona", "name", f.getPersonaNames());
            addLikeAnyIgnoreCase(predicates, cb, root, "prepStatus", "name", f.getPrepStatusNames());
            addLikeAnyIgnoreCase(predicates, cb, root, "coderpadResult1", "name", f.getCoderpadResult1Names());
            addLikeAnyIgnoreCase(predicates, cb, root, "coderpadResult2", "name", f.getCoderpadResult2Names());
            addLikeAnyIgnoreCase(predicates, cb, root, "karatReadyStatus", "name", f.getKaratReadyStatusNames());
            addLikeAnyIgnoreCase(predicates, cb, root, "karatTestResult", "name", f.getKaratTestResultNames());

            if (f.getMinKaratAgeing() != null) {
                predicates.add(cb.ge(
                        cb.function("DATEDIFF", Integer.class,
                                root.get("karatTestDate"),
                                root.get("karatReadyYesDate")
                        ),
                        f.getMinKaratAgeing()
                ));
            }

            if (hasTextList(f.getBand())) {
                predicates.add(
                        likeAnyIgnoreCase(cb, root.get("band"), f.getBand())
                );
            }

            if (f.getDateOfSubmission() != null) {
                predicates.add(cb.equal(
                        root.get("dateOfSubmission"),
                        f.getDateOfSubmission()
                ));
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ================== HELPERS ==================

    private static void addExperience(List<Predicate> p, CriteriaBuilder cb, Root<?> r, ProfilesFilterRequest f) {
        if (f.getMinExperience() != null)
            p.add(cb.ge(r.get("experience"), f.getMinExperience()));
        if (f.getMaxExperience() != null)
            p.add(cb.le(r.get("experience"), f.getMaxExperience()));
    }

    private static void addEqId(List<Predicate> p, CriteriaBuilder cb, Root<?> r, String join, Long id) {
        if (id != null)
            p.add(cb.equal(r.get(join).get("id"), id));
    }

    private static void addLike(List<Predicate> p, CriteriaBuilder cb, Path<String> field, String val) {
        if (hasText(val))
            p.add(cb.like(cb.lower(field), likePattern(val)));
    }

    /**
     * ✅ MULTI‑SELECT for column dropdown
     * OR inside column, AND outside
     */
    private static void addLikeAnyIgnoreCase(
            List<Predicate> p,
            CriteriaBuilder cb,
            Root<Profile> root,
            String relation,
            String field,
            List<String> values
    ) {
        if (!hasTextList(values)) return;

        List<Predicate> likes = new ArrayList<>();
        for (String v : values) {
            likes.add(cb.like(
                    cb.lower(root.get(relation).get(field)),
                    likePattern(v)
            ));
        }
        p.add(cb.or(likes.toArray(new Predicate[0])));
    }

    private static Predicate likeAnyIgnoreCase(
            CriteriaBuilder cb,
            Path<String> field,
            List<String> values
    ) {
        List<Predicate> likes = new ArrayList<>();
        for (String v : values) {
            if (hasText(v))
                likes.add(cb.like(cb.lower(field), likePattern(v)));
        }
        return likes.isEmpty()
                ? cb.conjunction()
                : cb.or(likes.toArray(new Predicate[0]));
    }

    private static String likePattern(String s) {
        return "%" + s.trim().toLowerCase() + "%";
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static boolean hasTextList(List<String> list) {
        return list != null && list.stream().anyMatch(ProfilesSpecifications::hasText);
    }

    private static boolean hasLongList(List<Long> list) {
        return list != null && list.stream().anyMatch(Objects::nonNull);
    }
}