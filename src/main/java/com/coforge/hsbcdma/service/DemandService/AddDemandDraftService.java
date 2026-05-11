package com.coforge.hsbcdma.service.DemandService;

import com.coforge.hsbcdma.dto.DemandsDTO.AddDemandDraftCreateRequest;
import com.coforge.hsbcdma.dto.DemandsDTO.AddDemandDraftOnlyCreateRequest;
import com.coforge.hsbcdma.dto.DemandsDTO.AddDemandRrDraftBulkRequest;
import com.coforge.hsbcdma.dto.DemandsDTO.DemandDraftEditRequest;
import com.coforge.hsbcdma.entity.AddDemandDraft;
import com.coforge.hsbcdma.entity.AddDemandRRDraft;
import com.coforge.hsbcdma.entity.Location;
import com.coforge.hsbcdma.entity.User;
import com.coforge.hsbcdma.entity.dropdownEntities.*;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandRRDraftRepository;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandsDraftRepository;
import com.coforge.hsbcdma.repository.UserAccountRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AddDemandDraftService {

    @Autowired
    private AddDemandsDraftRepository addDemandsDraftRepository;

    @Autowired
    private AddDemandRRDraftRepository addDemandRRDraftRepository;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private UserAccountRepository userAccountRepository;


    private static final Logger logger = LoggerFactory.getLogger(AddDemandDraftService.class);


    @Value("${app.jd.storage-path}")
    private String storagePath;

    private Path baseDir;
    private Path draftRootDir;   // <baseDir>/draft

    @PostConstruct
    public void init() {
        baseDir = Paths.get(storagePath).toAbsolutePath().normalize();

        // Create draft folder inside baseDir
        draftRootDir = baseDir.resolve("draft").normalize();

        try {
            Files.createDirectories(baseDir);       // ensures base exists
            Files.createDirectories(draftRootDir);  // ensures <baseDir>/draft exists
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not create JD storage directories: baseDir=" + baseDir + ", draftDir=" + draftRootDir,
                    e
            );
        }
    }


    @Transactional
    public Long createDraftWithRrDrafts(AddDemandDraftCreateRequest req, List<MultipartFile> files) {

        AddDemandDraft draft = new AddDemandDraft();
        draft.setFlag(req.getFlag());

        // NEW: karat flag (nullable, no default)
        draft.setKaratFlag(req.getKaratFlag());

        draft.setNumberOfPositions(req.getNumberOfPositions());
        draft.setExperience(req.getExperience());
        draft.setRemark(req.getRemark());
        draft.setDemandReceivedDate(req.getDemandReceivedDate());
        draft.setCreatedByUserId(getCurrentUserId());
        draft.setCreatedByName(getCurrentUserName());

        // ==========================================================
        // 2) Many-to-One mapping
        // ==========================================================
         draft.setHbu(refOrNull(Hbu.class, req.getHbuId()));
         draft.setHbuSpoc(refOrNull(HbuSpoc.class, req.getHubSpocId()));
         draft.setBand(refOrNull(Band.class, req.getBandId()));
         draft.setPriority(refOrNull(Priority.class, req.getPriorityId()));
         draft.setLob(refOrNull(Lob.class, req.getLobId()));
         draft.setDemandType(refOrNull(DemandType.class, req.getDemandTypeId()));
         draft.setDemandTimeline(refOrNull(DemandTimeLine.class, req.getDemandTimelineId()));
         draft.setExternalInternal(refOrNull(ExternalInternal.class, req.getExternalInternalId()));
         draft.setStatus(refOrNull(Status.class, req.getStatusId()));
         draft.setPod(refOrNull(Pod.class, req.getPodId()));
         draft.setPmoSpoc(refOrNull(PmoSpoc.class, req.getPmoSpocId()));
         draft.setPmo(refOrNull(Pmo.class, req.getPmoId()));
         draft.setSalesSpoc(refOrNull(SalesSpoc.class, req.getSalesSpocId()));
         draft.setHiringManager(refOrNull(HiringManager.class, req.getHiringManagerId()));
         draft.setDeliveryManager(refOrNull(DeliveryManager.class, req.getDeliveryManagerId()));
         draft.setSkillCluster(refOrNull(SkillCluster.class, req.getSkillClusterId()));

        // ==========================================================
        // 3) Many-to-Many mapping
         draft.setPrimarySkills(refSetOrNull(PrimarySkills.class, req.getPrimarySkillIds()));
         draft.setSecondarySkills(refSetOrNull(SecondarySkills.class, req.getSecondarySkillIds()));
         draft.setDemandLocations(refSetOrNull(Location.class, req.getLocationIds()));

        AddDemandDraft savedDraft = addDemandsDraftRepository.save(draft);
        Long draftId = savedDraft.getId();

        logger.info("Draft saved: draftId={}, positions={}", draftId, savedDraft.getNumberOfPositions());

        // Expected RR rows should match numberOfPositions
        int expected = (req.getNumberOfPositions() == null) ? 0 : req.getNumberOfPositions();

        // rrDrafts from request (may be null)
        List<AddDemandDraftCreateRequest.RrDraftRequest> rrList = req.getRrDrafts();

        if (rrList == null) {
            rrList = new ArrayList<>();
        }
        // if client send more that
        if (rrList.size() > expected) {
            throw new IllegalArgumentException(
                    "rrDrafts size (" + rrList.size() + ") cannot be greater than numberOfPositions (" + expected + ")"
            );
        }

        // If client sent less rows than positions
        while (rrList.size() < expected) {
            AddDemandDraftCreateRequest.RrDraftRequest placeholder = new AddDemandDraftCreateRequest.RrDraftRequest();
            placeholder.setRrNumber(null);
            placeholder.setFileIndex(null);
            placeholder.setJdText(null);
            placeholder.setFilenameHint(null);
            rrList.add(placeholder);
        }

        // rrList.size() == expected always
        List<AddDemandRRDraft> rrEntities = new ArrayList<>();

        for (AddDemandDraftCreateRequest.RrDraftRequest rr : rrList) {

            AddDemandRRDraft rrDraft = new AddDemandRRDraft();
            rrDraft.setDraftId(draft);
            rrDraft.setRrNumber(rr.getRrNumber());

            // NEW: map the subcon flag (nullable ok for draft)
            rrDraft.setIsSubconRR(rr.getIsSubconRR());

            String storedName = storeJDForRRDraft(draftId, rr, files);
            rrDraft.setFileName(storedName);
            rrEntities.add(rrDraft);
        }

        addDemandRRDraftRepository.saveAll(rrEntities);
        logger.info("RR rows saved: draftId={}, rowsSaved={}", draftId, rrEntities.size());
        return draftId;
    }


    @Transactional
    public Long editDraftWithRrDrafts(Long draftId, DemandDraftEditRequest req, List<MultipartFile> files) {

        AddDemandDraft draft = addDemandsDraftRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Draft not found: " + draftId));

        logger.info("Edit draft started: draftId={}, userId={}, userName={}",
                draftId, getCurrentUserId(), getCurrentUserName());

        // -------------------------
        // 1) Update draft header (ALL fields present in edit DTO)
        // -------------------------
        draft.setFlag(req.getFlag());

        // NEW: karat flag
        draft.setKaratFlag(req.getKaratFlag());
        
            draft.setNumberOfPositions(req.getNumberOfPositions());


        draft.setExperience(req.getExperience());
        draft.setRemark(req.getRemark());

            draft.setDemandReceivedDate(req.getDemandReceivedDate());

        // header fileName (if you really store something at header level)
//        draft.setFileName(req.getFileName());

        draft.setUpdatedByUserId(getCurrentUserId());
        draft.setUpdatedByName(getCurrentUserName());

        // -------------------------
        // 2) Many-to-one refs
        // -------------------------
        draft.setHbu(refOrNull(Hbu.class, req.getHbuId()));
        draft.setHbuSpoc(refOrNull(HbuSpoc.class, req.getHubSpocId()));
        draft.setBand(refOrNull(Band.class, req.getBandId()));
        draft.setPriority(refOrNull(Priority.class, req.getPriorityId()));
        draft.setLob(refOrNull(Lob.class, req.getLobId()));
        draft.setDemandType(refOrNull(DemandType.class, req.getDemandTypeId()));
        draft.setDemandTimeline(refOrNull(DemandTimeLine.class, req.getDemandTimelineId()));
        draft.setExternalInternal(refOrNull(ExternalInternal.class, req.getExternalInternalId()));
        draft.setStatus(refOrNull(Status.class, req.getStatusId()));
        draft.setPod(refOrNull(Pod.class, req.getPodId()));
        draft.setPmoSpoc(refOrNull(PmoSpoc.class, req.getPmoSpocId()));
        draft.setPmo(refOrNull(Pmo.class, req.getPmoId()));
        draft.setSalesSpoc(refOrNull(SalesSpoc.class, req.getSalesSpocId()));
        draft.setHiringManager(refOrNull(HiringManager.class, req.getHiringManagerId()));
        draft.setDeliveryManager(refOrNull(DeliveryManager.class, req.getDeliveryManagerId()));
        draft.setSkillCluster(refOrNull(SkillCluster.class, req.getSkillClusterId()));

        // -------------------------
        // 3) Many-to-many sets
        // -------------------------
        draft.setPrimarySkills(refSetOrNull(PrimarySkills.class, req.getPrimarySkillIds()));
        draft.setSecondarySkills(refSetOrNull(SecondarySkills.class, req.getSecondarySkillIds()));
        draft.setDemandLocations(refSetOrNull(Location.class, req.getLocationIds()));

        addDemandsDraftRepository.save(draft);
        logger.info("Draft saved: draftId={}", draftId);

        // -------------------------
        // 4) RR Rows sync with numberOfPositions
        // -------------------------
        int expected = (draft.getNumberOfPositions() == null) ? 0 : draft.getNumberOfPositions();

        List<AddDemandRRDraft> existing = addDemandRRDraftRepository.findByDraftId_Id(draftId);

        // if more rows than expected -> delete extra
        if (existing.size() > expected) {
            List<AddDemandRRDraft> toRemove = existing.subList(expected, existing.size());
            addDemandRRDraftRepository.deleteAllInBatch(toRemove);
            existing = existing.subList(0, expected);
        }

        // if less rows -> add placeholders
        if (existing.size() < expected) {
            List<AddDemandRRDraft> add = new ArrayList<>();
            for (int i = existing.size(); i < expected; i++) {
                AddDemandRRDraft rr = new AddDemandRRDraft();
                rr.setDraftId(draft);
                rr.setRrNumber(null);
                rr.setFileName(null);

                // NEW: leave it null for placeholders
                rr.setIsSubconRR(null);

                add.add(rr);
            }
            addDemandRRDraftRepository.saveAll(add);
            existing = addDemandRRDraftRepository.findByDraftId_Id(draftId);
        }

        logger.info("RR sync completed: draftId={}, finalRows={}", draftId, existing.size());
        // index maps
        Map<Long, AddDemandRRDraft> byRrId = existing.stream()
                .filter(r -> r.getId() != null)
                .collect(Collectors.toMap(AddDemandRRDraft::getId, r -> r));

        // -------------------------
        // 5) Apply RR edits
        // -------------------------
        if (req.getRrDrafts() != null && !req.getRrDrafts().isEmpty()) {
            logger.info("Applying RR edits: draftId={}, edits={}", draftId, req.getRrDrafts().size());
            for (DemandDraftEditRequest.RrDraftEditRequest e : req.getRrDrafts()) {

                AddDemandRRDraft rrRow;

                if (e.getRrDraftId() != null) {
                    rrRow = byRrId.get(e.getRrDraftId());
                    if (rrRow == null) {
                        throw new IllegalArgumentException("Invalid rrDraftId: " + e.getRrDraftId());
                    }
                } else if (e.getPositionIndex() != null) {
                    int pos = e.getPositionIndex();
                    if (pos < 1 || pos > existing.size()) {
                        throw new IllegalArgumentException("positionIndex out of range: " + pos);
                    }
                    rrRow = existing.get(pos - 1);
                } else {
                    throw new IllegalArgumentException("rrDraftId or positionIndex is required for RR update");
                }

                // rr number
                rrRow.setRrNumber(e.getRrNumber());

                // NEW: allow toggling isSubconRR only if provided
                if (e.getIsSubconRR() != null) {
                    rrRow.setIsSubconRR(e.getIsSubconRR());
                }


                // clear file
                if (Boolean.TRUE.equals(e.getClearFile())) {
                    rrRow.setFileName(null);
                    // optional: also delete file physically from disk if you want
                    // deleteStoredJDFile(draftId, rrRow.getDemandId(), oldFileName);
                }

                // store new file/text if given
                String newStored = storeJDForRRDraft_Edit(draftId, e, files);
                if (newStored != null) {
                    rrRow.setFileName(newStored);
                }
            }

            addDemandRRDraftRepository.saveAll(existing);

            logger.info("RR edits saved: draftId={}, rows={}", draftId, existing.size());
        }

        return draftId;
    }

