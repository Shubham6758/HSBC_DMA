package com.coforge.hsbcdma.serviceImpl;


import com.coforge.hsbcdma.dto.DemandDTO;
import com.coforge.hsbcdma.entity.Demand;
import com.coforge.hsbcdma.entity.enums.DemandStatus;
import com.coforge.hsbcdma.mapper.EntityDtoMapper;
import com.coforge.hsbcdma.repository.DemandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DemandService encapsulates Demand CRUD operations.
 */
@Service
@Transactional
public class DemandService {
    private static final Logger log = LoggerFactory.getLogger(DemandService.class);
    private final DemandRepository demandRepository;

    public DemandService(DemandRepository demandRepository) {
        this.demandRepository = demandRepository;
    }

    /**
     * Create a new demand.
     * Created by: Pooja.I
     */
    public DemandDTO create(DemandDTO dto) {
        Demand entity = EntityDtoMapper.toDemandEntity(dto);
        entity.setStatus(DemandStatus.OPEN);
        Demand saved = demandRepository.save(entity);
        return EntityDtoMapper.toDemandDTO(saved);
    }

    /**
     * Get demand by id.
     * Created by: Pooja.I
     */
    @Transactional(readOnly = true)
    public DemandDTO get(Long id) {
        Demand d = demandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demand not found: " + id));
        return EntityDtoMapper.toDemandDTO(d);
    }

    /**
     * List all demands.
     * Created by: Pooja.I
     */
    @Transactional(readOnly = true)
    public List<DemandDTO> list() {
        return demandRepository.findAll().stream()
                .map(EntityDtoMapper::toDemandDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update demand.
     * Created by: Pooja.I
     */
    public DemandDTO update(Long id, DemandDTO dto) {
        Demand d = demandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demand not found: " + id));
        // copy DTO into entity
        d.setDemandBusinessId(dto.getDemandBusinessId());
        d.setRr(dto.getRr());
        d.setLob(dto.getLob());
        d.setHiringManager(dto.getHiringManager());
        d.setSkillCluster(dto.getSkillCluster());
        d.setPrimarySkill(dto.getPrimarySkill());
        d.setSecondarySkill(dto.getSecondarySkill());
        d.setExternality(dto.getExternality());
        log.info("Demand updated id={} businessId={}",  demandRepository.save(d).getId(),  demandRepository.save(d).getDemandBusinessId());
        return EntityDtoMapper.toDemandDTO( demandRepository.save(d));
    }

    /**
     * Delete demand.
     * Created by: Pooja.I
     */
    public void delete(Long id) {
        Demand d = demandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Demand not found: " + id));
        demandRepository.delete(d);
        log.info("Demand deleted id={} businessId={}", d.getId(), d.getDemandBusinessId());
    }
}
