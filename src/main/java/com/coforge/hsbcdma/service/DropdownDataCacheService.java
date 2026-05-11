package com.coforge.hsbcdma.service;

import com.coforge.hsbcdma.dto.DropdownDataDTO;
import com.coforge.hsbcdma.entity.dropdownEntities.Lob;
import com.coforge.hsbcdma.repository.dropdownRepository.*;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class retrieves all the dropdown data from database tables required on Add New Demand page.
 * Created By : pratish.b
 */
@Service
public class DropdownDataCacheService {

    private static final Logger logger = LoggerFactory.getLogger(DropdownDataCacheService.class);

    @Autowired
    private LobRepository lobRepository;
    @Autowired
    private SkillClusterRepository skillClusterRepository;
    @Autowired
    private PrimarySkillsRepository primarySkillsRepository;
    @Autowired
    private SecondarySkillsRepository secondarySkillsRepository;
    @Autowired
    private HiringManagerRepository hiringManagerRepository;
    @Autowired
    private SalesSpocRepository salesSpocRepository;
    @Autowired
    private DeliveryManagerRepository deliveryManagerRepository;
    @Autowired
    private PmoRepository pmoRepository;
    @Autowired
    private HbuRepository hbuRepository;
    @Autowired
    private DemandTypeRepository demandTypeRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private DemandTimeLineRepository demandTimeLineRepository;
    @Autowired
    private ExternalInternalRepository externalInternalRepository;
    @Autowired
    private PmoSpocRepository pmoSpocRepository;

    @Getter
    @Setter
    private DropdownDataDTO drodownDTOCache;

    /**
     * This Method retrieves all the dropdown data from database tables and stores into DropDownDataDTO to be sent to UI.
     * Created By : pratish.b
     */
    public void loadDropdownDataFromDatabase(){

        DropdownDataDTO ddDto = new DropdownDataDTO();
        ddDto.setLobList(lobRepository.findAll());
        ddDto.setSkillClusterList(skillClusterRepository.findAll());
        ddDto.setPrimarySkillsList(primarySkillsRepository.findAll());
        ddDto.setSecondarySkillsList(secondarySkillsRepository.findAll());
        ddDto.setHiringManagerList(hiringManagerRepository.findAll());
        ddDto.setSalesSpocList(salesSpocRepository.findAll());
        ddDto.setDeliveryManagerList(deliveryManagerRepository.findAll());
        ddDto.setPmoList(pmoRepository.findAll());
        ddDto.setHbuList(hbuRepository.findAll());
        ddDto.setDemandTypeList(demandTypeRepository.findAll());
        ddDto.setStatusList(statusRepository.findAll());
        ddDto.setDemandTimeLineList(demandTimeLineRepository.findAll());
        ddDto.setExternalInternalList(externalInternalRepository.findAll());
        ddDto.setPmoSpocList(pmoSpocRepository.findAll());

        drodownDTOCache = ddDto;

        List<Lob> lobList = drodownDTOCache.getLobList();
        for(Lob lb : lobList){
            logger.info(" Lob is {} and Current Demand Id is {} for id {} ",lb.getLob(),lb.getCurrentDemandIdSequence(),lb.getId());
        }
        logger.info("Dropdown data successfully loaded from Database inside Method : {} ", new Object(){}.getClass().getEnclosingMethod().getName());
    }
    /**
     * This Method is called only once when server starts and then caches data in DropdownDataDTO which can be access by getter method of this serice class.
     * Created By : pratish.b
     */
    @PostConstruct
    public void preloadDropdownData(){
        logger.info("Preloading of dropdown data from Database inside inside Method : {} ", new Object(){}.getClass().getEnclosingMethod().getName());

        loadDropdownDataFromDatabase();
    }
}
