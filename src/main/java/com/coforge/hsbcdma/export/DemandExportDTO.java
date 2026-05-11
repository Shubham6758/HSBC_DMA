package com.coforge.hsbcdma.export;

import com.coforge.hsbcdma.entity.AddDemand;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record DemandExportDTO(
        Long id,
        String demandId,
        Long rrNumber,
        Boolean isSubconRR,
//        Boolean flag,
        Boolean karat,

        String hbu,
        String hbuSpoc,
        String lob,
        String band,
        String priority,
        String demandType,
        String demandTimeline,
        String externalInternal,
        String status,
        String pod,
        String pmo,
        String salesSpoc,
        String hiringManager,
        String deliveryManager,
        String projectManager,
        String skillCluster,

        String experience,
        String primarySkills,
        String secondarySkills,
        String locations,
        String fileName,
        String remark,
        LocalDate p1FlagDate,

        LocalDate demandReceivedDate,
        LocalDateTime createdAt,
        String createdByName,
        LocalDateTime updatedAt,
        String updatedByName
) {
    public static DemandExportDTO from(AddDemand d) {
        String prefix = Helper.resolveDemandPrefix(d);
        String formattedDemandId = Helper.formatDemandId(d);
        return new DemandExportDTO(
                d.getId(),
               formattedDemandId,
                d.getRrNumber(),
                d.getIsSubconRR(),
//                d.getFlag(),
                d.getKaratFlag(),
                label(d.getHbu()),
                label(d.getHbuSpoc()),
                label(d.getLob()),
                label(d.getBand()),
                label(d.getPriority()),
                label(d.getDemandType()),
                label(d.getDemandTimeline()),
                label(d.getExternalInternal()),
                label(d.getStatus()),
                label(d.getPod()),
                label(d.getPmo()),
                label(d.getSalesSpoc()),
                label(d.getHiringManager()),
                label(d.getDeliveryManager()),
                label(d.getProjectManager()),
                label(d.getSkillCluster()),

                d.getExperience(),
                joinLabels(d.getPrimarySkills()),
                joinLabels(d.getSecondarySkills()),
                joinLabels(d.getDemandLocations()),
                d.getFileName(),
                d.getRemark(),
                d.getP1FlagDate(),
                d.getDemandReceivedDate(),
                d.getCreatedAt(),
                d.getCreatedByName(),
                d.getUpdatedAt(),
                d.getUpdatedByName()
        );
    }

    /**
     * Calls getName() on dropdown entities.
     * If your dropdown uses a different getter (e.g., getTitle()), update here.
     */
    private static String label(Object o) {
        if (o == null) return null;
        try {
            Object v = o.getClass().getMethod("getName").invoke(o);
            return v == null ? null : v.toString();
        } catch (Exception e) {
            return o.toString();
        }
    }

    private static String joinLabels(Set<?> set) {
        if (set == null || set.isEmpty()) return null;
        return set.stream()
                .filter(Objects::nonNull)
                .map(DemandExportDTO::label)
                .filter(Objects::nonNull)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.joining(", "));
    }
}