//    separate api for draft and rr draft

    @Transactional
    public Long createDraftOnly(AddDemandDraftOnlyCreateRequest req) {

        AddDemandDraft draft = new AddDemandDraft();
        draft.setFlag(req.getFlag());

        // NEW: karat flag
        draft.setKaratFlag(req.getKaratFlag());

        draft.setNumberOfPositions(req.getNumberOfPositions());
        draft.setExperience(req.getExperience());
        draft.setRemark(req.getRemark());
        draft.setDemandReceivedDate(req.getDemandReceivedDate());
        draft.setCreatedByUserId(getCurrentUserId());
        draft.setCreatedByName(getCurrentUserName());

        // Many-to-One
        draft.setHbu(refOrNull(Hbu.class, req.getHbuId()));
        draft.setHbuSpoc(refOrNull(HbuSpoc.class, req.getHubSpocId()));
        draft.setBand(refOrNull(Band.class, req.getBandId()));
        draft.setPriority(refOrNull(Priority.class, req.getPriorityId()));
        draft.setLob(refOrNull(Lob.class, req.getLobId()));
        draft.setDemandType(refOrNull(DemandType.class, req.getDemandTypeId()));
        draft.setDemandTimeline(refOrNull(DemandTimeLine.class, req.getDemandTimelineId()));
        draft.setExternalInternal(refOrNull(ExternalInternal.class, req.getExternalInternalId()));
        draft.setStatus(refOrNull(Status.class, req.getStatusId()));
        draft.setPod(refOrNull(Pod.class, req.getPodId()));
        draft.setPmoSpoc(refOrNull(PmoSpoc.class, req.getPmoSpocId()));
        draft.setPmo(refOrNull(Pmo.class, req.getPmoId()));
        draft.setSalesSpoc(refOrNull(SalesSpoc.class, req.getSalesSpocId()));
        draft.setHiringManager(refOrNull(HiringManager.class, req.getHiringManagerId()));
        draft.setDeliveryManager(refOrNull(DeliveryManager.class, req.getDeliveryManagerId()));
        draft.setSkillCluster(refOrNull(SkillCluster.class, req.getSkillClusterId()));

        // Many-to-Many
        draft.setPrimarySkills(refSetOrNull(PrimarySkills.class, req.getPrimarySkillIds()));
        draft.setSecondarySkills(refSetOrNull(SecondarySkills.class, req.getSecondarySkillIds()));
        draft.setDemandLocations(refSetOrNull(Location.class, req.getLocationIds()));

        AddDemandDraft saved = addDemandsDraftRepository.save(draft);

        logger.info("Draft header saved: draftId={}, positions={}", saved.getId(), saved.getNumberOfPositions());
        return saved.getId();
    }

    @Transactional
    public Long saveRRDraftsForDraft(Long draftId,
                                     AddDemandRrDraftBulkRequest req,
                                     List<MultipartFile> files) {

        AddDemandDraft draft = addDemandsDraftRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Draft not found: " + draftId));

        int expected = (draft.getNumberOfPositions() == null) ? 0 : draft.getNumberOfPositions();

        List<AddDemandRrDraftBulkRequest.RrDraftRequest> rrList =
                (req == null || req.getRrDrafts() == null) ? new ArrayList<>() : req.getRrDrafts();

        if (rrList.size() > expected) {
            throw new IllegalArgumentException(
                    "rrDrafts size (" + rrList.size() + ") cannot be greater than numberOfPositions (" + expected + ")"
            );
        }

        while (rrList.size() < expected) {
            AddDemandRrDraftBulkRequest.RrDraftRequest placeholder = new AddDemandRrDraftBulkRequest.RrDraftRequest();
            placeholder.setRrNumber(null);
            placeholder.setFileName(null);
            placeholder.setJdText(null);
            placeholder.setFilenameHint(null);
            rrList.add(placeholder);
        }

        List<AddDemandRRDraft> rrEntities = new ArrayList<>();

        // NEW *******************************************
        boolean useSingle = Boolean.TRUE.equals(req.getUseSingleJdForAll());

        // Resolve single JD ONCE
        String singleFileName = null;
        String singleText = null;
        String singleHint = null;

        if (useSingle) {
            if (req.getSingleJdText() != null && !req.getSingleJdText().isBlank()) {
                singleText = req.getSingleJdText();
                singleHint = req.getSingleFilenameHint();
            } else if (req.getSingleJdFileName() != null && !req.getSingleJdFileName().isBlank()) {
                singleFileName = req.getSingleJdFileName();
            } else {
                throw new IllegalArgumentException(
                        "useSingleJdForAll=true but no single JD provided"
                );
            }
        }

        // *********************************************************

        for (AddDemandRrDraftBulkRequest.RrDraftRequest rr : rrList) {
            AddDemandRRDraft rrDraft = new AddDemandRRDraft();

            // keep your current mapping (you used setDraftId(draft))
            rrDraft.setDraftId(draft);

            rrDraft.setRrNumber(rr.getRrNumber());

            // NEW: map the subcon flag (nullable ok)
            rrDraft.setIsSubconRR(rr.getIsSubconRR());


            // APPLY SINGLE JD ONLY IF RR DID NOT PROVIDE ONE *********************
            boolean rrHasFile =
                    rr.getFileName() != null && !rr.getFileName().isBlank();
            boolean rrHasText =
                    rr.getJdText() != null && !rr.getJdText().isBlank();

            if (!rrHasFile && !rrHasText && useSingle) {
                if (singleText != null) {
                    rr.setJdText(singleText);
                    rr.setFilenameHint(singleHint);
                } else {
                    rr.setFileName(singleFileName);
                }
            }

            // ******************************************************

            String storedName = storeJDForRRDraft1(draftId, rr, files); // your existing method
            rrDraft.setFileName(storedName);

            rrEntities.add(rrDraft);
        }

        addDemandRRDraftRepository.saveAll(rrEntities);

        logger.info("RR Draft rows saved: draftId={}, rowsSaved={}", draftId, rrEntities.size());
        return draftId;
    }


