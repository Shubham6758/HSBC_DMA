package com.coforge.hsbcdma.Import;

import com.coforge.hsbcdma.entity.AddDemand;
import com.coforge.hsbcdma.entity.Location;
import com.coforge.hsbcdma.entity.dropdownEntities.*;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandRepository;
import com.coforge.hsbcdma.repository.LocationRepository;
import com.coforge.hsbcdma.repository.dropdownRepository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class ExcelImportHelper {

    public static final String EXCEL_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    // ─── All master-table repositories ───────────────────────────────────────
    private final LobRepository             lobRepo;
    private final SubLobRepository          subLobRepo;
    private final HbuRepository             hbuRepo;
    private final HbuSpocRepository         hbuSpocRepo;
    private final BandRepository            bandRepo;
    private final PriorityRepository        priorityRepo;
    private final StatusRepository          statusRepo;
    private final DemandTypeRepository      demandTypeRepo;
    private final DemandTimeLineRepository  demandTimelineRepo;
    private final ExternalInternalRepository externalInternalRepo;
    private final PodRepository             podRepo;
    private final PmoRepository             pmoRepo;
    private final PmoSpocRepository         pmoSpocRepo;
    private final SalesSpocRepository       salesSpocRepo;
    private final HiringManagerRepository   hiringManagerRepo;
    private final DeliveryManagerRepository deliveryManagerRepo;
    private final SkillClusterRepository    skillClusterRepo;
    private final ProjectManagerRepository  projectManagerRepo;
    private final PrimarySkillsRepository   primarySkillsRepo;
    private final SecondarySkillsRepository secondarySkillsRepo;
    private final LocationRepository        locationRepo;
    private final AddDemandRepository demandRepository;
    private final BusinessFunctionRepository businessFunctionRepo;
    private final JobCategoryRepository jobCategoryRepo;
    private final ContractTypeRepository contractTypeRepo;

    public boolean isValidExcelFile(MultipartFile file) {
        return EXCEL_TYPE.equals(file.getContentType());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Preload caches (name → entity) for all masters used in this import
    // to minimize DB roundtrips and allow create-if-missing in O(1).
    // ─────────────────────────────────────────────────────────────────────────

    private static <T> Map<String, T> preloadToMap(Iterable<T> all,
                                                   Function<T, String> nameGetter) {
        Map<String, T> map = new HashMap<>();
        for (T item : all) {
            String key = key(nameGetter.apply(item));
            if (key != null) map.put(key, item);
        }
        return map;
    }

    private static String key(String name) {
        if (name == null) return null;
        String trimmed = name.trim();
        return trimmed.isEmpty() ? null : trimmed.toLowerCase();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Generic resolvers (single value & comma-separated sets)
    // They check cache → DB → create & save → put into cache.
    // ─────────────────────────────────────────────────────────────────────────

    private static <T> T resolveOrCreate(
            String rawValue,
            Map<String, T> cache,
            Function<String, Optional<T>> finder,
            Supplier<T> factory,
            BiConsumer<T, String> setName,
            Function<T, T> saveFn
    ) {
        String k = key(rawValue);
        if (k == null) return null;

        // 1) Cache
        T cached = cache.get(k);
        if (cached != null) return cached;

        // 2) DB
        Optional<T> found = finder.apply(rawValue.trim());
        if (found.isPresent()) {
            T entity = found.get();
            cache.put(k, entity);
            return entity;
        }

        // 3) Create
        T entity = factory.get();
        setName.accept(entity, rawValue.trim());

        try {
            entity = saveFn.apply(entity);
        } catch (DataIntegrityViolationException e) {
            // Another thread/process likely created it concurrently → refetch
            entity = finder.apply(rawValue.trim()).orElseThrow(() -> e);
        }

        cache.put(k, entity);
        return entity;
    }

    private static <T> Set<T> resolveOrCreateSet(
            String csv,
            Map<String, T> cache,
            Function<String, Optional<T>> finder,
            Supplier<T> factory,
            BiConsumer<T, String> setName,
            Function<T, T> saveFn
    ) {
        Set<T> result = new LinkedHashSet<>();
        if (csv == null || csv.isBlank()) return result;

        String[] parts = csv.split(",");
        for (String part : parts) {
            String token = part.trim();
            if (token.isEmpty()) continue;
            T entity = resolveOrCreate(token, cache, finder, factory, setName, saveFn);
            if (entity != null) result.add(entity);
        }
        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MAIN: Parse Excel and auto-create any missing masters
    // NOTE: Keep this transactional so master creations are atomic & visible
    // within the same import run.
    // ─────────────────────────────────────────────────────────────────────────
    @Transactional
    public DemandImportResponse parseExcelFile(InputStream inputStream) {
        List<AddDemand> demands = new ArrayList<>();

        // ── Preload all master caches ───────────────────────────────────────
        Map<String, Lob>              lobCache              = preloadToMap(lobRepo.findAll(), Lob::getLob);
        Map<String, SubLob>           subLobCache           = preloadToMap(subLobRepo.findAll(), SubLob::getSubLob);
        Map<String, Hbu>              hbuCache              = preloadToMap(hbuRepo.findAll(), Hbu::getHbu);
        Map<String, Band>             bandCache             = preloadToMap(bandRepo.findAll(), Band::getBand);
        Map<String, Priority>         priorityCache         = preloadToMap(priorityRepo.findAll(), Priority::getPriority);
        Map<String, Status>           statusCache           = preloadToMap(statusRepo.findAll(), Status::getStatus);
        Map<String, DemandType>       demandTypeCache       = preloadToMap(demandTypeRepo.findAll(), DemandType::getDemandType);
        Map<String, DemandTimeLine>   demandTimelineCache   = preloadToMap(demandTimelineRepo.findAll(), DemandTimeLine::getDemandTimeLine);
        Map<String, ExternalInternal> externalInternalCache = preloadToMap(externalInternalRepo.findAll(), ExternalInternal::getExternalInternal);
        Map<String, Pod>              podCache              = preloadToMap(podRepo.findAll(), Pod::getPod);
        Map<String, Pmo>              pmoCache              = preloadToMap(pmoRepo.findAll(), Pmo::getPmo);
        Map<String, PmoSpoc>          pmoSpocCache          = preloadToMap(pmoSpocRepo.findAll(), PmoSpoc::getPmoSpoc);
        Map<String, SalesSpoc>        salesSpocCache        = preloadToMap(salesSpocRepo.findAll(), SalesSpoc::getSalesSpoc);
        Map<String, HiringManager>    hiringManagerCache    = preloadToMap(hiringManagerRepo.findAll(), HiringManager::getHiringManager);
        Map<String, DeliveryManager>  deliveryManagerCache  = preloadToMap(deliveryManagerRepo.findAll(), DeliveryManager::getDeliveryManager);
        Map<String, SkillCluster>     skillClusterCache     = preloadToMap(skillClusterRepo.findAll(), SkillCluster::getSkillCluster);
        Map<String, ProjectManager>   projectManagerCache   = preloadToMap(projectManagerRepo.findAll(), ProjectManager::getProjectManager);
        Map<String, HbuSpoc>          hbuSpocCache          = preloadToMap(hbuSpocRepo.findAll(), HbuSpoc::getHbuSpoc);
        Map<String, PrimarySkills>    primarySkillsCache    = preloadToMap(primarySkillsRepo.findAll(), PrimarySkills::getPrimarySkills);
        Map<String, SecondarySkills>  secondarySkillsCache  = preloadToMap(secondarySkillsRepo.findAll(), SecondarySkills::getSecondarySkills);
        Map<String, Location>         locationCache         = preloadToMap(locationRepo.findAll(), Location::getName);
        Map<String, BusinessFunction> businessFunctionCache =
                preloadToMap(
                        businessFunctionRepo.findAll(),
                        BusinessFunction::getBusinessFunction
                );
        Map<String,JobCategory> jobCategoryCache =
                preloadToMap(jobCategoryRepo.findAll(), JobCategory::getJobCategory);
        Map<String,ContractType> contractTypeCache =
                preloadToMap(contractTypeRepo.findAll(), ContractType::getContractType);

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {

            String[] sheets = {
                    "Offshore Demands",
                    "Onsite Demand Sheet"
            };

            List<String> errors = new ArrayList<>();

            for (String sheetName : sheets) {

                Sheet sheet = workbook.getSheet(sheetName);

                if (sheet == null) {
                    continue;
                }

                Map<String, Integer> headerMap =
                        ExcelColumnHelper.getHeaderIndexMap(sheet);

                for (Row row : sheet) {

                    if (row.getRowNum() == 0) {
                        continue;
                    }

                    if (isRowEmpty(row)) {
                        continue;
                    }

                    try {

                        AddDemand demand = new AddDemand();

                        // ============================
                        // DEMAND NUMBER (MANDATORY)
                        // ============================

                        Long demandNumber =
                                getLongValue(
                                        ExcelColumnHelper.getCell(
                                                row,
                                                headerMap,
                                                "Demand Number"
                                        )
                                );

                        if (demandNumber == null) {

                            errors.add(
                                    sheetName +
                                            " Row " +
                                            (row.getRowNum() + 1) +
                                            " : Demand Number is mandatory"
                            );

                            continue;
                        }

                        demand.setDemandId(demandNumber);

                        // ============================
                        // LOB (MANDATORY)
                        // ============================

                        String lobName =
                                getRequiredString(
                                        row,
                                        headerMap,
                                        "Lob"
                                );

                        if (lobName == null || lobName.isBlank()) {

                            errors.add(
                                    sheetName +
                                            " Row " +
                                            (row.getRowNum() + 1) +
                                            " : Lob is mandatory"
                            );

                            continue;
                        }

                        Lob lob =
                                getOrCreateLob(
                                        lobName,
                                        lobCache
                                );

                        demand.setLob(lob);

                        // ============================
                        // SUB LOB
                        // ============================

                        String subLobName =
                                getRequiredString(
                                        row,
                                        headerMap,
                                        "Sub Lob"
                                );

                        boolean hasSubLobby =
                                subLobRepo.existsByLobId(
                                        lob.getId()
                                );

                        if (hasSubLobby &&
                                (subLobName == null ||
                                        subLobName.isBlank())) {

                            errors.add(
                                    sheetName +
                                            " Row " +
                                            (row.getRowNum() + 1) +
                                            " : Sub Lob is mandatory for Lob "
                                            + lobName
                            );

                            continue;
                        }

                        if (subLobName != null &&
                                !subLobName.isBlank()) {

                            demand.setSubLob(
                                    getOrCreateSubLob(
                                            subLobName,
                                            lob,
                                            subLobCache
                                    )
                            );
                        }

                        // ============================
                        // DEMAND RECEIVED DATE
                        // ============================

                        demand.setDemandReceivedDate(

                                getDateValue(
                                        ExcelColumnHelper.getCell(
                                                row,
                                                headerMap,
                                                "Demand Received Date"
                                        )
                                )
                        );

                        // ============================
                        // POD
                        // ============================

                        demand.setPod(

                                resolveOrCreate(
                                        getRequiredString(
                                                row,
                                                headerMap,
                                                "POD/Program Name"
                                        ),
                                        podCache,
                                        podRepo::findByPodIgnoreCase,
                                        Pod::new,
                                        Pod::setPod,
                                        podRepo::save
                                )
                        );

                        // ============================
                        // HIRING MANAGER
                        // ============================

                        demand.setHiringManager(

                                resolveOrCreate(
                                        getRequiredString(
                                                row,
                                                headerMap,
                                                "Hiring Manager"
                                        ),
                                        hiringManagerCache,
                                        hiringManagerRepo::findByHiringManagerIgnoreCase,
                                        HiringManager::new,
                                        HiringManager::setHiringManager,
                                        hiringManagerRepo::save
                                )
                        );

                        // ============================
                        // DEMAND TYPE
                        // ============================

                        demand.setDemandType(

                                resolveOrCreate(
                                        getRequiredString(
                                                row,
                                                headerMap,
                                                "DemandType"
                                        ),
                                        demandTypeCache,
                                        demandTypeRepo::findByDemandTypeIgnoreCase,
                                        DemandType::new,
                                        DemandType::setDemandType,
                                        demandTypeRepo::save
                                )
                        );

                        // ============================
                        // SKILL CLUSTER
                        // ============================

                        demand.setSkillCluster(

                                resolveOrCreate(
                                        getRequiredString(
                                                row,
                                                headerMap,
                                                "Skill Cluster"
                                        ),
                                        skillClusterCache,
                                        skillClusterRepo::findBySkillClusterIgnoreCase,
                                        SkillCluster::new,
                                        SkillCluster::setSkillCluster,
                                        skillClusterRepo::save
                                )
                        );

                        // ============================
                        // PRIMARY SKILL
                        // ============================

                        demand.setPrimarySkills(

                                resolveOrCreateSet(
                                        getRequiredString(
                                                row,
                                                headerMap,
                                                "Primary Skill"
                                        ),
                                        primarySkillsCache,
                                        primarySkillsRepo::findByPrimarySkillsIgnoreCase,
                                        PrimarySkills::new,
                                        PrimarySkills::setPrimarySkills,
                                        primarySkillsRepo::save
                                )
                        );

                        // ============================
                        // SECONDARY SKILL
                        // ============================

                        demand.setSecondarySkills(

                                resolveOrCreateSet(
                                        getRequiredString(
                                                row,
                                                headerMap,
                                                "secondary skill"
                                        ),
                                        secondarySkillsCache,
                                        secondarySkillsRepo::findBySecondarySkillsIgnoreCase,
                                        SecondarySkills::new,
                                        SecondarySkills::setSecondarySkills,
                                        secondarySkillsRepo::save
                                )
                        );

                        demand.setExperience(
                                getRequiredString(
                                        row,
                                        headerMap,
                                        "Exp"
                                )
                        );

                        demand.setPriority(

                                resolveOrCreate(
                                        getRequiredString(
                                                row,
                                                headerMap,
                                                "Priority"
                                        ),
                                        priorityCache,
                                        priorityRepo::findByPriorityIgnoreCase,
                                        Priority::new,
                                        Priority::setPriority,
                                        priorityRepo::save
                                )
                        );

                        demand.setDemandLocations(

                                resolveOrCreateSet(
                                        getRequiredString(
                                                row,
                                                headerMap,
                                                "Demand Location"
                                        ),
                                        locationCache,
                                        locationRepo::findByNameIgnoreCase,
                                        Location::new,
                                        Location::setName,
                                        locationRepo::save
                                )
                        );

                        demand.setSalesSpoc(

                                resolveOrCreate(
                                        getRequiredString(
                                                row,
                                                headerMap,
                                                "Sales SPOC"
                                        ),
                                        salesSpocCache,
                                        salesSpocRepo::findBySalesSpocIgnoreCase,
                                        SalesSpoc::new,
                                        SalesSpoc::setSalesSpoc,
                                        salesSpocRepo::save
                                )
                        );

                        demand.setP1FlagDate(
                                getDateValue(
                                        ExcelColumnHelper.getCell(
                                                row,
                                                headerMap,
                                                "P1 Flag Date"
                                        )
                                )
                        );

                        demand.setPmo(

                                resolveOrCreate(
                                        getRequiredString(
                                                row,
                                                headerMap,
                                                "PMO"
                                        ),
                                        pmoCache,
                                        pmoRepo::findByPmoIgnoreCase,
                                        Pmo::new,
                                        Pmo::setPmo,
                                        pmoRepo::save
                                )
                        );

                        demand.setProjectManager(

                                resolveOrCreate(
                                        getRequiredString(
                                                row,
                                                headerMap,
                                                "PM"
                                        ),
                                        projectManagerCache,
                                        projectManagerRepo::findByProjectManagerIgnoreCase,
                                        ProjectManager::new,
                                        ProjectManager::setProjectManager,
                                        projectManagerRepo::save
                                )
                        );

                        demand.setHbu(

                                resolveOrCreate(
                                        getRequiredString(
                                                row,
                                                headerMap,
                                                "HBU"
                                        ),
                                        hbuCache,
                                        hbuRepo::findByHbuIgnoreCase,
                                        Hbu::new,
                                        Hbu::setHbu,
                                        hbuRepo::save
                                )
                        );

                        demand.setRrNumber(
                                getRequiredString(
                                        row,
                                        headerMap,
                                        "RR"
                                )
                        );

                        demand.setBand(

                                resolveOrCreate(
                                        getRequiredString(
                                                row,
                                                headerMap,
                                                "Band"
                                        ),
                                        bandCache,
                                        bandRepo::findByBandIgnoreCase,
                                        Band::new,
                                        Band::setBand,
                                        bandRepo::save
                                )
                        );

                        demand.setKaratFlag(
                                getBooleanValue(
                                        ExcelColumnHelper.getCell(
                                                row,
                                                headerMap,
                                                "KARAT required"
                                        )
                                )
                        );

                        demand.setStatus(

                                resolveOrCreate(
                                        getRequiredString(
                                                row,
                                                headerMap,
                                                "Status"
                                        ),
                                        statusCache,
                                        statusRepo::findByStatusIgnoreCase,
                                        Status::new,
                                        Status::setStatus,
                                        statusRepo::save
                                )
                        );

                        demand.setRemark(
                                getRequiredString(
                                        row,
                                        headerMap,
                                        "Remarks"
                                )
                        );

                        // ============================
                        // BUSINESS FUNCTION
                        // ============================

                        demand.setBusinessFunction(

                                resolveOrCreate(
                                        getRequiredString(
                                                row,
                                                headerMap,
                                                "Business Function"
                                        ),
                                        businessFunctionCache,
                                        businessFunctionRepo::findByBusinessFunctionIgnoreCase,
                                        BusinessFunction::new,
                                        BusinessFunction::setBusinessFunction,
                                        businessFunctionRepo::save
                                )
                        );


                        demand.setJobCategory(

                                resolveOrCreate(
                                        getRequiredString(
                                                row,
                                                headerMap,
                                                "Job Category"
                                        ),
                                        jobCategoryCache,
                                        jobCategoryRepo::findByJobCategoryIgnoreCase,
                                        JobCategory::new,
                                        JobCategory::setJobCategory,
                                        jobCategoryRepo::save
                                )
                        );

                        demand.setContractType(

                                resolveOrCreate(
                                        getRequiredString(
                                                row,
                                                headerMap,
                                                "Contract Type"
                                        ),
                                        contractTypeCache,
                                        contractTypeRepo::findByContractTypeIgnoreCase,
                                        ContractType::new,
                                        ContractType::setContractType,
                                        contractTypeRepo::save
                                )
                        );

                        demand.setCtoolId(
                                getLongValue(
                                        ExcelColumnHelper.getCell(
                                                row,
                                                headerMap,
                                                "Ctool Id"
                                        )
                                )
                        );

                        demands.add(demand);

                    } catch (Exception ex) {

                        errors.add(
                                sheetName +
                                        " Row " +
                                        (row.getRowNum() + 1) +
                                        " : " +
                                        ex.getMessage()
                        );
                    }
                }
            }

            if (!demands.isEmpty()) {

                System.out.println(
                        "Saving demands: "
                                + demands.size()
                );

                demandRepository.saveAll(demands);

                demandRepository.flush();
            }


            DemandImportResponse response =
                    new DemandImportResponse();

            response.setSuccessCount(
                    demands.size()
            );

            response.setFailedCount(
                    errors.size()
            );

            response.setErrors(errors);

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage(), e);
        }

    }

    // ─── Cell value utilities ─────────────────────────────────────────────────

    private String getStringValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                // Keep integers as whole numbers, otherwise toString() to preserve decimals
                double dv = cell.getNumericCellValue();
                long lv = (long) dv;
                yield (dv == lv) ? String.valueOf(lv) : String.valueOf(dv);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                // Evaluate as string if possible, fallback to cached numeric/boolean
                try { yield cell.getStringCellValue().trim(); }
                catch (IllegalStateException ex) {
                    if (cell.getCachedFormulaResultType() == CellType.NUMERIC) {
                        yield String.valueOf((long) cell.getNumericCellValue());
                    } else if (cell.getCachedFormulaResultType() == CellType.BOOLEAN) {
                        yield String.valueOf(cell.getBooleanCellValue());
                    } else {
                        yield null;
                    }
                }
            }
            default      -> null;
        };
    }

    private Long getLongValue(Cell cell) {

        if (cell == null) {
            return null;
        }

        String value = getStringValue(cell);

        if (value == null || value.isBlank()) {
            return null;
        }

        String numericPart = value.replaceAll("[^0-9]", "");

        if (numericPart.isEmpty()) {
            return null;
        }

        return Long.parseLong(numericPart);
    }

    private LocalDate getDateValue(Cell cell) {
        if (cell == null) {
            System.err.println("⚠️ Date cell is NULL");
            return null;
        }
        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date javaDate = DateUtil.getJavaDate(cell.getNumericCellValue());
                        yield javaDate.toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                    }
                    yield null;
                }
                case STRING -> {
                    String raw = cell.getStringCellValue().trim();
                    System.err.println("⚠️ String date raw value: [" + raw + "]");
                    if (raw.isEmpty()) yield null;
                    yield parseDateFlexible(raw);
                }
                default -> null;
            };
        } catch (Exception e) {
            System.err.println("⚠️ Date parse failed: " + e.getMessage());
            return null;
        }
    }

    // ✅ Tries multiple common date formats
    private LocalDate parseDateFlexible(String raw) {
        List<java.time.format.DateTimeFormatter> formatters = List.of(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy"),
                java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy"),
                java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd")
        );
        for (var formatter : formatters) {
            try { return LocalDate.parse(raw, formatter); }
            catch (Exception ignored) {}
        }
        System.err.println("⚠️ Unrecognized date format: " + raw);
        return null;
    }

    private Boolean getBooleanValue(Cell cell) {
        if (cell == null) return false;
        return switch (cell.getCellType()) {
            case BOOLEAN -> cell.getBooleanCellValue();
            case STRING  -> {
                String val = cell.getStringCellValue().trim().toLowerCase();
                yield val.equals("yes") || val.equals("true") || val.equals("1");
            }
            case NUMERIC -> cell.getNumericCellValue() == 1;
            default      -> false;
        };
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }

    private String getRequiredString(
            Row row,
            Map<String, Integer> headerMap,
            String columnName
    ) {

        return getStringValue(
                ExcelColumnHelper.getCell(
                        row,
                        headerMap,
                        columnName
                )
        );
    }

    private Lob getOrCreateLob(
            String lobName,
            Map<String, Lob> lobCache
    ) {

        String k = key(lobName);

        if (k == null) {
            return null;
        }

        Lob lob = lobCache.get(k);

        if (lob != null) {
            return lob;
        }

        lob = lobRepo.findByLob(lobName)
                .orElse(null);

        if (lob == null) {

            lob = new Lob();

            lob.setLob(lobName);

            lob.setCurrentDemandIdSequence(0);

            lob = lobRepo.save(lob);
        }

        lobCache.put(k, lob);

        return lob;
    }

    private SubLob getOrCreateSubLob(
            String subLobName,
            Lob lob,
            Map<String, SubLob> subLobCache
    ) {

        if (subLobName == null ||
                subLobName.isBlank()) {

            return null;
        }

        String k = key(subLobName);

        SubLob subLob =
                subLobCache.get(k);

        if (subLob != null) {

            return subLob;
        }

        subLob =
                subLobRepo
                        .findBySubLobIgnoreCase(
                                subLobName
                        )
                        .orElse(null);

        if (subLob == null) {

            subLob = new SubLob();

            subLob.setSubLob(subLobName);

            subLob.setLob(lob);

            subLob = subLobRepo.save(subLob);
        }

        subLobCache.put(k, subLob);

        return subLob;
    }
}