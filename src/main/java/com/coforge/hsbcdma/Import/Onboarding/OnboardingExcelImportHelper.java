package com.coforge.hsbcdma.Import.Onboarding;

import com.coforge.hsbcdma.Import.ExcelColumnHelper;
import com.coforge.hsbcdma.Import.ExcelDateHelper;
import com.coforge.hsbcdma.entity.AddDemand;
import com.coforge.hsbcdma.entity.Onboarding;
import com.coforge.hsbcdma.entity.Profile;
import com.coforge.hsbcdma.entity.ProfileTracker;
import com.coforge.hsbcdma.entity.dropdownEntities.Lob;
import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.BgvStatus;
import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.OnboardingStatus;
import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.WbsType;
import com.coforge.hsbcdma.entity.dropdownEntities.SubLob;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandRepository;
import com.coforge.hsbcdma.repository.OnboardingRepositories.BgvStatusRepository;
import com.coforge.hsbcdma.repository.OnboardingRepositories.OnboardingStatusRepository;
import com.coforge.hsbcdma.repository.OnboardingRepositories.WbsTypeRepository;
import com.coforge.hsbcdma.repository.ProfileRepositories.ProfileRepository;
import com.coforge.hsbcdma.repository.ProfileTrackerRepositories.ProfileTrackRepository;
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
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnboardingExcelImportHelper {

    private final AddDemandRepository demandRepo;
    private final ProfileRepository profileRepo;
    private final ProfileTrackRepository trackerRepo;

    private final LobRepository lobRepository;
    private final SubLobRepository subLobRepository;

    private final WbsTypeRepository wbsRepo;
    private final BgvStatusRepository bgvRepo;
    private final OnboardingStatusRepository statusRepo;

    /* ---------------- CACHE ---------------- */
    private final Map<String, Lob> lobCache = new HashMap<>();
    private final Map<String, SubLob> subLobCache = new HashMap<>();
    private final Map<String, Profile> profileCache = new HashMap<>();

    /* ---------------- MAIN ---------------- */
    public List<Onboarding> parse(InputStream in) {

        lobCache.clear();
        subLobCache.clear();
        profileCache.clear();

        Map<String, WbsType> wbsCache = preload(wbsRepo.findAll(), WbsType::getName);
        Map<String, BgvStatus> bgvCache = preload(bgvRepo.findAll(), BgvStatus::getName);
        Map<String, OnboardingStatus> statusCache =
                preload(statusRepo.findAll(), OnboardingStatus::getName);

        List<Onboarding> rows = new ArrayList<>();

        try (Workbook wb = new XSSFWorkbook(in)) {
            Sheet s = wb.getSheetAt(0);

            Map<String, Integer> headerMap =
                    ExcelColumnHelper.getHeaderIndexMap(s);

            for (Row r : s) {

                if (r.getRowNum() == 0 || isRowEmpty(r)) continue;

                int row = r.getRowNum();

                Long demandId = getLong(
                        ExcelColumnHelper.getCell(r, headerMap, "demand id")
                );

                String lobName = getString(
                        ExcelColumnHelper.getCell(r, headerMap, "lob")
                );

                String subLobName = getString(
                        ExcelColumnHelper.getCell(r, headerMap, "sub lob")
                );

                String empId = getString(
                        ExcelColumnHelper.getCell(r, headerMap, "emp id")
                );

                String pan = getString(
                        ExcelColumnHelper.getCell(r, headerMap, "pan number")
                );

                String emailId = getString(
                        ExcelColumnHelper.getCell(r, headerMap, "email id")
                );

                Long phoneNumber = getLong(
                        ExcelColumnHelper.getCell(r, headerMap, "phone number")
                );

                String wbsNm = getString(
                        ExcelColumnHelper.getCell(r, headerMap, "wbs type")
                );

                LocalDate offer = ExcelDateHelper.read(
                        ExcelColumnHelper.getCell(r, headerMap, "offer date")
                );

                LocalDate doj = ExcelDateHelper.read(
                        ExcelColumnHelper.getCell(r, headerMap, "date of joining")
                );

                Long ctool = getLong(
                        ExcelColumnHelper.getCell(r, headerMap, "ctool id")
                );

                String bgvNm = getString(
                        ExcelColumnHelper.getCell(r, headerMap, "bgv status")
                );

                LocalDate pev = ExcelDateHelper.read(
                        ExcelColumnHelper.getCell(r, headerMap, "pev upload date")
                );

                LocalDate vp = ExcelDateHelper.read(
                        ExcelColumnHelper.getCell(r, headerMap, "vp tagging")
                );

                LocalDate tech = ExcelDateHelper.read(
                        ExcelColumnHelper.getCell(r, headerMap, "tech select date")
                );

                LocalDate hsbc = ExcelDateHelper.read(
                        ExcelColumnHelper.getCell(r, headerMap, "hsbc onboarding date")
                );

                String statNm = getString(
                        ExcelColumnHelper.getCell(r, headerMap, "onboarding status")
                );


                AddDemand demand = lookupDemand(demandId, lobName, subLobName, row);
                Profile profile = lookupProfile(
                        empId,
                        pan,
                        emailId,
                        phoneNumber,
                        row
                );
                ProfileTracker tracker =
                        trackerRepo.findByDemandIdAndProfileId(
                                        demand.getId(), profile.getId())
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Row " + row +
                                                        ": ProfileTracker not found for demand=" + demandId));

                Onboarding ob = new Onboarding();
                ob.setDemand(demand);
                ob.setProfile(profile);
                ob.setProfileTracker(tracker);

                ob.setWbsType(resolveOrCreate(wbsNm, wbsCache,
                        wbsRepo::findByNameIgnoreCase,
                        WbsType::new, WbsType::setName, wbsRepo::save));

                ob.setOfferDate(offer);
                ob.setDateOfJoining(doj);
                ob.setCtoolId(ctool);

                ob.setBgvStatus(resolveOrCreate(bgvNm, bgvCache,
                        bgvRepo::findByNameIgnoreCase,
                        BgvStatus::new, BgvStatus::setName, bgvRepo::save));

                ob.setPevUploadDate(pev);
                ob.setVpTagging(vp);
                ob.setTechSelectDate(tech);
                ob.setHsbcOnboardingDate(hsbc);

                ob.setOnboardingStatus(resolveOrCreate(statNm, statusCache,
                        statusRepo::findByNameIgnoreCase,
                        OnboardingStatus::new, OnboardingStatus::setName, statusRepo::save));

                rows.add(ob);
            }

        } catch (Exception e) {
            throw new RuntimeException("IMPORT ERROR: " + e.getMessage(), e);
        }

        return rows;
    }

    /* ---------------- LOOKUPS ---------------- */

    private Lob lookupLob(String name, int row) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Row " + row + ": LOB required");

        return lobCache.computeIfAbsent(name.toLowerCase(),
                k -> lobRepository.findByLob(name)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Row " + row + ": LOB not found " + name)));
    }

    private SubLob lookupSubLob(String name, int row) {
        if (name == null || name.isBlank()) return null;

        return subLobCache.computeIfAbsent(name.toLowerCase(),
                k -> subLobRepository.findBySubLobIgnoreCase(name)
                        .orElseThrow(() ->
                                new IllegalArgumentException("Row " + row + ": SubLOB not found " + name)));
    }

    private AddDemand lookupDemand(Long demandId, String lobName, String subLobName, int row) {

        if (demandId == null)
            throw new IllegalArgumentException("Row " + row + ": Demand ID required");

        Lob lob = lookupLob(lobName, row);
        SubLob sub = lookupSubLob(subLobName, row);

        if (sub != null) {
            return demandRepo.findByDemandIdAndLobIdAndSubLobId(
                            demandId, lob.getId(), sub.getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Row " + row +
                                            ": No Demand for DemandId=" + demandId +
                                            ", LOB=" + lobName +
                                            ", SubLOB=" + subLobName));
        }

        return demandRepo.findByDemandIdAndLobId(
                        demandId, lob.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Row " + row +
                                        ": No Demand for DemandId=" + demandId +
                                        " and LOB=" + lobName));
    }

