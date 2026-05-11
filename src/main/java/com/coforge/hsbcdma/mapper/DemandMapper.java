package com.coforge.hsbcdma.mapper;

public class DemandMapper {

//    public static GetAllDemandsResponse toDto(
//            AddDemand d,
//            Map<Long, String> primarySkillIdToName,   // ✅ only primary fetched
//            Map<Long, String> secondarySkillIdToName, // ✅ only secondary fetched
//            Map<Long, String> locationIdToName
//
//    ) {
//
//        return GetAllDemandsResponse.builder()
//                .id(d.getId())
//                .flag(d.getFlag())
//
//                .demandId(d.getDemandId())
//                .rrNumber(d.getRrNumber())
//                .fileName(d.getFileName())
//
//                .experience(d.getExperience())
//                .remark(d.getRemark())
//                .demandReceivedDate(d.getDemandReceivedDate())
//
//                // JSON lists
//                .primarySkillIds(d.getPrimarySkillsId())
//                .primarySkillNames(mapNames(d.getPrimarySkillsId(), primarySkillIdToName))
//
//                .secondarySkillIds(d.getSecondarySkillsId())
//                .secondarySkillNames(mapNames(d.getSecondarySkillsId(), secondarySkillIdToName))
//
//                .locationIds(d.getDemandLocationId())
//                .locationNames(mapNames(d.getDemandLocationId(), locationIdToName))
//
//                // Dropdown references (ManyToOne)
//                .hbu(ref(d.getHbu(), x -> x.getId(), x -> x.getHbu()))
//                .hbuSpoc(ref(d.getHbuSpoc(), x -> x.getId(), x -> x.getHbuSpoc()))
//
//                .band(ref(d.getBand(), x -> x.getId(), x -> x.getBand()))
//                .priority(ref(d.getPriority(), x -> x.getId(), x -> x.getPriority()))
//                .lob(ref(d.getLob(), x -> x.getId(), x -> x.getLob()))
//                .demandType(ref(d.getDemandType(), x -> x.getId(), x -> x.getDemandType()))
//                .demandTimeline(ref(d.getDemandTimeline(), x -> x.getId(), x -> x.getDemandTimeLine()))
//                .externalInternal(ref(d.getExternalInternal(), x -> x.getId(), x -> x.getExternalInternal()))
//                .status(ref(d.getStatus(), x -> x.getId(), x -> x.getStatus()))
//                .pod(ref(d.getPod(), x -> x.getId(), x -> x.getPod()))
//
//                .pmo(ref(d.getPmo(), x -> x.getId(), x -> x.getPmo()))
//                .pmoSpoc(ref(d.getPmoSpoc(), x -> x.getId(), x -> x.getPmoSpoc()))
//                .salesSpoc(ref(d.getSalesSpoc(), x -> x.getId(), x -> x.getSalesSpoc()))
//
//                .hiringManager(ref(d.getHiringManager(), x -> x.getId(), x -> x.getHiringManager()))
//                .deliveryManager(ref(d.getDeliveryManager(), x -> x.getId(), x -> x.getDeliveryManager()))
//
//                .skillCluster(ref(d.getSkillCluster(), x -> x.getId(), x -> x.getSkillCluster()))
//
//                // Optional list (entity has single rr)
//                .demandRRDTOList(buildRRList(d))
//                .build();
//    }
//
//    private static List<DemandRRDTO> buildRRList(AddDemand d) {
//        // If you really want list in response; else remove from DTO.
//        DemandRRDTO rr = new DemandRRDTO(d.getDemandId(), d.getRrNumber(), d.getFileName());
//        return List.of(rr);
//    }
//
//    private static List<String> mapNames(List<Long> ids, Map<Long, String> idToName) {
//        if (ids == null) return List.of();
//        return ids.stream()
//                .map(idToName::get)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//    }
//
//    private static <T> RefDTO ref(T obj, Function<T, Long> idGetter, Function<T, String> nameGetter) {
//        if (obj == null) return null;
//        return RefDTO.builder()
//                .id(idGetter.apply(obj))
//                .name(nameGetter.apply(obj))
//                .build();
//    }

}
