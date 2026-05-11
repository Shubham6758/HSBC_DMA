package com.coforge.hsbcdma.Import.Profile;

import com.coforge.hsbcdma.Import.ExcelDateHelper;
import com.coforge.hsbcdma.entity.CountryCode;
import com.coforge.hsbcdma.entity.Location;
import com.coforge.hsbcdma.entity.Profile;
import com.coforge.hsbcdma.entity.dropdownEntities.*;
import com.coforge.hsbcdma.entity.dropdownEntities.Profile.*;
import com.coforge.hsbcdma.repository.CountryCodeRepository;
import com.coforge.hsbcdma.repository.LocationRepository;
import com.coforge.hsbcdma.repository.ProfileRepositories.*;
import com.coforge.hsbcdma.repository.dropdownRepository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileExcelImportHelper {

    public static final String EXCEL_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    // ✅ Existing repos
    private final SkillClusterRepository skillClusterRepo;
    private final LocationRepository locationRepo;
    private final HbuRepository hbuRepo;
    private final ExternalInternalRepository externalInternalRepo;
    private final CountryCodeRepository countryRepo;
    private final ProfileStatusRepository profileStatusRepo;
    private final PrimarySkillsRepository primarySkillsRepo;
    private final SecondarySkillsRepository secondarySkillsRepo;

    // ✅ NEW MASTER repos
    private final OriginRepository originRepo;
    private final KaratStatusMasterRepository karatRepo;
    private final SourceMasterRepository sourceRepo;
    private final OverallStatusMasterRepository overallRepo;

    public boolean isValidExcelFile(String contentType) {
        return EXCEL_TYPE.equals(contentType);
    }

    // ----------------------------------------------------------
    public List<Profile> parseProfiles(InputStream in) {




        List<Profile> profiles = new ArrayList<>();

        Map<String, SkillCluster> skillClusterCache = preload(skillClusterRepo.findAll(), SkillCluster::getSkillCluster);
        Map<String, Location> locationCache = preload(locationRepo.findAll(), Location::getName);
        Map<String, Hbu> hbuCache = preload(hbuRepo.findAll(), Hbu::getHbu);
        Map<String, ExternalInternal> extCache = preload(externalInternalRepo.findAll(), ExternalInternal::getExternalInternal);
        Map<String, CountryCode> countryCache = preload(countryRepo.findAll(), CountryCode::getName);
        Map<String, ProfileStatus> statusCache = preload(profileStatusRepo.findAll(), ProfileStatus::getName);
        Map<String, PrimarySkills> primaryCache = preload(primarySkillsRepo.findAll(), PrimarySkills::getPrimarySkills);
        Map<String, SecondarySkills> secondaryCache = preload(secondarySkillsRepo.findAll(), SecondarySkills::getSecondarySkills);

        Map<String, Origin> originCache = preload(originRepo.findAll(), Origin::getName);
        Map<String, KaratStatusMaster> karatCache = preload(karatRepo.findAll(), KaratStatusMaster::getName);
        Map<String, SourceMaster> sourceCache = preload(sourceRepo.findAll(), SourceMaster::getName);
        Map<String, OverallStatusMaster> overallCache = preload(overallRepo.findAll(), OverallStatusMaster::getName);

        try (Workbook wb = new XSSFWorkbook(in)) {

            Sheet sheet = wb.getSheetAt(0);

            for (Row row : sheet) {

                if (row.getRowNum() == 0) continue;
                if (isRowEmpty(row)) continue;

                Profile p = new Profile();

                try {

                    // BASIC
                    p.setCandidateName(getString(row.getCell(0)));
                    p.setEmailId(normalizeEmail(getString(row.getCell(1))));
                    p.setEmpId(getEmpId(row.getCell(2)));
                    p.setPhoneNumber(getLong(row.getCell(3)));
                    p.setIsActive(getBoolean(row.getCell(4)));
                    p.setExperience(getFloat(row.getCell(5)));

                    // MASTER
                    p.setSkillCluster(resolveOrCreate(getString(row.getCell(6)), skillClusterCache,
                            skillClusterRepo::findBySkillClusterIgnoreCase, SkillCluster::new, SkillCluster::setSkillCluster, skillClusterRepo::save));

                    p.setLocation(resolveOrCreate(getString(row.getCell(7)), locationCache,
                            locationRepo::findByNameIgnoreCase, Location::new, Location::setName, locationRepo::save));

                    p.setHbu(resolveOrCreate(getString(row.getCell(8)), hbuCache,
                            hbuRepo::findByHbuIgnoreCase, Hbu::new, Hbu::setHbu, hbuRepo::save));

                    p.setExternalInternal(resolveOrCreate(getString(row.getCell(9)), extCache,
                            externalInternalRepo::findByExternalInternalIgnoreCase, ExternalInternal::new, ExternalInternal::setExternalInternal, externalInternalRepo::save));

                    p.setCountry(resolveOrCreateCountry(getString(row.getCell(10)), getString(row.getCell(11))));

                    p.setProfileStatus(resolveOrCreate(getString(row.getCell(12)), statusCache,
                            profileStatusRepo::findByNameIgnoreCase, ProfileStatus::new, ProfileStatus::setName, profileStatusRepo::save));

                    // SKILLS
                    p.setPrimarySkills(resolveOrCreateSet(getString(row.getCell(13)), primaryCache,
                            primarySkillsRepo::findByPrimarySkillsIgnoreCase,
                            PrimarySkills::new, PrimarySkills::setPrimarySkills, primarySkillsRepo::save));

                    p.setSecondarySkills(resolveOrCreateSet(getString(row.getCell(14)), secondaryCache,
                            secondarySkillsRepo::findBySecondarySkillsIgnoreCase,
                            SecondarySkills::new, SecondarySkills::setSecondarySkills, secondarySkillsRepo::save));

                    // NORMAL
                    p.setPanNumber(normalizePAN(getString(row.getCell(15))));
                    p.setSummary(getString(row.getCell(16)));
                    p.setFileName(getString(row.getCell(17)));

                    // MASTER EXTRA
                    p.setOrigin(resolveOrCreate(getString(row.getCell(18)), originCache,
                            originRepo::findByNameIgnoreCase, Origin::new, Origin::setName, originRepo::save));

                    p.setKaratStatus(resolveOrCreate(getString(row.getCell(19)), karatCache,
                            karatRepo::findByNameIgnoreCase, KaratStatusMaster::new, KaratStatusMaster::setName, karatRepo::save));

                    p.setSource(resolveOrCreate(getString(row.getCell(20)), sourceCache,
                            sourceRepo::findByNameIgnoreCase, SourceMaster::new, SourceMaster::setName, sourceRepo::save));

                    p.setOverallStatusRdg(resolveOrCreate(getString(row.getCell(21)), overallCache,
                            overallRepo::findByNameIgnoreCase, OverallStatusMaster::new, OverallStatusMaster::setName, overallRepo::save));

                    // DATES
                    p.setDateOfSubmission(ExcelDateHelper.read(row.getCell(22)));
                    p.setKaratReadiness(getString(row.getCell(23)));
                    p.setWeekOf(ExcelDateHelper.read(row.getCell(24)));
                    p.setAccountReceivedOn(ExcelDateHelper.read(row.getCell(25)));
                    p.setStatusDate(ExcelDateHelper.read(row.getCell(26)));

                    // OTHERS
                    p.setLobShared(getString(row.getCell(27)));
                    p.setPractice(getString(row.getCell(28)));
                    p.setBand(getString(row.getCell(29)));
                    p.setAgeing(getInteger(row.getCell(30)));
                    p.setAgeingRange(getString(row.getCell(31)));
                    p.setCodes(getString(row.getCell(32)));
                    p.setCodeType(getString(row.getCell(33)));
                    p.setMinBillingRate(getDouble(row.getCell(34)));

                    // ✅ PROJECT CODE (FINAL FIX)
                    p.setProjectCode(getString(row.getCell(35)));

                    // EXTERNAL
                    p.setL1InterviewDate(ExcelDateHelper.read(row.getCell(36)));
                    p.setCurrentLocation(getString(row.getCell(37)));
                    p.setOfficialNP(getString(row.getCell(38)));
                    p.setNegotiableNpLwd(ExcelDateHelper.read(row.getCell(39)));
                    p.setRecruiter(getString(row.getCell(40)));

                    profiles.add(p);

                } catch (Exception ex) {

                    log.error("Row {} failed: {}", row.getRowNum() + 1, ex.getMessage());
                    continue;

                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Excel parse failed: " + e.getMessage(), e);
        }

        return profiles;
    }




    // ----------------------------------------------------------
    // ✅ GENERIC METHODS (unchanged)
    private static String key(String name) {
        return name == null ? null : name.trim().toLowerCase();
    }


    private String getEmpId(Cell c) {

        if (c == null) return null;

        try {
            if (c.getCellType() == CellType.NUMERIC) {

                // ✅ FIX: handle scientific + decimal
                BigDecimal bd = BigDecimal.valueOf(c.getNumericCellValue());
                return bd.toPlainString().split("\\.")[0]; // remove decimal part
            }

            String val = c.toString().trim();

            return val.isEmpty() ? null : val;

        } catch (Exception e) {
            return null;
        }
    }


    private LocalDate getDate(Cell c){ try { return c.getLocalDateTimeCellValue().toLocalDate(); } catch(Exception e){ return null; } }
    private Integer getInteger(Cell c){ try { return (int)c.getNumericCellValue(); } catch(Exception e){ return null; } }

    private Double getDouble(Cell c){ try { return c.getNumericCellValue(); } catch(Exception e){ return null; } }
    private static <T> Map<String, T> preload(Iterable<T> all, Function<T, String> nameGetter) {
        Map<String, T> map = new HashMap<>();
        for (T t : all) {
            map.put(key(nameGetter.apply(t)), t);
        }
        return map;
    }

    private static <T> T resolveOrCreate(
            String raw,
            Map<String, T> cache,
            Function<String, Optional<T>> finder,
            Supplier<T> factory,
            BiConsumer<T, String> setter,
            Function<T, T> saveFn) {

        String k = key(raw);
        if (k == null) return null;

        if (cache.containsKey(k)) return cache.get(k);

        Optional<T> existing = finder.apply(raw.trim());
        if (existing.isPresent()) {
            cache.put(k, existing.get());
            return existing.get();
        }

        T entity = factory.get();
        setter.accept(entity, raw.trim());
        entity = saveFn.apply(entity);
        cache.put(k, entity);
        return entity;
    }

    private static <T> Set<T> resolveOrCreateSet(
            String csv,
            Map<String, T> cache,
            Function<String, Optional<T>> finder,
            Supplier<T> factory,
            BiConsumer<T, String> setter,
            Function<T, T> saveFn) {

        Set<T> set = new HashSet<>();
        if (csv == null) return set;

        for (String val : csv.split(",")) {
            T t = resolveOrCreate(val.trim(), cache, finder, factory, setter, saveFn);
            if (t != null) set.add(t);
        }
        return set;
    }

    // ----------------------------------------------------------
    // ✅ CELL HELPERS

    private String getString(Cell c) {
        return c == null ? null : c.toString().trim();
    }

    private Long getLong(Cell c) {

        if (c == null) return null;

        try {
            if (c.getCellType() == CellType.NUMERIC) {
                long val = (long) c.getNumericCellValue();

                // ✅ FIX: treat 0 as null
                return val == 0 ? null : val;
            }

            String s = c.toString().trim();

            if (s.isEmpty()) return null;

            Long val = Long.parseLong(s);

            return val == 0 ? null : val; // ✅ FIX

        } catch (Exception e) {
            return null;
        }
    }

    private Float getFloat(Cell c) {
        try { return (float) c.getNumericCellValue(); }
        catch (Exception e) { return null; }
    }

    private Boolean getBoolean(Cell c) {
        if (c == null) return true;
        return c.toString().equalsIgnoreCase("true") || c.toString().equals("1");
    }

    private boolean isRowEmpty(Row row) {
        for (Cell c : row) {
            if (c.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }

    private String normalizeEmail(String email) {

        if (email == null) return null;

        String e = email.trim();

        if (e.isEmpty()) return null;  // ✅ FIX: convert blank → NULL

        return e.toLowerCase();

    }

    private String normalizePAN(String pan) {

        if (pan == null) return null;

        String p = pan.trim();

        if (p.isEmpty()) return null;  // ✅ FIX

        return p.toUpperCase();

    }

    private CountryCode resolveOrCreateCountry(String name, String callingCode) {

        // ✅ 1. Validate input
        if (name == null || name.trim().isEmpty()) {
            return null;   // country optional? otherwise throw error
        }

        String countryName = name.trim();

        // ✅ 2. Try find by name
        Optional<CountryCode> existingByName =
                countryRepo.findByNameIgnoreCase(countryName);

        if (existingByName.isPresent()) {

            CountryCode existing = existingByName.get();

            // ✅ If calling code provided → validate mismatch
            if (callingCode != null && !callingCode.trim().isEmpty()) {

                String existingCode = existing.getCallingCode();

                if (existingCode != null &&
                        !existingCode.equalsIgnoreCase(callingCode.trim())) {

                    throw new IllegalArgumentException(
                            "Country '" + countryName + "' already exists with calling code '" +
                                    existingCode + "' but Excel has '" + callingCode + "'"
                    );
                }
            }

            return existing;
        }

        // ✅ 3. If not found → calling code must be present
        if (callingCode == null || callingCode.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Calling code required for new country: " + countryName
            );
        }

        String code = callingCode.trim();

        // ✅ 4. Check calling code uniqueness conflict
        Optional<CountryCode> existingByCode =
                countryRepo.findByCallingCode(code);

        if (existingByCode.isPresent()) {

            CountryCode conflict = existingByCode.get();

            throw new IllegalArgumentException(
                    "Calling code '" + code + "' already mapped to country '" +
                            conflict.getName() + "'"
            );
        }

        // ✅ 5. Create new country
        CountryCode newCountry = new CountryCode();
        newCountry.setName(countryName);
        newCountry.setCallingCode(code);

        try {

            return countryRepo.save(newCountry);

        } catch (DataIntegrityViolationException ex) {

            // ✅ 6. Concurrent insert fallback
            return countryRepo.findByNameIgnoreCase(countryName)
                    .orElseThrow(() -> ex);
        }
    }
}
