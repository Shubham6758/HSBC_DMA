package com.coforge.hsbcdma.service.DemandService;

import com.coforge.hsbcdma.dto.DemandsDTO.AddDemandDropdownOptionsDTO;
import com.coforge.hsbcdma.dto.DemandsDTO.OptionDTO;
import com.coforge.hsbcdma.entity.Location;
import com.coforge.hsbcdma.entity.dropdownEntities.*;
import com.coforge.hsbcdma.repository.LocationRepository;
import com.coforge.hsbcdma.repository.dropdownRepository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@Service
public class AddDemandDropdownOptionsService {

    private final LobRepository lobRepository;
    private final SubLobRepository subLobRepository;
    private final SkillClusterRepository skillClusterRepository;
    private final PrimarySkillsRepository primarySkillsRepository;
    private final SecondarySkillsRepository secondarySkillsRepository;

    private final HiringManagerRepository hiringManagerRepository;
    private final SalesSpocRepository salesSpocRepository;
    private final DeliveryManagerRepository deliveryManagerRepository;

    private final PmoRepository pmoRepository;
    private final PmoSpocRepository pmoSpocRepository;

    private final DemandTypeRepository demandTypeRepository;
    private final StatusRepository statusRepository;
    private final DemandTimeLineRepository demandTimeLineRepository;
    private final ExternalInternalRepository externalInternalRepository;

    private final LocationRepository locationRepository;

    // ✅ New repos based on DTO additions
    private final HbuRepository hbuRepository;
    private final HbuSpocRepository hbuSpocRepository;
    private final ProjectManagerRepository projectManagerRepository;
    private final PodRepository podRepository;
    private final BandRepository bandRepository;
    private final PriorityRepository priorityRepository;

    public AddDemandDropdownOptionsService(
            LobRepository lobRepository,
            SubLobRepository subLobRepository,
            SkillClusterRepository skillClusterRepository,
            PrimarySkillsRepository primarySkillsRepository,
            SecondarySkillsRepository secondarySkillsRepository,
            HiringManagerRepository hiringManagerRepository,
            SalesSpocRepository salesSpocRepository,
            DeliveryManagerRepository deliveryManagerRepository,
            PmoRepository pmoRepository,
            PmoSpocRepository pmoSpocRepository,
            DemandTypeRepository demandTypeRepository,
            StatusRepository statusRepository,
            DemandTimeLineRepository demandTimeLineRepository,
            ExternalInternalRepository externalInternalRepository,
            LocationRepository locationRepository,

            // ✅ New
            HbuRepository hbuRepository,
            HbuSpocRepository hbuSpocRepository,
            ProjectManagerRepository projectManagerRepository,
            PodRepository podRepository,
            BandRepository bandRepository,
            PriorityRepository priorityRepository
    ) {
        this.lobRepository = lobRepository;
        this.subLobRepository = subLobRepository;
        this.skillClusterRepository = skillClusterRepository;
        this.primarySkillsRepository = primarySkillsRepository;
        this.secondarySkillsRepository = secondarySkillsRepository;
        this.hiringManagerRepository = hiringManagerRepository;
        this.salesSpocRepository = salesSpocRepository;
        this.deliveryManagerRepository = deliveryManagerRepository;
        this.pmoRepository = pmoRepository;
        this.pmoSpocRepository = pmoSpocRepository;
        this.demandTypeRepository = demandTypeRepository;
        this.statusRepository = statusRepository;
        this.demandTimeLineRepository = demandTimeLineRepository;
        this.externalInternalRepository = externalInternalRepository;
        this.locationRepository = locationRepository;

        // ✅ New
        this.hbuRepository = hbuRepository;
        this.hbuSpocRepository = hbuSpocRepository;
        this.projectManagerRepository = projectManagerRepository;
        this.podRepository = podRepository;
        this.bandRepository = bandRepository;
        this.priorityRepository = priorityRepository;
    }

