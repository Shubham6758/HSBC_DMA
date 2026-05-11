package com.coforge.hsbcdma.service.DemandService;
import com.coforge.hsbcdma.audit.AddDemandAuditService;
import com.coforge.hsbcdma.audit.AddDemandSnapshot1Builder;
import com.coforge.hsbcdma.audit.AuditHistory;
import com.coforge.hsbcdma.audit.Demand.AddDemandAuditMeta;
import com.coforge.hsbcdma.audit.core.GenericAuditService;
import com.coforge.hsbcdma.dto.DemandsDTO.*;
import com.coforge.hsbcdma.entity.AddDemand;
import com.coforge.hsbcdma.entity.Location;
import com.coforge.hsbcdma.entity.User;
import com.coforge.hsbcdma.entity.dropdownEntities.*;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandRRDraftRepository;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandRepository;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandsDraftRepository;
import com.coforge.hsbcdma.repository.UserAccountRepository;
import com.coforge.hsbcdma.repository.dropdownRepository.StatusRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddNewDemandService {

    private static final Logger log = LoggerFactory.getLogger(AddNewDemandService.class);

    private final EntityManager em;
    private final AddDemandRepository finalRepo;
    private final UserAccountRepository userAccountRepository;
    private final AddDemandsDraftRepository draftRepo;
    private final AddDemandRRDraftRepository rrDraftRepo;
    private final AddDemandAuditService auditService;
    private final AddDemandSnapshot1Builder snapshotBuilder;

    private final com.coforge.hsbcdma.audit.Demand.AddDemandSnapshotBuilder addDemandSnapshotBuilder;
    private final AddDemandAuditMeta addDemandAuditMeta;
    private final GenericAuditService genericAuditService;

    private final StatusRepository statusRepository;

    @Value("${app.jd.storage-path}")
    private String storagePath;

    private Path baseDir;

    @PostConstruct
    public void init() {
        baseDir = Paths.get(storagePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create JD storage directory: " + baseDir, e);
        }
    }
    @Transactional(readOnly = true)
    public Step1NextResponseDTO step1NextGenerateIds(AddNewDemandStep1DTO dto) {

        if (dto.getNumberOfPositions() == null || dto.getNumberOfPositions() <= 0) {
            throw new IllegalArgumentException("numberOfPositions must be > 0");
        }

        String prefix = resolveLobPrefix(dto.getLobId());
        Status openStatus = getOpenStatusOrThrow();
        int n = dto.getNumberOfPositions();

        List<DemandRRDTO> assignments = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            DemandRRDTO out = new DemandRRDTO();
            out.setTempId(String.valueOf(i));
            out.setDisplayDemandId(prefix + "-TEMP-" + i);
            out.setRrNumber(null);
            assignments.add(out);
        }

        Step1NextResponseDTO resp = new Step1NextResponseDTO();
        resp.setStep1(dto);                // echo request
        resp.setAssignments(assignments);
        return resp;
    }

    @Transactional
    public PublishResponseDTO step2SubmitFinal(Step2SubmitPayloadDTO payload, List<MultipartFile> files) {

        if (payload == null) throw new IllegalArgumentException("payload is required");

        if (payload.getNumberOfPositions() == null || payload.getNumberOfPositions() <= 0)
            throw new IllegalArgumentException("numberOfPositions must be > 0");

        if (payload.getRrs() == null || payload.getRrs().isEmpty())
            throw new IllegalArgumentException("rrs list is required");

        if (!payload.getNumberOfPositions().equals(payload.getRrs().size()))
            throw new IllegalArgumentException("rrs size must match numberOfPositions");

        // Validate rrNumbers present + unique in request
        Set<Long> seen = new HashSet<>();
        for (RrJdRequestDTO rr : payload.getRrs()) {
            if (rr.getRrNumber() == null)
                throw new IllegalArgumentException("rrNumber is required for each position");
            if (!seen.add(rr.getRrNumber()))
                throw new IllegalArgumentException("Duplicate rrNumber in payload: " + rr.getRrNumber());
        }

        // Validate rrNumbers not already in DB (rr_number is UNIQUE)
        List<Long> existing = finalRepo.findExistingRrNumbers(seen);
        if (!existing.isEmpty())
            throw new IllegalArgumentException("rrNumber already exists in DB: " + existing);

        String prefix = resolveLobPrefix(payload.getLobId());
        Status openStatus = getOpenStatusOrThrow();
        List<String> displayIds = new ArrayList<>(payload.getNumberOfPositions());

        // -------------------------------------------------------
        // NEW: Resolve single JD (either text or a single file)
        // -------------------------------------------------------
        final boolean useSingle = Boolean.TRUE.equals(payload.getUseSingleJdForAll());

        // Pre-index uploaded files by original filename for quick lookup
        Map<String, MultipartFile> filesByName = new HashMap<>();
        if (files != null) {
            for (MultipartFile f : files) {
                if (f != null && !f.isEmpty() && f.getOriginalFilename() != null) {
                    filesByName.put(f.getOriginalFilename(), f);
                }
            }
        }

        String resolvedSingleFileName = null; // we only need the name; actual bytes come from 'files' list later
        String resolvedSingleText = null;
        String resolvedSingleHint = null;

        if (useSingle) {
            boolean hasSingleText = payload.getSingleJdText() != null && !payload.getSingleJdText().isBlank();

            if (hasSingleText) {
                // Text-based single JD
                resolvedSingleText = payload.getSingleJdText();
                resolvedSingleHint = (payload.getSingleFilenameHint() != null && !payload.getSingleFilenameHint().isBlank())
                        ? payload.getSingleFilenameHint()
                        : "jd-all";
            } else {
                // File-based single JD
                if (payload.getSingleJdFileName() != null && !payload.getSingleJdFileName().isBlank()) {
                    if (!filesByName.containsKey(payload.getSingleJdFileName())) {
                        throw new IllegalArgumentException("singleJdFileName '" + payload.getSingleJdFileName() + "' not found in uploaded files");
                    }
                    resolvedSingleFileName = payload.getSingleJdFileName();
                } else {
                    // If exactly one file uploaded, use it implicitly
                    if (filesByName.size() == 1) {
                        resolvedSingleFileName = filesByName.keySet().iterator().next();
                    } else {
                        throw new IllegalArgumentException("useSingleJdForAll=true but ambiguous: upload exactly one file or provide singleJdFileName or singleJdText");
                    }
                }
            }
        }
        // -------------------------------------------------------


        for (RrJdRequestDTO rr : payload.getRrs()) {

            AddDemand fin = new AddDemand();

            // do NOT set demandId
            fin.setRrNumber(rr.getRrNumber());

            // copy common fields
            applyCommonFields(payload, fin);


            // NEW: set p1 flag date based on priority at creation time
            applyP1FlagDateOnCreate(fin);


            // NEW: set Subcon flag *per position* (default to false/RR when null)
            Boolean isSubcon = rr.getIsSubconRR();
            fin.setIsSubconRR(isSubcon != null ? isSubcon : Boolean.FALSE);


            // audit
            fin.setCreatedByUserId(getCurrentUserId());
            fin.setCreatedByName(getCurrentUserName());
            fin.setUpdatedByUserId(getCurrentUserId());
            fin.setUpdatedByName(getCurrentUserName());
            fin.setStatus(openStatus);

            // Save first to generate demand_id (via your DB trigger/logic)
            finalRepo.saveAndFlush(fin);
            em.refresh(fin);

            Long realDemandId = fin.getDemandId(); // generated

            // -------------------------------------------------------
            // NEW: If RR did not specify JD, fallback to single JD
            // -------------------------------------------------------
            boolean rrHasFile = rr.getFileName() != null && !rr.getFileName().isBlank();
            boolean rrHasText = rr.getJdText() != null && !rr.getJdText().isBlank();

            if (!rrHasFile && !rrHasText && useSingle) {
                if (resolvedSingleText != null) {
                    rr.setJdText(resolvedSingleText);
                    if (rr.getFilenameHint() == null || rr.getFilenameHint().isBlank()) {
                        rr.setFilenameHint(resolvedSingleHint);
                    }
                } else if (resolvedSingleFileName != null) {
                    rr.setFileName(resolvedSingleFileName);
                }
                // else: nothing to set (but by construction, when useSingle=true we will have one of the two)
            }
            // -------------------------------------------------------


            // store JD using fileName mapping logic (no index)
            String storedFileName = storeJDForFinalDemand(realDemandId, rr, files);
            fin.setFileName(storedFileName);

            finalRepo.save(fin);
//            for history
            auditService.recordCreate(fin);

            // response only displayDemandId
            displayIds.add(prefix + "-" + realDemandId);
        }


        PublishResponseDTO out = new PublishResponseDTO();
        out.setDisplayDemandIds(displayIds);
        return out;

    }

    /**
     * when priority p2 then p1 flag date null otherwise current date
     * */
    private void applyP1FlagDateOnCreate(AddDemand fin) {
        String priorityCode = fin.getPriority() != null ? fin.getPriority().getName() : null;
        if("P2".equalsIgnoreCase(priorityCode)){
            fin.setP1FlagDate(null);
        }
        else{
            fin.setP1FlagDate(LocalDate.now());
        }
    }

    private Status getOpenStatusOrThrow() {
        return statusRepository.findByStatusIgnoreCase("Open Positions")
                .orElseThrow(() -> new IllegalStateException(
                        "Master data missing: status 'Open Positions' not found. Seed it in status table."));
    }

    private void applyCommonFields(Step2SubmitPayloadDTO p, AddDemand fin) {

        setIfNotNull(p.getBandId(), id -> fin.setBand(ref(Band.class, id)));
        setIfNotNull(p.getPriorityId(), id -> fin.setPriority(ref(Priority.class, id)));
        setIfNotNull(p.getLobId(), id -> fin.setLob(ref(Lob.class, id)));
        setIfNotNull(p.getSubLobId(), id -> fin.setSubLob(ref(SubLob.class, id)));
        setIfNotNull(p.getDemandTypeId(), id -> fin.setDemandType(ref(DemandType.class, id)));
        setIfNotNull(p.getDemandTimelineId(), id -> fin.setDemandTimeline(ref(DemandTimeLine.class, id)));
        setIfNotNull(p.getExternalInternalId(), id -> fin.setExternalInternal(ref(ExternalInternal.class, id)));
        setIfNotNull(p.getStatusId(), id -> fin.setStatus(ref(Status.class, id)));
        setIfNotNull(p.getPodId(), id -> fin.setPod(ref(Pod.class, id)));
        setIfNotNull(p.getPmoSpocId(), id -> fin.setPmoSpoc(ref(PmoSpoc.class, id)));
        setIfNotNull(p.getSalesSpocId(), id -> fin.setSalesSpoc(ref(SalesSpoc.class, id)));
        setIfNotNull(p.getHiringManagerId(), id -> fin.setHiringManager(ref(HiringManager.class, id)));
        setIfNotNull(p.getProjectManagerId(), id -> fin.setProjectManager(ref(ProjectManager.class, id)));
        setIfNotNull(p.getDeliveryManagerId(), id -> fin.setDeliveryManager(ref(DeliveryManager.class, id)));
        setIfNotNull(p.getSkillClusterId(), id -> fin.setSkillCluster(ref(SkillCluster.class, id)));
        setIfNotNull(p.getPmoId(), id -> fin.setPmo(ref(Pmo.class, id)));
        setIfNotNull(p.getHbuId(), id -> fin.setHbu(ref(Hbu.class, id)));
        setIfNotNull(p.getHbuSpocId(), id -> fin.setHbuSpoc(ref(HbuSpoc.class, id)));

        // VARCHAR now => String
        fin.setExperience(p.getExperience());

        fin.setRemark(p.getRemark());
        fin.setDemandReceivedDate(LocalDate.now());


        // NEW: isSubconRR (default to RR when null)
        if (p.getIsSubconRR() != null) {
            fin.setIsSubconRR(p.getIsSubconRR());
        } else {
            fin.setIsSubconRR(Boolean.FALSE);
        }


        // NEW: karat_flag (nullable). If p.getKaratFlag() is null, DB stores NULL
        if (p.getKaratFlag() != null) {
            fin.setKaratFlag(p.getKaratFlag());
        }


        fin.setPrimarySkills(new HashSet<>(
                nonNullList(p.getPrimarySkillsId()).stream()
                        .map(id -> em.getReference(PrimarySkills.class, id))
                        .collect(Collectors.toSet())
        ));

        fin.setSecondarySkills(new HashSet<>(
                nonNullList(p.getSecondarySkillsId()).stream()
                        .map(id -> em.getReference(SecondarySkills.class, id))
                        .collect(Collectors.toSet())
        ));

        fin.setDemandLocations(new HashSet<>(
                nonNullList(p.getDemandLocationId()).stream()
                        .map(id -> em.getReference(Location.class, id))
                        .collect(Collectors.toSet())
        ));
    }


