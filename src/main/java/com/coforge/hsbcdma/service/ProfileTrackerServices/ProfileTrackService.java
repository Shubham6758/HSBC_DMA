package com.coforge.hsbcdma.service.ProfileTrackerServices;

import com.coforge.hsbcdma.audit.AuditHistory;
import com.coforge.hsbcdma.audit.Demand.AddDemandAuditMeta;
import com.coforge.hsbcdma.audit.Demand.AddDemandSnapshotBuilder;
import com.coforge.hsbcdma.audit.ProfileTracker.ProfileTrackerAuditMeta;
import com.coforge.hsbcdma.audit.ProfileTracker.ProfileTrackerAuditService;
import com.coforge.hsbcdma.audit.ProfileTracker.ProfileTrackerSnapshotBuilder;
import com.coforge.hsbcdma.audit.core.GenericAuditService;
import com.coforge.hsbcdma.dto.DemandsDTO.PageResponse;
import com.coforge.hsbcdma.dto.DemandsDTO.RefDTO;
import com.coforge.hsbcdma.dto.ProfileTrackDTO.*;
import com.coforge.hsbcdma.entity.*;
import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.OnboardingStatus;
import com.coforge.hsbcdma.entity.dropdownEntities.Profile.EvaluationStatus;
import com.coforge.hsbcdma.entity.dropdownEntities.Profile.ProfileTrackerStatus;
import com.coforge.hsbcdma.entity.dropdownEntities.Status;
import com.coforge.hsbcdma.exception.ResourceNotFoundException;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandRepository;
import com.coforge.hsbcdma.repository.OnboardingRepositories.OnboardingRepository;
import com.coforge.hsbcdma.repository.OnboardingRepositories.OnboardingStatusRepository;
import com.coforge.hsbcdma.repository.ProfileRepositories.ProfileRepository;
import com.coforge.hsbcdma.repository.ProfileTrackerRepositories.EvaluationStatusRepository;
import com.coforge.hsbcdma.repository.ProfileTrackerRepositories.ProfileTrackRepository;
import com.coforge.hsbcdma.repository.ProfileTrackerRepositories.ProfileTrackerStatusRepository;
import com.coforge.hsbcdma.repository.UserAccountRepository;
import com.coforge.hsbcdma.repository.dropdownRepository.StatusRepository;
import com.coforge.hsbcdma.service.EmailService;
import com.coforge.hsbcdma.service.NotificationServices.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileTrackService {

    private final ProfileTrackRepository trackerRepository;
    private final AddDemandRepository demandRepository;
    private final ProfileRepository profileRepository;
    private final UserAccountRepository userAccountRepository;
    private final EvaluationStatusRepository evaluationStatusRepository;
    private final ProfileTrackerStatusRepository profileTrackerStatusRepository;
    private final OnboardingRepository onboardingRepository;
    private final OnboardingStatusRepository onboardingStatusRepository;
    private final ProfileTrackerAuditService profileTrackerAuditService;
    private final StatusRepository statusRepository;

    private final ProfileTrackerSnapshotBuilder profileTrackerSnapshotBuilder;
    private final GenericAuditService genericAuditService;
    private final ProfileTrackerAuditMeta profileTrackerAuditMeta;


    private final AddDemandSnapshotBuilder addDemandSnapshotBuilder;
    private final AddDemandAuditMeta addDemandAuditMeta;

    private final EmailService emailService;

    private final NotificationService notificationService;

    /**
     * attach many profiles to one demand.
     */
    @Transactional
    public String attachProfilesToOneDemand(AttachProfilesToDemandRequest req) {

        // 1) Validate request
        if (req == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (req.getDemandPkId() == null) {
            throw new IllegalArgumentException("demandPkId is required");
        }
        if (req.getProfileIds() == null || req.getProfileIds().isEmpty()) {
            throw new IllegalArgumentException("profileIds are required");
        }

        // 2) Normalize profileIds (remove nulls + duplicates)
        List<Long> requestedProfileIds = req.getProfileIds().stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (requestedProfileIds.isEmpty()) {
            throw new IllegalArgumentException("profileIds cannot be empty/null values");
        }

        // 3) Load demand
        AddDemand demand = demandRepository.findById(req.getDemandPkId())
                .orElseThrow(() -> new RuntimeException("Demand not found: " + req.getDemandPkId()));

        // 4) Load all profiles in one query
        List<Profile> profiles = profileRepository.findAllById(requestedProfileIds);
        Set<Long> foundProfileIds = profiles.stream().map(Profile::getId).collect(Collectors.toSet());

        // 5) Fail fast if any profileIds are missing (production safe)
        List<Long> missingProfileIds = requestedProfileIds.stream()
                .filter(id -> !foundProfileIds.contains(id))
                .toList();

        if (!missingProfileIds.isEmpty()) {
            throw new RuntimeException("Profiles not found: " + missingProfileIds);
        }

        // 6) Pre-check existing mappings for this demand
        List<ProfileTracker> existing = trackerRepository
                .findAllByDemand_IdAndProfile_IdIn(req.getDemandPkId(), requestedProfileIds);

        Set<Long> alreadyAttachedProfileIds = existing.stream()
                .map(pt -> pt.getProfile().getId())
                .collect(Collectors.toSet());

        // 7) Compute which profileIds to create
        List<Long> toCreateProfileIds = requestedProfileIds.stream()
                .filter(id -> !alreadyAttachedProfileIds.contains(id))
                .toList();

        // 8) Build new ProfileTrack rows
        ProfileTrackerStatus attachedStatus = getDefaultAttachedStatusOrThrow();
        List<ProfileTracker> tracksToSave = profiles.stream()
                .filter(p -> toCreateProfileIds.contains(p.getId()))
                .map(p -> {
                    ProfileTracker pt = new ProfileTracker();
                    pt.setDemand(demand);
                    pt.setProfile(p);
                    pt.setProfileTrackerStatus(attachedStatus);
                    pt.setAttachedDate(LocalDate.now());

                    // Optional audit field
                    pt.setCreatedByUserId(getCurrentUserId());
                    return pt;
                })
                .toList();

        // 9) Bulk save
        trackerRepository.saveAll(tracksToSave);

        // 9b) Send notifications for newly attached profiles
        try {
            for (ProfileTracker pt : tracksToSave) {

                Profile profile = pt.getProfile();
                //AddDemand demand = pt.getDemand();

                notificationService.notifyAllUsersExceptActor(
                        "PROFILE_TRACKER",
                        pt.getId(), // reference: profile_tracker.id
                        "Profile Attached to Demand",
                        "Profile " + profile.getCandidateName()
                                + " has been attached to Demand " + resolveDemandPrefix(demand) + "-"
                                + demand.getDemandId(),
                        getCurrentUserId()
                );
            }
        } catch (Exception e) {
            log.warn("Failed to create attachment notifications: {}", e.getMessage());
        }


        // 10) Prepare response
        List<Long> skipped = requestedProfileIds.stream()
                .filter(alreadyAttachedProfileIds::contains)
                .toList();


//        for (ProfileTracker pt : tracksToSave) {
//            profileTrackerAuditService.recordAttach(pt, getCurrentUserId(), null);
//        }
//        for (ProfileTracker pt : tracksToSave) {
//            profileTrackerAuditService.recordAttach1(pt);
//        }


        for (ProfileTracker pt : tracksToSave) {
            genericAuditService.recordAttach(
                    AuditHistory.EntityType.PROFILE_TRACKER,
                    pt,
                    profileTrackerSnapshotBuilder,
                    profileTrackerAuditMeta,
                    getCurrentUserId()
            );
        }



        return "Profiles Attached to Demand Successfully";
    }



    @Transactional
    public String attachDemandsToOneProfile(AttachDemandsToProfileRequest req) {

        // 1) Validate request
        if (req == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (req.getProfilePkId() == null) {
            throw new IllegalArgumentException("profilePkId is required");
        }
        if (req.getDemandIds() == null || req.getDemandIds().isEmpty()) {
            throw new IllegalArgumentException("demandIds are required");
        }

        // 2) Normalize demandIds
        List<Long> requestedDemandIds = req.getDemandIds().stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (requestedDemandIds.isEmpty()) {
            throw new IllegalArgumentException("demandIds cannot be empty/null values");
        }

        // 3) Load profile
        Profile profile = profileRepository.findById(req.getProfilePkId())
                .orElseThrow(() -> new RuntimeException("Profile not found: " + req.getProfilePkId()));

        // 4) Load all demands in one query
        List<AddDemand> demands = demandRepository.findAllById(requestedDemandIds);
        Set<Long> foundDemandIds = demands.stream().map(AddDemand::getId).collect(Collectors.toSet());

        // 5) Fail fast if any demandIds are missing
        List<Long> missingDemandIds = requestedDemandIds.stream()
                .filter(id -> !foundDemandIds.contains(id))
                .toList();
        if (!missingDemandIds.isEmpty()) {
            throw new RuntimeException("Demands not found: " + missingDemandIds);
        }

        // 6) Pre-check existing mappings for this profile
        List<ProfileTracker> existing = trackerRepository
                .findAllByProfile_IdAndDemand_IdIn(req.getProfilePkId(), requestedDemandIds);

        Set<Long> alreadyAttachedDemandIds = existing.stream()
                .map(pt -> pt.getDemand().getId())
                .collect(Collectors.toSet());

        // 7) Compute which demandIds to create
        List<Long> toCreateDemandIds = requestedDemandIds.stream()
                .filter(id -> !alreadyAttachedDemandIds.contains(id))
                .toList();


        // 8) Build new ProfileTracker rows
        ProfileTrackerStatus attachedStatus = getDefaultAttachedStatusOrThrow();
        List<ProfileTracker> tracksToSave = demands.stream()
                .filter(d -> toCreateDemandIds.contains(d.getId()))
                .map(d -> {
                    ProfileTracker pt = new ProfileTracker();
                    pt.setDemand(d);
                    pt.setProfile(profile);
                    pt.setProfileTrackerStatus(attachedStatus);
                    pt.setAttachedDate(LocalDate.now());

                    // Optional audit field
                    pt.setCreatedByUserId(getCurrentUserId());
                    return pt;
                })
                .toList();

        // 9) Bulk save
        trackerRepository.saveAll(tracksToSave);
        // 9b) Send notifications for newly attached demands
        try {
            for (ProfileTracker pt : tracksToSave) {

                //Profile profile = pt.getProfile();
                AddDemand demand = pt.getDemand();

                notificationService.notifyAllUsersExceptActor(
                        "PROFILE_TRACKER",
                        pt.getId(),
                        "Profile Attached to Demand",
                        "Profile " + profile.getCandidateName()
                                + " has been attached to Demand " + resolveDemandPrefix(demand) + "-"
                                + demand.getDemandId(),
                        getCurrentUserId()
                );
            }
        } catch (Exception e) {
            log.warn("Failed to create attachment notifications: {}", e.getMessage());
        }


        // 10) Prepare response (you can return a richer object if needed)
        List<Long> skipped = requestedDemandIds.stream()
                .filter(alreadyAttachedDemandIds::contains)
                .toList();

        for (ProfileTracker pt : tracksToSave) {
            genericAuditService.recordAttach(
                    AuditHistory.EntityType.PROFILE_TRACKER,
                    pt,
                    profileTrackerSnapshotBuilder,
                    profileTrackerAuditMeta,
                    getCurrentUserId()
            );
        }

        return "Demands Attached to Profile Successfully";
    }

    private ProfileTrackerStatus getDefaultAttachedStatusOrThrow() {
        return profileTrackerStatusRepository.findByNameIgnoreCase("Attached")
                .orElseThrow(() -> new IllegalStateException(
                        "ProfileTrackerStatus 'Attached' not found. Please seed it in profile_tracker_status table."));
    }


    @Transactional(readOnly = true)
    public List<ProfileSharedResponseDTO> getProfilesByDemandId(Long demandId) {
        List<ProfileTracker> trackers = trackerRepository.findByDemand_Id(demandId);

        if (trackers == null || trackers.isEmpty()) {
            return Collections.emptyList();
        }

        return trackers.stream()
                .map(this::toProfileSharedResponse)
                .toList();

    }
    // mapper for profile shared response
    private ProfileSharedResponseDTO toProfileSharedResponse(ProfileTracker tracker) {
        Profile p = tracker.getProfile();

        return ProfileSharedResponseDTO.builder()
                .id(tracker.getId())
                .profileSharedDate(tracker.getProfileSharedDate())
                .attachedDate(tracker.getAttachedDate())
                .createdAt(tracker.getCreatedAt())
                .profileId(p != null ? p.getId() : null)
                .candidateName(p != null ? p.getCandidateName() : null)
                .experience(p != null ? p.getExperience() : null)
                .primarySkills(toRefList(p.getPrimarySkills()))
                .secondarySkills(toRefList(p.getSecondarySkills()))
                .hbu(toRef(p.getHbu()))
                .profileTrackerStatus(toRef(tracker.getProfileTrackerStatus()))
                .evaluationStatus(toRef(tracker.getEvaluationStatus()))
                .build();
    }



    @Transactional(readOnly = true)
    public List<DemandSharedResponseDTO> getDemandsByProfileId(Long profileId) {

        List<ProfileTracker> trackers = trackerRepository.findByProfile_Id(profileId);

        if (trackers == null || trackers.isEmpty()) {
            return Collections.emptyList();
        }

        return trackers.stream()
                .map(this::toDemandSharedResponse)
                .toList();
    }

    private DemandSharedResponseDTO toDemandSharedResponse(ProfileTracker tracker) {
        AddDemand d = tracker.getDemand();

        return DemandSharedResponseDTO.builder()
                .id(tracker.getId())
                .attachedDate(tracker.getAttachedDate())
                .createdAt(tracker.getCreatedAt())
//                .demandId(d != null ? d.getDemandId() : null) // Demand number
                .demand(d.getSubLob() != null ? d.getSubLob().getSubLob() + "-" + d.getDemandId() : d.getLob().getLob() + "-" + d.getDemandId())
                .primarySkills(d != null ? toRefList(d.getPrimarySkills()) : Collections.emptyList())
                .secondarySkills(d != null ? toRefList(d.getSecondarySkills()) : Collections.emptyList())
                .hbu(d != null ? toRef(d.getHbu()) : null)
                .profileTrackerStatus(toRef(tracker.getProfileTrackerStatus()))
                .evaluationStatus(toRef(tracker.getEvaluationStatus()))
                .demandStatus(toRef(d.getStatus()))
                .build();
    }


    @Transactional
    public String editProfileTracker(Long trackerId, EditProfileTrackerRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        ProfileTracker pt = trackerRepository.findById(trackerId)
                .orElseThrow(() -> new RuntimeException("ProfileTracker not found: " + trackerId));

        Map<String, Object> oldSnap = profileTrackerSnapshotBuilder.snapshot(pt);

        boolean changed = false;


        LocalDate oldProfileSharedDate = pt.getProfileSharedDate();
        boolean profileSharedDateChanged = false;


        // Capture old evaluation status (to detect change)
        EvaluationStatus oldEs = pt.getEvaluationStatus();

        // Dates: update only if non-null (no null clearing)
        if (req.getProfileSharedDate() != null) {
//            pt.setProfileSharedDate(req.getProfileSharedDate());
//            changed = true;


            LocalDate newDate = req.getProfileSharedDate();
            profileSharedDateChanged = !Objects.equals(oldProfileSharedDate, newDate);

            // Only set when actually different (prevents unnecessary updates)
            if (profileSharedDateChanged) {
                pt.setProfileSharedDate(newDate);
                changed = true;
            }

        }

        // ----- interviewDate change detection -----
        LocalDate oldInterviewDate = pt.getInterviewDate();
        boolean interviewDateChanged = false;

        if (req.getInterviewDate() != null) {
            LocalDate newInterview = req.getInterviewDate();
            interviewDateChanged = !Objects.equals(oldInterviewDate, newInterview);
            if (interviewDateChanged) {
                pt.setInterviewDate(newInterview);
                changed = true;
            }
        }

        if (req.getDecisionDate() != null) {
            pt.setDecisionDate(req.getDecisionDate());
            changed = true;
        }

        // profileTrackerStatus: update only if non-null
//        if (req.getProfileTrackerStatusId() != null) {
//            ProfileTrackerStatus pts = profileTrackerStatusRepository.findById(req.getProfileTrackerStatusId())
//                    .orElseThrow(() ->
//                            new IllegalArgumentException("Invalid profileTrackerStatusId: " + req.getProfileTrackerStatusId()));
//            changed = true;
//            pt.setProfileTrackerStatus(pts);
//        }




        // ------------------ Auto status rule ------------------
        // When ProfileSharedDate changed:
        // 1) Demand status -> "Profile Shared"
        // 2) ProfileTracker status -> "Awaiting Client Slot"
        // ✅ Separate function call: rule when profileSharedDate changed
        changed |= applyProfileSharedRuleIfNeeded(pt, req, profileSharedDateChanged);

        // B) When interviewDate changed → set status to "Evaluation In Progress"
        changed |= applyInterviewDateRuleIfNeeded(pt, interviewDateChanged,oldSnap);






        // ---- profileTrackerStatus change detection (ONLY ONCE) ----
        ProfileTrackerStatus currentPts = pt.getProfileTrackerStatus(); // capture BEFORE any set

        boolean profileTrackerStatusChanged = false;
        ProfileTrackerStatus newPts = null;
        if (req.getProfileTrackerStatusId() != null) {
            if (currentPts == null || !Objects.equals(currentPts.getId(), req.getProfileTrackerStatusId())) {
                newPts = profileTrackerStatusRepository.findById(req.getProfileTrackerStatusId())
                        .orElseThrow(() ->
                                new IllegalArgumentException("Invalid profileTrackerStatusId: " + req.getProfileTrackerStatusId()));

                // profileTrackerStatusChanged = (oldPts == null) || !Objects.equals(oldPts.getId(), newPts.getId());
                pt.setProfileTrackerStatus(newPts);
                profileTrackerStatusChanged = true; // Added
                changed = true;
            }
        }

        if (profileTrackerStatusChanged) {

            notificationService.notifyAllUsersExceptActor(
                    "PROFILE_TRACKER",
                    pt.getId(),
                    "Profile Tracker Updated",
                    "Profile " + pt.getProfile().getCandidateName()
                            + " moved to status "
                            + newPts.getName()
                            + " for Demand ID " + resolveDemandPrefix(pt.getDemand()) + "-"
                            + pt.getDemand().getDemandId(),
                    getCurrentUserId()
            );
        }

        else { // Added
            // No real change; keep current as newPts for downstream readability if needed
            newPts = currentPts;
        }


//        // 🔁 Rule: If PT status changed to "Client Cliented" -> create onboarding & set demand to "Demand Fulfilled"
//        changed |= applyClientSelectedRuleIfNeeded(pt, profileTrackerStatusChanged, newPts);

        // ✅ STRICT CHECK: prevent same profile onboarding for multiple demands
        if (profileTrackerStatusChanged
                && newPts != null
                && "Client Selected".equalsIgnoreCase(newPts.getName())) {

            validateUniqueClientSelection(pt);  // 🚫 BLOCK HERE
        }

        // ✅ Only runs if validation passed
        changed |= applyClientSelectedRuleIfNeeded(pt, profileTrackerStatusChanged, newPts);

        // evaluationStatus: update only if non-null
        if (req.getEvaluationStatusId() != null) {
            EvaluationStatus newEs = evaluationStatusRepository.findById(req.getEvaluationStatusId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Invalid evaluationStatusId: " + req.getEvaluationStatusId()));

            boolean evaluationStatusChanged = (oldEs == null) || !Objects.equals(oldEs.getId(), newEs.getId());
            pt.setEvaluationStatus(newEs);
            changed = true;

//            // If it changed TO "Client Selected" → create onboarding if not already present
//            if (evaluationStatusChanged && "Client Selected".equalsIgnoreCase(newEs.getName())) {
//                // Ensure idempotency – create only if not already there
//                if (!onboardingRepository.existsByProfileTracker_Id(pt.getId())) {
//                    Onboarding onboarding = new Onboarding();
//                    onboarding.setProfileTracker(pt);
//                    onboarding.setDemand(pt.getDemand());
//                    onboarding.setProfile(pt.getProfile());
//
//
//                    OnboardingStatus inProgress = onboardingStatusRepository
//                            .findByNameIgnoreCase("In Progress")
//                            .orElseThrow(() ->
//                                    new IllegalStateException("OnboardingStatus 'In Progress' not found in master table"));
//
//                    onboarding.setOnboardingStatus(inProgress);
//
//                    // Optional: audit fields if present in BaseEntity
////                    onboarding.setCreatedByUserId(getCurrentUserId());
//
//                    onboardingRepository.save(onboarding);
//                }
//            }
        }


        if (!changed) {
            throw new IllegalArgumentException("No editable fields provided");
        }

        // Audit
        pt.setUpdatedByUserId(getCurrentUserId());

        trackerRepository.save(pt);


//        profileTrackerAuditService.recordUpdate(pt, oldSnap, getCurrentUserId(),  null);

        // 5b) Generic table: audit_history

        genericAuditService.recordUpdate(
                AuditHistory.EntityType.PROFILE_TRACKER,
                AuditHistory.Action.UPDATE_PROFILE_TRACKER,
                pt,
                oldSnap,
                profileTrackerSnapshotBuilder,
                profileTrackerAuditMeta,
                getCurrentUserId()
        );

        return "Profile Tracker updated successfully";
    }

    private void validateUniqueClientSelection(ProfileTracker pt) {

        Long profileId = pt.getProfile().getId();
        Long demandId = pt.getDemand().getId();

        long count = trackerRepository.countClientSelectedForOtherDemand(profileId, demandId);

        if (count > 0) {
            throw new IllegalStateException(
                    "Profile '" + pt.getProfile().getCandidateName() +
                            "' is already Client Selected for another demand. Cannot onboard again."
            );
        }
    }

    private boolean applyProfileSharedRuleIfNeeded(ProfileTracker pt,
                                                   EditProfileTrackerRequest req,
                                                   boolean profileSharedDateChanged)
    {

        if (!profileSharedDateChanged) return false;

        boolean updated = false;

        // (A) ProfileTracker status auto-set OVERRIDING current status
        //     only when user did NOT explicitly provide a new status
        if (req.getProfileTrackerStatusId() == null) {

            ProfileTrackerStatus awaitingClientSlot = profileTrackerStatusRepository
                    .findByNameIgnoreCase("Awaiting Client Slot")
                    .orElseThrow(() -> new IllegalStateException(
                            "ProfileTrackerStatus 'Awaiting Client Slot' not found in master table"));

            // Idempotent update: update only if different
            if (pt.getProfileTrackerStatus() == null ||
                    !Objects.equals(pt.getProfileTrackerStatus().getId(), awaitingClientSlot.getId())) {

                pt.setProfileTrackerStatus(awaitingClientSlot);
                updated = true;
            }
        }



        // (B) Demand status auto-set
        if (pt.getDemand() != null) {
            var demand = pt.getDemand();

            // Take demand snapshot ONLY if we are going to update it (optional but efficient)
            Map<String, Object> oldDemandSnap = null;

            Status profileShared = statusRepository
                    .findByStatus("Profile Sent to Delivery")
                    .orElseThrow(() -> new IllegalStateException(
                            "DemandStatus 'Profile Sent to Delivery' not found in master table"));

            boolean demandStatusNeedsUpdate =
                    (demand.getStatus() == null) ||
                            !Objects.equals(demand.getStatus().getId(), profileShared.getId());

            if (demandStatusNeedsUpdate) {
                oldDemandSnap = addDemandSnapshotBuilder.snapshot(demand); // <-- if you have snapshot builder for demand
                demand.setStatus(profileShared);
                demand.setUpdatedByUserId(getCurrentUserId());
                demandRepository.save(demand);

                // Audit Demand update (recommended)
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

            // (C) Send mail notification
            sendProfileSharedMail(pt);
        }

        return updated;
    }

    // Mail helper function
    private void sendProfileSharedMail(ProfileTracker pt) {
        try {
            String candidateName = (pt.getProfile() != null)
                    ? pt.getProfile().getCandidateName()
                    : "N/A";

            if (pt.getDemand() == null) return;

            Long demandId = pt.getDemand().getDemandId() != null
                    ? pt.getDemand().getDemandId()
                    : 0;

            if (pt.getDemand().getCreatedByUserId() != null) {
                String toEmail = userAccountRepository.findByUserId(pt.getDemand().getCreatedByUserId())
                        .map(User::getEmailId)
                        .orElse(null);

                if (toEmail != null) {
                    emailService.sendProfileAttachedNotification(toEmail, candidateName, demandId);
                }
            }

        } catch (Exception e) {
            log.warn("Profile shared mail notification failed: {}", e.getMessage());
        }
    }


    private boolean applyInterviewDateRuleIfNeeded(ProfileTracker pt,
                                                   boolean interviewDateChanged,
                                                   Map<String, Object> oldPtSnap
    ) {
        if (!interviewDateChanged) return false;

        ProfileTrackerStatus evalInProgress = profileTrackerStatusRepository
                .findByNameIgnoreCase("Evaluation In Progress")
                .orElseThrow(() -> new IllegalStateException(
                        "ProfileTrackerStatus 'Evaluation In Progress' not found in master table"));

        ProfileTrackerStatus current = pt.getProfileTrackerStatus();
        if (current == null || !Objects.equals(current.getId(), evalInProgress.getId())) {
            pt.setProfileTrackerStatus(evalInProgress);

            // Audit this auto-update using the pre-mutation snapshot
            genericAuditService.recordUpdate(
                    AuditHistory.EntityType.PROFILE_TRACKER,
                    AuditHistory.Action.UPDATE_PROFILE_TRACKER,   // consider a dedicated AUTO_RULE action if you add it
                    pt,
                    oldPtSnap,
                    profileTrackerSnapshotBuilder,
                    profileTrackerAuditMeta,
                    getCurrentUserId()
            );

            return true;
        }
        return false;
    }


    /**
     * When ProfileTrackerStatus changed to "Soft Select":
     *  - Create Onboarding row (if not already present)
     *  - Set Demand status -> "Demand Fulfilled" (with audit)
     *
     * Returns true if this rule caused any additional updates.1
     */
//    private boolean applySoftSelectRuleIfNeeded(ProfileTracker pt,
//                                                boolean profileTrackerStatusChanged,
//                                                ProfileTrackerStatus newPts) {
//
////        log.info("****log into***{}{}{}",pt.toString(),profileTrackerStatusChanged,newPts.toString());
//        if (!profileTrackerStatusChanged) return false;
//        if (newPts == null || ! "Soft Select".equalsIgnoreCase(newPts.getName())) return false;
//
//        boolean updated = false;
//
//        // 1) Create onboarding if absent (idempotent)
//        if (!onboardingRepository.existsByProfileTracker_Id(pt.getId())) {
//            Onboarding onboarding = new Onboarding();
//            onboarding.setProfileTracker(pt);
//            onboarding.setDemand(pt.getDemand());
//            onboarding.setProfile(pt.getProfile());
//
//            // Choose default onboarding status; reuse "In Progress" as in your Client Selected rule
//            OnboardingStatus inProgress = onboardingStatusRepository
//                    .findByNameIgnoreCase("In Progress")
//                    .orElseThrow(() ->
//                            new IllegalStateException("OnboardingStatus 'In Progress' not found in master table"));
//
//            onboarding.setOnboardingStatus(inProgress);
//            onboardingRepository.save(onboarding);
//            updated = true;
//
//            // (Optional) If you have onboarding snapshot/audit meta, you can also audit this as ATTACH:
//            // genericAuditService.recordUpdate(
//            //     AuditHistory.EntityType.ONBOARDING,
//            //     AuditHistory.Action.ATTACH,
//            //     onboarding,
//            //     null,
//            //     onboardingSnapshotBuilder,
//            //     onboardingAuditMeta.withReason("Auto: PT Soft Select → Onboarding created"),
//            //     userId
//            // );
//        }
//
//        // 2) Demand status -> "Demand Fulfilled" (+ audit)
//        if (pt.getDemand() != null) {
//            var demand = pt.getDemand();
//
//            Status fulfilled = statusRepository
//                    .findByStatus("Demand Fulfilled")
//                    .orElseThrow(() ->
//                            new IllegalStateException("DemandStatus 'Demand Fulfilled' not found in master table"));
//
//            boolean needsUpdate = (demand.getStatus() == null) ||
//                    !Objects.equals(demand.getStatus().getId(), fulfilled.getId());
//
//            if (needsUpdate) {
//                Map<String, Object> oldDemandSnap = addDemandSnapshotBuilder.snapshot(demand);
//                demand.setStatus(fulfilled);
//                demand.setUpdatedByUserId(getCurrentUserId());
//                demandRepository.save(demand);
//
//                genericAuditService.recordUpdate(
//                        AuditHistory.EntityType.ADD_DEMAND,
//                        AuditHistory.Action.UPDATE_DEMAND,
//                        demand,
//                        oldDemandSnap,
//                        addDemandSnapshotBuilder,
//                        addDemandAuditMeta,
//                        getCurrentUserId()
//                );
//                updated = true;
//            }
//        }
//
//        return updated;
//    }

    // Client selected rule
    private boolean applyClientSelectedRuleIfNeeded(ProfileTracker pt,
                                                    boolean profileTrackerStatusChanged,
                                                    ProfileTrackerStatus newPts) {

//        log.info("****log into***{}{}{}",pt.toString(),profileTrackerStatusChanged,newPts.toString());
        if (!profileTrackerStatusChanged) return false;
        if (newPts == null || ! "Client Selected".equalsIgnoreCase(newPts.getName())) return false;

        boolean updated = false;

        // 1) Create onboarding if absent (idempotent)
        if (!onboardingRepository.existsByProfileTracker_Id(pt.getId())) {
            Onboarding onboarding = new Onboarding();
            onboarding.setProfileTracker(pt);
            onboarding.setDemand(pt.getDemand());
            onboarding.setProfile(pt.getProfile());

            // Choose default onboarding status; reuse "In Progress" as in your Client Selected rule
            OnboardingStatus inProgress = onboardingStatusRepository
                    .findByNameIgnoreCase("In Progress")
                    .orElseThrow(() ->
                            new IllegalStateException("OnboardingStatus 'In Progress' not found in master table"));

            onboarding.setOnboardingStatus(inProgress);
            onboardingRepository.save(onboarding);
            updated = true;

            // (Optional) If you have onboarding snapshot/audit meta, you can also audit this as ATTACH:
            // genericAuditService.recordUpdate(
            //     AuditHistory.EntityType.ONBOARDING,
            //     AuditHistory.Action.ATTACH,
            //     onboarding,
            //     null,
            //     onboardingSnapshotBuilder,
            //     onboardingAuditMeta.withReason("Auto: PT Soft Select → Onboarding created"),
            //     userId
            // );
        }

        // 2) Demand status -> "Demand Fulfilled" (+ audit)
        if (pt.getDemand() != null) {
            var demand = pt.getDemand();

            Status fulfilled = statusRepository
                    .findByStatus("Demand Fulfilled")
                    .orElseThrow(() ->
                            new IllegalStateException("DemandStatus 'Demand Fulfilled' not found in master table"));

            boolean needsUpdate = (demand.getStatus() == null) ||
                    !Objects.equals(demand.getStatus().getId(), fulfilled.getId());

            if (needsUpdate) {
                Map<String, Object> oldDemandSnap = addDemandSnapshotBuilder.snapshot(demand);
                demand.setStatus(fulfilled);
                demand.setUpdatedByUserId(getCurrentUserId());
                demandRepository.save(demand);

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

        return updated;
    }

    @Transactional
    public void markOtherProfilesAsAnotherCandidate(Long demandId, Long selectedProfileTrackerId) {

        ProfileTrackerStatus anotherStatus =
                profileTrackerStatusRepository.findByNameIgnoreCase("Another Candidate Selected On This Demand")
                        .orElseThrow(() -> new IllegalStateException(
                                "Status 'Another Candidate Selected On This Demand' not found"));

        List<ProfileTracker> trackers =
                trackerRepository.findByDemand_Id(demandId);

        for (ProfileTracker pt : trackers) {

            // skip the selected profile
            if (pt.getId().equals(selectedProfileTrackerId)) continue;

            // skip terminal statuses (optional, configurable)
            String status = pt.getProfileTrackerStatus() != null
                    ? pt.getProfileTrackerStatus().getName()
                    : null;

            if (status != null &&
                    (status.equalsIgnoreCase("Joined")
                            || status.equalsIgnoreCase("Not Joined")
                            || status.equalsIgnoreCase("Withdrawn by Candidate")
                            || status.equalsIgnoreCase("Demand Abandoned"))) {
                continue;
            }

            Map<String, Object> oldSnap = profileTrackerSnapshotBuilder.snapshot(pt);

            pt.setProfileTrackerStatus(anotherStatus);
            pt.setUpdatedByUserId(getCurrentUserId());
            trackerRepository.save(pt);

//            // audit
            genericAuditService.recordUpdate(
                    AuditHistory.EntityType.PROFILE_TRACKER,
                    AuditHistory.Action.UPDATE_PROFILE_TRACKER,
                    pt,
                    oldSnap,
                    profileTrackerSnapshotBuilder,
                    profileTrackerAuditMeta,
                    getCurrentUserId()
            );
        }
    }




    public ProfileTrackerDropdownResponse getDropdowns() {
        List<RefDTO> evaluationStatuses = evaluationStatusRepository.findAll()
                .stream()
                .map(es -> new RefDTO(es.getId(), es.getName()))
                .toList();

        List<RefDTO> profileTrackerStatuses = profileTrackerStatusRepository.findAll()
                .stream()
                .map(pts -> new RefDTO(pts.getId(), pts.getName()))
                .toList();

        return new ProfileTrackerDropdownResponse(evaluationStatuses, profileTrackerStatuses);
    }


    @Transactional(readOnly = true)
    public PageResponse getAllProfileTrackers(int page,int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProfileTracker> trackersPage = trackerRepository.findAll(pageable);
        List<GetAllProfileTrackerDTO> content = trackersPage.getContent()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PageResponse(
                content,
                trackersPage.isEmpty(),
                trackersPage.isFirst(),
                trackersPage.isLast(),
                trackersPage.getSize(),
                trackersPage.getTotalElements(),
                trackersPage.getTotalPages()
        );

    }

    @Transactional(readOnly = true)
    public PageResponse search(ProfileTrackerFilterRequest filter,int page,int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<ProfileTracker> trackerPage = trackerRepository.findAll(
                ProfileTrackerSpecifications.byFilter(filter),
                pageable
        );


        if (trackerPage.isEmpty()) {
            throw new ResourceNotFoundException("No profile trackers found for given filters");
        }

        List<GetAllProfileTrackerDTO> content = trackerPage.getContent()
                .stream()
                .map(this::toDto) // mapper
                .collect(Collectors.toList());

        return new PageResponse(
                content,
                trackerPage.isEmpty(),
                trackerPage.isFirst(),
                trackerPage.isLast(),
                trackerPage.getSize(),
                trackerPage.getTotalElements(),
                trackerPage.getTotalPages()
        );

    }

    // -------------------- MAPPERS --------------------

    private GetAllProfileTrackerDTO toDto(ProfileTracker pt) {
        GetAllProfileTrackerDTO dto = new GetAllProfileTrackerDTO();

        dto.setId(pt.getId());

        dto.setCreatedAt(pt.getCreatedAt());
        dto.setCreatedByUserId(pt.getCreatedByUserId());

        dto.setUpdatedAt(pt.getUpdatedAt());
        dto.setUpdatedByUserId(pt.getUpdatedByUserId());

        dto.setProfileSharedDate(pt.getProfileSharedDate());
        dto.setInterviewDate(pt.getInterviewDate());
        dto.setAttachedDate(pt.getAttachedDate());
        dto.setDecisionDate(pt.getDecisionDate());

        dto.setEvaluationStatus(toRef(pt.getEvaluationStatus()));
        dto.setProfileTrackerStatus(toRef(pt.getProfileTrackerStatus()));

        dto.setDemand(toDemandMini(pt.getDemand()));
        dto.setProfile(toProfileMini(pt.getProfile()));

        return dto;
    }

    private GetAllProfileTrackerDTO.DemandMiniDTO toDemandMini(AddDemand d) {
//        String lobName = (d.getLob() != null && d.getLob().getName() != null)
//                ? d.getLob().getName()
//                : "";
//
//        String displayDemandId = lobName.isBlank()
//                ? String.valueOf(d.getDemandId())
//                : lobName + "-" + d.getDemandId();
        String prefix = resolveDemandPrefix(d);
        String displayDemandId = prefix + "-" + d.getDemandId();
        GetAllProfileTrackerDTO.DemandMiniDTO dm = new GetAllProfileTrackerDTO.DemandMiniDTO();

        dm.setId(d.getId());
        dm.setFlag(d.getFlag());

        dm.setDemandId(d.getDemandId());
        dm.setDisplayDemandId(displayDemandId);
        dm.setRrNumber(d.getRrNumber());
        dm.setFileName(d.getFileName());

        dm.setExperience(d.getExperience());
        dm.setRemark(d.getRemark());
        dm.setDemandReceivedDate(d.getDemandReceivedDate());

        dm.setPrimarySkills(toRefList(d.getPrimarySkills()));
        dm.setSecondarySkills(toRefList(d.getSecondarySkills()));
        dm.setDemandLocations(toRefList(d.getDemandLocations()));

        dm.setHbu(toRef(d.getHbu()));
        dm.setHbuSpoc(toRef(d.getHbuSpoc()));

        dm.setBand(toRef(d.getBand()));
        dm.setPriority(toRef(d.getPriority()));
        dm.setLob(toRef(d.getLob()));
        dm.setDemandType(toRef(d.getDemandType()));
        dm.setDemandTimeline(toRef(d.getDemandTimeline()));
        dm.setExternalInternal(toRef(d.getExternalInternal()));
        dm.setStatus(toRef(d.getStatus()));
        dm.setPod(toRef(d.getPod()));

        dm.setPmo(toRef(d.getPmo()));
        dm.setPmoSpoc(toRef(d.getPmoSpoc()));
        dm.setSalesSpoc(toRef(d.getSalesSpoc()));

        dm.setHiringManager(toRef(d.getHiringManager()));
        dm.setDeliveryManager(toRef(d.getDeliveryManager()));

        dm.setSkillCluster(toRef(d.getSkillCluster()));
        dm.setProjectManager(toRef(d.getProjectManager()));

        return dm;
    }

    private GetAllProfileTrackerDTO.ProfileMiniDTO toProfileMini(Profile p) {
        GetAllProfileTrackerDTO.ProfileMiniDTO pm = new GetAllProfileTrackerDTO.ProfileMiniDTO();

        pm.setId(p.getId());
        pm.setCandidateName(p.getCandidateName());
        pm.setEmailId(p.getEmailId());
        pm.setEmpId(p.getEmpId());
        pm.setPhoneNumber(p.getPhoneNumber());

        pm.setIsActive(p.getIsActive());
        pm.setExperience(p.getExperience());

        pm.setSkillCluster(toRef(p.getSkillCluster()));
        pm.setLocation(toRef(p.getLocation()));
        pm.setHbu(toRef(p.getHbu()));
        pm.setExternalInternal(toRef(p.getExternalInternal()));

        pm.setSummary(p.getSummary());
        pm.setFileName(p.getFileName());

        pm.setPrimarySkills(toRefList(p.getPrimarySkills()));
        pm.setSecondarySkills(toRefList(p.getSecondarySkills()));

        pm.setCreatedByUserId(p.getCreatedByUserId());
        pm.setUpdatedByUserId(p.getUpdatedByUserId());

        pm.setCreatedAt(p.getCreatedAt());
        pm.setUpdatedAt(p.getUpdatedAt());

        return pm;
    }

    // -------------------- RefDTO helpers --------------------
    private RefDTO toRef(Object entity) {
        if (entity == null) return null;

        try {
            Long id = (Long) entity.getClass().getMethod("getId").invoke(entity);

            Object label;
            try {
                label = entity.getClass().getMethod("getName").invoke(entity);
            } catch (NoSuchMethodException ex) {
                label = entity.getClass().getMethod("getValue").invoke(entity);
            }

            RefDTO ref = new RefDTO();
            ref.setId(id);
            ref.setName(label != null ? label.toString() : null);
            return ref;

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to map to RefDTO for type: " + entity.getClass().getName(), e
            );
        }
    }

    private List<RefDTO> toRefList(Set<?> entities) {
        if (entities == null || entities.isEmpty()) return Collections.emptyList();
        return entities.stream().map(this::toRef).filter(Objects::nonNull).toList();
    }


    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        return auth.getName(); // this is userId because you set it as username
    }

    private String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;

        String userId = auth.getName();
        return userAccountRepository.findByUserId(userId)
                .map(User::getName)
                .orElse(null);
    }

    private String resolveDemandPrefix(AddDemand d) {
        if (d.getSubLob() != null && d.getSubLob().getSubLob() != null) {
            return d.getSubLob().getSubLob();       // WSIT, MSS
        }
        if (d.getLob() != null && d.getLob().getLob() != null) {
            return d.getLob().getLob();             // CIB
        }
        return "NA";
    }
}