    private <T> List<OptionDTO> toOptions(
            List<T> items,
            Function<T, Number> idFn,
            Function<T, String> nameFn
    ) {
        return items.stream()
                .map(e -> new OptionDTO(
                        idFn.apply(e) == null ? null : idFn.apply(e).longValue(),
                        nameFn.apply(e)))
                .sorted(Comparator.comparing(OptionDTO::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional(readOnly = true)
    public AddDemandDropdownOptionsDTO getAllAsOptions() {
        var dto = new AddDemandDropdownOptionsDTO();

        // Core masters
        dto.setLobList(toOptions(lobRepository.findAll(), Lob::getId, Lob::getLob));
        dto.setSubLobList(
                toOptions(
                        subLobRepository.findAll(),
                        SubLob::getId,
                        SubLob::getSubLob
                )
        );
        dto.setSkillClusterList(toOptions(skillClusterRepository.findAll(), SkillCluster::getId, SkillCluster::getSkillCluster));
        dto.setPrimarySkillsList(toOptions(primarySkillsRepository.findAll(), PrimarySkills::getId, PrimarySkills::getPrimarySkills));
        dto.setSecondarySkillsList(toOptions(secondarySkillsRepository.findAll(), SecondarySkills::getId, SecondarySkills::getSecondarySkills));

        // People masters
        dto.setHiringManagerList(toOptions(hiringManagerRepository.findAll(), HiringManager::getId, HiringManager::getHiringManager));
        dto.setSalesSpocList(toOptions(salesSpocRepository.findAll(), SalesSpoc::getId, SalesSpoc::getSalesSpoc));
        dto.setDeliveryManagerList(toOptions(deliveryManagerRepository.findAll(), DeliveryManager::getId, DeliveryManager::getDeliveryManager));

        dto.setPmoList(toOptions(pmoRepository.findAll(), Pmo::getId, Pmo::getPmo));
        dto.setPmoSpocList(toOptions(pmoSpocRepository.findAll(), PmoSpoc::getId, PmoSpoc::getPmoSpoc));

        // Demand masters
        dto.setDemandTypeList(toOptions(demandTypeRepository.findAll(), DemandType::getId, DemandType::getDemandType));
        dto.setStatusList(toOptions(statusRepository.findAll(), Status::getId, Status::getStatus));
        dto.setDemandTimelineList(toOptions(demandTimeLineRepository.findAll(), DemandTimeLine::getId, DemandTimeLine::getDemandTimeLine));
        dto.setExternalInternalList(toOptions(externalInternalRepository.findAll(), ExternalInternal::getId, ExternalInternal::getExternalInternal));

        // Location (optional)
//        if (locationRepository != null) {
//            dto.setDemandLocationList(toOptions(locationRepository.findAll(), Location::getId, Location::getName));
//        }

        if (locationRepository != null) {

            var locations = locationRepository.findAll();

            dto.setOnshoreLocationList(
                    toOptions(
                            locations.stream()
                                    .filter(l -> l.getType().name().equals("ONSHORE"))
                                    .toList(),
                            Location::getId,
                            Location::getName
                    )
            );

            dto.setOffshoreLocationList(
                    toOptions(
                            locations.stream()
                                    .filter(l -> l.getType().name().equals("OFFSHORE"))
                                    .toList(),
                            Location::getId,
                            Location::getName
                    )
            );
        }

        // ✅ Newly added dropdowns from your DTO
        dto.setHbuList(toOptions(hbuRepository.findAll(), Hbu::getId, Hbu::getHbu));                 // change getter as per entity
        dto.setHbuSpocList(toOptions(hbuSpocRepository.findAll(), HbuSpoc::getId, HbuSpoc::getHbuSpoc));
        dto.setProjectManagerList(toOptions(projectManagerRepository.findAll(), ProjectManager::getId, ProjectManager::getProjectManager));
        dto.setPodList(toOptions(podRepository.findAll(), Pod::getId, Pod::getPod));
        dto.setBandList(toOptions(bandRepository.findAll(), Band::getId, Band::getBand));
        dto.setPriorityList(toOptions(priorityRepository.findAll(), Priority::getId, Priority::getPriority));

        return dto;
    }
}
