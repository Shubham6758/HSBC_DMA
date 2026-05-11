package com.coforge.hsbcdma.service;

import com.coforge.hsbcdma.dto.DemandSheetDTO;
import com.coforge.hsbcdma.dto.UpdateDemandDTO;
import com.coforge.hsbcdma.entity.AddNewDemand;
import com.coforge.hsbcdma.repository.AddNewDemandRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UpdateDemandService {

    private static final Logger logger = LoggerFactory.getLogger(UpdateDemandService.class);

    @Autowired
    private AddNewDemandRepository addNewDemandRepository;

    @Autowired
    private DropdownDataCacheService dropdownDataCacheService;

    public DemandSheetDTO fetchAllDemands(){
        List<AddNewDemand> allDemands = addNewDemandRepository.findAll();

        if(allDemands.isEmpty()){
            throw new RuntimeException("Demands doesn't exist.");
        }
        DemandSheetDTO demandSheetDTO = new DemandSheetDTO();
        List<UpdateDemandDTO> updateDemandDTOList = allDemands.stream().map(this::convertEntityTODTO).collect(Collectors.toList());
        demandSheetDTO.setUpdateDemandDTOList(updateDemandDTOList);
        demandSheetDTO.setDropdownDataDTO(dropdownDataCacheService.getDrodownDTOCache());
        return demandSheetDTO;
    }

    public UpdateDemandDTO fetchDemandById(Long id){
         UpdateDemandDTO updateDemandDTO =convertEntityTODTO(addNewDemandRepository.findById(id).get());
         return updateDemandDTO;
    }

    @Transactional
    public void updateDemand(MultipartFile file, UpdateDemandDTO updateDemandDTO) throws IOException {

        //AddNewDemand demand = addNewDemandRepository.findById(updateDemandDTO.getId()).get();
        AddNewDemand demand = convertDTOToEntity(updateDemandDTO);
        //File upload for each id
        if (file != null && !file.isEmpty()) {
            demand.setFileName(file.getOriginalFilename());
            demand.setFileContentType(file.getContentType());
            demand.setData(file.getBytes());
        }
        addNewDemandRepository.save(demand);
    }

    private UpdateDemandDTO convertEntityTODTO(AddNewDemand demand){

        UpdateDemandDTO updateDemandDTO = new UpdateDemandDTO();
            updateDemandDTO.setId(demand.getId());
            updateDemandDTO.setDemandId(demand.getDemandId());
            updateDemandDTO.setRrNumber(demand.getRrNumber());
            updateDemandDTO.setLob(demand.getLob());
            updateDemandDTO.setPrimarySkills(demand.getPrimarySkills());
            updateDemandDTO.setSecondarySkills(demand.getSecondarySkills());
            updateDemandDTO.setDemandReceivedDate(demand.getDemandReceivedDate());
            updateDemandDTO.setHiringManager(demand.getHiringManager());
            updateDemandDTO.setSalesSpoc(demand.getSalesSpoc());
            updateDemandDTO.setDeliveryManager(demand.getDeliveryManager());
            updateDemandDTO.setPmo(demand.getPmo());
            updateDemandDTO.setHbu(demand.getHbu());
            updateDemandDTO.setDemandType(demand.getDemandType());
            updateDemandDTO.setDemandTimeline(demand.getDemandTimeline());
            updateDemandDTO.setProdProgramName(demand.getProdProgramName());
            updateDemandDTO.setExperience(demand.getExperience());
            updateDemandDTO.setPriority(demand.getPriority());
            updateDemandDTO.setDemandLocation(demand.getDemandLocation());
            updateDemandDTO.setPriorityComment(demand.getPriorityComment());
            updateDemandDTO.setPm(demand.getPm());
            updateDemandDTO.setBand(demand.getBand());
            updateDemandDTO.setP1Age(demand.getP1Age());
            updateDemandDTO.setCurrentProfileShared(demand.getCurrentProfileShared());
            updateDemandDTO.setExternalInternal(demand.getExternalInternal());
            updateDemandDTO.setStatus(demand.getStatus());
            updateDemandDTO.setPmoSpoc(demand.getPmoSpoc());
            updateDemandDTO.setRemark(demand.getRemark());

            return updateDemandDTO;
    }

    private AddNewDemand convertDTOToEntity(UpdateDemandDTO updateDemandDTO){

        AddNewDemand demand = addNewDemandRepository.findById(updateDemandDTO.getId()).get();
        demand.setId(updateDemandDTO.getId());
        demand.setDemandId(updateDemandDTO.getDemandId());
        demand.setRrNumber(updateDemandDTO.getRrNumber());
        demand.setLob(updateDemandDTO.getLob());
        demand.setPrimarySkills(updateDemandDTO.getPrimarySkills());
        demand.setSecondarySkills(updateDemandDTO.getSecondarySkills());
        demand.setDemandReceivedDate(updateDemandDTO.getDemandReceivedDate());
        demand.setHiringManager(updateDemandDTO.getHiringManager());
        demand.setSalesSpoc(updateDemandDTO.getSalesSpoc());
        demand.setDeliveryManager(updateDemandDTO.getDeliveryManager());
        demand.setPmo(updateDemandDTO.getPmo());
        demand.setHbu(updateDemandDTO.getHbu());
        demand.setDemandType(updateDemandDTO.getDemandType());
        demand.setDemandTimeline(updateDemandDTO.getDemandTimeline());
        demand.setProdProgramName(updateDemandDTO.getProdProgramName());
        demand.setExperience(updateDemandDTO.getExperience());
        demand.setPriority(updateDemandDTO.getPriority());
        demand.setDemandLocation(updateDemandDTO.getDemandLocation());
        demand.setPriorityComment(updateDemandDTO.getPriorityComment());
        demand.setPm(updateDemandDTO.getPm());
        demand.setBand(updateDemandDTO.getBand());
        demand.setP1Age(updateDemandDTO.getP1Age());
        demand.setCurrentProfileShared(updateDemandDTO.getCurrentProfileShared());
        demand.setExternalInternal(updateDemandDTO.getExternalInternal());
        demand.setStatus(updateDemandDTO.getStatus());
        demand.setPmoSpoc(updateDemandDTO.getPmoSpoc());
        demand.setRemark(updateDemandDTO.getRemark());
        return demand;
    }
}