//    private String storeJDForRRDraft_Edit(Long draftId,
//                                          DemandDraftEditRequest.RrDraftEditRequest rr,
//                                          List<MultipartFile> files) {
//
//        boolean hasFile = rr.getFileIndex() != null;
//        boolean hasText = rr.getJdText() != null && !rr.getJdText().isBlank();
//
//        if (!hasFile && !hasText) return null;
//
//        Path dir = baseDir.resolve("draft")
//                .resolve("draft-" + draftId)
//                .normalize();
//
//        ensureInsideBaseDir(dir);
//        createDirIfNotExists(dir);
//
//        if (hasFile) {
//            if (files == null || files.isEmpty())
//                throw new IllegalArgumentException("fileIndex provided but no files uploaded");
//
//            int idx = rr.getFileIndex();
//            if (idx < 0 || idx >= files.size())
//                throw new IllegalArgumentException("Invalid fileIndex " + idx + ", filesCount=" + files.size());
//
//            MultipartFile file = files.get(idx);
//            if (file == null || file.isEmpty())
//                throw new IllegalArgumentException("Empty file at index " + idx);
//
//            return storeJDFile(dir, file);
//        }
//
//        // text JD
//        return storeJDText(dir, rr.getJdText(), rr.getFilenameHint());
//    }

