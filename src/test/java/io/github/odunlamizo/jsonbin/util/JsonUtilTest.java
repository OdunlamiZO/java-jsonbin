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

    @Test
    void shouldSerializeSimpleObjectToJson() throws JsonProcessingException {
        User user = new User();
        user.setName("Morounfoluwa Mary");
        user.setAge(19);

        String json = JsonUtil.toJson(user);

        assertNotNull(json);
        assertTrue(json.contains("\"name\":\"Morounfoluwa Mary\""));
        assertTrue(json.contains("\"age\":19"));
    }

    @Test
    void shouldSerializeBinWithNestedRecord() throws JsonProcessingException {
        User user1 = new User();
        user1.setName("Mary");
        user1.setAge(19);

        User user2 = new User();
        user2.setName("John");
        user2.setAge(22);

        UserList userList = new UserList();
        userList.setUsers(List.of(user1, user2));

        Bin<UserList> bin = new Bin<>();
        bin.setRecord(userList);

        String json = JsonUtil.toJson(bin);

        assertNotNull(json);
        assertTrue(json.contains("\"users\""));
        assertTrue(json.contains("\"name\":\"Mary\""));
        assertTrue(json.contains("\"name\":\"John\""));
    }

    @Test
    void shouldSerializeZonedDateTimeAsIsoString() throws JsonProcessingException {
        ZonedDateTime createdAt = ZonedDateTime.parse("2024-01-31T06:27:31.021Z");

        Metadata metadata = new Metadata();
        metadata.setId("bin-id");
        metadata.setCreatedAt(createdAt);
        metadata.set_private(true);

        Bin<String> bin = new Bin<>();
        bin.setMetadata(metadata);
        bin.setRecord("test");

        String json = JsonUtil.toJson(bin);

        assertNotNull(json);
        assertTrue(json.contains("\"createdAt\":\"2024-01-31T06:27:31.021Z\""));
    }

    @Test
    void shouldThrowWhenObjectIsNotSerializable() {
        Object nonSerializable =
                new Object() {
                    @SuppressWarnings("unused")
                    public Object getSelf() {
                        return this; // circular reference
                    }
                };

        assertThrows(JsonProcessingException.class, () -> JsonUtil.toJson(nonSerializable));
    }
}
