package com.coforge.hsbcdma.audit;


import com.coforge.hsbcdma.entity.AddDemand;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class AddDemandSnapshot1Builder {

    private final AddDemandRepository demandRepo;

    public Map<String, Object> snapshot(AddDemand d) {
        if (d == null) return Collections.emptyMap();
        if (d.getId() == null) throw new IllegalArgumentException("AddDemand PK id is null, save first!");

        Map<String, Object> m = new LinkedHashMap<>();

        // PK + business ids
        m.put("id", d.getId());
        m.put("flag", d.getFlag());
        m.put("demandId", d.getDemandId());
        m.put("rrNumber", d.getRrNumber());

        // Scalars
        m.put("experience", d.getExperience());
        m.put("fileName", d.getFileName());
        m.put("remark", d.getRemark());
        m.put("demandReceivedDate", d.getDemandReceivedDate() != null ? d.getDemandReceivedDate().toString() : null);

        // Who fields from AddDemand
        m.put("createdByUserId", d.getCreatedByUserId());
        m.put("createdByName", d.getCreatedByName());
        m.put("updatedByUserId", d.getUpdatedByUserId());
        m.put("updatedByName", d.getUpdatedByName());

        // ManyToOne references (store only ids; proxy-safe)
        m.put("hbuId", idOf(d.getHbu()));
        m.put("hbuSpocId", idOf(d.getHbuSpoc()));
        m.put("bandId", idOf(d.getBand()));
        m.put("priorityId", idOf(d.getPriority()));
        m.put("lobId", idOf(d.getLob()));
        m.put("demandTypeId", idOf(d.getDemandType()));
        m.put("demandTimelineId", idOf(d.getDemandTimeline()));
        m.put("externalInternalId", idOf(d.getExternalInternal()));
        m.put("statusId", idOf(d.getStatus()));
        m.put("podId", idOf(d.getPod()));
        m.put("pmoSpocId", idOf(d.getPmoSpoc()));
        m.put("pmoId", idOf(d.getPmo()));
        m.put("salesSpocId", idOf(d.getSalesSpoc()));
        m.put("hiringManagerId", idOf(d.getHiringManager()));
        m.put("deliveryManagerId", idOf(d.getDeliveryManager()));
        m.put("skillClusterId", idOf(d.getSkillCluster()));
        m.put("projectManagerId", idOf(d.getProjectManager()));

        // Collections via mapping tables (sorted)
        m.put("primarySkillsIds", sorted(demandRepo.listPrimarySkillIds(d.getId())));
        m.put("secondarySkillsIds", sorted(demandRepo.listSecondarySkillIds(d.getId())));
        m.put("locationIds", sorted(demandRepo.listLocationIds(d.getId())));

        return m;
    }

    private List<Long> sorted(List<Long> ids) {
        if (ids == null) return List.of();
        return ids.stream().filter(Objects::nonNull).sorted().toList();
    }

    private Long idOf(Object ref) {
        if (ref == null) return null;
        try {
            return (Long) ref.getClass().getMethod("getId").invoke(ref);
        } catch (Exception e) {
            return null;
        }
    }
}