//    private void validateTempIds(List<Step2DemandUpdateDTO> assignments, int expectedCount) {
//        Set<String> seen = new HashSet<>();
//        for (Step2DemandUpdateDTO a : assignments) {
//            String t = a.getTempId();
//            if (t == null || t.isBlank()) throw new IllegalArgumentException("tempId is required");
//            if (!t.matches("\\d+")) throw new IllegalArgumentException("tempId must be numeric: " + t);
//            int val = Integer.parseInt(t);
//            if (val < 1 || val > expectedCount) {
//                throw new IllegalArgumentException("tempId out of range: " + t);
//            }
//            if (!seen.add(t)) {
//                throw new IllegalArgumentException("Duplicate tempId: " + t);
//            }
//        }
//    }


    // ---------------------------------------------------
// Edit Demand (RR unique, experience String, JD by fileName)
// ---------------------------------------------------
    @Transactional
    public EditDemandResponseDTO editDemandById(Long id, EditDemandPayloadDTO dto, List<MultipartFile> files) {
        AddDemand demand = finalRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demand not found for id: " + id));
        Map<String, Object> oldSnap = addDemandSnapshotBuilder.snapshot(demand);

        // capture old priority before update
        String oldPriorityCode = demand.getPriority() != null ? demand.getPriority().getName() : null;

        // 0) RR uniqueness validation (because rr_number is UNIQUE)
        if (dto.getRrNumber() != null) {
            Long newRr = dto.getRrNumber();
            Long currentRr = demand.getRrNumber();

            // validate only if rr is changing
            if (currentRr == null || !newRr.equals(currentRr)) {
                boolean exists = finalRepo.existsByRrNumberAndIdNot(newRr, demand.getId());
                if (exists) {
                    throw new IllegalArgumentException("rrNumber already exists: " + newRr);
                }
            }
        }

        // 1) Update editable scalar fields
        setIfNotNull(dto.getRrNumber(), demand::setRrNumber);
        setIfNotNull(dto.getExperience(), demand::setExperience); // String now
        setIfNotNull(dto.getRemark(), demand::setRemark);


        // NEW: toggle Subcon flag
        setIfNotNull(dto.getIsSubconRR(), demand::setIsSubconRR);

        // NEW: karat_flag (nullable)
        setIfNotNull(dto.getKaratFlag(), demand::setKaratFlag);



        // FK mappings (editable)
        setIfNotNull(dto.getBandId(), v -> demand.setBand(ref(Band.class, v)));
        setIfNotNull(dto.getPriorityId(), v -> demand.setPriority(ref(Priority.class, v)));
        setIfNotNull(dto.getLobId(), v -> demand.setLob(ref(Lob.class, v)));
        setIfNotNull(dto.getSubLobId(), v -> demand.setSubLob(ref(SubLob.class, v)));
        setIfNotNull(dto.getDemandTypeId(), v -> demand.setDemandType(ref(DemandType.class, v)));
        setIfNotNull(dto.getDemandTimelineId(), v -> demand.setDemandTimeline(ref(DemandTimeLine.class, v)));
        setIfNotNull(dto.getExternalInternalId(), v -> demand.setExternalInternal(ref(ExternalInternal.class, v)));
        setIfNotNull(dto.getStatusId(), v -> demand.setStatus(ref(Status.class, v)));
        setIfNotNull(dto.getPodId(), v -> demand.setPod(ref(Pod.class, v)));
        setIfNotNull(dto.getPmoSpocId(), v -> demand.setPmoSpoc(ref(PmoSpoc.class, v)));
        setIfNotNull(dto.getSalesSpocId(), v -> demand.setSalesSpoc(ref(SalesSpoc.class, v)));
        setIfNotNull(dto.getHiringManagerId(), v -> demand.setHiringManager(ref(HiringManager.class, v)));
        setIfNotNull(dto.getProjectManagerId(), v -> demand.setProjectManager(ref(ProjectManager.class, v)));
        setIfNotNull(dto.getDeliveryManagerId(), v -> demand.setDeliveryManager(ref(DeliveryManager.class, v)));
        setIfNotNull(dto.getSkillClusterId(), v -> demand.setSkillCluster(ref(SkillCluster.class, v)));
        setIfNotNull(dto.getPmoId(), v -> demand.setPmo(ref(Pmo.class, v)));
        setIfNotNull(dto.getHbuId(), v -> demand.setHbu(ref(Hbu.class, v)));
        setIfNotNull(dto.getHbuSpocId(), v -> demand.setHbuSpoc(ref(HbuSpoc.class, v)));


        // 2) Many-to-many replace semantics
        if (dto.getPrimarySkillsId() != null) {
            demand.setPrimarySkills(dto.getPrimarySkillsId().isEmpty()
                    ? new HashSet<>()
                    : dto.getPrimarySkillsId().stream()
                    .map(x -> em.getReference(PrimarySkills.class, x))
                    .collect(Collectors.toSet()));
        }

        if (dto.getSecondarySkillsId() != null) {
            demand.setSecondarySkills(dto.getSecondarySkillsId().isEmpty()
                    ? new HashSet<>()
                    : dto.getSecondarySkillsId().stream()
                    .map(x -> em.getReference(SecondarySkills.class, x))
                    .collect(Collectors.toSet()));
        }

        if (dto.getDemandLocationId() != null) {
            demand.setDemandLocations(dto.getDemandLocationId().isEmpty()
                    ? new HashSet<>()
                    : dto.getDemandLocationId().stream()
                    .map(x -> em.getReference(Location.class, x))
                    .collect(Collectors.toSet()));
        }

        // 3) JD update (fileName based)
        Long realDemandId = demand.getDemandId();
        if (realDemandId == null) {
            throw new IllegalStateException("demandId is null for id: " + id);
        }

        if (Boolean.TRUE.equals(dto.getClearJd())) {
            demand.setFileName(null);
        } else {

            boolean hasFile = dto.getFileName() != null && !dto.getFileName().isBlank();
            boolean hasText = dto.getJdText() != null && !dto.getJdText().isBlank();

            if (hasFile || hasText) {
                Long rrToUse = (dto.getRrNumber() != null) ? dto.getRrNumber() : demand.getRrNumber();

                RrJdRequestDTO rr = new RrJdRequestDTO();
                rr.setRrNumber(rrToUse);
                rr.setFileName(dto.getFileName());
                rr.setJdText(dto.getJdText());
                rr.setFilenameHint(dto.getFilenameHint());

                String storedName = storeJDForFinalDemand(realDemandId, rr, files);
                demand.setFileName(storedName);
            }
        }

        // 4) Audit
        demand.setUpdatedByUserId(getCurrentUserId());
        demand.setUpdatedByName(getCurrentUserName());


        // 4) Apply P1 flag date logic
        applyP1FlagDateIfNeeded(demand, oldPriorityCode);


        // 5) Save
        finalRepo.save(demand);


//        auditService.recordUpdate(demand, oldSnap);


        genericAuditService.recordUpdate(
                AuditHistory.EntityType.ADD_DEMAND,
                AuditHistory.Action.UPDATE_DEMAND,
                demand,
                oldSnap,
                addDemandSnapshotBuilder,
                addDemandAuditMeta,
                getCurrentUserId()
        );




        // 6) Response
        String prefix = resolveLobPrefix(demand.getLob() != null ? demand.getLob().getId() : null);

        EditDemandResponseDTO out = new EditDemandResponseDTO();
        out.setId(demand.getId());
        out.setDemandId(demand.getDemandId());
        out.setDisplayDemandId(prefix + "-" + demand.getDemandId());
        out.setRrNumber(demand.getRrNumber());
        out.setFileName(demand.getFileName());
        return out;
    }


    /**
     * Copy functionality business logic*/
    @Transactional(readOnly = true)
    public AddDemandCopyDTO copyDemand(Long sourceDemandPkId) {

        AddDemand source = finalRepo.findById(sourceDemandPkId)
                .orElseThrow(() ->
                        new RuntimeException("Demand not found with id " + sourceDemandPkId)
                );

        AddDemandCopyDTO dto = new AddDemandCopyDTO();

        /* ---------- BASIC FIELDS ---------- */
        dto.setIsSubconRR(source.getIsSubconRR());
        dto.setKaratFlag(source.getKaratFlag());
        dto.setP1FlagDate(source.getP1FlagDate());
        dto.setExperience(source.getExperience());
        dto.setRemark("Copied from Demand PK ID " + sourceDemandPkId);

        /* ---------- BUSINESS ---------- */
        dto.setDemandId(source.getDemandId());
        dto.setRrNumber(source.getRrNumber());
        dto.setDemandReceivedDate(source.getDemandReceivedDate());

        /* ---------- MASTER REFs ---------- */
        dto.setHbu(toRef(source.getHbu(), Hbu::getHbu));
        dto.setHbuSpoc(toRef(source.getHbuSpoc(), HbuSpoc::getName));
        dto.setBand(toRef(source.getBand(), Band::getName));
        dto.setPriority(toRef(source.getPriority(), Priority::getName));
        dto.setLob(toRef(source.getLob(), Lob::getName));
        dto.setSubLob(toRef(source.getSubLob(), SubLob::getName));
        dto.setDemandType(toRef(source.getDemandType(), DemandType::getName));
        dto.setDemandTimeline(toRef(source.getDemandTimeline(), DemandTimeLine::getName));
        dto.setExternalInternal(toRef(source.getExternalInternal(), ExternalInternal::getName));
        dto.setStatus(toRef(source.getStatus(), Status::getStatus));
        dto.setPod(toRef(source.getPod(), Pod::getName));
        dto.setPmo(toRef(source.getPmo(), Pmo::getName));
        dto.setPmoSpoc(toRef(source.getPmoSpoc(), PmoSpoc::getName));
        dto.setSalesSpoc(toRef(source.getSalesSpoc(), SalesSpoc::getName));
        dto.setHiringManager(toRef(source.getHiringManager(), HiringManager::getName));
        dto.setDeliveryManager(toRef(source.getDeliveryManager(), DeliveryManager::getName));
        dto.setSkillCluster(toRef(source.getSkillCluster(), SkillCluster::getName));
        dto.setProjectManager(toRef(source.getProjectManager(), ProjectManager::getName));

        /* ---------- MANY TO MANY ---------- */
        dto.setPrimarySkills(
                toRefSet(source.getPrimarySkills(), PrimarySkills::getName)
        );

        dto.setSecondarySkills(
                toRefSet(source.getSecondarySkills(), SecondarySkills::getName)
        );

        dto.setDemandLocations(
                toRefSet(source.getDemandLocations(), Location::getName)
        );

        return dto; // ✅ preview only, no DB insert
    }


    /**
     *  When priority change from p2 to p1 or p0 then set p1flag to current date
     */
    private void applyP1FlagDateIfNeeded(AddDemand demand, String oldPriorityCode) {
        String newPriorityCode  = demand.getPriority() != null ? demand.getPriority().getName() : null;
        boolean movedFromP2ToP0OrP1 = "P2".equalsIgnoreCase(oldPriorityCode)
                && ("P0".equalsIgnoreCase(newPriorityCode) || "P1".equalsIgnoreCase(newPriorityCode));
        if(movedFromP2ToP0OrP1){
            demand.setP1FlagDate(LocalDate.now());
        }
    }

    // --------------------------------------------
    // JD storage (same style, folder per demandId)
    // --------------------------------------------
    private String storeJDForFinalDemand(Long demandId,
                                         RrJdRequestDTO rr,
                                         List<MultipartFile> files) {

        boolean hasFile = (rr.getFileName() != null && !rr.getFileName().isBlank());
        boolean hasText = (rr.getJdText() != null && !rr.getJdText().isBlank());

        if (!hasFile && !hasText) return null;

        Path dir = baseDir.resolve("demands")
                .resolve("demand-" + demandId)
                .normalize();

        ensureInsideBaseDir(dir);
        createDirIfNotExists(dir);

        if (hasFile) {
            if (files == null || files.isEmpty()) {
                throw new IllegalArgumentException(
                        "fileName provided but no files uploaded for demandId " + demandId +
                                " rrNumber " + rr.getRrNumber()
                );
            }

            MultipartFile file = findFileByOriginalFilename(files, rr.getFileName());

            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException(
                        "No matching uploaded file found for fileName '" + rr.getFileName() +
                                "' demandId " + demandId + " rrNumber " + rr.getRrNumber()
                );
            }

            // prefix with rrNumber like your draft logic
            return storeJDFileWithPrefix(dir, file, "rr-" + rr.getRrNumber());
        }

        // else store jdText into a file
        String hint = (rr.getFilenameHint() != null && !rr.getFilenameHint().isBlank())
                ? rr.getFilenameHint()
                : ("rr-" + rr.getRrNumber() + "-jd");

        return storeJDText(dir, rr.getJdText(), "rr-" + rr.getRrNumber() + "_" + hint);
    }


    private MultipartFile findFileByOriginalFilename(List<MultipartFile> files, String expectedFileName) {
        if (expectedFileName == null) return null;

        String expected = expectedFileName.trim();

        for (MultipartFile f : files) {
            if (f == null) continue;
            String original = f.getOriginalFilename();
            if (original == null) continue;

            if (original.equals(expected)) {
                return f; // exact match
            }
        }
        return null;
    }

    private String storeJDFileWithPrefix(Path dir, MultipartFile file, String prefix) {

        validateJdUpload(file);

        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("jd.txt");
        String base = sanitizeBaseName(stripExtension(original));
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        String ext = getSafeDotExtension(original); // <-- keeps .pdf/.docx/.xlsx/.pptx/.png etc.

        // rr-101_profile_20260204_164603.pdf (example)
        String finalName = sanitizeBaseName(prefix) + "_" + base + "_" + ts + ext;

        Path target = dir.resolve(finalName).normalize();
        ensureInsideBaseDir(target);

        try {
            Path tmp = dir.resolve(finalName + ".tmp");
            Files.copy(file.getInputStream(), tmp, StandardCopyOption.REPLACE_EXISTING);
            Files.move(tmp, target,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
            return finalName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store RR JD file", e);
        }
    }

    private String resolveLobPrefix(Long lobId) {
        if (lobId == null) return "NA";
        Lob lob = em.getReference(Lob.class, lobId);
        String val = lob.getLob();
        return (val == null || val.isBlank()) ? "NA" : val.trim();
    }

    // --------------------------------------------
    // JD storage (same style, folder per demandId)
    // --------------------------------------------
    private String storeJDForDemand(Long demandId, Step2DemandUpdateDTO a, List<MultipartFile> files) {

        boolean hasFile = (a.getFileIndex() != null);
        boolean hasText = (a.getJdText() != null && !a.getJdText().isBlank());

        if (!hasFile && !hasText) return null;

        Path dir = baseDir.resolve("demands").resolve("demand-" + demandId).normalize();
        ensureInsideBaseDir(dir);
        createDirIfNotExists(dir);

        if (hasFile && files != null) {
            int idx = a.getFileIndex();
            if (idx < 0 || idx >= files.size()) {
                throw new IllegalArgumentException("Invalid fileIndex " + idx + " for demandId " + demandId);
            }
            MultipartFile file = files.get(idx);
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Empty file for demandId " + demandId);
            }
            return storeJDFile(dir, file);
        }

        if (hasText) {
            return storeJDText(dir, a.getJdText(), a.getFilenameHint());
        }

        return null;
    }

    private String storeJDFile(Path dir, MultipartFile file) {

        validateJdUpload(file);

        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("jd.txt");
        String base = sanitizeBaseName(stripExtension(original));
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        String ext = getSafeDotExtension(original);

        String finalName = base + "_" + ts + ext;

        Path target = dir.resolve(finalName).normalize();
        ensureInsideBaseDir(target);

        try {
            Path tmp = dir.resolve(finalName + ".tmp");
            Files.copy(file.getInputStream(), tmp, StandardCopyOption.REPLACE_EXISTING);
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            return finalName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store JD file", e);
        }
    }

    private String storeJDText(Path dir, String jdText, String filenameHint) {
        String base = (filenameHint != null && !filenameHint.isBlank())
                ? stripExtension(filenameHint)
                : "jd";
        base = sanitizeBaseName(base);

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String finalName = base + "_" + ts + ".txt";

        Path target = dir.resolve(finalName).normalize();
        ensureInsideBaseDir(target);

        try {
            Files.writeString(target, jdText, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return finalName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JD text", e);
        }
    }

    // --------------------------------------------
    // Helpers
    // --------------------------------------------
    private <T> T ref(Class<T> entityClass, Long id) { return em.getReference(entityClass, id); }
    private <T> void setIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }    private void setIfNotNull(Long id, java.util.function.Consumer<Long> setter) { if (id != null) setter.accept(id); }

    private <T> List<T> nonNullList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    private String stripExtension(String filename) {
        String name = filename.trim();
        int dot = name.lastIndexOf('.');
        return (dot > 0) ? name.substring(0, dot) : name;
    }
    private String sanitizeBaseName(String base) {
        String cleaned = base.replaceAll("[^a-zA-Z0-9_-]", "_");
        return cleaned.isBlank() ? "jd" : cleaned;
    }
    private void createDirIfNotExists(Path dir) {
        try { Files.createDirectories(dir); }
        catch (IOException e) { throw new RuntimeException("Failed to create directory: " + dir, e); }
    }
    private void ensureInsideBaseDir(Path path) {
        Path normalized = path.toAbsolutePath().normalize();
        if (!normalized.startsWith(baseDir)) {
            throw new IllegalArgumentException("Invalid path outside storage base dir: " + normalized);
        }
    }

