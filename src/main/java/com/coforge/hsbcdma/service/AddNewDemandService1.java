package com.coforge.hsbcdma.service;

import com.coforge.hsbcdma.dto.DemandsDTO.DemandsFilterRequest;
import com.coforge.hsbcdma.dto.DemandsDTO.GetAllDemandsResponse;
import com.coforge.hsbcdma.dto.DemandsDTO.PageResponse;
import com.coforge.hsbcdma.dto.DemandsDTO.RefDTO;
import com.coforge.hsbcdma.entity.AddDemand;
import com.coforge.hsbcdma.repository.AddNewDemandRepository;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandRepository;
import com.coforge.hsbcdma.repository.DummyRepository;
import com.coforge.hsbcdma.repository.LocationRepository;
import com.coforge.hsbcdma.repository.dropdownRepository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is a service class to insert new demands into database.
 * Created By : pratish.b
 */
@Service
public class AddNewDemandService1 {

    @Autowired
    private LobRepository lobRepository;
    @Autowired
    private AddNewDemandRepository addNewDemandRepository;
    @Autowired
    private DummyRepository dummyRepository;

    @Autowired
    private AddDemandRepository addDemandRepository;


    @Autowired
    private SkillClusterRepository skillClusterRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PrimarySkillsRepository primarySkillsRepository;

    @Autowired
    private SecondarySkillsRepository secondarySkillsRepository;

    @Autowired
    private DemandTypeRepository demandTypeRepository;

    @PersistenceContext
    private EntityManager em;


    @Value("${file.upload-dir}")
    private String uploadDir;



    private static final Logger logger = LoggerFactory.getLogger(AddNewDemandService1.class);

