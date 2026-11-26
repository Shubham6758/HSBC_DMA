package com.coforge.hsbcdma.master_api;

import com.coforge.hsbcdma.entity.CountryCode;
import com.coforge.hsbcdma.entity.Department;
import com.coforge.hsbcdma.entity.Location;
import com.coforge.hsbcdma.entity.SubDepartment;
import com.coforge.hsbcdma.entity.dropdownEntities.*;
import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.BgvStatus;
import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.OnboardingStatus;
import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.WbsType;
import com.coforge.hsbcdma.entity.dropdownEntities.Profile.Karat.*;
import com.coforge.hsbcdma.entity.dropdownEntities.Profile.*;
import com.coforge.hsbcdma.repository.CountryCodeRepository;
import com.coforge.hsbcdma.repository.DepartmentRepository;
import com.coforge.hsbcdma.repository.LocationRepository;
import com.coforge.hsbcdma.repository.OnboardingRepositories.BgvStatusRepository;
import com.coforge.hsbcdma.repository.OnboardingRepositories.OnboardingStatusRepository;
import com.coforge.hsbcdma.repository.OnboardingRepositories.WbsTypeRepository;
import com.coforge.hsbcdma.repository.ProfileRepositories.KaratRepositories.*;
import com.coforge.hsbcdma.repository.ProfileRepositories.*;
import com.coforge.hsbcdma.repository.ProfileTrackerRepositories.ProfileTrackerStatusRepository;
import com.coforge.hsbcdma.repository.SubDepartmentRepository;
import com.coforge.hsbcdma.repository.dropdownRepository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MasterService {

    private final LobRepository lobRepository;
    private final HbuRepository hbuRepository;
    private final BandRepository bandRepository;
    private final BusinessFunctionRepository businessFunctionRepository;
    private final DeliveryManagerRepository deliveryManagerRepository;
    private final DemandTimeLineRepository demandTimeLineRepository;
    private final DemandTypeRepository demandTypeRepository;
    private final ExternalInternalRepository externalInternalRepository;
    private final HiringManagerRepository hiringManagerRepository;
    private final PmoRepository pmoRepository;
    private final PodRepository podRepository;
    private final PrimarySkillsRepository primarySkillsRepository;
    private final PriorityRepository priorityRepository;
    private final ProjectManagerRepository projectManagerRepository;
    private final SalesSpocRepository salesSpocRepository;
    private final SecondarySkillsRepository secondarySkillsRepository;
    private final SkillClusterRepository skillClusterRepository;
    private final StatusRepository statusRepository;
    private final SubLobRepository subLobRepository;
    private final CountryCodeRepository countryCodeRepository;
    private final DepartmentRepository departmentRepository;
    private final SubDepartmentRepository subDepartmentRepository;
    private final LocationRepository locationRepository;
    private final BgvStatusRepository bgvStatusRepository;
    private final OnboardingStatusRepository onboardingStatusRepository;
    private final WbsTypeRepository wbsTypeRepository;
    private final CoderpadResultMasterRepository coderpadResultMasterRepository;
    private final KaratReadyStatusMasterRepository karatReadyStatusMasterRepository;
    private final KaratTestResultMasterRepository karatTestResultMasterRepository;
    private final PersonaMasterRepository personaMasterRepository;
    private final PrepStatusMasterRepository prepStatusMasterRepository;
    private final KaratStatusMasterRepository karatStatusMasterRepository;
    private final OriginRepository originRepository;
    private final OverallStatusMasterRepository overallStatusMasterRepository;
    private final ProfileStatusRepository profileStatusRepository;
    private final ProfileTrackerStatusRepository profileTrackerStatusRepository;
    private final SourceMasterRepository sourceMasterRepository;
    private final JobCategoryRepository jobCategortRepository;
    private final ContractTypeRepository contractTypeRepository;

    public MasterResponse saveMasters(MasterRequest request) {

        List<String> messages = new ArrayList<>();
        int success = 0;
        int duplicate = 0;

        for (MasterDataRequest master : request.getMasters()) {

            String masterType = master.getMasterType();

            for (MasterValueRequest value : master.getValues()) {

                try {

                    if (processMaster(masterType, value)) {
                        success++;
                    } else {
                        duplicate++;
                    }

                } catch (Exception ex) {

                    messages.add(
                            masterType +
                                    " : " +
                                    value.getName() +
                                    " -> " +
                                    ex.getMessage());
                }
            }
        }

        return new MasterResponse(
                success,
                duplicate,
                messages);
    }

    private boolean processMaster(
            String type,
            MasterValueRequest value) {

        switch (type.toUpperCase()) {

            case "LOB":
                return saveLob(value);

            case "HBU":
                return saveHbu(value);

            case "BAND":
                return saveBand(value);

            case "PMO":
                return savePmo(value);

            case "POD":
                return savePod(value);

            case "BUSINESS_FUNCTION":
                return saveBusinessFunction(value);

            case "DEMAND_TYPE":
                return saveDemandType(value);

            case "DEMAND_TIMELINE":
                return saveDemandTimeline(value);

            case "EXTERNAL_INTERNAL":
                return saveExternalInternal(value);

            case "PRIMARY_SKILLS":
                return savePrimarySkill(value);

            case "SECONDARY_SKILLS":
                return saveSecondarySkill(value);

            case "SKILL_CLUSTER":
                return saveSkillCluster(value);

            case "DELIVERY_MANAGER":
                return saveDeliveryManager(value);

            case "HIRING_MANAGER":
                return saveHiringManager(value);

            case "PROJECT_MANAGER":
                return saveProjectManager(value);

            case "SALES_SPOC":
                return saveSalesSpoc(value);

            case "PRIORITY":
                return savePriority(value);

            case "STATUS":
                return saveStatus(value);

            case "SUB_LOB":
                return saveSubLob(value);

            case "COUNTRY_CODE":
                return saveCountryCode(value);

            case "DEPARTMENT":
                return saveDepartment(value);

            case "SUB_DEPARTMENT":
                return saveSubDepartment(value);

            case "LOCATION":
                return saveLocation(value);

            case "BGV_STATUS":
                return saveBgvStatus(value);

            case "ONBOARDING_STATUS":
                return saveOnboardingStatus(value);

            case "WBS_TYPE":
                return saveWbsType(value);

            case "CODERPAD_RESULT_MASTER":
                return saveCoderpadResult(value);

            case "KARAT_READY_STATUS_MASTER":
                return saveKaratReadyStatus(value);

            case "KARAT_TEST_RESULT_MASTER":
                return saveKaratTestResult(value);

            case "PERSONA_MASTER":
                return savePersona(value);

            case "PREP_STATUS_MASTER":
                return savePrepStatus(value);

            case "KARAT_STATUS_MASTER":
                return saveKaratStatus(value);

            case "ORIGIN":
                return saveOrigin(value);

            case "OVERALL_STATUS_MASTER":
                return saveOverallStatus(value);

            case "PROFILE_STATUS":
                return saveProfileStatus(value);

            case "PROFILE_TRACKER_STATUS":
                return saveProfileTrackerStatus(value);

            case "SOURCE_MASTER":
                return saveSource(value);

            case "JOB_CATEGORY":
                return saveJobCategory(value);

            case "CONTRACT_TYPE":
                return saveContractType(value);

            default:
                throw new RuntimeException(
                        "Unsupported master type : " + type);
        }
    }

    private boolean saveContractType(MasterValueRequest value) {
        if (contractTypeRepository
                .findByContractTypeIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        ContractType entity = new ContractType();
        entity.setContractType(value.getName());

        contractTypeRepository.save(entity);

        return true;
    }

    private boolean saveJobCategory(MasterValueRequest value) {

        if (jobCategortRepository
                .findByJobCategoryIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        JobCategory entity = new JobCategory();
        entity.setJobCategory(value.getName());

        jobCategortRepository.save(entity);

        return true;
    }

    private boolean saveLob(MasterValueRequest value) {

        if (lobRepository.existsByLobIgnoreCase(value.getName())) {
            return false;
        }

        Lob lob = new Lob();
        lob.setLob(value.getName());
        lob.setCurrentDemandIdSequence(1);

        lobRepository.save(lob);

        return true;
    }

    private boolean saveHbu(MasterValueRequest value) {

        if (hbuRepository.existsByHbuIgnoreCase(value.getName())) {
            return false;
        }

        Hbu entity = new Hbu();
        entity.setHbu(value.getName());

        hbuRepository.save(entity);

        return true;
    }

    private boolean saveBand(MasterValueRequest value) {

        if (bandRepository.existsByBandIgnoreCase(value.getName())) {
            return false;
        }

        Band entity = new Band();
        entity.setBand(value.getName());

        bandRepository.save(entity);

        return true;
    }

    private boolean savePmo(MasterValueRequest value) {

        if (pmoRepository.findByPmoIgnoreCase(value.getName()).isPresent()) {
            return false;
        }

        Pmo entity = new Pmo();
        entity.setPmo(value.getName());

        pmoRepository.save(entity);

        return true;
    }

    private boolean savePod(MasterValueRequest value) {

        if (podRepository.findByPodIgnoreCase(value.getName()).isPresent()) {
            return false;
        }

        Pod entity = new Pod();
        entity.setPod(value.getName());

        podRepository.save(entity);

        return true;
    }

    private boolean saveBusinessFunction(MasterValueRequest value) {

        if (businessFunctionRepository
                .findByBusinessFunctionIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        BusinessFunction entity = new BusinessFunction();
        entity.setBusinessFunction(value.getName());

        businessFunctionRepository.save(entity);

        return true;
    }

    private boolean saveSubLob(MasterValueRequest value) {

        if(subLobRepository
                .findBySubLobIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        Lob lob = (Lob) lobRepository
                .findByLobIgnoreCase(value.getParentName())
                .orElseThrow(() ->
                        new RuntimeException("LOB not found"));

        SubLob entity = new SubLob();
        entity.setSubLob(value.getName());
        entity.setLob(lob);

        subLobRepository.save(entity);

        return true;
    }

    private boolean saveCountryCode(MasterValueRequest value) {

        if(countryCodeRepository
                .findByNameIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        CountryCode entity = new CountryCode();

        entity.setName(value.getName());
        entity.setCallingCode(value.getCallingCode());

        countryCodeRepository.save(entity);

        return true;
    }

    private boolean saveLocation(MasterValueRequest value) {

        if(locationRepository
                .findByNameIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        Location location = new Location();

        location.setName(value.getName());

        location.setType(
                Location.LocationType.valueOf(
                        value.getLocationType().toUpperCase()));

        locationRepository.save(location);

        return true;
    }

    private boolean saveStatus(MasterValueRequest value) {

        if(statusRepository
                .findByStatusIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        Status status = new Status();

        status.setStatus(value.getName());
        status.setPriority(value.getPriority());

        statusRepository.save(status);

        return true;
    }

    private boolean saveDemandType(MasterValueRequest value) {

        if (demandTypeRepository
                .findByDemandTypeIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        DemandType entity = new DemandType();
        entity.setDemandType(value.getName());

        demandTypeRepository.save(entity);

        return true;
    }

    private boolean saveDemandTimeline(MasterValueRequest value) {

        if (demandTimeLineRepository
                .findByDemandTimeLineIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        DemandTimeLine entity = new DemandTimeLine();
        entity.setDemandTimeLine(value.getName());

        demandTimeLineRepository.save(entity);

        return true;
    }

    private boolean saveExternalInternal(MasterValueRequest value) {

        if (externalInternalRepository
                .findByExternalInternalIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        ExternalInternal entity = new ExternalInternal();
        entity.setExternalInternal(value.getName());

        externalInternalRepository.save(entity);

        return true;
    }

    private boolean savePrimarySkill(MasterValueRequest value) {

        if (primarySkillsRepository
                .findByPrimarySkillsIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        PrimarySkills entity = new PrimarySkills();
        entity.setPrimarySkills(value.getName());

        primarySkillsRepository.save(entity);

        return true;
    }

    private boolean saveSkillCluster(MasterValueRequest value) {

        if (skillClusterRepository
                .findBySkillClusterIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        SkillCluster entity = new SkillCluster();
        entity.setSkillCluster(value.getName());

        skillClusterRepository.save(entity);

        return true;
    }

    private boolean saveDeliveryManager(MasterValueRequest value) {

        if (deliveryManagerRepository
                .findByDeliveryManagerIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        DeliveryManager entity = new DeliveryManager();
        entity.setDeliveryManager(value.getName());

        deliveryManagerRepository.save(entity);

        return true;
    }

    private boolean saveHiringManager(MasterValueRequest value) {

        if (hiringManagerRepository
                .findByHiringManagerIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        HiringManager entity = new HiringManager();
        entity.setHiringManager(value.getName());

        hiringManagerRepository.save(entity);

        return true;
    }

    private boolean saveSalesSpoc(MasterValueRequest value) {

        if (salesSpocRepository
                .findBySalesSpocIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        SalesSpoc entity = new SalesSpoc();
        entity.setSalesSpoc(value.getName());

        salesSpocRepository.save(entity);

        return true;
    }

    private boolean savePriority(MasterValueRequest value) {

        if (priorityRepository
                .findByPriorityIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        Priority entity = new Priority();
        entity.setPriority(value.getName());

        priorityRepository.save(entity);

        return true;
    }

    private boolean saveBgvStatus(MasterValueRequest value) {

        if (bgvStatusRepository
                .findByNameIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        BgvStatus entity = new BgvStatus();
        entity.setName(value.getName());

        bgvStatusRepository.save(entity);

        return true;
    }

    private boolean saveOnboardingStatus(MasterValueRequest value) {

        if (onboardingStatusRepository
                .findByNameIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        OnboardingStatus entity = new OnboardingStatus();
        entity.setName(value.getName());

        onboardingStatusRepository.save(entity);

        return true;
    }

    private boolean saveWbsType(MasterValueRequest value) {

        if (wbsTypeRepository
                .findByNameIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        WbsType entity = new WbsType();
        entity.setName(value.getName());

        wbsTypeRepository.save(entity);

        return true;
    }

    private boolean saveCoderpadResult(MasterValueRequest value) {

        if (coderpadResultMasterRepository
                .existsByNameIgnoreCase(value.getName())) {
            return false;
        }

        CoderpadResultMaster entity = new CoderpadResultMaster();
        entity.setName(value.getName());

        coderpadResultMasterRepository.save(entity);

        return true;
    }

    private boolean saveKaratReadyStatus(MasterValueRequest value) {

        if (karatReadyStatusMasterRepository
                .existsByNameIgnoreCase(value.getName())) {
            return false;
        }

        KaratReadyStatusMaster entity = new KaratReadyStatusMaster();
        entity.setName(value.getName());

        karatReadyStatusMasterRepository.save(entity);

        return true;
    }

    private boolean saveKaratTestResult(MasterValueRequest value) {

        if (karatTestResultMasterRepository
                .existsByNameIgnoreCase(value.getName())) {
            return false;
        }

        KaratTestResultMaster entity = new KaratTestResultMaster();
        entity.setName(value.getName());

        karatTestResultMasterRepository.save(entity);

        return true;
    }

    private boolean savePersona(MasterValueRequest value) {

        if (personaMasterRepository
                .existsByNameIgnoreCase(value.getName())) {
            return false;
        }

        PersonaMaster entity = new PersonaMaster();
        entity.setName(value.getName());

        personaMasterRepository.save(entity);

        return true;
    }

    private boolean savePrepStatus(MasterValueRequest value) {

        if (prepStatusMasterRepository
                .existsByNameIgnoreCase(value.getName())) {
            return false;
        }

        PrepStatusMaster entity = new PrepStatusMaster();
        entity.setName(value.getName());

        prepStatusMasterRepository.save(entity);

        return true;
    }

    private boolean saveKaratStatus(MasterValueRequest value) {

        if (karatStatusMasterRepository
                .findByNameIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        KaratStatusMaster entity = new KaratStatusMaster();
        entity.setName(value.getName());

        karatStatusMasterRepository.save(entity);

        return true;
    }

    private boolean saveOrigin(MasterValueRequest value) {

        if (originRepository
                .findByNameIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        Origin entity = new Origin();
        entity.setName(value.getName());

        originRepository.save(entity);

        return true;
    }

    private boolean saveProfileStatus(MasterValueRequest value) {

        if (profileStatusRepository
                .findByNameIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        ProfileStatus entity = new ProfileStatus();
        entity.setName(value.getName());

        profileStatusRepository.save(entity);

        return true;
    }

    private boolean saveProfileTrackerStatus(MasterValueRequest value) {

        if (profileTrackerStatusRepository
                .findByNameIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        ProfileTrackerStatus entity = new ProfileTrackerStatus();
        entity.setName(value.getName());

        profileTrackerStatusRepository.save(entity);

        return true;
    }

    private boolean saveSource(MasterValueRequest value) {

        if (sourceMasterRepository
                .findByNameIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        SourceMaster entity = new SourceMaster();
        entity.setName(value.getName());

        sourceMasterRepository.save(entity);

        return true;
    }

    private boolean saveSecondarySkill(MasterValueRequest value) {

        if (secondarySkillsRepository
                .findBySecondarySkillsIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        SecondarySkills entity = new SecondarySkills();
        entity.setSecondarySkills(value.getName());

        secondarySkillsRepository.save(entity);

        return true;
    }

    private boolean saveProjectManager(MasterValueRequest value) {

        if (projectManagerRepository
                .findByProjectManagerIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        ProjectManager entity = new ProjectManager();
        entity.setProjectManager(value.getName());

        projectManagerRepository.save(entity);

        return true;
    }

    private boolean saveOverallStatus(MasterValueRequest value) {

        if (overallStatusMasterRepository
                .findByNameIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        OverallStatusMaster entity = new OverallStatusMaster();
        entity.setName(value.getName());

        overallStatusMasterRepository.save(entity);

        return true;
    }

    private boolean saveDepartment(MasterValueRequest value) {

        if (departmentRepository
                .findByNameIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        Department entity = new Department();
        entity.setName(value.getName());

        departmentRepository.save(entity);

        return true;
    }

    private boolean saveSubDepartment(MasterValueRequest value) {

        if (subDepartmentRepository
                .findByNameIgnoreCase(value.getName())
                .isPresent()) {
            return false;
        }

        SubDepartment entity = new SubDepartment();
        entity.setName(value.getName());

        subDepartmentRepository.save(entity);

        return true;
    }

}