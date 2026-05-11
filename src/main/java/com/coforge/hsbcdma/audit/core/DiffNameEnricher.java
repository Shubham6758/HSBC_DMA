package com.coforge.hsbcdma.audit.core;


import com.coforge.hsbcdma.dto.DemandsDTO.RefDTO;
import com.coforge.hsbcdma.entity.CountryCode;
import com.coforge.hsbcdma.entity.Location;
import com.coforge.hsbcdma.entity.dropdownEntities.*;
import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.BgvStatus;
import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.OnboardingStatus;
import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.WbsType;
import com.coforge.hsbcdma.entity.dropdownEntities.Profile.EvaluationStatus;
import com.coforge.hsbcdma.entity.dropdownEntities.Profile.ProfileTrackerStatus;
import com.coforge.hsbcdma.repository.CountryCodeRepository;
import com.coforge.hsbcdma.repository.LocationRepository;
import com.coforge.hsbcdma.repository.OnboardingRepositories.BgvStatusRepository;
import com.coforge.hsbcdma.repository.OnboardingRepositories.OnboardingStatusRepository;
import com.coforge.hsbcdma.repository.OnboardingRepositories.WbsTypeRepository;
import com.coforge.hsbcdma.repository.ProfileTrackerRepositories.EvaluationStatusRepository;
import com.coforge.hsbcdma.repository.ProfileTrackerRepositories.ProfileTrackerStatusRepository;
import com.coforge.hsbcdma.repository.dropdownRepository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DiffNameEnricher {
    //  onboarding
    private final OnboardingStatusRepository onboardingStatusRepo;
    private final BgvStatusRepository bgvStatusRepo;
    private final WbsTypeRepository wbsTypeRepo;


    // for ProfileTracker
    private final EvaluationStatusRepository evaluationStatusRepo;
    private final ProfileTrackerStatusRepository profileTrackerStatusRepo;

    // ------------------- Demand/Profile dropdowns -------------------
    private final HbuRepository hbuRepo;
    private final LobRepository lobRepo;
    private final BandRepository bandRepo;
    private final PriorityRepository priorityRepo;
    private final StatusRepository statusRepo;
    private final SkillClusterRepository skillClusterRepo;
    private final ExternalInternalRepository externalInternalRepo;
    private final CountryCodeRepository countryCodeRepo;
    private final LocationRepository locationRepo;


    // ------------------- ✅ AddDemand remaining dropdowns -------------------
    private final DemandTypeRepository demandTypeRepo;
    private final DemandTimeLineRepository demandTimeLineRepo;
    private final PodRepository podRepo;
    private final PmoSpocRepository pmoSpocRepo;
    private final PmoRepository pmoRepo;
    private final SalesSpocRepository salesSpocRepo;
    private final HiringManagerRepository hiringManagerRepo;
    private final DeliveryManagerRepository deliveryManagerRepo;
    private final HbuSpocRepository hbuSpocRepo;
    private final ProjectManagerRepository projectManagerRepo;


    // Many-to-many skills
    private final PrimarySkillsRepository primarySkillsRepo;
    private final SecondarySkillsRepository secondarySkillsRepo;



    // Add more repos here later (evaluationStatusRepo, profileTrackerStatusRepo, etc.)

    public Map<String, Object> enrich(Map<String, Object> diff) {
        if (diff == null || diff.isEmpty()) return Collections.emptyMap();

        // 1) Collect IDs by type
        Set<Long> onboardingStatusIds = new HashSet<>();
        Set<Long> bgvStatusIds = new HashSet<>();
        Set<Long> wbsTypeIds = new HashSet<>();


        // Demand/Profile (single)
        Set<Long> hbuIds = new HashSet<>();
        Set<Long> lobIds = new HashSet<>();
        Set<Long> bandIds = new HashSet<>();
        Set<Long> priorityIds = new HashSet<>();
        Set<Long> statusIds = new HashSet<>();
        Set<Long> skillClusterIds = new HashSet<>();
        Set<Long> externalInternalIds = new HashSet<>();
        Set<Long> countryIds = new HashSet<>();
        Set<Long> locationSingleIds = new HashSet<>();

        // Demand/Profile (lists)
        Set<Long> primarySkillIds = new HashSet<>();
        Set<Long> secondarySkillIds = new HashSet<>();
        Set<Long> locationIds = new HashSet<>();


        // ✅ AddDemand remaining fields
        Set<Long> demandTypeIds = new HashSet<>();
        Set<Long> demandTimelineIds = new HashSet<>();
        Set<Long> podIds = new HashSet<>();
        Set<Long> pmoSpocIds = new HashSet<>();
        Set<Long> pmoIds = new HashSet<>();
        Set<Long> salesSpocIds = new HashSet<>();
        Set<Long> hiringManagerIds = new HashSet<>();
        Set<Long> deliveryManagerIds = new HashSet<>();
        Set<Long> hbuSpocIds = new HashSet<>();
        Set<Long> projectManagerIds = new HashSet<>();



        // ✅ Added sets for ProfileTracker
        Set<Long> evaluationStatusIds = new HashSet<>();
        Set<Long> profileTrackerStatusIds = new HashSet<>();

        // detect known keys and collect ids from old/new
        collectIds(diff, "onboardingStatusId", onboardingStatusIds);
        collectIds(diff, "bgvStatusId", bgvStatusIds);
        collectIds(diff, "wbsTypeId", wbsTypeIds);


        // ✅ ProfileTracker keys
        collectIds(diff, "evaluationStatusId", evaluationStatusIds);
        collectIds(diff, "profileTrackerStatusId", profileTrackerStatusIds);

        // --- Demand/Profile single keys ---
        collectIds(diff, "hbuId", hbuIds);
        collectIds(diff, "lobId", lobIds);
        collectIds(diff, "bandId", bandIds);
        collectIds(diff, "priorityId", priorityIds);
        collectIds(diff, "statusId", statusIds);
        collectIds(diff, "skillClusterId", skillClusterIds);
        collectIds(diff, "externalInternalId", externalInternalIds);
        collectIds(diff, "countryId", countryIds);
        collectIds(diff, "locationId", locationSingleIds);

        // --- Demand/Profile list keys ---
        collectListIds(diff, "primarySkillIds", primarySkillIds);
        collectListIds(diff, "secondarySkillIds", secondarySkillIds);
        collectListIds(diff, "locationIds", locationIds);


        // ✅ AddDemand remaining FK keys
        collectIds(diff, "demandTypeId", demandTypeIds);
        collectIds(diff, "demandTimelineId", demandTimelineIds);
        collectIds(diff, "podId", podIds);
        collectIds(diff, "pmoSpocId", pmoSpocIds);
        collectIds(diff, "pmoId", pmoIds);
        collectIds(diff, "salesSpocId", salesSpocIds);
        collectIds(diff, "hiringManagerId", hiringManagerIds);
        collectIds(diff, "deliveryManagerId", deliveryManagerIds);
        collectIds(diff, "hbuSpocId", hbuSpocIds);
        collectIds(diff, "projectManagerId", projectManagerIds);



        // 2) Batch load names
        Map<Long, String> onboardingStatusNames = loadOnboardingStatusNames(onboardingStatusIds);
        Map<Long, String> bgvStatusNames = loadBgvStatusNames(bgvStatusIds);
        Map<Long, String> wbsTypeNames = loadWbsTypeNames(wbsTypeIds);


        // ✅ ProfileTracker name maps
        Map<Long, String> evaluationStatusNames = loadEvaluationStatusNames(evaluationStatusIds);
        Map<Long, String> profileTrackerStatusNames = loadProfileTrackerStatusNames(profileTrackerStatusIds);


        // Demand Maps Names
        Map<Long, String> hbuNames = loadHbuNames(hbuIds);
        Map<Long, String> lobNames = loadLobNames(lobIds);
        Map<Long, String> bandNames = loadBandNames(bandIds);
        Map<Long, String> priorityNames = loadPriorityNames(priorityIds);
        Map<Long, String> statusNames = loadStatusNames(statusIds);
        Map<Long, String> skillClusterNames = loadSkillClusterNames(skillClusterIds);
        Map<Long, String> externalInternalNames = loadExternalInternalNames(externalInternalIds);
        Map<Long, String> countryNames = loadCountryNames(countryIds);
        Map<Long, String> locationNamesSingle = loadLocationNames(locationSingleIds);
        Map<Long, String> locationNamesList = loadLocationNames(locationIds);

        Map<Long, String> primarySkillNames = loadPrimarySkillNames(primarySkillIds);
        Map<Long, String> secondarySkillNames = loadSecondarySkillNames(secondarySkillIds);


        // ✅ AddDemand remaining FK name maps
        Map<Long, String> demandTypeNames = loadDemandTypeNames(demandTypeIds);
        Map<Long, String> demandTimelineNames = loadDemandTimelineNames(demandTimelineIds);
        Map<Long, String> podNames = loadPodNames(podIds);
        Map<Long, String> pmoSpocNames = loadPmoSpocNames(pmoSpocIds);
        Map<Long, String> pmoNames = loadPmoNames(pmoIds);
        Map<Long, String> salesSpocNames = loadSalesSpocNames(salesSpocIds);
        Map<Long, String> hiringManagerNames = loadHiringManagerNames(hiringManagerIds);
        Map<Long, String> deliveryManagerNames = loadDeliveryManagerNames(deliveryManagerIds);
        Map<Long, String> hbuSpocNames = loadHbuSpocNames(hbuSpocIds);
        Map<Long, String> projectManagerNames = loadProjectManagerNames(projectManagerIds);



        // 3) Rewrite diff entries (replace xxxId with xxx => {old:{id,name}, new:{id,name}})
        Map<String, Object> out = new LinkedHashMap<>(diff);

        rewrite(out, "onboardingStatusId", "onboardingStatus", onboardingStatusNames);
        rewrite(out, "bgvStatusId", "bgvStatus", bgvStatusNames);
        rewrite(out, "wbsTypeId", "wbsType", wbsTypeNames);

        // ✅ ProfileTracker rewrites
        rewrite(out, "evaluationStatusId", "evaluationStatus", evaluationStatusNames);
        rewrite(out, "profileTrackerStatusId", "profileTrackerStatus", profileTrackerStatusNames);



        // --- Demand/Profile single rewrites ---
        rewrite(out, "hbuId", "hbu", hbuNames);
        rewrite(out, "lobId", "lob", lobNames);
        rewrite(out, "bandId", "band", bandNames);
        rewrite(out, "priorityId", "priority", priorityNames);
        rewrite(out, "statusId", "status", statusNames);
        rewrite(out, "skillClusterId", "skillCluster", skillClusterNames);
        rewrite(out, "externalInternalId", "externalInternal", externalInternalNames);
        rewrite(out, "countryId", "country", countryNames);
        rewrite(out, "locationId", "location", locationNamesSingle);

        // --- Demand/Profile list rewrites ---
        rewriteList(out, "primarySkillIds", "primarySkills", primarySkillNames);
        rewriteList(out, "secondarySkillIds", "secondarySkills", secondarySkillNames);
        rewriteList(out, "locationIds", "locations", locationNamesList);


        // ✅ AddDemand remaining FK rewrites
        rewrite(out, "demandTypeId", "demandType", demandTypeNames);
        rewrite(out, "demandTimelineId", "demandTimeline", demandTimelineNames);
        rewrite(out, "podId", "pod", podNames);
        rewrite(out, "pmoSpocId", "pmoSpoc", pmoSpocNames);
        rewrite(out, "pmoId", "pmo", pmoNames);
        rewrite(out, "salesSpocId", "salesSpoc", salesSpocNames);
        rewrite(out, "hiringManagerId", "hiringManager", hiringManagerNames);
        rewrite(out, "deliveryManagerId", "deliveryManager", deliveryManagerNames);
        rewrite(out, "hbuSpocId", "hbuSpoc", hbuSpocNames);
        rewrite(out, "projectManagerId", "projectManager", projectManagerNames);


        return out;
    }

    private void collectIds(Map<String, Object> diff, String key, Set<Long> target) {
        Object val = diff.get(key);
        if (!(val instanceof Map<?, ?> m)) return;

        target.add(toLong(m.get("old")));
        target.add(toLong(m.get("new")));
        target.remove(null);
    }


    /** for list keys like primarySkillIds: {old:[1,2], new:[2,3]} */
    private void collectListIds(Map<String, Object> diff, String key, Set<Long> target) {
        Object val = diff.get(key);
        if (!(val instanceof Map<?, ?> m)) return;

        addAll(target, m.get("old"));
        addAll(target, m.get("new"));
        target.remove(null);
    }

    private void addAll(Set<Long> target, Object v) {
        if (v == null) return;
        if (v instanceof Collection<?> c) {
            for (Object x : c) target.add(toLong(x));
        } else {
            target.add(toLong(v));
        }
    }



    private void rewrite(Map<String, Object> out, String idKey, String outKey, Map<Long, String> names) {
        Object val = out.get(idKey);
        if (!(val instanceof Map<?, ?> m)) return;

        Long oldId = toLong(m.get("old"));
        Long newId = toLong(m.get("new"));

        Map<String, Object> enriched = new LinkedHashMap<>();
        enriched.put("old", oldId == null ? null : new RefDTO(oldId, names.get(oldId)));
        enriched.put("new", newId == null ? null : new RefDTO(newId, names.get(newId)));

        // add new enriched key and keep/remove old id key as you prefer
        out.put(outKey, enriched);
        out.remove(idKey); // remove old id-only key (optional)
    }


    /** list ids -> list of RefDTO */
    private void rewriteList(Map<String, Object> out, String idsKey, String outKey, Map<Long, String> names) {
        Object val = out.get(idsKey);
        if (!(val instanceof Map<?, ?> m)) return;

        List<RefDTO> oldRefs = toRefList(m.get("old"), names);
        List<RefDTO> newRefs = toRefList(m.get("new"), names);

        Map<String, Object> enriched = new LinkedHashMap<>();
        enriched.put("old", oldRefs);
        enriched.put("new", newRefs);

        out.put(outKey, enriched);
        out.remove(idsKey);
    }


    private List<RefDTO> toRefList(Object v, Map<Long, String> names) {
        if (!(v instanceof Collection<?> c)) return Collections.emptyList();
        List<RefDTO> list = new ArrayList<>();
        for (Object x : c) {
            Long id = toLong(x);
            if (id != null) list.add(new RefDTO(id, names.get(id)));
        }
        list.sort(Comparator.comparing(RefDTO::getId));
        return list;
    }



    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Long l) return l;
        if (v instanceof Integer i) return i.longValue();
        if (v instanceof String s && !s.isBlank()) return Long.parseLong(s);
        return null;
    }

