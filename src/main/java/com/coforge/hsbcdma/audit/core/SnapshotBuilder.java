package com.coforge.hsbcdma.audit.core;

import java.util.Map;

public interface SnapshotBuilder<T>{
    Map<String, Object> snapshot(T entity);
}