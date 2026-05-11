package com.coforge.hsbcdma.service.DemandService;

import com.coforge.hsbcdma.dto.DemandsDTO.AddDemandDraftResponseDTO;
import com.coforge.hsbcdma.dto.DemandsDTO.RefDTO;
import com.coforge.hsbcdma.entity.AddDemandDraft;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandsDraftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ViewDemandDraftService {
    @Autowired
    private AddDemandsDraftRepository addDemandDraftRepository;

    public List<AddDemandDraftResponseDTO> getAllDemandDrafts() {

        return addDemandDraftRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }


    public AddDemandDraftResponseDTO getDraftById(Long id) {
        return addDemandDraftRepository.findFullDraftById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Draft Not Found"));
    }

    private AddDemandDraftResponseDTO mapToDTO(AddDemandDraft d){
        AddDemandDraftResponseDTO dto = new AddDemandDraftResponseDTO();

        dto.setId(d.getId());
        dto.setFlag(d.getFlag());
        dto.setKaratFlag(d.getKaratFlag());
        dto.setNumberOfPositions(d.getNumberOfPositions());
        dto.setExperience(d.getExperience());
        dto.setRemark(d.getRemark());
        dto.setDemandReceivedDate(d.getDemandReceivedDate());

        dto.setCreatedByName(d.getCreatedByName());
        dto.setCreatedByUserId(d.getCreatedByUserId());
        dto.setUpdatedByName(d.getUpdatedByName());
        dto.setUpdatedByUserId(d.getUpdatedByUserId());

        dto.setBand(d.getBand() != null ?
                new RefDTO(d.getBand().getId(), d.getBand().getBand()) : null);

        dto.setPriority(d.getPriority() != null ?
                new RefDTO(d.getPriority().getId(), d.getPriority().getPriority()) : null);

        dto.setLob(d.getLob() != null ?
                new RefDTO(d.getLob().getId(), d.getLob().getLob()) : null);

        dto.setDemandType(d.getDemandType() != null ?
                new RefDTO(d.getDemandType().getId(), d.getDemandType().getDemandType()) : null);

        dto.setDemandTimeline(d.getDemandTimeline() != null ?
                new RefDTO(d.getDemandTimeline().getId(), d.getDemandTimeline().getDemandTimeLine()) : null);

        dto.setExternalInternal(d.getExternalInternal() != null ?
                new RefDTO(d.getExternalInternal().getId(), d.getExternalInternal().getExternalInternal()) : null);

        dto.setStatus(d.getStatus() != null ?
                new RefDTO(d.getStatus().getId(), d.getStatus().getStatus()) : null);

        dto.setPod(d.getPod() != null ?
                new RefDTO(d.getPod().getId(), d.getPod().getPod()) : null);

        dto.setPmoSpoc(d.getPmoSpoc() != null ?
                new RefDTO(d.getPmoSpoc().getId(), d.getPmoSpoc().getPmoSpoc()) : null);

        dto.setPmo(d.getPmo() != null ?
                new RefDTO(d.getPmo().getId(), d.getPmo().getPmo()) : null);

        dto.setSalesSpoc(d.getSalesSpoc() != null ?
                new RefDTO(d.getSalesSpoc().getId(), d.getSalesSpoc().getSalesSpoc()) : null);

        dto.setHiringManager(d.getHiringManager() != null ?
                new RefDTO(d.getHiringManager().getId(), d.getHiringManager().getHiringManager()) : null);

        dto.setDeliveryManager(d.getDeliveryManager() != null ?
                new RefDTO(d.getDeliveryManager().getId(), d.getDeliveryManager().getDeliveryManager()) : null);

        dto.setHbu(d.getHbu() != null ?
                new RefDTO(d.getHbu().getId(), d.getHbu().getHbu()) : null);

        dto.setHbuSpoc(d.getHbuSpoc() != null ?
                new RefDTO(d.getHbuSpoc().getId(), d.getHbuSpoc().getHbuSpoc()) : null);

        dto.setSkillCluster(d.getSkillCluster() != null ?
                new RefDTO(d.getSkillCluster().getId(), d.getSkillCluster().getSkillCluster()) : null);


        dto.setPrimarySkills(
                d.getPrimarySkills() == null ? Collections.emptyList() :
                        d.getPrimarySkills().stream()
                                // Change getPrimarySkills() below to your “name” field getter
                                .map(ps -> new RefDTO(ps.getId(), ps.getPrimarySkills()))
                                .collect(Collectors.toList())
        );

        dto.setSecondarySkills(
                d.getSecondarySkills() == null ? Collections.emptyList() :
                        d.getSecondarySkills().stream()
                                // Change getSecondarySkills() below to your “name” field getter
                                .map(ss -> new RefDTO(ss.getId(), ss.getSecondarySkills()))
                                .collect(Collectors.toList())
        );

        dto.setDemandLocations(
                d.getDemandLocations() == null ? Collections.emptyList() :
                        d.getDemandLocations().stream()
                                // Change getLocation() below to your “name” field getter
                                .map(loc -> new RefDTO(loc.getId(), loc.getName()))
                                .collect(Collectors.toList())
        );


        dto.setRrDrafts(
                d.getRrDrafts() == null
                        ? Collections.emptyList()
                        : d.getRrDrafts().stream()
                        .map(rr -> new AddDemandDraftResponseDTO.AddDemandRRDraftDTO(
                                rr.getId(),        // RR Draft PK
                                rr.getRrNumber(),
                                rr.getFileName()
                        ))
                        .collect(Collectors.toList())
        );
//
        return dto;
        //  }).toList();

    }
}