//    onboarding loaders
    private Map<Long, String> loadOnboardingStatusNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return onboardingStatusRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(OnboardingStatus::getId, OnboardingStatus::getName));
    }

    private Map<Long, String> loadBgvStatusNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return bgvStatusRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(BgvStatus::getId, BgvStatus::getName));
    }

    private Map<Long, String> loadWbsTypeNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return wbsTypeRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(WbsType::getId, WbsType::getName));
    }


    // ---------- ✅ ProfileTracker loaders ----------
    private Map<Long, String> loadEvaluationStatusNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return evaluationStatusRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(EvaluationStatus::getId, EvaluationStatus::getName));
    }

    private Map<Long, String> loadProfileTrackerStatusNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return profileTrackerStatusRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(ProfileTrackerStatus::getId, ProfileTrackerStatus::getName));
    }


    // Demand/Profile dropdown loaders
    private Map<Long, String> loadHbuNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return hbuRepo.findAllById(ids).stream()
                // ✅ change getter if needed (getName vs getHbuName)
                .collect(Collectors.toMap(Hbu::getId, Hbu::getHbu));
    }

    private Map<Long, String> loadLobNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return lobRepo.findAllById(ids).stream()
                // ✅ change getter if needed (getName vs getLobName)
                .collect(Collectors.toMap(Lob::getId, Lob::getLob));
    }

    private Map<Long, String> loadBandNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return bandRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(Band::getId, Band::getBand));
    }

    private Map<Long, String> loadPriorityNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return priorityRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(Priority::getId, Priority::getPriority));
    }

    private Map<Long, String> loadStatusNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return statusRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(Status::getId, Status::getStatus));
    }

    private Map<Long, String> loadSkillClusterNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return skillClusterRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(SkillCluster::getId, SkillCluster::getSkillCluster));
    }

    private Map<Long, String> loadExternalInternalNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return externalInternalRepo.findAllById(ids).stream()
                // ✅ change getter if needed (getName)
                .collect(Collectors.toMap(ExternalInternal::getId, ExternalInternal::getName));
    }

    private Map<Long, String> loadCountryNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return countryCodeRepo.findAllById(ids).stream()
                // ✅ change getter if needed (getCountryName)
                .collect(Collectors.toMap(CountryCode::getId, CountryCode::getCallingCode));
    }

    private Map<Long, String> loadLocationNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return locationRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(Location::getId, Location::getName));
    }

    // Skills (lists)
    private Map<Long, String> loadPrimarySkillNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return primarySkillsRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(PrimarySkills::getId, PrimarySkills::getPrimarySkills));
    }

    private Map<Long, String> loadSecondarySkillNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return secondarySkillsRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(SecondarySkills::getId, SecondarySkills::getSecondarySkills));
    }


    // ------------------- ✅ AddDemand remaining loaders -------------------
    private Map<Long, String> loadDemandTypeNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return demandTypeRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(DemandType::getId, DemandType::getName)); // adjust getter if needed
    }

    private Map<Long, String> loadDemandTimelineNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return demandTimeLineRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(DemandTimeLine::getId, DemandTimeLine::getName)); // adjust getter if needed
    }

    private Map<Long, String> loadPodNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return podRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(Pod::getId, Pod::getName)); // adjust getter if needed
    }

    private Map<Long, String> loadPmoSpocNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return pmoSpocRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(PmoSpoc::getId, PmoSpoc::getName)); // adjust getter if needed
    }

    private Map<Long, String> loadPmoNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return pmoRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(Pmo::getId, Pmo::getName)); // adjust getter if needed
    }

    private Map<Long, String> loadSalesSpocNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return salesSpocRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(SalesSpoc::getId, SalesSpoc::getName)); // adjust getter if needed
    }

    private Map<Long, String> loadHiringManagerNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return hiringManagerRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(HiringManager::getId, HiringManager::getName)); // adjust getter if needed
    }

    private Map<Long, String> loadDeliveryManagerNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return deliveryManagerRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(DeliveryManager::getId, DeliveryManager::getName)); // adjust getter if needed
    }

    private Map<Long, String> loadHbuSpocNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return hbuSpocRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(HbuSpoc::getId, HbuSpoc::getName)); // adjust getter if needed
    }

    private Map<Long, String> loadProjectManagerNames(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return projectManagerRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(ProjectManager::getId, ProjectManager::getName)); // adjust getter if needed
    }
}