    /**
     * This method generates new demand_id based on numberOfPositions and creates a list
     * of DemandRRDTO objects to map demand_id with RR_Number wherein RR number is entered corresponding
     * to each demand_id.
     * Created By : pratish.b
     */
//    public void buildDemandIdsToNumberOfPositions(AddNewDemandDTO addNewDemandDTO){
//
//        String lob = addNewDemandDTO.getLob();
//        int numberOfPositions = addNewDemandDTO.getNoOfPositions();
//        logger.info("Updating DemandRRDTO list for LOB {} and Number Of Positions {} ",lob,numberOfPositions);
//        Optional<Lob> lobOpt = lobRepository.findByLob(lob);
//        if(!lobOpt.isPresent()){
//            throw new RuntimeException("Invalid line Of Service (LOB).");
//        }
//        Integer currentDemandSequence = lobOpt.get().getCurrentDemandIdSequence();
//        AtomicInteger counter = new AtomicInteger(currentDemandSequence);
//        List<DemandRRDTO> demandRRDTOList = new ArrayList<>(numberOfPositions);
//
//        IntStream.rangeClosed(1, numberOfPositions).forEach(i -> {
//            DemandRRDTO dd = new DemandRRDTO();
//            dd.setDemandId(lob+"-"+counter.getAndIncrement());
//            demandRRDTOList.add(dd);
//        });
//        addNewDemandDTO.setDemandRRDTOList(demandRRDTOList);
//    }
//
//    /**
//     * This method would create new demands in database AddNewDemand table.
//     * Created By : pratish.b
//     */
//    @Transactional
//    public void insertNewDemandsInDB(MultipartFile file, AddNewDemandDTO addNewDemandDTO) throws IOException {
//        if (addNewDemandDTO == null) {
//            throw new RuntimeException("AddNewDemandDTO is null");
//        }
//
//        for (DemandRRDTO demandRRDTO : addNewDemandDTO.getDemandRRDTOList()) {
//
//            AddNewDemand addNewDemand = new AddNewDemand();
//
//            addNewDemand.setLob(addNewDemandDTO.getLob());
//            addNewDemand.setNoOfPositions(addNewDemandDTO.getNoOfPositions());
//            addNewDemand.setSkillCluter(addNewDemandDTO.getSkillCluster());
//            addNewDemand.setPrimarySkills(addNewDemandDTO.getPrimarySkills());
//            addNewDemand.setSecondarySkills(addNewDemandDTO.getSecondarySkills());
//            addNewDemand.setDemandReceivedDate(addNewDemandDTO.getDemandReceivedDate());
//            addNewDemand.setHiringManager(addNewDemandDTO.getHiringManager());
//            addNewDemand.setSalesSpoc(addNewDemandDTO.getSalesSpoc());
//            addNewDemand.setDeliveryManager(addNewDemandDTO.getDeliveryManager());
//            addNewDemand.setPmo(addNewDemandDTO.getPmo());
//            addNewDemand.setHbu(addNewDemandDTO.getHbu());
//            addNewDemand.setDemandType(addNewDemandDTO.getDemandType());
//            addNewDemand.setDemandTimeline(addNewDemandDTO.getDemandTimeline());
//            addNewDemand.setProdProgramName(addNewDemandDTO.getProdProgramName());
//            addNewDemand.setDemandLocation(addNewDemandDTO.getDemandLocation());
//            addNewDemand.setExperience(addNewDemandDTO.getExperience());
//            addNewDemand.setPriority(addNewDemandDTO.getPriority());
//            addNewDemand.setPriorityComment(addNewDemandDTO.getPriorityComment());
//            addNewDemand.setPm(addNewDemand.getPm());
//            addNewDemand.setBand(addNewDemandDTO.getBand());
//            addNewDemand.setP1Age(addNewDemandDTO.getP1Age());
//            addNewDemand.setCurrentProfileShared(addNewDemandDTO.getCurrentProfileShared());
//            addNewDemand.setExternalInternal(addNewDemandDTO.getExternalInternal());
//            addNewDemand.setStatus(addNewDemandDTO.getStatus());
//            addNewDemand.setPmoSpoc(addNewDemandDTO.getPmoSpoc());
//            addNewDemand.setRemark(addNewDemandDTO.getRemark());
//
//            //File upload for each demand_id
//            if (file != null && !file.isEmpty()) {
//                addNewDemand.setFileName(file.getOriginalFilename());
//                addNewDemand.setFileContentType(file.getContentType());
//                addNewDemand.setData(file.getBytes());
//            }
//
//            addNewDemand.setDemandId(demandRRDTO.getDemandId());
//            addNewDemand.setRrNumber(demandRRDTO.getRrNumber());
//
//            //Save demand_id and RR_number with other data into AddNewDemand table
//            addNewDemandRepository.save(addNewDemand);
//        }
//
//        //finally update CURRENT_DEMAND_ID_SEQUENCE in LOB table for the corresponding LOB
//        Optional<Lob> lobOpt = lobRepository.findByLob(addNewDemandDTO.getLob());
//        if (lobOpt.isPresent()) {
//            Lob lob = lobOpt.get();
//            Integer currentIdSeq = lob.getCurrentDemandIdSequence();
//            lob.setCurrentDemandIdSequence(currentIdSeq + addNewDemandDTO.getNoOfPositions());
//            //Update currentDemandId in Lob table
//            lobRepository.save(lob);
//        }
//    }
//    /**
//     * This is a dummy method. Not to be use in application. For test purpose only.
//     * Created By : pratish.b
//     */
//        @Transactional(Transactional.TxType.REQUIRES_NEW)
//        public void dummyInsertDB(MultipartFile file, DummyDTO dummyDTO) throws IOException {
//
//            if (dummyDTO == null) {
//                throw new RuntimeException("DummyDTO is null");
//            }
//            DummyEntity dummyEntity = new DummyEntity();
//            //File upload for each demand_id
//            if (file != null && !file.isEmpty()) {
//                dummyEntity.setFileName(file.getOriginalFilename());
//                dummyEntity.setFileContentType(file.getContentType());
//                dummyEntity.setData(file.getBytes());
//            }
//            dummyEntity.setLob(dummyDTO.getLob());
//            dummyEntity.setDecisionDate(dummyDTO.getDecisionDate());
//            dummyRepository.save(dummyEntity);//Save entity to database along with attached file
//        }
//    /**
//     * This is a dummy method. Not to be use in application. For test purpose only.
//     * Created By : pratish.b
//     */
//    @Transactional(Transactional.TxType.REQUIRES_NEW)
//    public DummyDTO getDummyDataFromDB() throws IOException {
//
//        List<DummyEntity> entities = dummyRepository.findAll();
//        DummyDTO dummyDTO = new DummyDTO();
//        for(DummyEntity dummyEntity : entities) {
//            dummyDTO.setLob(dummyEntity.getLob());
//            dummyDTO.setDecisionDate(dummyEntity.getDecisionDate());
//        }
//        return dummyDTO;
//    }
//
//
//    public void buildDemandIdsToNumberOfPositions1(AddDemandRequestDTO addNewDemandDTO) {
//
//        Long lobId = addNewDemandDTO.getLobId();   // <-- Use lobId now
//        Integer numberOfPositions = addNewDemandDTO.getNoOfPositions();
//
//        logger.info("Updating DemandRRDTO list for LOB_ID {} and Number Of Positions {} ", lobId, numberOfPositions);
//
//        if (lobId == null) {
//            throw new IllegalArgumentException("LOB ID is required.");
//        }
//        if (numberOfPositions <= 0) {
//            throw new IllegalArgumentException("Number of positions must be > 0");
//        }
//
//        // 1) Fetch LOB entity using ID
//        Lob lobEntity = lobRepository.findById(lobId)
//                .orElseThrow(() -> new RuntimeException("Invalid line Of Service (LOB ID): " + lobId));
//
//        // 2) Take the LOB name/code from entity (change getter as per your Lob class)
//        String lobName = lobEntity.getLob(); // OR getName()/getLobName()/getLobCode()
//
//        // 3) Get sequence and generate IDs
//        Integer currentDemandSequence = lobEntity.getCurrentDemandIdSequence();
//        if (currentDemandSequence == null) currentDemandSequence = 1;
//
//        AtomicInteger counter = new AtomicInteger(currentDemandSequence);
//        List<DemandRRDTO> demandRRDTOList = new ArrayList<>(numberOfPositions);
//
//        IntStream.rangeClosed(1, numberOfPositions).forEach(i -> {
//            DemandRRDTO dd = new DemandRRDTO();
//            dd.setDemandId(lobName + "-" + counter.getAndIncrement());
//            demandRRDTOList.add(dd);
//        });
//
//        addNewDemandDTO.setDemandRRDTOList(demandRRDTOList);
//    }


//    @Transactional
//    public void insertIntoDB1(List<MultipartFile> files,AddDemandRequestDTO addDemandRequestDTO) throws IOException {
//
//// ---------- validations ----------
//        if (addDemandRequestDTO == null) {
//            throw new IllegalArgumentException("AddDemandRequestDTO is null");
//        }
//        if (addDemandRequestDTO.getDemandRRDTOList() == null || addDemandRequestDTO.getDemandRRDTOList().isEmpty()) {
//            throw new IllegalArgumentException("demandRRDTOList cannot be null/empty");
//        }
//        if (files == null) files = List.of();
//
//        // If you want 1-to-1 mapping: file[i] -> rr[i]
//        if (!files.isEmpty() && files.size() != addDemandRequestDTO.getDemandRRDTOList().size()) {
//            throw new IllegalArgumentException(
//                    "files.size() (" + files.size() + ") must match demandRRDTOList.size() (" + addDemandRequestDTO.getDemandRRDTOList().size() + ")"
//            );
//        }
//
//// ---------- create ID-only references (NO DB SELECT) ----------
//        Lob lobRef = addDemandRequestDTO.getLobId() == null ? null : em.getReference(Lob.class, addDemandRequestDTO.getLobId());
//        SkillCluster clusterRef = addDemandRequestDTO.getSkillClusterId() == null ? null : em.getReference(SkillCluster.class, addDemandRequestDTO.getSkillClusterId());
//        DemandTimeLine timelineRef = addDemandRequestDTO.getDemandTimelineId() == null ? null : em.getReference(DemandTimeLine.class, addDemandRequestDTO.getDemandTimelineId());
//        HiringManager hiringManagerRef = addDemandRequestDTO.getHiringManagerId() == null ? null : em.getReference(HiringManager.class, addDemandRequestDTO.getHiringManagerId());
//        DeliveryManager deliveryManagerRef = addDemandRequestDTO.getDeliveryManagerId() == null ? null : em.getReference(DeliveryManager.class, addDemandRequestDTO.getDeliveryManagerId());
//        Pmo pmoRef = addDemandRequestDTO.getPmoId() == null ? null : em.getReference(Pmo.class, addDemandRequestDTO.getPmoId());
//        PmoSpoc pmoSpocRef = addDemandRequestDTO.getPmoSpocId() == null ? null : em.getReference(PmoSpoc.class, addDemandRequestDTO.getPmoSpocId());
//        SalesSpoc salesSpocRef = addDemandRequestDTO.getSalesSpocId() == null ? null : em.getReference(SalesSpoc.class, addDemandRequestDTO.getSalesSpocId());
//
//        List<PrimarySkills> primarySkillRefs = toRefs(PrimarySkills.class, addDemandRequestDTO.getPrimarySkillIds());
//        List<SecondarySkills> secondarySkillRefs = toRefs(SecondarySkills.class, addDemandRequestDTO.getSecondarySkillIds());
//        List<Location> locationRefs = toRefs(Location.class, addDemandRequestDTO.getLocationIds());
//
//        // ---------- base folder ----------
//        String dateFolder = (addDemandRequestDTO.getDemandReceivedDate() != null ? addDemandRequestDTO.getDemandReceivedDate() : LocalDateTime.now()).toString();
//        Path basePath = Paths.get(uploadDir, dateFolder).toAbsolutePath().normalize();
//        Files.createDirectories(basePath);
//
//        // ---------- build entities ----------
//        List<AddDemand> rows = new ArrayList<>(addDemandRequestDTO.getDemandRRDTOList().size());
//
//        for (int i = 0; i < addDemandRequestDTO.getDemandRRDTOList().size(); i++) {
//
//            DemandRRDTO rr = addDemandRequestDTO.getDemandRRDTOList().get(i);
//
//            AddDemand e = new AddDemand();
//            // Common fields
////            DemandType demandType = new DemandType();
////            demandType.setDemandType(addDemandRequestDTO.getDemandType());
////            e.setDemandType(demandTypeRepository.getReferenceById(addDemandRequestDTO.getDemandTypeId()));
////            e.setExternalInternal(addDemandRequestDTO.getExternalInternal());
////
////            e.setBand(String.valueOf(addDemandRequestDTO.getBand()));
////            e.(addDemandRequestDTO.getPod());
//            e.setLob(lobRef);
//
////            e.setNoOfPositions(addDemandRequestDTO.getNoOfPositions());
//            e.setFlag(addDemandRequestDTO.getFlag());
//
//            e.setSkillCluster(clusterRef);
////            e.setPrimarySkills(primarySkillRefs);
////            e.setSecondarySkills(secondarySkillRefs);
//
////            e.setDemandLocation(locationRefs);
//
//            e.setHiringManager(hiringManagerRef);
//            e.setDeliveryManager(deliveryManagerRef);
//            e.setPmo(pmoRef);
//            e.setPmoSpoc(pmoSpocRef);
//            e.setSalesSpoc(salesSpocRef);
//
//            e.setDemandReceivedDate(addDemandRequestDTO.getDemandReceivedDate());
//            e.setDemandTimeline(timelineRef);
//
//            e.setRemark(addDemandRequestDTO.getRemark());
//
//            // RR-specific
////            e.setDemandId(rr.getDemandId());
//            e.setRrNumber(rr.getRrNumber());
//
//            // ---------- FILE SAVE ON DISK ----------
//            if (!files.isEmpty()) {
//                MultipartFile file = files.get(i);
//
//                if (file != null && !file.isEmpty()) {
//
//                    // Create a folder per RR (safe name)
//                    String rrFolderName = safeFolderName(rr.getDemandId() + "_" + rr.getRrNumber());
//                    Path rrFolderPath = basePath.resolve(rrFolderName).normalize();
//                    Files.createDirectories(rrFolderPath);
//
//                    // Clean filename and make it unique to avoid overwrite
//                    String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
//                    String uniqueName = UUID.randomUUID() + "_" + originalName;
//
//                    // Prevent path traversal
//                    Path targetPath = rrFolderPath.resolve(uniqueName).normalize();
//                    if (!targetPath.startsWith(rrFolderPath)) {
//                        throw new SecurityException("Invalid file path detected: " + originalName);
//                    }
//
//                    // Save to disk
//                    Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
//
//                    // Store only metadata + path in DB
//                    e.setFileName(originalName);
//                }
//            }
//
//            rows.add(e);
//        }
//
//        addDemandRepository.saveAll(rows);
//    }
//
//    public List<GetAllDemandsResponse>getAllDemands(){
//        List<AddDemand> demands = addDemandRepository.findAllWithRefs();
//        if (demands.isEmpty()) return List.of();
//
//        logger.info("****demands***{}",demands.stream().toList());
//
//        // 2) collect all skills + locations ids for bulk fetch
//        List<Long> allPrimaryskillIds = new ArrayList<>();
//        List<Long> allSecondaryskillIds = new ArrayList<>();
//        List<Long> allLocationIds = new ArrayList<>();
//
////        for (AddDemand d : demands) {
////            if (d.getPrimarySkills() != null) allPrimaryskillIds.addAll(d.getPrimarySkillsId());
////            if (d.getSecondarySkillsId() != null) allSecondaryskillIds.addAll(d.getSecondarySkillsId());
////            if (d.getDemandLocationId() != null) allLocationIds.addAll(d.getDemandLocationId());
////        }
//
//
////        Map<Long, String> primarySkillMap = primarySkillsRepository.findByIdIn(allPrimaryskillIds)
////                .stream().collect(Collectors.toMap(PrimarySkills::getId, PrimarySkills::getPrimarySkills, (a,b) -> a));
////
////        Map<Long, String> secondarySkillMap = secondarySkillsRepository.findByIdIn(allSecondaryskillIds)
////                .stream().collect(Collectors.toMap(SecondarySkills::getId, SecondarySkills::getSecondarySkills, (a,b) -> a));
////
////        Map<Long, String> locationIdToName = locationRepository.findByIdIn(allLocationIds)
////                .stream()
////                .collect(Collectors.toMap(Location::getId, Location::getName, (a, b) -> a));
//
//
//
//        // 4) map to DTO
////        return demands.stream()
////                .map(d -> DemandMapper.toDto(d, primarySkillMap, secondarySkillMap, locationIdToName))
////                .toList();
//
//
//    }



//    public Page<GetAllDemandsResponse> getAllDemands1(int page, int size) {
//
//        Pageable pageable = PageRequest.of(page, size);
//
//        Page<AddDemand> demandsPage = addDemandRepository.findAllWithRefs(pageable);
//
//        return demandsPage.map(this::toResponse);
//    }


