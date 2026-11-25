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
import com.coforge.hsbcdma.util.AESUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
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
    private final PasswordEncoder passwordEncoder; // ✅ ADD THIS

    @Value("${app.encryption.key}")
    private String systemKey;

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
        System.out.println("RAW PAN INPUT: " + dto.getPanNumber());
        if (dto.getPanNumber() != null && !dto.getPanNumber().isBlank()) {

            try {


                String key = getSystemEncryptionKey();
                System.out.println("KEY USED IN CREATE: " + key);

                String pan = dto.getPanNumber().trim();

                // ✅ ALWAYS ENCRYPT (no guessing)
                String encryptedPan = AESUtil.encrypt(pan, key);

                p.setPanNumber(encryptedPan);

                System.out.println("ENCRYPTED PAN SAVED: " + encryptedPan);

//                p.setPanNumber(encryptedPan);

            } catch (Exception e) {
                throw new RuntimeException("PAN encryption failed");
            }
        }
        System.out.println("ENCRYPTED PAN SAVED: " + p.getPanNumber());
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
        ProfileStatus defaultStatus =
                profileStatusRepository.findByNameIgnoreCase("Pending with HBU")
                        .orElseThrow(() -> new RuntimeException("Default status not found"));

        p.setProfileStatus(defaultStatus);

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
        // ===== TA fields =====
        p.setL1InterviewDate(dto.getL1InterviewDate());
        p.setCurrentLocation(dto.getCurrentLocation());
        p.setOfficialNP(dto.getOfficialNP());
        p.setNegotiableNpLwd(dto.getNegotiableNpLwd());
        p.setRecruiter(dto.getRecruiter());

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

