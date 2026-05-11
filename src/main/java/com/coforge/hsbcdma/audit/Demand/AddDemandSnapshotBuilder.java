package com.coforge.hsbcdma.audit.Demand;

import com.coforge.hsbcdma.audit.core.ReflectionIdUtil;
import com.coforge.hsbcdma.audit.core.SnapshotBuilder;
import com.coforge.hsbcdma.entity.AddDemand;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AddDemandSnapshotBuilder implements SnapshotBuilder<AddDemand> {

    @Override
    public Map<String, Object> snapshot(AddDemand d) {
        if (d == null) return Collections.emptyMap();

        Map<String, Object> m = new LinkedHashMap<>();

        // ---- identity ----
        m.put("id", d.getId());               // PK
        m.put("demandId", d.getDemandId());   // business demand_id
        m.put("rrNumber", d.getRrNumber());

        // ---- scalar fields ----
        m.put("flag", d.getFlag());
        m.put("isSubconRR", d.getIsSubconRR());
        m.put("karatFlag", d.getKaratFlag());
        m.put("experience", d.getExperience());
        m.put("fileName", d.getFileName());
        m.put("remark", d.getRemark());
        m.put("demandReceivedDate", d.getDemandReceivedDate());

        // ---- ManyToOne refs -> IDs only ----
        m.put("hbuId", ReflectionIdUtil.idOf(d.getHbu()));
        m.put("hbuSpocId", ReflectionIdUtil.idOf(d.getHbuSpoc()));
        m.put("bandId", ReflectionIdUtil.idOf(d.getBand()));
        m.put("priorityId", ReflectionIdUtil.idOf(d.getPriority()));
        m.put("lobId", ReflectionIdUtil.idOf(d.getLob()));
        m.put("demandTypeId", ReflectionIdUtil.idOf(d.getDemandType()));
        m.put("demandTimelineId", ReflectionIdUtil.idOf(d.getDemandTimeline()));
        m.put("externalInternalId", ReflectionIdUtil.idOf(d.getExternalInternal()));
        m.put("statusId", ReflectionIdUtil.idOf(d.getStatus()));
        m.put("podId", ReflectionIdUtil.idOf(d.getPod()));
        m.put("pmoSpocId", ReflectionIdUtil.idOf(d.getPmoSpoc()));
        m.put("pmoId", ReflectionIdUtil.idOf(d.getPmo()));
        m.put("salesSpocId", ReflectionIdUtil.idOf(d.getSalesSpoc()));
        m.put("hiringManagerId", ReflectionIdUtil.idOf(d.getHiringManager()));
        m.put("deliveryManagerId", ReflectionIdUtil.idOf(d.getDeliveryManager()));
        m.put("skillClusterId", ReflectionIdUtil.idOf(d.getSkillCluster()));
        m.put("projectManagerId", ReflectionIdUtil.idOf(d.getProjectManager()));

        // ---- ManyToMany -> sorted IDs for stable diff ----
        m.put("primarySkillIds", sortedIds(d.getPrimarySkills()));
        m.put("secondarySkillIds", sortedIds(d.getSecondarySkills()));
        m.put("locationIds", sortedIds(d.getDemandLocations()));

        return m;
    }

    private <E> List<Long> sortedIds(Set<E> set) {
        if (set == null || set.isEmpty()) return Collections.emptyList();
        return set.stream()
                .map(ReflectionIdUtil::idOf)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
    }

}
