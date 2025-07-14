package io.github.odunlamizo.jsonbin.util;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class JsonUtilTest {

    @Test
    void shouldDeserializeJsonToMap() throws JsonProcessingException {
        String json = "{\"name\":\"Odun\", \"age\":25}";
        Map<String, Object> result = JsonUtil.toValue(json, new TypeReference<>() {});

        assertEquals("Odun", result.get("name"));
        assertEquals(25, result.get("age"));
    }

    @Test
    void shouldDeserializeJsonToList() throws JsonProcessingException {
        String json = "[\"Lagos\", \"Abuja\", \"Ibadan\"]";
        List<String> result = JsonUtil.toValue(json, new TypeReference<>() {});

        assertEquals(3, result.size());
        assertTrue(result.contains("Lagos"));
        assertTrue(result.contains("Ibadan"));
    }

    //    @Test
    //    void shouldDeserializeJsonToCustomObject() throws JsonProcessingException {
    //        String json = "{\"currency\":\"NGN\",\"name\":\"Zenith Bank\"}";
    //
    //        Bank bank = JsonUtil.toValue(json, new TypeReference<>() {});
    //
    //        assertEquals("NGN", bank.getCurrency());
    //        assertEquals("Zenith Bank", bank.getName());
    //    }

    @Test
    void shouldThrowExceptionForInvalidJson() {
        String invalidJson = "{invalidJson}";

        assertThrows(
                JsonProcessingException.class,
                () -> {
                    JsonUtil.toValue(invalidJson, new TypeReference<Map<String, Object>>() {});
                });
    }
}
