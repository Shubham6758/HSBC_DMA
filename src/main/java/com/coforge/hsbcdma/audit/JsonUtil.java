package com.coforge.hsbcdma.audit;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class JsonUtil {

    private final ObjectMapper mapper;

    public JsonUtil(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {

            log.error("JSON serialization failed. objType={}, obj={}",
                    (obj == null ? "null" : obj.getClass().getName()),
                    obj,
                    e
            );

            throw new IllegalStateException("JSON serialization failed", e);
        }
    }
}

