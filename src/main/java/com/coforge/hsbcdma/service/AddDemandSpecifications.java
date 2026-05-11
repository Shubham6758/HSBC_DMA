package com.coforge.hsbcdma.service;

import com.coforge.hsbcdma.dto.DemandsDTO.DemandsFilterRequest;
import com.coforge.hsbcdma.entity.AddDemand;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class AddDemandSpecifications {

    private AddDemandSpecifications() {}

    private static final String ID = "id";

    // ---- ManyToMany relation names (from AddDemand entity) ----
    private static final String REL_PRIMARY_SKILLS = "primarySkills";
    private static final String REL_SECONDARY_SKILLS = "secondarySkills";
    private static final String REL_LOCATIONS = "demandLocations";

    // =========================================================
    // ENTRY POINT
    // =========================================================
    public static Specification<AddDemand> build(DemandsFilterRequest f) {
        return Specification
                .where(byIds(f))
                .and(byNames(f));
    }

    // =========================================================
    // ID / SCALAR BASED FILTERS
    // =========================================================
    public static Specification<AddDemand> byIds(DemandsFilterRequest f) {
        return (root, query, cb) -> {
            query.distinct(true);
            if (f == null) return cb.conjunction();

            List<Predicate> p = new ArrayList<>();

            // ---- Scalars ----
            if (f.getFlag() != null)
                p.add(cb.equal(root.get("flag"), f.getFlag()));

            if (f.getIsSubconRR() != null)
                p.add(cb.equal(root.get("isSubconRR"), f.getIsSubconRR()));

            if (f.getKaratFlag() != null)
                p.add(cb.equal(root.get("karatFlag"), f.getKaratFlag()));

            if (f.getDemandId() != null)
                p.add(cb.equal(root.get("demandId"), f.getDemandId()));

            if (f.getRrNumber() != null)
                p.add(cb.equal(root.get("rrNumber"), f.getRrNumber()));

            // ---- Date ranges ----
            if (f.getReceivedFrom() != null)
                p.add(cb.greaterThanOrEqualTo(
                        root.get("demandReceivedDate"), f.getReceivedFrom()));

            if (f.getReceivedTo() != null)
                p.add(cb.lessThanOrEqualTo(
                        root.get("demandReceivedDate"), f.getReceivedTo()));

            if (f.getP1FlagDateFrom() != null)
                p.add(cb.greaterThanOrEqualTo(
                        root.get("p1FlagDate"), f.getP1FlagDateFrom()));

            if (f.getP1FlagDateTo() != null)
                p.add(cb.lessThanOrEqualTo(
                        root.get("p1FlagDate"), f.getP1FlagDateTo()));

            // ---- ManyToOne IDs ----
            addEqId(p, cb, root, "hbu", f.getHbuId());
            addEqId(p, cb, root, "hbuSpoc", f.getHbuSpocId());
            addEqId(p, cb, root, "band", f.getBandId());
            addEqId(p, cb, root, "priority", f.getPriorityId());
            addEqId(p, cb, root, "status", f.getStatusId());
            addEqId(p, cb, root, "lob", f.getLobId());
            addEqId(p, cb, root, "subLob", f.getSubLobId());
            addEqId(p, cb, root, "pod", f.getPodId());
            addEqId(p, cb, root, "skillCluster", f.getSkillClusterId());
            addEqId(p, cb, root, "projectManager", f.getProjectManagerId());
            addEqId(p, cb, root, "pmo", f.getPmoId());
            addEqId(p, cb, root, "pmoSpoc", f.getPmoSpocId());
            addEqId(p, cb, root, "salesSpoc", f.getSalesSpocId());
            addEqId(p, cb, root, "hiringManager", f.getHiringManagerId());
            addEqId(p, cb, root, "deliveryManager", f.getDeliveryManagerId());
            addEqId(p, cb, root, "demandType", f.getDemandTypeId());
            addEqId(p, cb, root, "externalInternal", f.getExternalInternalId());

            // ---- ManyToMany ID filters (ANY) ----
            joinIds(p, root, REL_PRIMARY_SKILLS, f.getPrimarySkillIds());
            joinIds(p, root, REL_SECONDARY_SKILLS, f.getSecondarySkillIds());
            joinIds(p, root, REL_LOCATIONS, f.getLocationIds());

            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    // =========================================================
    // NAME BASED (DROPDOWN) FILTERS
    // =========================================================
    public static Specification<AddDemand> byNames(DemandsFilterRequest f) {
        return (root, query, cb) -> {
            query.distinct(true);
            if (f == null) return cb.conjunction();

            List<Predicate> p = new ArrayList<>();

            // ---- ManyToOne dropdowns (OR inside column) ----
            addLikeAny(p, cb, root, "hbu", "hbu", f.getHbuNames());
            addLikeAny(p, cb, root, "hbuSpoc", "hbuSpoc", f.getHbuSpocNames());
            addLikeAny(p, cb, root, "band", "band", f.getBandNames());
            addLikeAny(p, cb, root, "priority", "priority", f.getPriorityNames());
            addLikeAny(p, cb, root, "status", "status", f.getStatusNames());
            addLikeAny(p, cb, root, "lob", "lob", f.getLobNames());
            addLikeAny(p, cb, root, "subLob", "subLob", f.getSubLobNames());
            addLikeAny(p, cb, root, "pod", "pod", f.getPodNames());
            addLikeAny(p, cb, root, "skillCluster", "skillCluster", f.getSkillClusterNames());

            addLikeAny(p, cb, root, "projectManager", "projectManager", f.getProjectManagerNames());
            addLikeAny(p, cb, root, "pmo", "pmo", f.getPmoNames());
            addLikeAny(p, cb, root, "pmoSpoc", "pmoSpoc", f.getPmoSpocNames());
            addLikeAny(p, cb, root, "salesSpoc", "salesSpoc", f.getSalesSpocNames());
            addLikeAny(p, cb, root, "hiringManager", "hiringManager", f.getHiringManagerNames());
            addLikeAny(p, cb, root, "deliveryManager", "deliveryManager", f.getDeliveryManagerNames());

            addLikeAny(p, cb, root, "demandType", "demandType", f.getDemandTypeNames());
            addLikeAny(p, cb, root, "externalInternal", "externalInternal", f.getExternalInternalNames());

            addLikeAny(p, cb, root,
                    "demandTimeline",
                    "demandTimeLine",
                    f.getDemandTimelineNames()
            );

            // ---- ManyToMany dropdowns ----
            likeJoinAny(p, cb, root, REL_PRIMARY_SKILLS, "primarySkills", f.getPrimarySkillNames());
            likeJoinAny(p, cb, root, REL_SECONDARY_SKILLS, "secondarySkills", f.getSecondarySkillNames());
            likeJoinAny(p, cb, root, REL_LOCATIONS, "name", f.getLocationNames());

            // ---- Experience (string) ----
            if (hasText(f.getExperienceRange())) {
                p.add(cb.like(
                        cb.lower(root.get("experience")),
                        "%" + f.getExperienceRange().trim().toLowerCase() + "%"
                ));
            }
//            if (f.getExperienceFrom() != null && f.getExperienceTo() != null) {
//
//                Expression<Integer> expStart =
//                        cb.function(
//                                "CAST",
//                                Integer.class,
//                                cb.function(
//                                        "SUBSTRING_INDEX",
//                                        String.class,
//                                        root.get("experience"),
//                                        cb.literal("-"),
//                                        cb.literal(1)
//                                )
//                        );
//
//                Expression<Integer> expEnd =
//                        cb.function(
//                                "CAST",
//                                Integer.class,
//                                cb.function(
//                                        "SUBSTRING_INDEX",
//                                        String.class,
//                                        root.get("experience"),
//                                        cb.literal("-"),
//                                        cb.literal(-1)
//                                )
//                        );
//
//                p.add(cb.and(
//                        cb.lessThanOrEqualTo(expStart, f.getExperienceTo()),
//                        cb.greaterThanOrEqualTo(expEnd, f.getExperienceFrom())
//                ));
//            }

            // ---- Keyword ----
            if (hasText(f.getKeyword())) {
                String like = "%" + f.getKeyword().trim().toLowerCase() + "%";
                p.add(cb.or(
                        cb.like(cb.lower(root.get("fileName")), like),
                        cb.like(cb.lower(root.get("remark")), like)
                ));
            }

            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    // =========================================================
    // HELPERS
    // =========================================================
    private static void addEqId(List<Predicate> p, CriteriaBuilder cb,
                                Root<AddDemand> r, String rel, Long id) {
        if (id != null) {
            p.add(cb.equal(r.get(rel).get(ID), id));
        }
    }

    private static void joinIds(List<Predicate> p, Root<AddDemand> r,
                                String rel, List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        p.add(r.join(rel, JoinType.INNER).get(ID).in(ids));
    }

    private static void addLikeAny(List<Predicate> p, CriteriaBuilder cb,
                                   Root<AddDemand> r,
                                   String rel, String field,
                                   List<String> values) {

        if (!hasTextList(values)) return;

        List<Predicate> ors = new ArrayList<>();
        for (String v : values) {
            ors.add(cb.like(
                    cb.lower(r.get(rel).get(field)),
                    "%" + v.trim().toLowerCase() + "%"
            ));
        }
        p.add(cb.or(ors.toArray(new Predicate[0])));
    }

    private static void likeJoinAny(List<Predicate> p, CriteriaBuilder cb,
                                    Root<AddDemand> r,
                                    String rel, String field,
                                    List<String> values) {

        if (!hasTextList(values)) return;

        Join<Object, Object> j = r.join(rel, JoinType.INNER);
        List<Predicate> ors = new ArrayList<>();
        for (String v : values) {
            ors.add(cb.like(
                    cb.lower(j.get(field)),
                    "%" + v.trim().toLowerCase() + "%"
            ));
        }
        p.add(cb.or(ors.toArray(new Predicate[0])));
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static boolean hasTextList(List<String> list) {
        return list != null && list.stream().anyMatch(AddDemandSpecifications::hasText);
    }
}