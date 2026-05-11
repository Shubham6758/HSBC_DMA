package com.coforge.hsbcdma.audit.core;


public final class ReflectionIdUtil {

    private ReflectionIdUtil() {}

    public static Long idOf(Object ref) {
        if (ref == null) return null;
        try {
            Object v = ref.getClass().getMethod("getId").invoke(ref);
            return (v instanceof Long l) ? l : null;
        } catch (Exception e) {
            return null;
        }
    }
}
