package com.coforge.hsbcdma.service.OnboardingServices;

// package com.coforge.hsbcdma.spec;

import com.coforge.hsbcdma.dto.OnboardingDTO.OnboardingFilterRequest;
import com.coforge.hsbcdma.entity.AddDemand;
import com.coforge.hsbcdma.entity.Onboarding;
import com.coforge.hsbcdma.entity.Profile;
import com.coforge.hsbcdma.entity.ProfileTracker;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class OnboardingSpecifications {

    private OnboardingSpecifications() {}

    /* ========= Master attribute names (match your entities!) ========= */
    // Profile-side
    private static final String ATTR_EXTERNAL_INTERNAL     = "externalInternal";   // ExternalInternal.externalInternal

    // Demand-side (same canon as your ProfileTracker spec)
    private static final String ATTR_LOB                   = "lob";                // Lob.lob
    private static final String ATTR_BAND                  = "band";               // Band.band
    private static final String ATTR_HBU                   = "hbu";                // Hbu.hbu
    private static final String ATTR_HIRING_MANAGER        = "hiringManager";      // HiringManager.hiringManager
    private static final String ATTR_PMO_SPOC              = "pmoSpoc";            // PmoSpoc.pmoSpoc

    // Onboarding-side masters
    private static final String ATTR_ONBOARDING_STATUS     = "onboardingStatus";   // OnboardingStatus.onboardingStatus
    private static final String ATTR_BGV_STATUS            = "bgvStatus";          // BgvStatus.bgvStatus
    private static final String ATTR_WBS_TYPE              = "wbsType";            // WbsType.wbsType

    public static Specification<Onboarding> byFilter(OnboardingFilterRequest f) {
        return (root, query, cb) -> {

            if (query.getResultType() != Long.class) {
                query.distinct(true);
            }
            List<Predicate> predicates = new ArrayList<>();
            if (f == null) return cb.conjunction();

            // Decide needed joins
            boolean needsProfile =
                    hasText(f.getCandidateName()) ||
                            hasText(f.getEmpId()) ||
                            hasText(f.getExternalInternal());

            boolean needsDemand =
                    f.getDemandId() != null ||
                            hasText(f.getLob()) ||
                            hasText(f.getBand()) ||
                            hasText(f.getHiringManager()) ||
                            hasText(f.getHbu()) ||
                            hasText(f.getPmoSpoc());

            boolean needsTracker =
                    f.getProfileSharedDate() != null ||
                            f.getProfileSharedDateFrom() != null ||
                            f.getProfileSharedDateTo() != null;

            final Join<Onboarding, Profile>       p  = needsProfile  ? root.join("profile", JoinType.INNER) : null;
            final Join<Onboarding, AddDemand>     d  = needsDemand   ? root.join("demand", JoinType.INNER) : null;
            final Join<Onboarding, ProfileTracker> t = needsTracker  ? root.join("profileTracker", JoinType.INNER) : null;

            // ----- Profile -----
            if (hasText(f.getCandidateName())) {
                predicates.add(cb.like(
                        cb.lower((p != null ? p : root.join("profile")).get("candidateName")),
                        like(f.getCandidateName())));
            }
            if (hasText(f.getEmpId())) {
                predicates.add(cb.equal(
                        cb.lower((p != null ? p : root.join("profile")).get("empId")),
                        f.getEmpId().trim().toLowerCase()));
            }
            if (hasText(f.getExternalInternal())) {
                predicates.add(cb.like(
                        cb.lower((p != null ? p : root.join("profile"))
                                .join("externalInternal", JoinType.LEFT)
                                .get(ATTR_EXTERNAL_INTERNAL)),
                        like(f.getExternalInternal())));
            }

            // ----- Demand -----
            if (f.getDemandId() != null) {
                predicates.add(cb.equal((d != null ? d : root.join("demand")).get("demandId"), f.getDemandId()));
            }
            if (hasText(f.getLob())) {
                predicates.add(cb.like(
                        cb.lower((d != null ? d : root.join("demand"))
                                .join("lob", JoinType.LEFT)
                                .get(ATTR_LOB)),
                        like(f.getLob())));
            }
            if (hasText(f.getBand())) {
                predicates.add(cb.like(
                        cb.lower((d != null ? d : root.join("demand"))
                                .join("band", JoinType.LEFT)
                                .get(ATTR_BAND)),
                        like(f.getBand())));
            }
            if (hasText(f.getHiringManager())) {
                predicates.add(cb.like(
                        cb.lower((d != null ? d : root.join("demand"))
                                .join("hiringManager", JoinType.LEFT)
                                .get(ATTR_HIRING_MANAGER)),
                        like(f.getHiringManager())));
            }
            if (hasText(f.getHbu())) {
                predicates.add(cb.like(
                        cb.lower((d != null ? d : root.join("demand"))
                                .join("hbu", JoinType.LEFT)
                                .get(ATTR_HBU)),
                        like(f.getHbu())));
            }
            if (hasText(f.getPmoSpoc())) {
                predicates.add(cb.like(
                        cb.lower((d != null ? d : root.join("demand"))
                                .join("pmoSpoc", JoinType.LEFT)
                                .get(ATTR_PMO_SPOC)),
                        like(f.getPmoSpoc())));
            }

            // ----- Onboarding masters -----
            if (hasText(f.getOnboardingStatus())) {
                predicates.add(cb.like(
                        cb.lower(root.join("onboardingStatus", JoinType.LEFT).get(ATTR_ONBOARDING_STATUS)),
                        like(f.getOnboardingStatus())));
            }
            if (hasText(f.getBgvStatus())) {
                predicates.add(cb.like(
                        cb.lower(root.join("bgvStatus", JoinType.LEFT).get(ATTR_BGV_STATUS)),
                        like(f.getBgvStatus())));
            }
            if (hasText(f.getWbsType())) {
                predicates.add(cb.like(
                        cb.lower(root.join("wbsType", JoinType.LEFT).get(ATTR_WBS_TYPE)),
                        like(f.getWbsType())));
            }

            // ----- Onboarding scalar -----
            if (f.getCtoolId() != null) {
                predicates.add(cb.equal(root.get("ctoolId"), f.getCtoolId()));
            }

            // ----- Onboarding dates (eq has precedence; else inclusive range) -----
            addDateEqOrRange(cb, predicates, root.get("offerDate"),
                    f.getOfferDate(), f.getOfferDateFrom(), f.getOfferDateTo());
            addDateEqOrRange(cb, predicates, root.get("dateOfJoining"),
                    f.getDateOfJoining(), f.getDateOfJoiningFrom(), f.getDateOfJoiningTo());
            addDateEqOrRange(cb, predicates, root.get("pevUploadDate"),
                    f.getPevUploadDate(), f.getPevUploadDateFrom(), f.getPevUploadDateTo());
            addDateEqOrRange(cb, predicates, root.get("vpTagging"),
                    f.getVpTagging(), f.getVpTaggingFrom(), f.getVpTaggingTo());
            addDateEqOrRange(cb, predicates, root.get("techSelectDate"),
                    f.getTechSelectDate(), f.getTechSelectDateFrom(), f.getTechSelectDateTo());
            addDateEqOrRange(cb, predicates, root.get("hsbcOnboardingDate"),
                    f.getHsbcOnboardingDate(), f.getHsbcOnboardingDateFrom(), f.getHsbcOnboardingDateTo());

            // ----- ProfileTracker date (from onboarding) -----
            if (f.getProfileSharedDate() != null ||
                    f.getProfileSharedDateFrom() != null ||
                    f.getProfileSharedDateTo() != null) {
                Path<LocalDate> path = (t != null ? t : root.join("profileTracker")).get("profileSharedDate");
                addDateEqOrRange(cb, predicates, path,
                        f.getProfileSharedDate(), f.getProfileSharedDateFrom(), f.getProfileSharedDateTo());
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /* ========================= helpers ========================= */

    private static boolean hasText(String s) { return s != null && !s.trim().isEmpty(); }
    private static String like(String input) { return "%" + input.trim().toLowerCase() + "%"; }

    private static void addDateEqOrRange(CriteriaBuilder cb, List<Predicate> preds, Path<LocalDate> path,
                                         LocalDate eq, LocalDate from, LocalDate to) {
        if (eq != null) {
            preds.add(cb.equal(path, eq));
            return;
        }
        if (from != null && to != null) {
            preds.add(cb.between(path, from, to));
        } else if (from != null) {
            preds.add(cb.greaterThanOrEqualTo(path, from));
        } else if (to != null) {
            preds.add(cb.lessThanOrEqualTo(path, to));
        }
    }
}