// for edit files
    private String storeJDForRRDraft_Edit(Long draftId,
                                          DemandDraftEditRequest.RrDraftEditRequest rr,
                                          List<MultipartFile> files) {

        // SAME detection logic as create
        boolean hasFile = (rr.getFileName() != null && !rr.getFileName().isBlank());
        boolean hasText = (rr.getJdText() != null && !rr.getJdText().isBlank());

        // Optional: if edit supports clearFile flag
        // If clearFile=true and no new file/text provided -> return null to clear existing in DB
        if (Boolean.TRUE.equals(rr.getClearFile()) && !hasFile && !hasText) {
            return null;
        }

        if (!hasFile && !hasText) return null;

        Path dir = baseDir.resolve("draft")
                .resolve("draft-" + draftId)
                .normalize();

        ensureInsideBaseDir(dir);
        createDirIfNotExists(dir);

        // 1) If fileName provided -> find matching multipart by original filename
        if (hasFile) {
            if (files == null || files.isEmpty()) {
                throw new IllegalArgumentException(
                        "RR fileName provided but no files uploaded for draftId " + draftId +
                                " rrNumber " + rr.getRrNumber()
                );
            }

            MultipartFile file = findFileByOriginalFilename(files, rr.getFileName());

            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException(
                        "No matching uploaded file found for fileName '" + rr.getFileName() +
                                "' draftId " + draftId + " rrNumber " + rr.getRrNumber()
                );
            }

            return storeJDFileWithPrefix(dir, file, "rr-" + rr.getRrNumber());
        }

        // 2) Else store jdText into .txt file (same as create)
        if (hasText) {
            String hint = (rr.getFilenameHint() != null && !rr.getFilenameHint().isBlank())
                    ? rr.getFilenameHint()
                    : ("rr-" + rr.getRrNumber() + "-jd");

            return storeJDText(dir, rr.getJdText(), "rr-" + rr.getRrNumber() + "_" + hint);
        }

        return null;
    }


    // --------------------------------------------
    // RR JD storage
    // Folder: <baseDir>/draft/draft-<draftId>/
    // Uses rr.fileIndex OR rr.jdText
    // --------------------------------------------
