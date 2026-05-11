package com.coforge.hsbcdma.export;

import com.coforge.hsbcdma.entity.AddDemand;
import com.coforge.hsbcdma.entity.Location;
import com.coforge.hsbcdma.entity.dropdownEntities.PrimarySkills;
import com.coforge.hsbcdma.entity.dropdownEntities.SecondarySkills;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class Helper {
//    public static String formatDemandId(AddDemand demand) {
//        if (demand == null) {
//            return null;
//        }
//
//        String lobName = null;
//
//        // If your field is getLob() then replace getLobId() with getLob()
//        if (demand.getLob() != null) {
//            lobName = demand.getLob().getName();
//        }
//
//        Long demandId = demand.getDemandId();
//
//        if (lobName != null && demandId != null) {
//            return lobName + "-" + demandId;
//        }
//        if (demandId != null) {
//            return String.valueOf(demandId);
//        }
//        return lobName;
//    }


    private static String label(Object o) {
        if (o == null) return null;
        try { Object v = o.getClass().getMethod("getName").invoke(o); return v == null ? null : v.toString(); }
        catch (Exception e) { return o.toString(); }
    }

    static String toProperCase(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }

        return Arrays.stream(value.trim().split("\\s+"))
                .filter(word -> !word.isBlank())
                .map(word -> Character.toUpperCase(word.charAt(0)) +
                        word.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(" "));
    }

    static String toProperCaseCommaSeparated(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Helper::toProperCase)
                .collect(Collectors.joining(", "));
    }


    static String joinPrimarySkills(Set<PrimarySkills> skills) {
        if (skills == null || skills.isEmpty()) {
            return "";
        }

        return skills.stream()
                .filter(Objects::nonNull)
                .map(PrimarySkills::getPrimarySkills)   // change getter if your field name is different
                .filter(Objects::nonNull)
                .map(Helper::toProperCase)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    static String joinSecondarySkills(Set<SecondarySkills> skills) {
        if (skills == null || skills.isEmpty()) {
            return "";
        }

        return skills.stream()
                .filter(Objects::nonNull)
                .map(SecondarySkills::getSecondarySkills)   // change getter if your field name is different
                .filter(Objects::nonNull)
                .map(Helper::toProperCase)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    static String joinLocations(Set<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            return "";
        }

        return locations.stream()
                .filter(Objects::nonNull)
                .map(Location::getName)   // change getter if your field name is different
                .filter(Objects::nonNull)
                .map(Helper::toProperCase)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    static String resolveDemandPrefix(AddDemand d) {

        if (d == null) return "NA";

        // Prefer SUB-LOB if present
        if (d.getSubLob() != null && d.getSubLob().getSubLob() != null) {
            return d.getSubLob().getSubLob().trim();   // WSIT, MSS
        }

        // Fallback to LOB
        if (d.getLob() != null && d.getLob().getLob() != null) {
            return d.getLob().getLob().trim();         // CIM, BFS...
        }

        return "NA";
    }


    public static String formatDemandId(AddDemand demand) {
        String prefix = resolveDemandPrefix(demand);
        Long id = demand != null ? demand.getDemandId() : null;
        return (id != null) ? prefix + "-" + id : prefix + "-NA";
    }

}
