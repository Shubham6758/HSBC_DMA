package com.coforge.hsbcdma.service.OnboardingServices;

import com.coforge.hsbcdma.audit.AuditHistory;
import com.coforge.hsbcdma.audit.Demand.AddDemandAuditMeta;
import com.coforge.hsbcdma.audit.Demand.AddDemandSnapshotBuilder;
import com.coforge.hsbcdma.audit.Onboarding.OnboardingAuditMeta;
import com.coforge.hsbcdma.audit.Onboarding.OnboardingSnapshotBuilder;
import com.coforge.hsbcdma.audit.ProfileTracker.ProfileTrackerAuditMeta;
import com.coforge.hsbcdma.audit.ProfileTracker.ProfileTrackerSnapshotBuilder;
import com.coforge.hsbcdma.audit.core.GenericAuditService;
import com.coforge.hsbcdma.dto.DemandsDTO.PageResponse;
import com.coforge.hsbcdma.dto.DemandsDTO.RefDTO;
import com.coforge.hsbcdma.dto.OnboardingDTO.*;
import com.coforge.hsbcdma.entity.AddDemand;
import com.coforge.hsbcdma.entity.Onboarding;
import com.coforge.hsbcdma.entity.Profile;
import com.coforge.hsbcdma.entity.ProfileTracker;
import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.BgvStatus;
import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.OnboardingStatus;
import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.WbsType;
import com.coforge.hsbcdma.entity.dropdownEntities.Profile.ProfileTrackerStatus;
import com.coforge.hsbcdma.entity.dropdownEntities.Status;
import com.coforge.hsbcdma.exception.ResourceNotFoundException;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandRepository;
import com.coforge.hsbcdma.repository.OnboardingRepositories.BgvStatusRepository;
import com.coforge.hsbcdma.repository.OnboardingRepositories.OnboardingRepository;
import com.coforge.hsbcdma.repository.OnboardingRepositories.OnboardingStatusRepository;
import com.coforge.hsbcdma.repository.OnboardingRepositories.WbsTypeRepository;
import com.coforge.hsbcdma.repository.ProfileTrackerRepositories.ProfileTrackRepository;
import com.coforge.hsbcdma.repository.ProfileTrackerRepositories.ProfileTrackerStatusRepository;
import com.coforge.hsbcdma.repository.dropdownRepository.StatusRepository;
import com.coforge.hsbcdma.service.ProfileTrackerServices.ProfileTrackService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final OnboardingRepository onboardingRepository;
    private final WbsTypeRepository wbsTypeRepository;
    private final BgvStatusRepository bgvStatusRepository;
    private final OnboardingStatusRepository onboardingStatusRepository;

    private final StatusRepository statusRepository;
    private final ProfileTrackerStatusRepository profileTrackerStatusRepository;

    private final ProfileTrackRepository profileTrackRepository;
    private final AddDemandRepository addDemandRepository;

    private final OnboardingSnapshotBuilder onboardingSnapshotBuilder;
    private final OnboardingAuditMeta onboardingAuditMeta;
    private final GenericAuditService genericAuditService;

    private final AddDemandSnapshotBuilder addDemandSnapshotBuilder;
    private final AddDemandAuditMeta addDemandAuditMeta;

    private final ProfileTrackerSnapshotBuilder profileTrackerSnapshotBuilder;
    private final ProfileTrackerAuditMeta profileTrackerAuditMeta;

    private final ProfileTrackService profileTrackService;


    @Transactional
    public void editOnboarding(Long onboardingId, EditOnboardingRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        Onboarding ob = onboardingRepository.findById(onboardingId)
                .orElseThrow(() -> new IllegalArgumentException("Onboarding not found: " + onboardingId));
//        taking snapshot
        Map<String, Object> oldSnap = onboardingSnapshotBuilder.snapshot(ob);


        // ---- capture old ctool to detect change ----
        Long oldCtoolId = ob.getCtoolId();

        // ---- capture old onboarding status to detect change ----
        OnboardingStatus oldOs = ob.getOnboardingStatus();



        // ---- capture old bgv status ----
        Long oldBgvStatusId = (ob.getBgvStatus() != null) ? ob.getBgvStatus().getId() : null;



        // ---- capture old hsbc onboarding date to detect change ----
        LocalDate oldHsbcOnboardingDate = ob.getHsbcOnboardingDate();



        boolean changed = false;

        // ----- Master refs (update only if non-null) -----
        if (req.getWbsTypeId() != null) {
            WbsType wbs = wbsTypeRepository.findById(req.getWbsTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid wbsTypeId: " + req.getWbsTypeId()));
            ob.setWbsType(wbs);
            changed = true;
        }

        if (req.getBgvStatusId() != null) {
            BgvStatus bgv = bgvStatusRepository.findById(req.getBgvStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid bgvStatusId: " + req.getBgvStatusId()));
            ob.setBgvStatus(bgv);
            changed = true;
        }

        if (req.getOnboardingStatusId() != null) {
            OnboardingStatus os = onboardingStatusRepository.findById(req.getOnboardingStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid onboardingStatusId: " + req.getOnboardingStatusId()));
            ob.setOnboardingStatus(os);
            changed = true;
        }

        // ----- Simple fields (update only if non-null) -----
        changed |= setIfNotNull(req.getOfferDate(), ob::setOfferDate);
        changed |= setIfNotNull(req.getDateOfJoining(), ob::setDateOfJoining);
        changed |= setIfNotNull(req.getCtoolId(), ob::setCtoolId);
        changed |= setIfNotNull(req.getPevUploadDate(), ob::setPevUploadDate);
        changed |= setIfNotNull(req.getVpTagging(), ob::setVpTagging);
        changed |= setIfNotNull(req.getTechSelectDate(), ob::setTechSelectDate);
        changed |= setIfNotNull(req.getHsbcOnboardingDate(), ob::setHsbcOnboardingDate);

//        Detect change
        boolean hsbcOnboardingDateChanged =
                (req.getHsbcOnboardingDate() != null) &&
                        !Objects.equals(oldHsbcOnboardingDate, req.getHsbcOnboardingDate());


        // ---- detect ctool change AFTER potentially setting it ----
        boolean ctoolChanged = (req.getCtoolId() != null) && !Objects.equals(oldCtoolId, req.getCtoolId());

        // ---- Auto rule: if ctool updated, set onboarding status based on profile type ----
        changed |= applyCtoolRuleIfNeeded(ob, ctoolChanged);


        // ---- detect bgv status change AFTER potentially setting it ----
        boolean bgvChanged = (req.getBgvStatusId() != null) && !Objects.equals(oldBgvStatusId, req.getBgvStatusId());



        // ✅ NEW RULE:
        // If BGV becomes REJECTED => set onboarding status to "Abort Red BGV"
        // (Keep this BEFORE completed rule so rejection takes priority)
        changed |= applyBgvRejectedRuleIfNeeded(ob, bgvChanged);

        // ✅ NEW RULE:
        // If BGV becomes COMPLETED => set onboarding status to "DOJ Awaited"
        changed |= applyBgvCompletedRuleIfNeeded(ob, bgvChanged, req);

        // ---- Detect onboarding status change AFTER all above updates (manual + ctool rule) ----
        OnboardingStatus newOs = ob.getOnboardingStatus();
        boolean onboardingStatusChanged =
                (oldOs == null && newOs != null) ||
                        (oldOs != null && newOs != null && !Objects.equals(oldOs.getId(), newOs.getId()));

        // ---- Backout Rule: if status becomes Backout Candidate/Resign -> update PT & Demand ----
        changed |= applyOnboardingBackoutRuleIfNeeded(ob, onboardingStatusChanged, newOs);

        // ✅ NEW RULE: if status becomes Demand Abandoned -> update PT & Demand
        changed |= applyOnboardingDemandAbandonedRuleIfNeeded(ob, onboardingStatusChanged, newOs);


        changed |= applyHsbcOnboardedRuleIfNeeded(ob, hsbcOnboardingDateChanged, req);
        if (!changed) {
            throw new IllegalArgumentException("No editable fields provided");
        }

        // Audit (if BaseEntity has these)
//        ob.setUpdatedByUserId(getCurrentUserId());

        onboardingRepository.save(ob);

        genericAuditService.recordUpdate(

                AuditHistory.EntityType.ONBOARDING,     // entityType
                AuditHistory.Action.UPDATE_ONBOARDING,
                ob,                                     // entity (updated)
                oldSnap,                                 // old snapshot
                onboardingSnapshotBuilder,                // snapshot builder
                onboardingAuditMeta,                      // meta
                getCurrentUserId()                      // us

        );
    }


    /**
     * If ctool changed:
     *  - If profile is External  -> OnboardingStatus = "Awaiting Joining"
     *  - If profile is Internal  -> OnboardingStatus = "Tagging and Tech Selection Pending"
     *  profile tracker status -> client selected
     *  demand status -> candidate shortlisted
     *
     * Returns true if onboarding status was updated.
     */

    private boolean applyCtoolRuleIfNeeded(Onboarding ob, boolean ctoolChanged) {
        if (!ctoolChanged) return false;

        String profileType = null;
        if (ob.getProfile() != null && ob.getProfile().getExternalInternal() != null) {
            profileType = ob.getProfile().getExternalInternal().getName();
        }
        if (profileType == null || profileType.isBlank()) return false;

        String desiredStatusName = switch (profileType.toLowerCase()) {
            case "external" -> "Awaiting Joining";
            case "internal" -> "Tagging and Tech Selection Pending";
            default -> null;
        };
        if (desiredStatusName == null) return false;

        OnboardingStatus desired = onboardingStatusRepository.findByNameIgnoreCase(desiredStatusName)
                .orElseThrow(() -> new IllegalStateException("OnboardingStatus '" + desiredStatusName + "' not found"));

        OnboardingStatus current = ob.getOnboardingStatus();
        if (current == null || !Objects.equals(current.getId(), desired.getId())) {
            // optional: if you want a separate *rule audit* row:
            Map<String, Object> oldSnapLocal = onboardingSnapshotBuilder.snapshot(ob);

            ob.setOnboardingStatus(desired);

            // ==========================================================
            // ✅ DIRECT MAPPING: Update ONLY the mapped ProfileTracker
            // ==========================================================
            if (ob.getProfileTracker() == null || ob.getProfileTracker().getId() == null) {
                throw new IllegalStateException("Onboarding has no mapped ProfileTracker. onboardingId=" + ob.getId());
            }

            Long ptId = ob.getProfileTracker().getId();

            ProfileTracker pt = profileTrackRepository.findById(ptId)
                    .orElseThrow(() -> new IllegalStateException("ProfileTracker not found: " + ptId));

            String ptCurrentStatus = (pt.getProfileTrackerStatus() != null) ? pt.getProfileTrackerStatus().getName() : null;

            if (ptCurrentStatus == null || !ptCurrentStatus.equalsIgnoreCase("Client Selected")) {

                // snapshot for PT audit (optional)
                Map<String, Object> oldPtSnap = profileTrackerSnapshotBuilder.snapshot(pt);

                ProfileTrackerStatus clientSelected = profileTrackerStatusRepository.findByNameIgnoreCase("Client Selected")
                        .orElseThrow(() -> new IllegalStateException("Status 'Client Selected' not found in status master"));

                pt.setProfileTrackerStatus(clientSelected);
                profileTrackRepository.save(pt);

                // ==========================================
                // ⭐ NEW: Update all other profiles of demand
                // ==========================================
                profileTrackService.markOtherProfilesAsAnotherCandidate(
                        pt.getDemand().getId(),
                        pt.getId()
                );

                // ✅ PT audit (optional)
                genericAuditService.recordUpdate(
                        AuditHistory.EntityType.PROFILE_TRACKER,
                        AuditHistory.Action.UPDATE_PROFILE_TRACKER, // use your action if different
                        pt,
                        oldPtSnap,
                        profileTrackerSnapshotBuilder,
                        profileTrackerAuditMeta,
                        getCurrentUserId()
                );


            }

            // Rule-specific audit entry (optional). If you want to avoid double-rows, you can skip this
            // and rely only on the final UPDATE_ONBOARDING audit (which already uses the big oldSnap).
//            genericAuditService.recordUpdate(
//                    AuditHistory.EntityType.ONBOARDING,
//                    AuditHistory.Action.UPDATE_ONBOARDING, // or a custom action like AUTO_STATUS_FROM_CTOOL
//                    ob,
//                    oldSnapLocal,
//                    onboardingSnapshotBuilder,
//                    onboardingAuditMeta,
//                    getCurrentUserId()
//            );


            // ================================================================
            // ⭐ NEW: Update Demand status → "Candidate Shortlisted"
            // ================================================================
            if (pt.getDemand() != null && pt.getDemand().getId() != null) {

                Long demandId = pt.getDemand().getId();

                AddDemand demand = addDemandRepository.findById(demandId)
                        .orElseThrow(() -> new IllegalStateException("Demand not found: " + demandId));

                String demandCurrentStatus = (demand.getStatus() != null)
                        ? demand.getStatus().getName() : null;

                if (demandCurrentStatus == null || !demandCurrentStatus.equalsIgnoreCase("Candidate Shortlisted")) {

                    Map<String, Object> oldDemandSnap = addDemandSnapshotBuilder.snapshot(demand);

                    Status candidateShortlisted = statusRepository.findByStatus("Candidate Shortlisted")
                            .orElseThrow(() -> new IllegalStateException("Status 'Candidate Shortlisted' not found in status master"));

                    demand.setStatus(candidateShortlisted);
                    addDemandRepository.save(demand);

                    genericAuditService.recordUpdate(
                            AuditHistory.EntityType.ADD_DEMAND,
                            AuditHistory.Action.UPDATE_DEMAND,
                            demand,
                            oldDemandSnap,
                            addDemandSnapshotBuilder,
                            addDemandAuditMeta,
                            getCurrentUserId()
                    );
                }
            }

            return true;
        }
        return false;

    }


    /**
     * When OnboardingStatus changes to "Backout Candidate":
     *  - External -> PT: "Backout Before Joining"  + Demand: "Open Positions" (audits)
     *  - Internal -> PT: "Mapped to Other Demand"  + Demand: "Open Positions" (audits)
     *  OT -> Abort Red BGV Pt: Red BGV Demand : Open positions
     *
     * @return true if any update was performed by this rule
     */
    private boolean applyOnboardingBackoutRuleIfNeeded(Onboarding ob,
                                                       boolean onboardingStatusChanged,
                                                       OnboardingStatus newOs) {
        if (!onboardingStatusChanged || newOs == null) return false;

        String name = newOs.getName();
        if (name == null) return false;
        boolean updated = false;

        // ---------- New: Abort Red BGV branch (applies to both External & Internal) ----------
        if (name.equalsIgnoreCase("Abort Red BGV")) {
            // 1) PT -> Red BGV
            ProfileTracker pt = ob.getProfileTracker();
            if (pt != null) {
                ProfileTrackerStatus redBgv = profileTrackerStatusRepository
                        .findByNameIgnoreCase("Red BGV")
                        .orElseThrow(() -> new IllegalStateException(
                                "ProfileTrackerStatus 'Red BGV' not found in master table"));

                boolean needsPtUpdate = (pt.getProfileTrackerStatus() == null) ||
                        !Objects.equals(pt.getProfileTrackerStatus().getId(), redBgv.getId());

                if (needsPtUpdate) {
                    Map<String, Object> oldPtSnap = profileTrackerSnapshotBuilder.snapshot(pt);
                    pt.setProfileTrackerStatus(redBgv);

                    profileTrackRepository.save(pt);

                    genericAuditService.recordUpdate(
                            AuditHistory.EntityType.PROFILE_TRACKER,
                            AuditHistory.Action.UPDATE_PROFILE_TRACKER,
                            pt,
                            oldPtSnap,
                            profileTrackerSnapshotBuilder,
                            profileTrackerAuditMeta,
                            getCurrentUserId()
                    );
                    updated = true;
                }
            }

            // 2) Demand -> Open Positions
            if (ob.getDemand() != null) {
                var demand = ob.getDemand();

                Status openPositions = statusRepository
                        .findByStatus("Open Positions")
                        .orElseThrow(() -> new IllegalStateException(
                                "DemandStatus 'Open Positions' not found in master table"));

                boolean needsDemandUpdate = (demand.getStatus() == null) ||
                        !Objects.equals(demand.getStatus().getId(), openPositions.getId());

                if (needsDemandUpdate) {
                    Map<String, Object> oldDemandSnap = addDemandSnapshotBuilder.snapshot(demand);
                    demand.setStatus(openPositions);
                    demand.setUpdatedByUserId(getCurrentUserId());
                    addDemandRepository.save(demand);

                    genericAuditService.recordUpdate(
                            AuditHistory.EntityType.ADD_DEMAND,
                            AuditHistory.Action.UPDATE_DEMAND,
                            demand,
                            oldDemandSnap,
                            addDemandSnapshotBuilder,
                            addDemandAuditMeta,
                            getCurrentUserId()
                    );
                    updated = true;
                }
            }

            // return after handling Abort Red BGV (no need to evaluate other branches)
            return updated;
        }
        // --------------------------------------------------------------------


        boolean isBackoutOrResign =
                "Backout Candidate".equalsIgnoreCase(name) ||
                        "Resign".equalsIgnoreCase(name);

        if (!isBackoutOrResign) return false;

        // Resolve profile type safely
        String profileType = null;
        if (ob.getProfile() != null && ob.getProfile().getExternalInternal() != null) {
            profileType = ob.getProfile().getExternalInternal().getName(); // adjust getter if different
        }
        

        // Resolve Demand status "Open Positions"
        Status openPositions = statusRepository
                .findByStatus("Open Positions")
                .orElseThrow(() -> new IllegalStateException("DemandStatus 'Open Positions' not found in master table"));

        // ---------- External branch ----------
        if ("External".equalsIgnoreCase(profileType)) {

            // PT -> Backout Before Joining
            ProfileTracker pt = ob.getProfileTracker();
            if (pt != null) {
                ProfileTrackerStatus backoutBeforeJoining = profileTrackerStatusRepository
                        .findByNameIgnoreCase("Backout Before Joining")
                        .orElseThrow(() -> new IllegalStateException(
                                "ProfileTrackerStatus 'Backout Before Joining' not found in master table"));

                boolean needsPtUpdate = (pt.getProfileTrackerStatus() == null) ||
                        !Objects.equals(pt.getProfileTrackerStatus().getId(), backoutBeforeJoining.getId());

                if (needsPtUpdate) {
                    Map<String, Object> oldPtSnap = profileTrackerSnapshotBuilder.snapshot(pt);
                    pt.setProfileTrackerStatus(backoutBeforeJoining);

                    profileTrackRepository.save(pt); // matches your repo name in snippet

                    genericAuditService.recordUpdate(
                            AuditHistory.EntityType.PROFILE_TRACKER,
                            AuditHistory.Action.UPDATE_PROFILE_TRACKER,
                            pt,
                            oldPtSnap,
                            profileTrackerSnapshotBuilder,
                            profileTrackerAuditMeta,
                            getCurrentUserId()
                    );
                    updated = true;
                }
            }

            // Demand -> Open Positions
            if (ob.getDemand() != null) {
                var demand = ob.getDemand();

                boolean needsDemandUpdate = (demand.getStatus() == null) ||
                        !Objects.equals(demand.getStatus().getId(), openPositions.getId());

                if (needsDemandUpdate) {
                    Map<String, Object> oldDemandSnap = addDemandSnapshotBuilder.snapshot(demand);
                    demand.setStatus(openPositions);
                    demand.setUpdatedByUserId(getCurrentUserId());
                    addDemandRepository.save(demand); // matches your repo name in snippet

                    genericAuditService.recordUpdate(
                            AuditHistory.EntityType.ADD_DEMAND,
                            AuditHistory.Action.UPDATE_DEMAND,
                            demand,
                            oldDemandSnap,
                            addDemandSnapshotBuilder,
                            addDemandAuditMeta,
                            getCurrentUserId()
                    );
                    updated = true;
                }
            }

            // ---------- Internal branch ----------
        } else if ("Internal".equalsIgnoreCase(profileType)) {

            // PT -> Mapped to Other Demand
            ProfileTracker pt = ob.getProfileTracker();
            if (pt != null) {
                ProfileTrackerStatus mapped = profileTrackerStatusRepository
                        .findByNameIgnoreCase("Mapped to Other Demand")
                        .orElseThrow(() -> new IllegalStateException(
                                "ProfileTrackerStatus 'Mapped to Other Demand' not found in master table"));

                boolean needsPtUpdate = (pt.getProfileTrackerStatus() == null) ||
                        !Objects.equals(pt.getProfileTrackerStatus().getId(), mapped.getId());

                if (needsPtUpdate) {
                    Map<String, Object> oldPtSnap = profileTrackerSnapshotBuilder.snapshot(pt);
                    pt.setProfileTrackerStatus(mapped);

                    profileTrackRepository.save(pt);

                    genericAuditService.recordUpdate(
                            AuditHistory.EntityType.PROFILE_TRACKER,
                            AuditHistory.Action.UPDATE_PROFILE_TRACKER,
                            pt,
                            oldPtSnap,
                            profileTrackerSnapshotBuilder,
                            profileTrackerAuditMeta,
                            getCurrentUserId()
                    );
                    updated = true;
                }
            }

            // Demand -> Open Positions
            if (ob.getDemand() != null) {
                var demand = ob.getDemand();

                boolean needsDemandUpdate = (demand.getStatus() == null) ||
                        !Objects.equals(demand.getStatus().getId(), openPositions.getId());

                if (needsDemandUpdate) {
                    Map<String, Object> oldDemandSnap = addDemandSnapshotBuilder.snapshot(demand);
                    demand.setStatus(openPositions);
                    demand.setUpdatedByUserId(getCurrentUserId());
                    addDemandRepository.save(demand);

                    genericAuditService.recordUpdate(
                            AuditHistory.EntityType.ADD_DEMAND,
                            AuditHistory.Action.UPDATE_DEMAND,
                            demand,
                            oldDemandSnap,
                            addDemandSnapshotBuilder,
                            addDemandAuditMeta,
                            getCurrentUserId()
                    );
                    updated = true;
                }
            }
        }

        return updated;
    }



    /**
     * ✅ NEW: If BGV changed and becomes COMPLETED => set onboarding status to DOJ Awaited.
     *
     * Priority rule implemented:x
     * - If user explicitly sends onboardingStatusId in request, don't override.
     *   (If you want override ALWAYS, remove that check.)
     */
    /**
     * ✅ If BGV changed and becomes "Completed" => ALWAYS set onboarding status to "DOJ Awaited".
     */
    private boolean applyBgvCompletedRuleIfNeeded(Onboarding ob, boolean bgvChanged, EditOnboardingRequest req) {
        if (!bgvChanged) return false;

        BgvStatus bgv = ob.getBgvStatus();
        if (bgv == null) return false;

        if (!isBgvCompleted(bgv)) return false;

        // ✅ Take local snapshot BEFORE applying rule change
        Map<String, Object> oldSnapLocal = onboardingSnapshotBuilder.snapshot(ob);


        OnboardingStatus dojAwaited = onboardingStatusRepository
                .findByNameIgnoreCase("DOJ Awaited")
                .orElseThrow(() -> new IllegalArgumentException("OnboardingStatus not found: DOJ Awaited"));

        OnboardingStatus current = ob.getOnboardingStatus();
        if (current != null && Objects.equals(current.getId(), dojAwaited.getId())) {
            return false; // already DOJ Awaited
        }

        // ✅ OVERRIDE: regardless of req.getOnboardingStatusId()
        ob.setOnboardingStatus(dojAwaited);


        // ✅ Rule-level audit entry
//        genericAuditService.recordUpdate(
//                AuditHistory.EntityType.ONBOARDING,
//                AuditHistory.Action.UPDATE_ONBOARDING, // better: create AUTO_BGV_COMPLETED action if you want
//                ob,
//                oldSnapLocal,
//                onboardingSnapshotBuilder,
//                onboardingAuditMeta,
//                getCurrentUserId()
//        );

        return true;
    }

    private boolean applyBgvRejectedRuleIfNeeded(Onboarding ob, boolean bgvChanged) {
        if (!bgvChanged) return false;

        if (ob.getBgvStatus() == null) return false;

        // Change this getter if your BgvStatus uses a different field like getStatusName()/getCode()
        String bgvName = ob.getBgvStatus().getName();

        if (bgvName == null || bgvName.isBlank()) return false;

        // If BGV is REJECTED -> set onboarding status to Abort Red BGV
        if (!"Rejected".equalsIgnoreCase(bgvName.trim())) return false;


        // ✅ Take local snapshot BEFORE applying rule change
        Map<String, Object> oldSnapLocal = onboardingSnapshotBuilder.snapshot(ob);


        OnboardingStatus abortRedBgv = onboardingStatusRepository
                .findByNameIgnoreCase("Abort Red BGV")
                .orElseThrow(() -> new IllegalArgumentException("OnboardingStatus not found: Abort Red BGV"));

        OnboardingStatus current = ob.getOnboardingStatus();
        if (current == null || !Objects.equals(current.getId(), abortRedBgv.getId())) {
            ob.setOnboardingStatus(abortRedBgv);
            return true;
        }


        // ✅ Rule-level audit entry
//        genericAuditService.recordUpdate(
//                AuditHistory.EntityType.ONBOARDING,
//                AuditHistory.Action.UPDATE_ONBOARDING, // better: AUTO_BGV_REJECTED
//                ob,
//                oldSnapLocal,
//                onboardingSnapshotBuilder,
//                onboardingAuditMeta,
//                getCurrentUserId()
//        );

        return false;
    }

    private boolean isBgvCompleted(BgvStatus bgv) {
        // Prefer code if present:
        // return "COMPLETED".equalsIgnoreCase(bgv.getCode());

        String name = bgv.getName();
        return name != null && name.equalsIgnoreCase("Completed");
    }



    /**
     * ✅ If HSBC onboarding date is set/changed => ALWAYS set onboarding status to "Onboarded"
     * Keeps similar signature for consistency with your other rule methods.
     */
    private boolean applyHsbcOnboardedRuleIfNeeded(Onboarding ob, boolean hsbcOnboardingDateChanged, EditOnboardingRequest req) {
        if (!hsbcOnboardingDateChanged) return false;

        // If date is present on entity, enforce status
        if (ob.getHsbcOnboardingDate() == null) return false;

        OnboardingStatus onboarded = onboardingStatusRepository
                .findByNameIgnoreCase("Awaiting Onboarding")
                .orElseThrow(() -> new IllegalArgumentException("OnboardingStatus not found: Onboarded"));

        OnboardingStatus current = ob.getOnboardingStatus();
        if (current != null && Objects.equals(current.getId(), onboarded.getId())) {
            return false; // already Onboarded
        }

        // ✅ OVERRIDE (even if req.getOnboardingStatusId() is sent)
        ob.setOnboardingStatus(onboarded);
        return true;
    }



    private boolean applyOnboardingDemandAbandonedRuleIfNeeded(Onboarding ob,
                                                               boolean onboardingStatusChanged,
                                                               OnboardingStatus newOs) {
        if (!onboardingStatusChanged || newOs == null || newOs.getName() == null) {
            return false;
        }

        // trigger only when onboarding status is Demand Abandoned
        if (!"Demand Abandoned".equalsIgnoreCase(newOs.getName().trim())) {
            return false;
        }

        boolean changed = false;

        // -------------------------
        // Resolve Demand by demandId
        // -------------------------
        Long demandId = ob.getDemand() != null ?ob.getDemand().getId() : null;
        if (demandId == null) {
            // choose strictness: throw or skip
            throw new IllegalStateException("Onboarding " + ob.getId() + " has null demandId");
        }

        AddDemand demand = addDemandRepository.findById(demandId)
                .orElseThrow(() -> new IllegalArgumentException("Demand not found: " + demandId));

        // -------------------------
        // Resolve ProfileTracker by profileTrackerId
        // -------------------------
        Long ptId = ob.getProfileTracker() != null ? ob.getProfileTracker().getId() : null;
        ProfileTracker pt = null;
        if (ptId != null) {
            pt = profileTrackRepository.findById(ptId)
                    .orElseThrow(() -> new IllegalArgumentException("ProfileTracker not found: " + ptId));
        } else {
            // If PT id must exist for onboarding, make it strict:
            throw new IllegalStateException("Onboarding " + ob.getId() + " has null profileTrackerId");
            // Or if optional, just skip PT update:
            // pt = null;
        }

        // -------------------------
        // Update Demand.status -> Demand Abandoned
        // -------------------------
        // IMPORTANT: avoid NonUniqueResultException if status table has duplicates
        Status demandAbandoned = statusRepository
                .findByStatus("Demand Abandoned")
                .orElseThrow(() -> new IllegalArgumentException("Status not found: Demand Abandoned"));

        if (demand.getStatus() == null || !Objects.equals(demand.getStatus().getId(), demandAbandoned.getId())) {
            Map<String, Object> oldDemandSnap = addDemandSnapshotBuilder.snapshot(demand);

            demand.setStatus(demandAbandoned);
            addDemandRepository.save(demand);
            changed = true;

            genericAuditService.recordUpdate(
                    AuditHistory.EntityType.ADD_DEMAND,
                    AuditHistory.Action.UPDATE_DEMAND,
                    demand,
                    oldDemandSnap,
                    addDemandSnapshotBuilder,
                    addDemandAuditMeta,
                    getCurrentUserId()
            );
        }

        // -------------------------
        // Update ProfileTracker.profileTrackerStatus -> Demand Abandoned
        // -------------------------
        ProfileTrackerStatus ptAbandoned = profileTrackerStatusRepository
                .findByNameIgnoreCase("Demand Abandoned")
                .orElseThrow(() -> new IllegalArgumentException("ProfileTrackerStatus not found: Demand Abandoned"));

        if (pt.getProfileTrackerStatus() == null ||
                !Objects.equals(pt.getProfileTrackerStatus().getId(), ptAbandoned.getId())) {

            Map<String, Object> oldPtSnap = profileTrackerSnapshotBuilder.snapshot(pt);

            pt.setProfileTrackerStatus(ptAbandoned);
            profileTrackRepository.save(pt);
            changed = true;

            genericAuditService.recordUpdate(
                    AuditHistory.EntityType.PROFILE_TRACKER,
                    AuditHistory.Action.UPDATE_PROFILE_TRACKER,
                    pt,
                    oldPtSnap,
                    profileTrackerSnapshotBuilder,
                    profileTrackerAuditMeta,
                    getCurrentUserId()
            );
        }

        return changed;
    }









    public Page<OnboardingResponseDTO> getAllOnboardings(Pageable pageable) {

        Page<Onboarding> onboardingPage =
                onboardingRepository.findAll(pageable);

        return onboardingPage.map(this::convertToDTO);
    }


    public PageResponse search(OnboardingFilterRequest filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Onboarding> obPage = onboardingRepository.findAll(OnboardingSpecifications.byFilter(filter), pageable);

        if (obPage.isEmpty()) {
            throw new ResourceNotFoundException("No onboarding records found for given filters");
        }

        List<GetAllOnboardingDTO> content = obPage.getContent()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PageResponse(
                content,
                obPage.isEmpty(),
                obPage.isFirst(),
                obPage.isLast(),
                obPage.getSize(),
                obPage.getTotalElements(),
                obPage.getTotalPages()
        );
    }
    private GetAllOnboardingDTO toDto(Onboarding ob) {
        Profile p = ob.getProfile();
        AddDemand d = ob.getDemand();
        ProfileTracker t = ob.getProfileTracker();

        return GetAllOnboardingDTO.builder()
                .id(ob.getId())
                .ctoolId(ob.getCtoolId())
                .onboardingStatusName(ob.getOnboardingStatus() != null ? ob.getOnboardingStatus().getName() : null)
                .bgvStatusName(ob.getBgvStatus() != null ? ob.getBgvStatus().getName() : null)
                .wbsTypeName(ob.getWbsType() != null ? ob.getWbsType().getName() : null)
                .offerDate(ob.getOfferDate())
                .dateOfJoining(ob.getDateOfJoining())
                .pevUploadDate(ob.getPevUploadDate())
                .vpTagging(ob.getVpTagging())
                .techSelectDate(ob.getTechSelectDate())
                .hsbcOnboardingDate(ob.getHsbcOnboardingDate())

                .profileId(p != null ? p.getId() : null)
                .candidateName(p != null ? p.getCandidateName() : null)
                .empId(p != null ? p.getEmpId() : null)
                .externalInternalName(p != null && p.getExternalInternal() != null ? p.getExternalInternal().getExternalInternal() : null)

                .demandPkId(d != null ? d.getId() : null)
                .demandNumber(d != null ? d.getDemandId() : null)
                .lobName(d != null && d.getLob() != null ? d.getLob().getLob() : null)
                .bandName(d != null && d.getBand() != null ? d.getBand().getBand() : null)
                .hiringManagerName(d != null && d.getHiringManager() != null ? d.getHiringManager().getHiringManager() : null)
                .hbuName(d != null && d.getHbu() != null ? d.getHbu().getHbu() : null)
                .pmoSpocName(d != null && d.getPmoSpoc() != null ? d.getPmoSpoc().getPmoSpoc() : null)

                .profileSharedDate(t != null ? t.getProfileSharedDate() : null)
                .build();
    }

    private OnboardingResponseDTO convertToDTO(Onboarding onboarding) {


        ProfileTracker tracker = onboarding.getProfileTracker();

        AddDemand demandEntity = tracker.getDemand();
        Profile profileEntity = tracker.getProfile();

//        String lobName = (demandEntity.getLob() != null && demandEntity.getLob().getName() != null)
//                ? demandEntity.getLob().getName()
//                : "";
//
//        String displayDemandId = lobName.isBlank()
//                ? String.valueOf(demandEntity.getDemandId())
//                : lobName + "-" + demandEntity.getDemandId();

        String prefix = resolveDemandPrefix(demandEntity);
        String displayDemandId = prefix + "-" + demandEntity.getDemandId();

        return OnboardingResponseDTO.builder()

                .onboardingId(onboarding.getId())

                .offerDate(onboarding.getOfferDate())
                .dateOfJoining(onboarding.getDateOfJoining())

                .ctoolId(onboarding.getCtoolId())

                .pevUploadDate(onboarding.getPevUploadDate())
                .vpTagging(onboarding.getVpTagging())
                .techSelectDate(onboarding.getTechSelectDate())
                .hsbcOnboardingDate(onboarding.getHsbcOnboardingDate())
//changed
                .bgvStatus(onboarding.getBgvStatus() != null
                        ? RefDTO.builder()
                        .id(onboarding.getBgvStatus().getId())
                        .name(onboarding.getBgvStatus().getName())
                        .build()
                        : null)
                .onboardingStatus(onboarding.getOnboardingStatus() != null
                        ? RefDTO.builder()
                        .id(onboarding.getOnboardingStatus().getId())
                        .name(onboarding.getOnboardingStatus().getName())
                        .build()
                        : null)
                .wbsType(onboarding.getWbsType() != null
                        ? RefDTO.builder()
                        .id(onboarding.getWbsType().getId())
                        .name(onboarding.getWbsType().getName())
                        .build()
                        : null)

                .demand(OnboardingDemandDTO.builder()
                        .demandId(demandEntity.getDemandId())
                        .displayDemandId(displayDemandId)
                        .lob(demandEntity.getLob() != null
                                ? demandEntity.getLob().getName()
                                : null)

                        .band(demandEntity.getBand() != null
                                ? demandEntity.getBand().getName()
                                : null)

                        .pmoSpoc(demandEntity.getPmo() != null
                                ? demandEntity.getPmo().getName()
                                : null)

                        .hiringManager(demandEntity.getHiringManager() != null
                                ? demandEntity.getHiringManager().getName()
                                : null)
                        .hbu(demandEntity.getHbu() != null
                                ? demandEntity.getHbu().getName()
                                : null)

                        .build()
                )

                .profile(OnboardingProfileDTO.builder()
                        .profileId(profileEntity.getId())
                        .empId(profileEntity.getEmpId())
                        .candidateName(profileEntity.getCandidateName())
                        .externalInternal(profileEntity.getExternalInternal() != null
                                ? profileEntity.getExternalInternal().getExternalInternal()
                                : null)
                        .build())

                .profileTracker(OnboardingProfileTrackerDTO.builder()
                        .profileTrackerId(tracker.getId())
                        .profileSharedDate(tracker.getProfileSharedDate())
                        .build())

                .build();
    }

    public OnboardingDropdownResponse getOnboardingDropdowns(){
        return OnboardingDropdownResponse.builder()
                .wbsTypes(
                        wbsTypeRepository.findAll().stream()
                                .map(w -> new RefDTO(w.getId(), w.getName()))
                                .collect(Collectors.toList())
                )
                .bgvStatuses(
                        bgvStatusRepository.findAll().stream()
                                .map(b -> new RefDTO(b.getId(), b.getName()))
                                .collect(Collectors.toList())
                )
                .onboardingStatuses(
                        onboardingStatusRepository.findAll().stream()
                                .map(o -> new RefDTO(o.getId(), o.getName()))
                                .collect(Collectors.toList())
                )
                .build();

    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        return auth.getName(); // this is userId because you set it as username
    }

    // Small helper for "update if not null"
    private static <T> boolean setIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
            return true;
        }
        return false;
    }

    private String resolveDemandPrefix(AddDemand d) {

        if (d == null) return "NA";

        // Prefer SUB-LOB if present
        if (d.getSubLob() != null && d.getSubLob().getSubLob() != null) {
            return d.getSubLob().getSubLob().trim();   // WSIT, MSS
        }

        // Fallback to LOB
        if (d.getLob() != null && d.getLob().getLob() != null) {
            return d.getLob().getLob().trim();         // CIM, BFS...
        }

        return "NA";
    }
}