//    private String storeJDForRRDraft(Long draftId,
//                                     AddDemandDraftCreateRequest.RrDraftRequest rr,
//                                     List<MultipartFile> files) {
//
//        boolean hasFile = (rr.getFileIndex() != null);
//        boolean hasText = (rr.getJdText() != null && !rr.getJdText().isBlank());
//
//        if (!hasFile && !hasText) return null;
//        Path dir = baseDir.resolve("draft")
//                .resolve("draft-" + draftId)
//                .normalize();
//
//        ensureInsideBaseDir(dir);
//        createDirIfNotExists(dir);
//
//        // 1) If fileIndex provided, store uploaded file
//        if (hasFile && files != null) {
//            int idx = rr.getFileIndex();
//            if (idx < 0 || idx >= files.size()) {
//                throw new IllegalArgumentException(
//                        "Invalid RR fileIndex " + idx + " for draftId " + draftId + " rrNumber " + rr.getRrNumber()
//                );
//            }
//
//            MultipartFile file = files.get(idx);
//            if (file == null || file.isEmpty()) {
//                throw new IllegalArgumentException(
//                        "Empty RR file for draftId " + draftId + " rrNumber " + rr.getRrNumber()
//                );
//            }
//            return storeJDFileWithPrefix(dir, file, "rr-" + rr.getRrNumber());
//        }

        private String storeJDForRRDraft(Long draftId,
                AddDemandDraftCreateRequest.RrDraftRequest rr,
                List<MultipartFile> files) {

            boolean hasFile = (rr.getFileName() != null);
            boolean hasText = (rr.getJdText() != null && !rr.getJdText().isBlank());

            if (!hasFile && !hasText) return null;
            Path dir = baseDir.resolve("draft")
                    .resolve("draft-" + draftId)
                    .normalize();

            ensureInsideBaseDir(dir);
            createDirIfNotExists(dir);

            if (hasFile) {
                if (files == null || files.isEmpty()) {
                    throw new IllegalArgumentException(
                            "RR fileName provided but no files uploaded for draftId " + draftId +
                                    " rrNumber " + rr.getRrNumber()
                    );
                }

                MultipartFile file = findFileByOriginalFilename(files, rr.getFileName());

                if (file == null || file.isEmpty()) {
                    throw new IllegalArgumentException(
                            "No matching uploaded file found for fileName '" + rr.getFileName() +
                                    "' draftId " + draftId + " rrNumber " + rr.getRrNumber()
                    );
                }

                return storeJDFileWithPrefix(dir, file, "rr-" + rr.getRrNumber());
            }

        // 2) Else store jdText into file
        if (hasText) {
            String hint = (rr.getFilenameHint() != null && !rr.getFilenameHint().isBlank())
                    ? rr.getFilenameHint()
                    : ("rr-" + rr.getRrNumber() + "-jd");

            return storeJDText(dir, rr.getJdText(), "rr-" + rr.getRrNumber() + "_" + hint);
        }

        return null;
    }

    private String storeJDForRRDraft1(Long draftId,
                                     AddDemandRrDraftBulkRequest.RrDraftRequest rr,
                                     List<MultipartFile> files) {

        boolean hasFile = (rr.getFileName() != null);
        boolean hasText = (rr.getJdText() != null && !rr.getJdText().isBlank());

        if (!hasFile && !hasText) return null;
        Path dir = baseDir.resolve("draft")
                .resolve("draft-" + draftId)
                .normalize();

        ensureInsideBaseDir(dir);
        createDirIfNotExists(dir);

        if (hasFile) {
            if (files == null || files.isEmpty()) {
                throw new IllegalArgumentException(
                        "RR fileName provided but no files uploaded for draftId " + draftId +
                                " rrNumber " + rr.getRrNumber()
                );
            }

            MultipartFile file = findFileByOriginalFilename(files, rr.getFileName());

            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException(
                        "No matching uploaded file found for fileName '" + rr.getFileName() +
                                "' draftId " + draftId + " rrNumber " + rr.getRrNumber()
                );
            }

            return storeJDFileWithPrefix(dir, file, "rr-" + rr.getRrNumber());
        }

        // 2) Else store jdText into file
        if (hasText) {
            String hint = (rr.getFilenameHint() != null && !rr.getFilenameHint().isBlank())
                    ? rr.getFilenameHint()
                    : ("rr-" + rr.getRrNumber() + "-jd");

            return storeJDText(dir, rr.getJdText(), "rr-" + rr.getRrNumber() + "_" + hint);
        }

        return null;
    }

    private MultipartFile findFileByOriginalFilename(List<MultipartFile> files, String expectedFileName) {
        if (expectedFileName == null) return null;

        String expected = expectedFileName.trim();

        for (MultipartFile f : files) {
            if (f == null) continue;

            String original = f.getOriginalFilename();
            if (original == null) continue;

            // exact match
            if (original.equals(expected)) {
                return f;
            }
        }

        // OPTIONAL: fallback case-insensitive match (uncomment if needed)
    /*
    for (MultipartFile f : files) {
        if (f == null) continue;
        String original = f.getOriginalFilename();
        if (original != null && original.equalsIgnoreCase(expected)) {
            return f;
        }
    }
    */

        return null;
    }

