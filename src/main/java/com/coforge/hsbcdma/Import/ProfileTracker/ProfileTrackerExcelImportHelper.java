package com.coforge.hsbcdma.Import.ProfileTracker;

import com.coforge.hsbcdma.Import.ExcelDateHelper;
import com.coforge.hsbcdma.entity.AddDemand;
import com.coforge.hsbcdma.entity.Profile;
import com.coforge.hsbcdma.entity.ProfileTracker;
import com.coforge.hsbcdma.entity.dropdownEntities.Lob;
import com.coforge.hsbcdma.entity.dropdownEntities.Profile.ProfileTrackerStatus;
import com.coforge.hsbcdma.entity.dropdownEntities.SubLob;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandRepository;
import com.coforge.hsbcdma.repository.ProfileRepositories.ProfileRepository;
import com.coforge.hsbcdma.repository.ProfileTrackerRepositories.ProfileTrackerStatusRepository;
import com.coforge.hsbcdma.repository.dropdownRepository.LobRepository;
import com.coforge.hsbcdma.repository.dropdownRepository.SubLobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.function.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileTrackerExcelImportHelper {

    public static final String EXCEL_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final AddDemandRepository addDemandRepo;
    private final ProfileRepository profileRepo;
    private final ProfileTrackerStatusRepository trackerStatusRepo;
    private final LobRepository lobRepository;
    private final SubLobRepository subLobRepository;

    /* ---------------- caches ---------------- */
    private final Map<String, Lob> lobCache = new HashMap<>();
    private final Map<String, SubLob> subLobCache = new HashMap<>();
    private final Map<String, Profile> profileCache = new HashMap<>();

    public boolean isValidExcelFile(String contentType) {
        return EXCEL_TYPE.equals(contentType);
    }

    private static String key(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t.toLowerCase();
    }

    private static <T> Map<String, T> preload(Iterable<T> all, Function<T, String> nameGetter) {
        Map<String, T> m = new HashMap<>();
        for (T t : all) {
            String k = key(nameGetter.apply(t));
            if (k != null) m.put(k, t);
        }
        return m;
    }

    private static <T> T resolveOrCreate(
            String raw,
            Map<String, T> cache,
            Function<String, Optional<T>> finder,
            Supplier<T> factory,
            BiConsumer<T, String> setName,
            Function<T, T> saveFn
    ) {
        String k = key(raw);
        if (k == null) return null;

        if (cache.containsKey(k)) return cache.get(k);

        Optional<T> found = finder.apply(raw.trim());
        if (found.isPresent()) {
            cache.put(k, found.get());
            return found.get();
        }

        T ent = factory.get();
        setName.accept(ent, raw.trim());

        try {
            ent = saveFn.apply(ent);
        } catch (DataIntegrityViolationException e) {
            ent = finder.apply(raw.trim()).orElseThrow(() -> e);
        }

        cache.put(k, ent);
        return ent;
    }

    /* ---------------- LOB ---------------- */
    private Lob lookupLob(String lobName, int row) {
        if (lobName == null || lobName.trim().isEmpty()) {
            throw new IllegalArgumentException("Row " + row + ": LOB Name is required");
        }

        return lobCache.computeIfAbsent(lobName.toLowerCase(), k ->
                lobRepository.findByLob(lobName)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Row " + row + ": No LOB found for '" + lobName + "'"))
        );
    }

    /* ---------------- SubLOB ---------------- */
    private SubLob lookupSubLob(String subLobName, int row) {

        if (subLobName == null || subLobName.trim().isEmpty())
            return null;

        return subLobCache.computeIfAbsent(subLobName.toLowerCase(), k ->
                subLobRepository.findBySubLobIgnoreCase(subLobName)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Row " + row + ": No SubLOB found for '" + subLobName + "'"))
        );
    }

    /* ---------------- Demand ---------------- */
    private AddDemand lookupDemand(Long demandId, String lobName, String subLobName, int row) {

        if (demandId == null) {
            throw new IllegalArgumentException("Row " + row + ": Demand ID is required");
        }

        Lob lob = lookupLob(lobName, row);
        SubLob subLob = lookupSubLob(subLobName, row);

        if (subLob != null) {
            return addDemandRepo
                    .findByDemandIdAndLobIdAndSubLobId(demandId, lob.getId(), subLob.getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Row " + row + ": No Demand for DemandId=" + demandId +
                                            ", LOB=" + lobName + ", SubLOB=" + subLobName));
        }

        return addDemandRepo
                .findByDemandIdAndLobId(demandId, lob.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Row " + row + ": No Demand for DemandId=" + demandId +
                                        ", LOB=" + lobName));
    }

    /* ---------------- Profile ---------------- */
    private Profile lookupProfile(String empId, String pan, int row) {

        if (empId != null && !empId.trim().isEmpty()) {
            return profileCache.computeIfAbsent("EMP_" + empId,
                    k -> profileRepo.findByEmpId(empId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException("Row " + row +
                                            ": EmpId not found " + empId)));
        }

        if (pan != null && !pan.trim().isEmpty()) {
            return profileCache.computeIfAbsent("PAN_" + pan,
                    k -> profileRepo.findByPanNumberIgnoreCase(pan)
                            .orElseThrow(() ->
                                    new IllegalArgumentException("Row " + row +
                                            ": PAN not found " + pan)));
        }

        throw new IllegalArgumentException(
                "Row " + row + ": Either EmpId or PAN must be provided");
    }

    /* ---------------- MAIN ---------------- */
    public List<ProfileTracker> parseProfileTrackers(InputStream in) {

        lobCache.clear();
        subLobCache.clear();
        profileCache.clear();

        Map<String, ProfileTrackerStatus> trackerStatusCache =
                preload(trackerStatusRepo.findAll(), ProfileTrackerStatus::getName);

        List<ProfileTracker> rows = new ArrayList<>();

        try (Workbook wb = new XSSFWorkbook(in)) {
            Sheet s = wb.getSheetAt(0);

            for (Row r : s) {

                if (r.getRowNum() == 0 || isRowEmpty(r)) continue;

                int row = r.getRowNum();

                Long demandId = getLong(r.getCell(0));
                String lobName = getString(r.getCell(1));
                String subLobName = getString(r.getCell(2));

                String empId = getString(r.getCell(3));
                String pan = getString(r.getCell(4));

                LocalDate shared = getDateValue(r.getCell(5));
                LocalDate iv = getDateValue(r.getCell(6));
                LocalDate attach = getDateValue(r.getCell(7));
                LocalDate decide = getDateValue(r.getCell(8));
                String statusNm = getString(r.getCell(9));

                AddDemand demand = lookupDemand(demandId, lobName, subLobName, row);
                Profile profile = lookupProfile(empId, pan, row);

                ProfileTrackerStatus status =
                        resolveOrCreate(
                                statusNm,
                                trackerStatusCache,
                                trackerStatusRepo::findByNameIgnoreCase,
                                ProfileTrackerStatus::new,
                                ProfileTrackerStatus::setName,
                                trackerStatusRepo::save
                        );

                ProfileTracker pt = new ProfileTracker();
                pt.setDemand(demand);
                pt.setProfile(profile);
                pt.setProfileSharedDate(shared);
                pt.setInterviewDate(iv);
                pt.setAttachedDate(attach);
                pt.setDecisionDate(decide);
                pt.setProfileTrackerStatus(status);

                rows.add(pt);
            }

        } catch (Exception e) {
            throw new RuntimeException("Import Error: " + e.getMessage(), e);
        }

        return rows;
    }

    /* ---------------- utils ---------------- */
    private String getString(Cell c) {
        if (c == null) return null;

        String value;

        if (c.getCellType() == CellType.STRING) {
            value = c.getStringCellValue().trim();
        } else if (c.getCellType() == CellType.NUMERIC) {
            value = String.valueOf((long) c.getNumericCellValue());
        } else {
            return null;
        }

        // ✅ normalize invalid values
        if (value == null
                || value.trim().isEmpty()
                || value.trim().equals("0")) {
            return null;
        }

        return value.trim();
    }

    private Long getLong(Cell c) {
        if (c == null) return null;
        try {
            return c.getCellType() == CellType.NUMERIC
                    ? (long) c.getNumericCellValue()
                    : Long.parseLong(c.getStringCellValue().replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate getDateValue(Cell c) {
        return ExcelDateHelper.read(c);
    }

    private boolean isRowEmpty(Row row) {
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell c = row.getCell(i);
            if (c != null && c.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }
}