// ✅ detach to prevent update
//        entityManager.detach(saved);


        try {
            notificationService.notifyAllUsersExceptActor(
                    "PROFILE",
                    saved.getId(),
                    "New Profile Created",
                    "Profile " + saved.getCandidateName()
                            + " has been created",
                    getCurrentUserId()
            );
        } catch (Exception e) {
            // optional: log
        }


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

    private String getSystemEncryptionKey() {
        return deriveKey(systemKey);  // Hardcoded
    }


    public String deriveKey(String input) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = sha.digest(input.getBytes(StandardCharsets.UTF_8));

            // ✅ 16 bytes for AES-128
            return Base64.getEncoder().encodeToString(Arrays.copyOf(key, 16));

        } catch (Exception e) {
            throw new RuntimeException("Key generation failed");
        }
    }
    private String getUserEncryptionKey() {

        String userId = getCurrentUserId();

        User user = userAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Always derive key
        return deriveKey(user.getUserId());
    }

    public String decryptPan(Long profileId, String password) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String userId = auth.getName();

        User user = userAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Step 1: validate password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // ✅ Step 2: fetch profile
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        System.out.println("DB PAN: " + profile.getPanNumber());
        if (profile.getPanNumber() == null || profile.getPanNumber().isBlank()) {
            throw new RuntimeException("PAN not available");
        }

        // ✅ Step 3: decrypt using system key
        try {

            String key = getSystemEncryptionKey();


            System.out.println("---- DEBUG ----");
            System.out.println("Encrypted PAN: " + profile.getPanNumber());
            System.out.println("System Key: " + key);
            System.out.println("Decoded Key Length: " + Base64.getDecoder().decode(key).length);
            System.out.println("KEY USED IN DECRYPT: " + getSystemEncryptionKey());

            String result = AESUtil.decrypt(profile.getPanNumber(), key);

            System.out.println("Decrypted PAN: " + result);

            return result;


        } catch (Exception e) {
            throw new RuntimeException("Decryption failed");
        }
    }



    public long getPendingWithHbuCount(Long hbuId) {
        return profileRepository.countByStatusAndHbu(
                "Pending with HBU",
                hbuId
        );
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

                    // ✅  AUDIT CALL
                    genericAuditService.recordCreate(
                            AuditHistory.EntityType.PROFILE,
                            profile,
                            profileSnapshotBuilder,
                            profileAuditMeta,
                            getCurrentUserId()
                    );
                    success++;
                } catch (Exception ex) {

                    String reason = ex.getMessage();

                    Map<String, Object> err = new HashMap<>();
                    err.put("row", i + 1);
                    err.put("candidate", getCell(row, h.get("Candidate Name"))); // nullable safe
                    err.put("reason", reason);

                    errors.add(err);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Invalid Excel file", e);
        }


        try {
            if (success > 0) {
                notificationService.notifyAllUsersExceptActor(
                        "PROFILE",
                        null, // no single reference ID
                        "Bulk Profile Upload",
                        success + " profiles uploaded successfully via TA bulk upload",
                        getCurrentUserId()
                );
            }
        } catch (Exception e) {
            // optional log
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
                "Coforge Location",
                "Official NP",
                "Negotiable NP/LWD",
                "Recruiter",
                "Current Status",
                "SAP ID"
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
        String coforgeLocation      = getRequired(r, h, "Coforge Location");
        String officialNP           = getRequired(r, h, "Official NP");
        String negotiableLwd     = getRequired(r, h, "Negotiable NP/LWD");
        String recruiter            = getRequired(r, h, "Recruiter");
        String profileStatusName    = getRequired(r, h, "Current Status");
        String sapId = getRequired(r, h, "SAP ID");


        // ✅ uniqueness check using existing method
        if (profileRepository.existsBySapId(sapId)) {
            throw new RuntimeException("SAP ID already exists: " + sapId);
        }


        Profile p = new Profile();
        p.setCandidateName(candidateName);
        p.setExperience(experience);
        p.setOfficialNP(officialNP);
        p.setNegotiableNpLwd(negotiableLwd);
        p.setL1InterviewDate(l1Date);
        p.setRecruiter(recruiter);
        p.setSapId(sapId);
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

    public Map<String, Object> bulkUploadRDGProfiles(MultipartFile file) {

        List<Map<String, Object>> errors = new ArrayList<>();
        int success = 0;

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> h = buildHeaderMap(headerRow);

            validateRDGMandatoryHeaders(h);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (isRowCompletelyEmpty(row)) continue;

                try {
                    Profile profile = buildRDGProfileFromRow(row, h);
                    profileRepository.save(profile);

                    // ✅  AUDIT CALL
                    genericAuditService.recordCreate(
                            AuditHistory.EntityType.PROFILE,
                            profile,
                            profileSnapshotBuilder,
                            profileAuditMeta,
                            getCurrentUserId()
                    );
                    success++;
                } catch (Exception ex) {


                    Map<String, Object> err = new HashMap<>();
                    err.put("row", i + 1);
                    err.put("empId", getCell(row, h.get("E Code"))); // can be null ✅
                    err.put("reason", ex.getMessage());

                    errors.add(err);

                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();  // ✅ TEMP for debugging
            throw new RuntimeException(e.getMessage(), e);
        }

        return Map.of(
                "totalRows", success + errors.size(),
                "successCount", success,
                "failureCount", errors.size(),
                "errors", errors
        );
    }

    private void validateRDGMandatoryHeaders(Map<String, Integer> h) {


        List<String> mandatory = List.of(
                "E Code",
                "Name"
        );

        for (String col : mandatory) {
            if (!h.containsKey(col)) {
                throw new RuntimeException("Missing mandatory column: " + col);
            }
        }

    }

    private Profile buildRDGProfileFromRow(Row r, Map<String, Integer> h) {

        String empId = getRequired(r, h, "E Code");
        String name  = getRequired(r, h, "Name");

        // ✅ uniqueness check
        if (profileRepository.existsByEmpId(empId)) {
            throw new RuntimeException("empId already exists: " + empId);
        }

        Profile p = new Profile();

        p.setEmpId(empId);
        p.setCandidateName(getCell(r, h.get("Name")));

        // ✅ Skill → SkillCluster
        String skill = getCell(r, h.get("Skill"));
        if (!isBlank(skill)) {
            p.setSkillCluster(em.getReference(
                    SkillCluster.class,
                    skillClusterId(skill)
            ));
        }

        // ✅ Primary Skills (optional)
        String primarySkillsCsv = getCell(r, h.get("Primary Skills"));

        if (!isBlank(primarySkillsCsv)) {
            Set<PrimarySkills> skills = parsePrimarySkills(primarySkillsCsv);
            p.setPrimarySkills(skills);
        }

        // ✅ Source
        String source = getCell(r, h.get("Source"));
        if (!isBlank(source)) {
            p.setSource(em.getReference(
                    SourceMaster.class,
                    sourceId(source)
            ));
        }

        // ✅ Overall Status
        String status = getCell(r, h.get("Overall Status"));
        if (!isBlank(status)) {
            p.setOverallStatusRdg(em.getReference(
                    OverallStatusMaster.class,
                    overallStatusId(status)
            ));
        }

        // ✅ Location
        String location = getCell(r, h.get("Location"));
        if (!isBlank(location)) {
            p.setLocation(em.getReference(
                    Location.class,
                    locationId(location)
            ));
        }

        // ✅ Date fields
        p.setDateOfSubmission(parseDateSafe(getCell(r, h.get("Date of submission"))));
        p.setWeekOf(parseDateSafe(getCell(r, h.get("Week Of"))));
        p.setStatusDate(parseDateSafe(getCell(r, h.get("Status Date"))));

        // ✅ Practice, Band
        p.setPractice(getCell(r, h.get("Practice")));
        p.setBand(getCell(r, h.get("Band")));
//      Internal by defaulr for rdg
        p.setExternalInternal(em.getReference(
                ExternalInternal.class, externalInternalId("Internal")
        ));

        // ✅ Ageing
        Integer ageing = parseIntegerSafe(getCell(r, h.get("Ageing")));
        p.setAgeing(ageing);

        // ✅ Ageing Range
        p.setAgeing(parseIntegerSafe(getCell(r, h.get("Ageing"))));

        // ✅ default values (REQUIRED for your entity)
        p.setIsActive(true);
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
        // ===== TA fields =====
        setIfNotNull(dto.getL1InterviewDate(), p::setL1InterviewDate);
        setIfNotNull(dto.getCurrentLocation(), p::setCurrentLocation);
        setIfNotNull(dto.getOfficialNP(), p::setOfficialNP);
        setIfNotNull(dto.getNegotiableNpLwd(), p::setNegotiableNpLwd);
        setIfNotNull(dto.getRecruiter(), p::setRecruiter);

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


    public PageResponse getAllProfiles(int page, int size, Long hbuId, String sortBy, String sortDir) {

        Sort sort;

        if ("ageing".equalsIgnoreCase(sortBy)) {
            // ✅ ageing DESC = createdAt ASC
            sort = "DESC".equalsIgnoreCase(sortDir)
                    ? Sort.by(Sort.Direction.ASC, "createdAt")
                    : Sort.by(Sort.Direction.DESC, "createdAt");

        } else {
            sort = Sort.by(
                    "DESC".equalsIgnoreCase(sortDir)
                            ? Sort.Direction.DESC
                            : Sort.Direction.ASC,
                    sortBy
            );
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Profile> profilesPage =
                profileRepository.findAllWithRefsByHbu(hbuId, pageable);

        List<GetAllProfilesResponse> content = profilesPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

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
                .ageing(calculateAgeing(p)) // ✅ dynamic valuep.s
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

    private Long sourceId(String name) {
        return em.createQuery(
                        "SELECT s.id FROM SourceMaster s WHERE LOWER(s.name)=LOWER(:name)", Long.class)
                .setParameter("name", name.trim())
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid Source: " + name));
    }

    private Long overallStatusId(String name) {
        return em.createQuery(
                        "SELECT o.id FROM OverallStatusMaster o WHERE LOWER(o.name)=LOWER(:name)", Long.class)
                .setParameter("name", name.trim())
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid Overall Status: " + name));
    }

    private LocalDate parseDateSafe(String val) {
        if (isBlank(val)) return null;
        return parseDate(val);
    }

    private Integer parseIntegerSafe(String val) {
        if (isBlank(val)) return null;
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Set<PrimarySkills> parsePrimarySkills(String csv) {

        Set<PrimarySkills> skills = new HashSet<>();

        for (String name : csv.split(",")) {
            String skillName = name.trim();

            if (skillName.isEmpty()) continue;

            PrimarySkills skill = primarySkillsRepository
                    .findByPrimarySkillsIgnoreCase(skillName)
                    .orElseThrow(() ->
                            new RuntimeException("Invalid Primary Skill: " + skillName)
                    );

            skills.add(skill);
        }

        return skills;
    }


    private int calculateAgeing(Profile p) {

        LocalDate startDate = p.getCreatedAt() != null
                ? p.getCreatedAt().toLocalDate()
                : LocalDate.now();

        LocalDate endDate = LocalDate.now();

        // ✅ Stop ageing if stopped
        if (Boolean.TRUE.equals(p.getAgeingStopped()) && p.getAgeingStopDate() != null) {
            endDate = p.getAgeingStopDate();
        }

        return (int) ChronoUnit.DAYS.between(startDate, endDate);
    }

    @Transactional
    public void takeAction(Long profileId, String action) {

        Profile p = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        if ("REJECTED".equalsIgnoreCase(action)) {

            p.setActionStatus("REJECTED");

            // ✅ stop ageing
            p.setAgeingStopped(true);
            p.setAgeingStopDate(LocalDate.now());

        } else if ("ACCEPTED".equalsIgnoreCase(action)) {

            p.setActionStatus("ACCEPTED");

            // ✅ ageing continues
            p.setAgeingStopped(false);
            p.setAgeingStopDate(null);

        } else {
            throw new RuntimeException("Invalid action");
        }

        p.setUpdatedByUserId(getCurrentUserId());

        profileRepository.save(p);
    }

}
