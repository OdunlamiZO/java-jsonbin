package io.github.odunlamizo.jsonbin.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class JsonUtil {

    private static final ObjectMapper MAPPER;

    static {
        MAPPER =
                new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .findAndRegisterModules()
                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private JsonUtil() {}

    public static <T> T toValue(String jsonData, TypeReference<T> valueTypeRef)
            throws JsonProcessingException {
        return MAPPER.readValue(jsonData, valueTypeRef);
    }
}
