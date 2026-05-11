package com.coforge.hsbcdma.service.ProfileServices;

import com.coforge.hsbcdma.audit.AuditHistory;
import com.coforge.hsbcdma.audit.Profile.ProfileAuditMeta;
import com.coforge.hsbcdma.audit.Profile.ProfileSnapshotBuilder;
import com.coforge.hsbcdma.audit.core.GenericAuditService;
import com.coforge.hsbcdma.dto.DemandsDTO.PageResponse;
import com.coforge.hsbcdma.dto.DemandsDTO.RefDTO;
import com.coforge.hsbcdma.dto.ProfilesDTO.*;
import com.coforge.hsbcdma.entity.CountryCode;
import com.coforge.hsbcdma.entity.Location;
import com.coforge.hsbcdma.entity.Profile;
import com.coforge.hsbcdma.entity.User;
import com.coforge.hsbcdma.entity.dropdownEntities.*;
import com.coforge.hsbcdma.entity.dropdownEntities.Profile.*;
import com.coforge.hsbcdma.exception.ResourceNotFoundException;
import com.coforge.hsbcdma.repository.CountryCodeRepository;
import com.coforge.hsbcdma.repository.LocationRepository;
import com.coforge.hsbcdma.repository.ProfileRepositories.ProfileRepository;
import com.coforge.hsbcdma.repository.ProfileRepositories.ProfileStatusRepository;
import com.coforge.hsbcdma.repository.UserAccountRepository;
import com.coforge.hsbcdma.repository.dropdownRepository.*;
import com.coforge.hsbcdma.service.NotificationServices.NotificationService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserAccountRepository userAccountRepository;
    private final EntityManager em;
    private final ProfileStorageService profileStorageService;


    private final ProfileSnapshotBuilder profileSnapshotBuilder;
    private final ProfileAuditMeta profileAuditMeta;
    private final GenericAuditService genericAuditService;

    private final LocationRepository locationRepository;
    private final SkillClusterRepository skillClusterRepository;
    private final HbuRepository hbuRepository;
    private final CountryCodeRepository countryCodeRepository;
    private final ExternalInternalRepository externalInternalRepository;
    private final ProfileStatusRepository profileStatusRepository;
    private final PrimarySkillsRepository primarySkillsRepository;
    private final SecondarySkillsRepository secondarySkillsRepository;

    private final NotificationService notificationService;

    @Transactional
    public ProfileCreateResponseDTO createProfile(ProfileCreateRequestDTO dto, MultipartFile file) {

        // 1) Uniqueness checks
        if (dto.getEmailId() != null && !dto.getEmailId().isBlank()
                && profileRepository.existsByEmailId(dto.getEmailId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "email_id already exists: " + dto.getEmailId()
            );
        }

        if (dto.getEmpId() != null && !dto.getEmpId().isBlank()
                && profileRepository.existsByEmpId(dto.getEmpId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "emp_id already exists: " + dto.getEmpId()
            );
        }

        if (dto.getPhoneNumber() != null
                && profileRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "phone_number already exists: " + dto.getPhoneNumber()
            );
        }

        if (dto.getPanNumber() != null && !dto.getPanNumber().isBlank()
                && profileRepository.existsByPanNumberIgnoreCase(dto.getPanNumber())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "pan_number already exists: " + dto.getPanNumber()
            );
        }

    /* -------------------------------------------------
       2) Build entity (CV IS OPTIONAL)
       ------------------------------------------------- */
        Profile p = new Profile();
        p.setCandidateName(dto.getCandidateName());
        p.setEmailId(dto.getEmailId());
        p.setEmpId(dto.getEmpId());
        p.setSapId(dto.getSapId());                 // ✅ IMPORTANT
        p.setPhoneNumber(dto.getPhoneNumber());
        p.setPanNumber(dto.getPanNumber());
        p.setIsActive(true);
        p.setExperience(dto.getExperience());
        p.setSummary(dto.getSummary());

        if (dto.getSkillClusterId() != null) {
            p.setSkillCluster(em.getReference(SkillCluster.class, dto.getSkillClusterId()));
        }
        if (dto.getLocationId() != null) {
            p.setLocation(em.getReference(Location.class, dto.getLocationId()));
        }
        if (dto.getHbuId() != null) {
            p.setHbu(em.getReference(Hbu.class, dto.getHbuId()));
        }
        if (dto.getExternalInternalId() != null) {
            p.setExternalInternal(em.getReference(ExternalInternal.class, dto.getExternalInternalId()));
        }
        if (dto.getCountryId() != null) {
            p.setCountry(em.getReference(CountryCode.class, dto.getCountryId()));
        }
        if (dto.getProfileStatusId() != null) {
            p.setProfileStatus(em.getReference(ProfileStatus.class, dto.getProfileStatusId()));
        }

        p.setPrimarySkills(toPrimarySkills(dto.getPrimarySkillsIds()));
        p.setSecondarySkills(toSecondarySkills(dto.getSecondarySkillsIds()));

        p.setCreatedByUserId(getCurrentUserId());

        // -------- NEW ManyToOne --------
        if (dto.getOriginId() != null) {
            p.setOrigin(em.getReference(Origin.class, dto.getOriginId()));
        }
        if (dto.getKaratStatusId() != null) {
            p.setKaratStatus(em.getReference(KaratStatusMaster.class, dto.getKaratStatusId()));
        }
        if (dto.getSourceId() != null) {
            p.setSource(em.getReference(SourceMaster.class, dto.getSourceId()));
        }
        if (dto.getOverallStatusRdgId() != null) {
            p.setOverallStatusRdg(
                    em.getReference(OverallStatusMaster.class, dto.getOverallStatusRdgId())
            );
        }