    public PageResponse getAllDemands1(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<AddDemand> demandsPage = addDemandRepository.findAllWithRefs(pageable);

//        return demandsPage.map(this::toResponse);


        List<GetAllDemandsResponse> content = demandsPage.getContent()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new PageResponse(
                content,
                demandsPage.isEmpty(),
                demandsPage.isFirst(),
                demandsPage.isLast(),
                demandsPage.getSize(),            // requested size
                demandsPage.getTotalElements(),
                demandsPage.getTotalPages()
        );

    }

    public PageResponse getAllDemands2(int page, int size,Long hbuId) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<AddDemand> demandsPage = addDemandRepository.findAllWithRefsByHbu(hbuId,pageable);

//        return demandsPage.map(this::toResponse);


        List<GetAllDemandsResponse> content = demandsPage.getContent()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new PageResponse(
                content,
                demandsPage.isEmpty(),
                demandsPage.isFirst(),
                demandsPage.isLast(),
                demandsPage.getSize(),            // requested size
                demandsPage.getTotalElements(),
                demandsPage.getTotalPages()
        );

    }

    private GetAllDemandsResponse toResponse(AddDemand d) {
//        String lobName = (d.getLob() != null && d.getLob().getName() != null)
//                ? d.getLob().getName()
//                : "";

        String name = "";

        if (d.getSubLob() != null && d.getSubLob().getName() != null) {
            name = d.getSubLob().getName();   // WSIT, MSS
        } else if (d.getLob() != null && d.getLob().getName() != null) {
            name = d.getLob().getName();      // CIM, others
        }


        String displayDemandId = name.isBlank()
                ? String.valueOf(d.getDemandId())
                : name + "-" + d.getDemandId();

//        String displayDemandId = lobName.isBlank()
//                ? String.valueOf(d.getDemandId())
//                : lobName + "-" + d.getDemandId();

//        List<GetAllDemandsResponse.ProfileTrackerSummaryDTO> trackerSummaries =
//                (d.getProfileTrackers() == null)
//                        ? List.of()
//                        : d.getProfileTrackers().stream()
//                        .map(t -> GetAllDemandsResponse.ProfileTrackerSummaryDTO.builder()
//                                .id(t.getId())
//                                .profileId(t.getProfile() != null ? t.getProfile().getId() : null)
//                                .candidateName(
//                                        t.getProfile().getCandidateName()
//                                )
//                                .profileSharedDate(t.getProfileSharedDate())
//                                .attachedDate(t.getAttachedDate())
//                                .experience(t.getProfile().getExperience())
//                                .primarySkills(t.getProfile().getPrimarySkills())
//                                .secondarySkills(t.getProfile().getSecondarySkills())
//                                .hbu(ref(t.getProfile().getHbu()))
//                                .createdAt(t.getCreatedAt())
//                                .build())
//                        .toList();

        return GetAllDemandsResponse.builder()
                .id(d.getId())
                .flag(d.getFlag())

                .demandId(d.getDemandId())
                .displayDemandId(displayDemandId)
                .rrNumber(d.getRrNumber())
                .fileName(d.getFileName())

                .experience(d.getExperience())
                .remark(d.getRemark())
                .demandReceivedDate(d.getDemandReceivedDate())

                .primarySkills(d.getPrimarySkills() == null ? List.of()
                        : d.getPrimarySkills().stream().map(this::ref).toList())
                .secondarySkills(d.getSecondarySkills() == null ? List.of()
                        : d.getSecondarySkills().stream().map(this::ref).toList())
                .demandLocations(d.getDemandLocations() == null ? List.of()
                        : d.getDemandLocations().stream().map(this::ref).toList())

                .hbu(ref(d.getHbu()))
                .hbuSpoc(ref(d.getHbuSpoc()))
                .band(ref(d.getBand()))
                .priority(ref(d.getPriority()))
                .lob(ref(d.getLob()))
                .demandType(ref(d.getDemandType()))
                .demandTimeline(ref(d.getDemandTimeline()))
                .externalInternal(ref(d.getExternalInternal()))
                .status(ref(d.getStatus()))
                .pod(ref(d.getPod()))
                .pmo(ref(d.getPmo()))
                .pmoSpoc(ref(d.getPmoSpoc()))
                .salesSpoc(ref(d.getSalesSpoc()))
                .hiringManager(ref(d.getHiringManager()))
                .deliveryManager(ref(d.getDeliveryManager()))
                .skillCluster(ref(d.getSkillCluster()))
                .projectManager(ref(d.getProjectManager()))
                .karatFlag(d.getKaratFlag())
                .isSubcon(d.getIsSubconRR())
//                .profileShared(trackerSummaries)
                .build();
    }

