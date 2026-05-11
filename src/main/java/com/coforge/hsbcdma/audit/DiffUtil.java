package com.coforge.hsbcdma.audit;

import java.util.*;

public class DiffUtil {

    /**
     * Returns: field -> {old:..., new:...}
     */
    public static Map<String, Object> diff(Map<String, Object> oldM, Map<String, Object> newM) {
        Map<String, Object> out = new LinkedHashMap<>();
        Set<String> keys = new LinkedHashSet<>();
        keys.addAll(oldM.keySet());
        keys.addAll(newM.keySet());

        for (String k : keys) {
            Object o = oldM.get(k);
            Object n = newM.get(k);
            if (!equalsValue(o, n)) {
                Map<String, Object> pair = new LinkedHashMap<>();
                pair.put("old", o);
                pair.put("new", n);
                out.put(k, pair);
            }
        }
        return out;
    }

    private static boolean equalsValue(Object a, Object b) {
        if (Objects.equals(a, b)) return true;
        if (a == null || b == null) return false;

        // for lists (IDs), compare as sets (order independent)
        if (a instanceof List<?> la && b instanceof List<?> lb) {
            if (la.size() != lb.size()) return false;
            return new HashSet<>(la).equals(new HashSet<>(lb));
        }
        return a.equals(b);
    }
}
