package io.github.odunlamizo.jsonbin.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtil {

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper().findAndRegisterModules();
    }

    private JsonUtil() {}

    public static <T> T toValue(String jsonData, TypeReference<T> valueTypeRef)
            throws JsonProcessingException {
        return MAPPER.readValue(jsonData, valueTypeRef);
    }
}