//    private String storeJDFileWithPrefix(Path dir, MultipartFile file, String prefix) {
//
//        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("jd.txt");
//        String base = sanitizeBaseName(stripExtension(original));
//
//        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
//
//        // rr-101_profile_20260204_164603.txt
//        String finalName = sanitizeBaseName(prefix) + "_" + base + "_" + ts + ".txt";
//
//        Path target = dir.resolve(finalName).normalize();
//        ensureInsideBaseDir(target);
//
//        try {
//            Path tmp = dir.resolve(finalName + ".tmp");
//            Files.copy(file.getInputStream(), tmp, StandardCopyOption.REPLACE_EXISTING);
//            Files.move(tmp, target,
//                    StandardCopyOption.REPLACE_EXISTING,
//                    StandardCopyOption.ATOMIC_MOVE);
//            return finalName;
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to store RR JD file", e);
//        }
//    }

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

    // --------------------------------------------
    // Store File (Atomic move)
    // NOTE: Your logic saves as ".txt" always.
    // --------------------------------------------
    private String storeJDFile(Path dir, MultipartFile file) {
        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("jd.txt");
        String base = sanitizeBaseName(stripExtension(original));
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String finalName = base + "_" + ts + ".txt";

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
            throw new RuntimeException("Failed to store JD file", e);
        }
    }

    // --------------------------------------------
    // Store Text
    // --------------------------------------------
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
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            return finalName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JD text", e);
        }
    }

    // --------------------------------------------
    // Helpers (same as your style)
    // --------------------------------------------


    // ============================================================
    // Helpers
    // ============================================================
    private <T> T refOrNull(Class<T> cls, Long id) {
        return (id == null) ? null : em.getReference(cls, id);
    }

    private <T> Set<T> refSetOrNull(Class<T> cls, List<Long> ids) {
        if (ids == null) return null;
        return ids.stream()
                .filter(Objects::nonNull)
                .map(id -> em.getReference(cls, id))
                .collect(Collectors.toSet());
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
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + dir, e);
        }
    }

    private void ensureInsideBaseDir(Path path) {
        Path normalized = path.toAbsolutePath().normalize();
        if (!normalized.startsWith(baseDir)) {
            throw new IllegalArgumentException("Invalid path outside storage base dir: " + normalized);
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



}