//    private String getExtension(String filename) {
//        if (filename == null) return "";
//        String name = filename.trim();
//        int dot = name.lastIndexOf('.');
//        if (dot < 0 || dot == name.length() - 1) return "";
//        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
//    }

    // returns ".pdf" / ".docx" etc. default ".txt"

    private void validateJdUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File too large. Max allowed: " + MAX_FILE_SIZE_BYTES + " bytes");
        }

        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("");
        String ext = getExtension(original); // no dot, lowercase

        if (ext.isBlank() || !ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("Unsupported file extension: " + ext +
                    ". Allowed: " + ALLOWED_EXT);
        }

        String mime = Optional.ofNullable(file.getContentType()).orElse("").toLowerCase(Locale.ROOT);

        // Some clients send null/unknown MIME; in that case extension gate is still enforced.
        if (!mime.isBlank() && !ALLOWED_MIME.contains(mime)) {
            // Optional: tolerate octet-stream if ext is allowed
            if (!"application/octet-stream".equals(mime)) {
                throw new IllegalArgumentException("Unsupported content type: " + mime +
                        ". Allowed: " + ALLOWED_MIME);
            }
        }

        // Strong check: magic bytes sniff
        if (!isSignatureValidFor(ext, file)) {
            throw new IllegalArgumentException("File signature does not match extension: " + ext);
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        String name = filename.trim();
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) return "";
        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    // returns ".pdf" / ".docx" etc. default ".txt"
    private String getSafeDotExtension(String filename) {
        String ext = getExtension(filename);
        if (ext.isBlank() || !ALLOWED_EXT.contains(ext)) return ".txt";
        return "." + ext;
    }

    private boolean isSignatureValidFor(String ext, MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            byte[] header = in.readNBytes(12);

            // TEXT
            if (ext.equals("txt")) return true;

            // RTF usually starts with "{\rtf"
            if (ext.equals("rtf")) {
                String s = new String(header, StandardCharsets.US_ASCII);
                return s.startsWith("{\\rtf");
            }

            // PDF starts with "%PDF"
            if (ext.equals("pdf")) {
                String s = new String(header, StandardCharsets.US_ASCII);
                return s.startsWith("%PDF");
            }

            // JPG starts with FF D8 FF
            if (ext.equals("jpg") || ext.equals("jpeg")) {
                return header.length >= 3 &&
                        (header[0] & 0xFF) == 0xFF &&
                        (header[1] & 0xFF) == 0xD8 &&
                        (header[2] & 0xFF) == 0xFF;
            }

            // PNG starts with 89 50 4E 47 0D 0A 1A 0A
            if (ext.equals("png")) {
                byte[] sig = new byte[]{(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
                if (header.length < sig.length) return false;
                for (int i = 0; i < sig.length; i++) {
                    if (header[i] != sig[i]) return false;
                }
                return true;
            }

            // DOC (legacy) & PPT (legacy) are often OLE2 compound docs:
            // D0 CF 11 E0 A1 B1 1A E1
            if (ext.equals("doc") || ext.equals("ppt")) {
                byte[] ole = new byte[]{
                        (byte)0xD0,(byte)0xCF,0x11,(byte)0xE0,(byte)0xA1,(byte)0xB1,0x1A,(byte)0xE1
                };
                if (header.length < ole.length) return false;
                for (int i = 0; i < ole.length; i++) {
                    if (header[i] != ole[i]) return false;
                }
                return true;
            }

            // OOXML formats are ZIP containers => start with 'PK'
            // docx/xlsx/pptx
            if (ext.equals("docx") || ext.equals("xlsx") || ext.equals("pptx")) {
                return header.length >= 2 && header[0] == 'P' && header[1] == 'K';
            }

            // If extension is allowed but we didn't define signature rule, allow.
            return true;

        } catch (IOException e) {
            throw new RuntimeException("Could not validate file signature", e);
        }
    }

    // --------------------------------------------
    // Audit: fetch from SecurityContext (uses your CustomUserDetails)
    // --------------------------------------------
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

    // Allowed extensions (lowercase, no dots)
    private static final Set<String> ALLOWED_EXT = Set.of(
            "txt", "pdf", "doc", "docx", "rtf",
            "jpg", "jpeg", "png",
            "xlsx", "ppt", "pptx"
    );

    // Allowed MIME types (lowercase)
    private static final Set<String> ALLOWED_MIME = Set.of(
            "text/plain",
            "application/pdf",

            // Legacy Office
            "application/msword",
            "application/vnd.ms-powerpoint",

            // OOXML Office (zip container)
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",

            "application/rtf",
            "image/jpeg",
            "image/png"
    );

    // Optional: max size protection (example: 10 MB)
    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024;


//    Helpers

    private <T> RefDTO toRef(T entity, Function<T, String> nameExtractor) {
        if (entity == null) return null;
        try {
            Method getId = entity.getClass().getMethod("getId");
            Long id = (Long) getId.invoke(entity);
            return new RefDTO(id, nameExtractor.apply(entity));
        } catch (Exception e) {
            throw new RuntimeException("Error mapping RefDTO", e);
        }
    }

    private <T> Set<RefDTO> toRefSet(Set<T> entities, Function<T, String> nameExtractor) {
        if (entities == null) return Collections.emptySet();
        return entities.stream()
                .map(e -> toRef(e, nameExtractor))
                .collect(Collectors.toSet());
    }

}