// -------- NEW Normal fields --------
        p.setDateOfSubmission(dto.getDateOfSubmission());
        p.setKaratReadiness(dto.getKaratReadiness());
        p.setWeekOf(dto.getWeekOf());
        p.setAccountReceivedOn(dto.getAccountReceivedOn());
        p.setStatusDate(dto.getStatusDate());
        p.setLobShared(dto.getLobShared());
        p.setPractice(dto.getPractice());
        p.setBand(dto.getBand());
        p.setAgeing(dto.getAgeing());
        p.setAgeingRange(dto.getAgeingRange());
        p.setCodes(dto.getCodes());
        p.setCodeType(dto.getCodeType());
        p.setMinBillingRate(dto.getMinBillingRate());
        p.setProjectCode(dto.getProjectCode());

        // 3) Save profile FIRST (to get ID)
        Profile saved = profileRepository.save(p);

    /* -------------------------------------------------
       4) CV upload (OPTIONAL ✅)
       ------------------------------------------------- */
        if (file != null && !file.isEmpty()) {
            String storedFileName =
                    profileStorageService.storeProfileFile(saved.getId(), file);
            saved.setFileName(storedFileName);
            profileRepository.save(saved);
        }

        return new ProfileCreateResponseDTO(saved.getId());
    }


    public Map<String, Object> bulkUploadTAProfiles(MultipartFile file) {

        List<Map<String, Object>> errors = new ArrayList<>();
        int success = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> h = buildHeaderMap(headerRow);

            validateTAMandatoryHeaders(h);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (isRowCompletelyEmpty(row)) continue;

                try {
                    Profile profile = buildTAProfileFromRow(row, h);
                    profileRepository.save(profile);
                    success++;
                } catch (Exception ex) {

                    String reason;

                    if (ex instanceof jakarta.validation.ConstraintViolationException cve) {
                        reason = cve.getConstraintViolations()
                                .stream()
                                .findFirst()
                                .map(v -> {
                                    if ("panNumber".equals(v.getPropertyPath().toString())) {
                                        return "Invalid PAN format (expected ABCDE1234F)";
                                    }
                                    return v.getMessage();
                                })
                                .orElse("Validation error");
                    } else {
                        reason = ex.getMessage();
                    }

                    errors.add(Map.of(
                            "row", i + 1,
                            "candidate", getCell(row, h.get("Candidate Name")),
                            "reason", reason
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Invalid Excel file", e);
        }

        return Map.of(
                "totalRows", success + errors.size(),
                "successCount", success,
                "failureCount", errors.size(),
                "errors", errors
        );
    }

    private void validateTAMandatoryHeaders(Map<String, Integer> h) {

        List<String> mandatory = List.of(
                "Status",
                "L1 Interview Date",
                "Candidate Name",
                "HBU",
                "Primary Skill",
                "Total Exp.",
                "Current Location",
                "Coforge Location",
                "Official NP",
                "Negotiable NP/LWD",
                "Recruiter",
                "Current Status",
                "PAN No"
        );

        for (String col : mandatory) {
            if (!h.containsKey(col)) {
                throw new RuntimeException("Missing mandatory column: " + col);
            }
        }
    }

    private Profile buildTAProfileFromRow(Row r, Map<String, Integer> h) {

        // ---- Extract values ----
        String status              = getRequired(r, h, "Status");
        LocalDate l1Date            = parseDate(getRequired(r, h, "L1 Interview Date"));
        String candidateName        = getRequired(r, h, "Candidate Name");
        String hbuName              = getRequired(r, h, "HBU");
        String primarySkillName     = getRequired(r, h, "Primary Skill");
        Float experience            = parseExp(getRequired(r, h, "Total Exp."));
        String currentLocation      = getRequired(r, h, "Current Location");
        String coforgeLocation      = getRequired(r, h, "Coforge Location");
        String officialNP           = getRequired(r, h, "Official NP");
        LocalDate negotiableLwd     = parseDate(getRequired(r, h, "Negotiable NP/LWD"));
        String recruiter            = getRequired(r, h, "Recruiter");
        String profileStatusName    = getRequired(r, h, "Current Status");
        String pan                  = getRequired(r, h, "PAN No");

        // ---- Uniqueness ----
        validateUniquenessPanOnly(pan);

        Profile p = new Profile();
        p.setCandidateName(candidateName);
        p.setExperience(experience);
        p.setCurrentLocation(currentLocation);
        p.setOfficialNP(officialNP);
        p.setNegotiableNpLwd(negotiableLwd);
        p.setL1InterviewDate(l1Date);
        p.setRecruiter(recruiter);
        p.setPanNumber(pan);
        p.setIsActive("Active".equalsIgnoreCase(status));

        p.setExternalInternal(em.getReference(
                ExternalInternal.class, externalInternalId("External")
        ));

        p.setProfileStatus(
                em.getReference(ProfileStatus.class, profileStatusId(profileStatusName))
        );

        p.setHbu(em.getReference(Hbu.class, hbuId(hbuName)));
        p.setLocation(em.getReference(Location.class, locationId(coforgeLocation)));

        PrimarySkills skill =
                primarySkillsRepository.findByPrimarySkillsIgnoreCase(primarySkillName)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid Primary Skill: " + primarySkillName));

        p.setPrimarySkills(Set.of(skill));
        p.setCountry(em.getReference(CountryCode.class, countryId("India")));

        p.setCreatedByUserId(getCurrentUserId());

        return p;
    }


    @Transactional
    private void createProfileFromExcel(ProfileCreateRequestDTO dto) {
        validateUniqueness(dto);
        Profile profile = buildProfileEntity(dto);
        profileRepository.save(profile);
    }


    @Transactional
    public ProfileCreateResponseDTO updateProfile(Long id, ProfileUpdateRequestDTO dto, MultipartFile file) {

        Profile p = profileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found: " + id));

        Map<String, Object> oldSnap = profileSnapshotBuilder.snapshot(p);

        // ----------------------------
        // 1) Uniqueness checks (only if changed)
        // ----------------------------
        if (dto.getEmailId() != null && !dto.getEmailId().isBlank()
                && !dto.getEmailId().equalsIgnoreCase(p.getEmailId())) {

            if (profileRepository.existsByEmailId(dto.getEmailId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "email_id already exists: " + dto.getEmailId());
            }
            p.setEmailId(dto.getEmailId());
        }

        if (dto.getEmpId() != null && !dto.getEmpId().isBlank()
                && !Objects.equals(dto.getEmpId(), p.getEmpId())) {

            if (profileRepository.existsByEmpId(dto.getEmpId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "emp_id already exists: " + dto.getEmpId());
            }
            p.setEmpId(dto.getEmpId());
        }

        if (dto.getSapId() != null
                && !dto.getSapId().isBlank()
                && !Objects.equals(dto.getSapId(), p.getSapId())) {

            if (profileRepository.existsBySapId(dto.getSapId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "sap_id already exists: " + dto.getSapId()
                );
            }

            p.setSapId(dto.getSapId());
        }


        // Phone unique if changed
        if (dto.getPhoneNumber() != null && !Objects.equals(dto.getPhoneNumber(), p.getPhoneNumber())) {
            if (profileRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "phone_number already exists: " + dto.getPhoneNumber());
            }
            p.setPhoneNumber(dto.getPhoneNumber());
        }

        // PAN unique if changed (case-insensitive)
        if (dto.getPanNumber() != null && !dto.getPanNumber().isBlank()
                && !dto.getPanNumber().equalsIgnoreCase(p.getPanNumber() == null ? "" : p.getPanNumber())) {
            if (profileRepository.existsByPanNumberIgnoreCase(dto.getPanNumber())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "pan_number already exists: " + dto.getPanNumber());
            }
            p.setPanNumber(dto.getPanNumber()); // normalized by @PreUpdate
        }



        // ----------------------------
        // 2) Update simple fields (editable)
        // ----------------------------
        setIfNotNull(dto.getCandidateName(), p::setCandidateName);
        setIfNotNull(dto.getPhoneNumber(), p::setPhoneNumber);
        setIfNotNull(dto.getIsActive(), p::setIsActive);
        setIfNotNull(dto.getExperience(), p::setExperience);
        setIfNotNull(dto.getSummary(), p::setSummary);

        // ----------------------------
        // 3) Update ManyToOne refs
        // (Set to null if dto explicitly sends null? Decide policy)
        // Here: only update if non-null.
        // ----------------------------
        if (dto.getSkillClusterId() != null) {
            p.setSkillCluster(em.getReference(SkillCluster.class, dto.getSkillClusterId()));
        }
        if (dto.getLocationId() != null) {
            p.setLocation(em.getReference(Location.class, dto.getLocationId()));
        }
        if (dto.getHbuId() != null) {
            p.setHbu(em.getReference(Hbu.class, dto.getHbuId()));
        }
        if (dto.getExternalInternalId() != null) {
            p.setExternalInternal(em.getReference(ExternalInternal.class, dto.getExternalInternalId()));
        }
        if (dto.getCountryId() != null) {
            p.setCountry(em.getReference(CountryCode.class, dto.getCountryId()));
        }
//        if(dto.getProfileStatusId() != null){
//            p.setProfileStatus(em.getReference(ProfileStatus.class,dto.getProfileStatusId()));
//        }


        ProfileStatus oldStatus = p.getProfileStatus();

        if (dto.getProfileStatusId() != null) {

            ProfileStatus newStatus =
                    em.getReference(ProfileStatus.class, dto.getProfileStatusId());

            boolean changed =
                    oldStatus == null ||
                            !Objects.equals(oldStatus.getId(), newStatus.getId());

            if (changed) {
                p.setProfileStatus(newStatus);

                notificationService.notifyAllUsersExceptActor(
                        "PROFILE",
                        p.getId(),
                        "Profile Status Updated",
                        "Profile " + p.getCandidateName()
                                + " status changed from "
                                + (oldStatus != null ? oldStatus.getName() : "N/A")
                                + " to "
                                + newStatus.getName(),
                        getCurrentUserId()
                );
            }
        }



        // ----------------------------
        // 4) Update ManyToMany skills
        // For PUT, usually replace sets completely if provided.
        // If not provided (null) => keep existing.
        // If provided empty => clear.
        // ----------------------------
        if (dto.getPrimarySkillsIds() != null) {
            p.setPrimarySkills(toPrimarySkills(dto.getPrimarySkillsIds()));
        }
        if (dto.getSecondarySkillsIds() != null) {
            p.setSecondarySkills(toSecondarySkills(dto.getSecondarySkillsIds()));
        }

        // ----------------------------
        // 5) File update (optional)
        // If file is present, store new file and update file_name.
        // ----------------------------
        if (file != null && !file.isEmpty()) {
            String storedFileName = profileStorageService.storeProfileFile(p.getId(), file);
            p.setFileName(storedFileName);
        }

        // Naye fields
        if (dto.getOriginId() != null) {
            p.setOrigin(em.getReference(Origin.class, dto.getOriginId()));
        }
        if (dto.getKaratStatusId() != null) {
            p.setKaratStatus(em.getReference(KaratStatusMaster.class, dto.getKaratStatusId()));
        }
        if (dto.getSourceId() != null) {
            p.setSource(em.getReference(SourceMaster.class, dto.getSourceId()));
        }
        if (dto.getOverallStatusRdgId() != null) {
            p.setOverallStatusRdg(
                    em.getReference(OverallStatusMaster.class, dto.getOverallStatusRdgId())
            );
        }

        setIfNotNull(dto.getDateOfSubmission(), p::setDateOfSubmission);
        setIfNotNull(dto.getKaratReadiness(), p::setKaratReadiness);
        setIfNotNull(dto.getWeekOf(), p::setWeekOf);
        setIfNotNull(dto.getAccountReceivedOn(), p::setAccountReceivedOn);
        setIfNotNull(dto.getStatusDate(), p::setStatusDate);
        setIfNotNull(dto.getLobShared(), p::setLobShared);
        setIfNotNull(dto.getPractice(), p::setPractice);
        setIfNotNull(dto.getBand(), p::setBand);
        setIfNotNull(dto.getAgeing(), p::setAgeing);
        setIfNotNull(dto.getAgeingRange(), p::setAgeingRange);
        setIfNotNull(dto.getCodes(), p::setCodes);
        setIfNotNull(dto.getCodeType(), p::setCodeType);
        setIfNotNull(dto.getMinBillingRate(), p::setMinBillingRate);
        setIfNotNull(dto.getProjectCode(), p::setProjectCode);

        // ----------------------------
        // 6) Audit update fields
        // ----------------------------
        String userId = getCurrentUserId();
        String userName = getCurrentUserName();
        p.setUpdatedByUserId(userId);
//        p.setUpdatedByName(userName);

        // save (not strictly required because JPA dirty checking in @Transactional)
        profileRepository.save(p);

        genericAuditService.recordUpdate(
                AuditHistory.EntityType.PROFILE,
                AuditHistory.Action.UPDATE_PROFILE,
                p,
                oldSnap,
                profileSnapshotBuilder,
                profileAuditMeta,
                getCurrentUserId()
        );

        return new ProfileCreateResponseDTO(p.getId());
    }


    public PageResponse getAllProfiles(int page, int size,Long hbuId) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Profile> profilesPage = profileRepository.findAllWithRefsByHbu(hbuId,pageable);

        List<GetAllProfilesResponse> content = profilesPage.getContent()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new PageResponse(
                content,
                profilesPage.isEmpty(),
                profilesPage.isFirst(),
                profilesPage.isLast(),
                profilesPage.getSize(),
                profilesPage.getTotalElements(),
                profilesPage.getTotalPages()
        );
    }


    @Transactional(readOnly = true)
    public PageResponse searchProfiles(ProfilesFilterRequest filter, int page, int size) throws BadRequestException {
        if (filter == null) {
            throw new BadRequestException("Filter body is required");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Profile> profilesPage = profileRepository.findAll(
                ProfilesSpecifications.build(filter),
                pageable
        );

        if (profilesPage.isEmpty()) {
            throw new ResourceNotFoundException("No profiles found for given filters");
        }

        List<GetAllProfilesResponse> content = profilesPage.getContent()
                .stream()
                .map(this::toResponse) // your existing mapper
                .collect(Collectors.toList());

        return new PageResponse(
                content,
                profilesPage.isEmpty(),
                profilesPage.isFirst(),
                profilesPage.isLast(),
                profilesPage.getSize(),
                profilesPage.getTotalElements(),
                profilesPage.getTotalPages()
        );
    }



    private GetAllProfilesResponse toResponse(Profile p) {
        return GetAllProfilesResponse.builder()
                .id(p.getId())
                .candidateName(p.getCandidateName())
                .emailId(p.getEmailId())
                .empId(p.getEmpId())
                .sapId(p.getSapId())
                .phoneNumber(p.getPhoneNumber())
                .isActive(p.getIsActive())
                .experience(p.getExperience())

                .skillCluster(ref(p.getSkillCluster()))
                .location(ref(p.getLocation()))
                .hbu(ref(p.getHbu()))
                .externalInternal(ref(p.getExternalInternal()))
                .profileStatus(ref(p.getProfileStatus()))

                .summary(p.getSummary())
                .fileName(p.getFileName())

                // ⚠️ ManyToMany can be lazy: this may trigger extra queries.
                // If you want production-fast list API, return List.of() here and expose details API for skills.
                .primarySkills(p.getPrimarySkills() == null ? List.of()
                        : p.getPrimarySkills().stream().map(this::ref).toList())
                .secondarySkills(p.getSecondarySkills() == null ? List.of()
                        : p.getSecondarySkills().stream().map(this::ref).toList())

                .createdByUserId(p.getCreatedByUserId())
//                .createdByName(p.getCreatedByName())
                .updatedByUserId(p.getUpdatedByUserId())
//                .updatedByName(p.getUpdatedByName())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .panNumber(p.getPanNumber())
                .l1InterviewDate(p.getL1InterviewDate())
                .currentLocation(p.getCurrentLocation())
                .officialNP(p.getOfficialNP())
                .negotiableNpLwd(p.getNegotiableNpLwd())
                .recruiter(p.getRecruiter())
                .origin(ref(p.getOrigin()))
                .karatStatus(ref(p.getKaratStatus()))
                .source(ref(p.getSource()))
                .overallStatusRdg(ref(p.getOverallStatusRdg()))

                .dateOfSubmission(p.getDateOfSubmission())
                .karatReadiness(p.getKaratReadiness())
                .weekOf(p.getWeekOf())
                .accountReceivedOn(p.getAccountReceivedOn())
                .statusDate(p.getStatusDate())
                .lobShared(p.getLobShared())
                .practice(p.getPractice())
                .band(p.getBand())
                .ageing(p.getAgeing())
                .ageingRange(p.getAgeingRange())
                .codes(p.getCodes())
                .codeType(p.getCodeType())
                .minBillingRate(p.getMinBillingRate())
                .projectCode(p.getProjectCode())
                .build();
    }

    /**
     * Generic "ref" mapper.
     * Works if the dropdown entity has getId() and getName() methods.
     */
    private RefDTO ref(Object entity) {
        if (entity == null) return null;
        try {
            Long id = (Long) entity.getClass().getMethod("getId").invoke(entity);
            String name = (String) entity.getClass().getMethod("getName").invoke(entity);
            return new RefDTO(id, name == null ? "" : name);
        } catch (Exception e) {
            return new RefDTO(null, "");
        }
    }



    // helper: set if not null
    private <T> void setIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }


    private Set<PrimarySkills> toPrimarySkills(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        return ids.stream()
                .map(id -> em.getReference(PrimarySkills.class, id))
                .collect(Collectors.toSet());
    }

    private Set<SecondarySkills> toSecondarySkills(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        return ids.stream()
                .map(id -> em.getReference(SecondarySkills.class, id))
                .collect(Collectors.toSet());
    }

    // --------------------------------------------
    // Audit: fetch from SecurityContext
    // --------------------------------------------
    private String getCurrentUserId() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        return auth.getName(); // userId
    }

    private String getCurrentUserName() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;

        String userId = auth.getName();
        return userAccountRepository.findByUserId(userId)
                .map(User::getName)
                .orElse(null);
    }

    /*********** Import helpers *****************/

    private Long locationId(String name) {
        if (isBlank(name)) return null;

        return locationRepository.findByNameIgnoreCase(name.trim())
                .orElseThrow(() -> new RuntimeException("Invalid location: " + name))
                .getId();
    }

    private Long skillClusterId(String name) {
        if (isBlank(name)) return null;

        return skillClusterRepository.findBySkillClusterIgnoreCase(name.trim())
                .orElseThrow(() -> new RuntimeException("Invalid SkillCluster: " + name))
                .getId();
    }

    private Long hbuId(String name) {
        if (isBlank(name)) return null;

        return hbuRepository.findByHbuIgnoreCase(name.trim())
                .orElseThrow(() -> new RuntimeException("Invalid HBU: " + name))
                .getId();
    }

    private Long countryId(String name) {
        if (isBlank(name)) return null;

        return countryCodeRepository.findByNameIgnoreCase(name.trim())
                .orElseThrow(() -> new RuntimeException("Invalid Country: " + name))
                .getId();
    }

    private Long externalInternalId(String name) {
        if (isBlank(name)) return null;

        return externalInternalRepository.findByExternalInternalIgnoreCase(name.trim())
                .orElseThrow(() -> new RuntimeException("Invalid External/Internal: " + name))
                .getId();
    }

    private Long profileStatusId(String status) {
        if (isBlank(status)) return null;

        return profileStatusRepository.findByNameIgnoreCase(status.trim())
                .orElseThrow(() -> new RuntimeException("Invalid ProfileStatus: " + status))
                .getId();
    }



    private Set<Long> primarySkillIds(String csv) {
        if (csv == null || csv.isBlank()) {
            return Collections.emptySet();
        }

        Set<Long> ids = new HashSet<>();

        for (String name : csv.split(",")) {
            String skillName = name.trim();

            PrimarySkills skill = primarySkillsRepository
                    .findByPrimarySkillsIgnoreCase(skillName)
                    .orElseThrow(() ->
                            new RuntimeException("Invalid Primary Skill: " + skillName)
                    );

            ids.add(skill.getId());
        }

        return ids;
    }

    private Set<Long> secondarySkillIds(String csv) {
        if (csv == null || csv.isBlank()) {
            return Collections.emptySet();
        }

        Set<Long> ids = new HashSet<>();

        for (String name : csv.split(",")) {
            String skillName = name.trim();

            SecondarySkills skill = secondarySkillsRepository
                    .findBySecondarySkillsIgnoreCase(skillName)
                    .orElseThrow(() ->
                            new RuntimeException("Invalid Secondary Skill: " + skillName)
                    );

            ids.add(skill.getId());
        }

        return ids;
    }


    private void validateUniqueness(ProfileCreateRequestDTO dto) {

        if (profileRepository.existsByEmailId(dto.getEmailId())) {
            throw new RuntimeException("email_id already exists: " + dto.getEmailId());
        }

        if (!isBlank(dto.getSapId())
                && profileRepository.existsBySapId(dto.getSapId())) {
            throw new RuntimeException("sap_id already exists: " + dto.getSapId());
        }

        if (!isBlank(dto.getEmpId())
                && profileRepository.existsByEmpId(dto.getEmpId())) {
            throw new RuntimeException("emp_id already exists: " + dto.getEmpId());
        }

        if (dto.getPhoneNumber() != null
                && profileRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new RuntimeException("phone_number already exists: " + dto.getPhoneNumber());
        }

        if (!isBlank(dto.getPanNumber())
                && profileRepository.existsByPanNumberIgnoreCase(dto.getPanNumber())) {
            throw new RuntimeException("pan_number already exists: " + dto.getPanNumber());
        }
    }


    private Profile buildProfileEntity(ProfileCreateRequestDTO dto) {

        Profile p = new Profile();

        p.setCandidateName(dto.getCandidateName());
        p.setEmailId(dto.getEmailId());
        p.setSapId(dto.getSapId());
        p.setEmpId(dto.getEmpId());
        p.setPhoneNumber(dto.getPhoneNumber());
        p.setExperience(dto.getExperience());
        p.setSummary(dto.getSummary());
        p.setPanNumber(dto.getPanNumber());
        p.setIsActive(true);

        if (dto.getSkillClusterId() != null) {
            p.setSkillCluster(em.getReference(SkillCluster.class, dto.getSkillClusterId()));
        }
        if (dto.getLocationId() != null) {
            p.setLocation(em.getReference(Location.class, dto.getLocationId()));
        }
        if (dto.getHbuId() != null) {
            p.setHbu(em.getReference(Hbu.class, dto.getHbuId()));
        }
        if (dto.getExternalInternalId() != null) {
            p.setExternalInternal(em.getReference(ExternalInternal.class, dto.getExternalInternalId()));
        }
        if (dto.getCountryId() != null) {
            p.setCountry(em.getReference(CountryCode.class, dto.getCountryId()));
        }
        if (dto.getProfileStatusId() != null) {
            p.setProfileStatus(em.getReference(ProfileStatus.class, dto.getProfileStatusId()));
        }

        p.setPrimarySkills(toPrimarySkills(dto.getPrimarySkillsIds()));
        p.setSecondarySkills(toSecondarySkills(dto.getSecondarySkillsIds()));

        String userId = getCurrentUserId();
        p.setCreatedByUserId(userId);

        return p;
    }

    private String getCell(Row row, Integer index) {
        if (index == null || index < 0) return null;

        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;

        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private Map<String, Integer> buildHeaderMap(Row headerRow) {
        Map<String, Integer> map = new HashMap<>();
        for (Cell cell : headerRow) {
            map.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
        }
        return map;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private Long parseLong(String in) {
        if (in == null || in.trim().isEmpty()) {
            return null;
        }
        return Long.parseLong(in.trim());
    }

    private Float parseFloat(String in) {
        if (in == null || in.trim().isEmpty()) {
            return null;
        }
        return Float.parseFloat(in.trim());
    }

    private void validateMandatoryHeaders(Map<String, Integer> h) {

        List<String> mandatory = List.of(
                "Candidate_Name",
                "Email_ID",
//                "Phone_Number",
                "Country_Code",
                "Location",
                "Skill_Cluster",
                "HBU",
                "Primary_Skills",
                "Secondary_Skills"
        );

        for (String col : mandatory) {
            if (!h.containsKey(col)) {
                throw new RuntimeException("Missing mandatory column: " + col);
            }
        }

        // ✅ At least one identity column must exist
        if (!h.containsKey("Sap_id") && !h.containsKey("Emp_id")) {
            throw new RuntimeException(
                    "Excel must contain at least one of Sap_id or Emp_id"
            );
        }
    }

    private boolean isRowCompletelyEmpty(Row row) {
        if (row == null) return true;

        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private String getRequired(Row row, Map<String, Integer> h, String col) {
        String val = getCell(row, h.get(col));
        if (val == null || val.isBlank()) {
            throw new RuntimeException(col + " is mandatory");
        }
        return val.trim();
    }

    private LocalDate parseDate(String input) {

        if (input == null || input.isBlank()) {
            throw new RuntimeException("Date is mandatory");
        }

        // ✅ Excel numeric date (e.g. 46118)
        if (input.matches("\\d+")) {
            int excelDate = Integer.parseInt(input);
            return LocalDate.of(1899, 12, 30).plusDays(excelDate);
        }

        List<DateTimeFormatter> formats = List.of(
                DateTimeFormatter.ofPattern("dd-MMM-yy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy")
        );

        for (DateTimeFormatter f : formats) {
            try {
                return LocalDate.parse(input.trim(), f);
            } catch (DateTimeParseException ignored) { }
        }

        throw new RuntimeException("Invalid date format: " + input);
    }


    private Float parseExp(String in) {
        return Float.parseFloat(in.replaceAll("[^0-9.]", ""));
    }

    private void validateUniquenessPanOnly(String pan) {
        if (profileRepository.existsByPanNumberIgnoreCase(pan)) {
            throw new RuntimeException("PAN already exists: " + pan);
        }
    }


}