//    @Transactional(readOnly = true)
//    public Page<GetAllDemandsResponse> searchDemands(DemandsFilterRequest filter, int page, int size) {
//
//        Pageable pageable = PageRequest.of(page, size);
//
//        Page<AddDemand> demandsPage = addDemandRepository.findAll(
//                AddDemandSpecifications.byNames(filter),
//                pageable
//        );
//
//        // If BatchSize/subselect enabled, LAZY collections load efficiently while mapping
//        return demandsPage.map(this::toResponse);
//    }

//    @Transactional(readOnly = true)
//    public PageResponse searchDemands(DemandsFilterRequest filter, int page, int size) {
//
//        Pageable pageable = PageRequest.of(page, size);
//
//        Page<AddDemand> demandsPage = addDemandRepository.findAll(
//                AddDemandSpecifications.byNames(filter),
//                pageable
//        );
//
//        return toPageResponse(demandsPage);
//    }

    @Transactional(readOnly = true)
    public PageResponse searchDemands(DemandsFilterRequest filter, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<AddDemand> demandsPage = addDemandRepository.findAll(
                AddDemandSpecifications.byIds(filter)
                        .and(AddDemandSpecifications.byNames(filter)),
                pageable
        );

        return toPageResponse(demandsPage);
    }



    private PageResponse toPageResponse(Page<AddDemand> demandsPage) {

        List<GetAllDemandsResponse> content = demandsPage.getContent()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new PageResponse(
                content,
                demandsPage.isEmpty(),
                demandsPage.isFirst(),
                demandsPage.isLast(),
                demandsPage.getSize(),          // requested size
                demandsPage.getTotalElements(),
                demandsPage.getTotalPages()
        );
    }



    private RefDTO ref(Object entity) {
        if (entity == null) return null;

        Long id = null;
        String name = null;

        try {
            // --- getId() ---
            Method getId = entity.getClass().getMethod("getId");
            Object idObj = getId.invoke(entity);
            if (idObj instanceof Long) {
                id = (Long) idObj;
            } else if (idObj != null) {
                // In case id is Integer etc.
                id = Long.valueOf(idObj.toString());
            }

            // --- try common name getters ---
            for (String m : List.of("getName", "getDisplayName", "getValue")) {
                try {
                    Method method = entity.getClass().getMethod(m);
                    Object v = method.invoke(entity);
                    if (v != null) {
                        name = v.toString();
                        break;
                    }
                } catch (NoSuchMethodException ignored) {
                    // try next
                }
            }

            // --- fallback: any get*Name() method ---
            if (name == null) {
                for (Method m : entity.getClass().getMethods()) {
                    if (m.getParameterCount() == 0
                            && m.getName().startsWith("get")
                            && m.getName().endsWith("Name")) {
                        Object v = m.invoke(entity);
                        if (v != null) {
                            name = v.toString();
                            break;
                        }
                    }
                }
            }

        } catch (Exception ignored) {
            // If anything fails, still return what we could parse
        }

        if (id == null && name == null) return null;
        return new RefDTO(id, name);
    }

}