//    private Profile lookupProfile(String empId, String pan, int row) {
//
//        if (empId != null && !empId.isBlank()) {
//            return profileCache.computeIfAbsent("EMP_" + empId,
//                    k -> profileRepo.findByEmpId(empId)
//                            .orElseThrow(() ->
//                                    new IllegalArgumentException("Row " + row +
//                                            ": EmpId not found " + empId)));
//        }
//
//        if (pan != null && !pan.isBlank()) {
//            return profileCache.computeIfAbsent("PAN_" + pan,
//                    k -> profileRepo.findByPanNumberIgnoreCase(pan)
//                            .orElseThrow(() ->
//                                    new IllegalArgumentException("Row " + row +
//                                            ": PAN not found " + pan)));
//        }
//
//        throw new IllegalArgumentException("Row " + row + ": EmpId or PAN required");
//    }

    private Profile lookupProfile(
            String empId,
            String pan,
            String emailId,
            Long phoneNumber,
            int row) {

        if (empId != null && !empId.trim().isEmpty()) {
            return profileCache.computeIfAbsent(
                    "EMP_" + empId,
                    k -> profileRepo.findByEmpId(empId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Row " + row +
                                                    ": EmpId not found " + empId))
            );
        }

        if (pan != null && !pan.trim().isEmpty()) {
            return profileCache.computeIfAbsent(
                    "PAN_" + pan.toUpperCase(),
                    k -> profileRepo.findByPanNumberIgnoreCase(pan)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Row " + row +
                                                    ": PAN not found " + pan))
            );
        }

        if (emailId != null && !emailId.trim().isEmpty()) {
            return profileCache.computeIfAbsent(
                    "EMAIL_" + emailId.toLowerCase(),
                    k -> profileRepo.findByEmailIdIgnoreCase(emailId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Row " + row +
                                                    ": Email Id not found " + emailId))
            );
        }

        if (phoneNumber != null) {
            return profileCache.computeIfAbsent(
                    "PHONE_" + phoneNumber,
                    k -> profileRepo.findByPhoneNumber(phoneNumber)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Row " + row +
                                                    ": Phone Number not found " + phoneNumber))
            );
        }

        throw new IllegalArgumentException(
                "Row " + row +
                        ": Either EmpId, PAN, Email Id or Phone Number must be provided");
    }

    /* ---------------- GENERIC ---------------- */

    private static <T> Map<String, T> preload(Iterable<T> all, Function<T, String> g) {
        Map<String, T> m = new HashMap<>();
        for (T t : all) {
            if (g.apply(t) != null)
                m.put(g.apply(t).toLowerCase(), t);
        }
        return m;
    }

    private static <T> T resolveOrCreate(
            String raw,
            Map<String, T> cache,
            Function<String, Optional<T>> finder,
            Supplier<T> factory,
            BiConsumer<T, String> setter,
            Function<T, T> saver) {

        if (raw == null || raw.isBlank()) return null;

        return cache.computeIfAbsent(raw.toLowerCase(), k -> {
            return finder.apply(raw.trim()).orElseGet(() -> {
                T t = factory.get();
                setter.accept(t, raw.trim());
                try { return saver.apply(t); }
                catch (DataIntegrityViolationException e) {
                    return finder.apply(raw.trim()).orElseThrow();
                }
            });
        });
    }

    /* ---------------- UTILS ---------------- */

    private Long getLong(Cell c) {
        try {
            return c == null ? null :
                    c.getCellType() == CellType.NUMERIC
                            ? (long) c.getNumericCellValue()
                            : Long.parseLong(c.getStringCellValue().replaceAll("[^0-9]", ""));
        } catch (Exception e) { return null; }
    }

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

    private boolean isRowEmpty(Row r) {
        for (int i = r.getFirstCellNum(); i < r.getLastCellNum(); i++) {
            Cell c = r.getCell(i);
            if (c != null && c.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }
}