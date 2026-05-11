package com.coforge.hsbcdma.audit.core;


import java.util.LinkedHashMap;
import java.util.Map;

public final class RefSnapshotUtil {

    private RefSnapshotUtil() {}

    public static Map<String, Object> ref(Object entity) {
        if (entity == null) return null;

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", ReflectionIdUtil.idOf(entity));
        m.put("name", nameOf(entity));
        return m;
    }

    private static String nameOf(Object entity) {
        // Try common getter names used in dropdown/master entities
        String[] getters = {"getName", "getValue", "getLabel", "getStatusName", "getStatus"};

        for (String g : getters) {
            try {
                Object v = entity.getClass().getMethod(g).invoke(entity);
                if (v instanceof String s && !s.isBlank()) return s;
            } catch (Exception ignored) { }
        }
        return null;
    }
}
