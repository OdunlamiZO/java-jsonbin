package io.github.odunlamizo.jsonbin.util;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.odunlamizo.jsonbin.model.*;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class JsonUtilTest {

    @Test
    void shouldDeserializeBinWithUserList() throws JsonProcessingException {
        String json =
                """
            {
              "record": {
                "users": [
                  {
                    "name": "Morounfoluwa Mary",
                    "age": 19
                  },
                  {
                    "name": "John Doe",
                    "age": 22
                  }
                ]
              },
              "metadata": {
                "id": "65b9e853266cfc3fde83a460",
                "private": true,
                "createdAt": "2024-01-31T06:27:31.021Z",
                "name": "User List Bin"
              }
            }
            """;

        Bin<UserList> bin = JsonUtil.toValue(json, new TypeReference<>() {});

        assertNotNull(bin);
        assertNotNull(bin.getRecord());
        assertNotNull(bin.getMetadata());

        List<User> users = bin.getRecord().getUsers();
        assertEquals(2, users.size());

        assertEquals("Morounfoluwa Mary", users.get(0).getName());
        assertEquals(19, users.get(0).getAge());

        assertEquals("John Doe", users.get(1).getName());
        assertEquals(22, users.get(1).getAge());

        assertEquals("65b9e853266cfc3fde83a460", bin.getMetadata().getId());
        assertTrue(bin.getMetadata().is_private());
        assertEquals("User List Bin", bin.getMetadata().getName());
        assertEquals(
                ZonedDateTime.parse("2024-01-31T06:27:31.021Z"), bin.getMetadata().getCreatedAt());
    }

    @Test
    void shouldThrowOnInvalidJson() {
        String invalidJson = "{ invalid json }";
        assertThrows(
                JsonProcessingException.class,
                () -> JsonUtil.toValue(invalidJson, new TypeReference<Bin<UserList>>() {}));
    }
}
