package com.coforge.hsbcdma.Import;

import com.coforge.hsbcdma.entity.AddDemand;
import com.coforge.hsbcdma.entity.Location;
import com.coforge.hsbcdma.entity.dropdownEntities.*;
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
    public List<AddDemand> parseExcelFile(InputStream inputStream) {
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

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // skip header
                if (isRowEmpty(row))     continue; // skip blank rows

                AddDemand demand = new AddDemand();

                // ── Col A : Demand ID ──────────────────────────────────────
                demand.setDemandId(getLongValue(row.getCell(0)));

                // ── Col B : RR Number ─────────────────────────────────────
                demand.setRrNumber(getLongValue(row.getCell(1)));

                // ── Col C : LOB ───────────────────────────────────────────
                demand.setLob(resolveOrCreate(
                        getStringValue(row.getCell(2)),
                        lobCache,
                        lobRepo::findByLob,     // adjust if you have IgnoreCase variant
                        Lob::new,
                        Lob::setLob,
                        lobRepo::save
                ));

                // ── Col D : Sub LOB ───────────────────────────────────────
                demand.setSubLob(resolveOrCreate(
                        getStringValue(row.getCell(3)),
                        subLobCache,
                        subLobRepo::findBySubLobIgnoreCase,
                        SubLob::new,
                        SubLob::setSubLob,
                        subLobRepo::save
                ));

                // ── Col E : HBU ───────────────────────────────────────────
                demand.setHbu(resolveOrCreate(
                        getStringValue(row.getCell(4)),
                        hbuCache,
                        hbuRepo::findByHbuIgnoreCase,
                        Hbu::new,
                        Hbu::setHbu,
                        hbuRepo::save
                ));

                // ── Col F : Band ──────────────────────────────────────────
                demand.setBand(resolveOrCreate(
                        getStringValue(row.getCell(5)),
                        bandCache,
                        bandRepo::findByBandIgnoreCase,
                        Band::new,
                        Band::setBand,
                        bandRepo::save
                ));

                // ── Col G : Priority ──────────────────────────────────────
                demand.setPriority(resolveOrCreate(
                        getStringValue(row.getCell(6)),
                        priorityCache,
                        priorityRepo::findByPriorityIgnoreCase,
                        Priority::new,
                        Priority::setPriority,
                        priorityRepo::save
                ));

                // ── Col H : Status ────────────────────────────────────────
                demand.setStatus(resolveOrCreate(
                        getStringValue(row.getCell(7)),
                        statusCache,
                        statusRepo::findByStatusIgnoreCase,
                        Status::new,
                        Status::setStatus,
                        statusRepo::save
                ));

                // ── Col I : Demand Type ───────────────────────────────────
                demand.setDemandType(resolveOrCreate(
                        getStringValue(row.getCell(8)),
                        demandTypeCache,
                        demandTypeRepo::findByDemandTypeIgnoreCase,
                        DemandType::new,
                        DemandType::setDemandType,
                        demandTypeRepo::save
                ));

                // ── Col J : Demand Timeline ───────────────────────────────
                demand.setDemandTimeline(resolveOrCreate(
                        getStringValue(row.getCell(9)),
                        demandTimelineCache,
                        demandTimelineRepo::findByDemandTimeLineIgnoreCase,
                        DemandTimeLine::new,
                        DemandTimeLine::setDemandTimeLine,
                        demandTimelineRepo::save
                ));

                // ── Col K : External / Internal ───────────────────────────
                demand.setExternalInternal(resolveOrCreate(
                        getStringValue(row.getCell(10)),
                        externalInternalCache,
                        externalInternalRepo::findByExternalInternalIgnoreCase,
                        ExternalInternal::new,
                        ExternalInternal::setExternalInternal,
                        externalInternalRepo::save
                ));

                // ── Col L : POD ───────────────────────────────────────────
                demand.setPod(resolveOrCreate(
                        getStringValue(row.getCell(11)),
                        podCache,
                        podRepo::findByPodIgnoreCase,
                        Pod::new,
                        Pod::setPod,
                        podRepo::save
                ));

                // ── Col M : PMO ───────────────────────────────────────────
                demand.setPmo(resolveOrCreate(
                        getStringValue(row.getCell(12)),
                        pmoCache,
                        pmoRepo::findByPmoIgnoreCase,
                        Pmo::new,
                        Pmo::setPmo,
                        pmoRepo::save
                ));

                // ── Col N : PMO SPOC ──────────────────────────────────────
                demand.setPmoSpoc(resolveOrCreate(
                        getStringValue(row.getCell(13)),
                        pmoSpocCache,
                        pmoSpocRepo::findByPmoSpocIgnoreCase,
                        PmoSpoc::new,
                        PmoSpoc::setPmoSpoc,
                        pmoSpocRepo::save
                ));

                // ── Col O : Sales SPOC ────────────────────────────────────
                demand.setSalesSpoc(resolveOrCreate(
                        getStringValue(row.getCell(14)),
                        salesSpocCache,
                        salesSpocRepo::findBySalesSpocIgnoreCase,
                        SalesSpoc::new,
                        SalesSpoc::setSalesSpoc,
                        salesSpocRepo::save
                ));

                // ── Col P : Hiring Manager ────────────────────────────────
                demand.setHiringManager(resolveOrCreate(
                        getStringValue(row.getCell(15)),
                        hiringManagerCache,
                        hiringManagerRepo::findByHiringManagerIgnoreCase,
                        HiringManager::new,
                        HiringManager::setHiringManager,
                        hiringManagerRepo::save
                ));

                // ── Col Q : Delivery Manager ──────────────────────────────
                demand.setDeliveryManager(resolveOrCreate(
                        getStringValue(row.getCell(16)),
                        deliveryManagerCache,
                        deliveryManagerRepo::findByDeliveryManagerIgnoreCase,
                        DeliveryManager::new,
                        DeliveryManager::setDeliveryManager,
                        deliveryManagerRepo::save
                ));

                // ── Col R : Skill Cluster ─────────────────────────────────
                demand.setSkillCluster(resolveOrCreate(
                        getStringValue(row.getCell(17)),
                        skillClusterCache,
                        skillClusterRepo::findBySkillClusterIgnoreCase,
                        SkillCluster::new,
                        SkillCluster::setSkillCluster,
                        skillClusterRepo::save
                ));

                // ── Col S : Project Manager ───────────────────────────────
                demand.setProjectManager(resolveOrCreate(
                        getStringValue(row.getCell(18)),
                        projectManagerCache,
                        projectManagerRepo::findByProjectManagerIgnoreCase,
                        ProjectManager::new,
                        ProjectManager::setProjectManager,
                        projectManagerRepo::save
                ));

                // ── Col T : HBU SPOC ──────────────────────────────────────
                demand.setHbuSpoc(resolveOrCreate(
                        getStringValue(row.getCell(19)),
                        hbuSpocCache,
                        hbuSpocRepo::findByHbuSpocIgnoreCase,
                        HbuSpoc::new,
                        HbuSpoc::setHbuSpoc,
                        hbuSpocRepo::save
                ));

                // ── Col U : Primary Skills (comma-separated) ──────────────
                Set<PrimarySkills> primarySkills = resolveOrCreateSet(
                        getStringValue(row.getCell(20)),
                        primarySkillsCache,
                        primarySkillsRepo::findByPrimarySkillsIgnoreCase,
                        PrimarySkills::new,
                        PrimarySkills::setPrimarySkills,
                        primarySkillsRepo::save
                );
                demand.setPrimarySkills(primarySkills);

                // ── Col V : Secondary Skills (comma-separated) ────────────
                Set<SecondarySkills> secondarySkills = resolveOrCreateSet(
                        getStringValue(row.getCell(21)),
                        secondarySkillsCache,
                        secondarySkillsRepo::findBySecondarySkillsIgnoreCase,
                        SecondarySkills::new,
                        SecondarySkills::setSecondarySkills,
                        secondarySkillsRepo::save
                );
                demand.setSecondarySkills(secondarySkills);

                // ── Col W : Locations (comma-separated) ───────────────────
                Set<Location> locations = resolveOrCreateSet(
                        getStringValue(row.getCell(22)),
                        locationCache,
                        locationRepo::findByNameIgnoreCase,
                        Location::new,
                        Location::setName,
                        locationRepo::save
                );
                // If you want "onshore by default" for new Location:
                // -> Add a default in the entity or here after creation by checking if it's new.
                demand.setDemandLocations(locations);

                // ── Col X : Experience ────────────────────────────────────
                demand.setExperience(getStringValue(row.getCell(23)));

                // ── Col Y : Remark ────────────────────────────────────────
                demand.setRemark(getStringValue(row.getCell(24)));

                // ── Col Z : Demand Received Date ──────────────────────────
                demand.setDemandReceivedDate(getDateValue(row.getCell(25)));

                // ── Col AA : Is Subcon RR ─────────────────────────────────
                demand.setIsSubconRR(getBooleanValue(row.getCell(26)));

                // ── Col AB : Karat Flag ───────────────────────────────────
                demand.setKaratFlag(getBooleanValue(row.getCell(27)));

                demands.add(demand);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage(), e);
        }

        return demands;
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
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> (long) cell.getNumericCellValue();
            case STRING  -> {
                try { yield Long.parseLong(cell.getStringCellValue().trim()); }
                catch (NumberFormatException e) { yield null; }
            }
            default -> null;
        };
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
}