package io.github.odunlamizo.jsonbin.okhttp;

import static org.junit.jupiter.api.Assertions.*;

import io.github.odunlamizo.jsonbin.JsonBinException;
import io.github.odunlamizo.jsonbin.model.Bin;
import io.github.odunlamizo.jsonbin.model.User;
import io.github.odunlamizo.jsonbin.model.UserList;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JsonBinOkHttpTest {

    private MockWebServer mockWebServer;

    @BeforeEach
    void setup() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void teardown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void shouldDeserializeValidBinResponse() {
        String json =
                """
                {
                  "record": {
                    "users": [
                      { "name": "Morounfoluwa Mary", "age": 19 }
                    ]
                  },
                  "metadata": {
                    "id": "abc123",
                    "private": true,
                    "createdAt": "2024-01-01T10:00:00Z",
                    "name": "Test Bin"
                  }
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(json)
                        .addHeader("Content-Type", "application/json")
                        .setResponseCode(200));

        String mockUrl = mockWebServer.url("").toString().replaceAll("/$", "");

        JsonBinOkHttp<UserList> jsonBin =
                new JsonBinOkHttp.Builder()
                        .withMasterKey("dummy-key")
                        .withBaseUrl(mockUrl)
                        .build(UserList.class);

        Bin<UserList> result = jsonBin.readBin("test-bin-id");

        assertNotNull(result);
        assertEquals("abc123", result.getMetadata().getId());
        assertTrue(result.getMetadata().is_private());
        assertEquals("Test Bin", result.getMetadata().getName());

        List<User> users = result.getRecord().getUsers();
        assertEquals(1, users.size());
        assertEquals("Morounfoluwa Mary", users.get(0).getName());
        assertEquals(19, users.get(0).getAge());
    }

    @Test
    void shouldThrowExceptionOnErrorResponse() {
        String errorJson =
                """
                { "message": "Bin not found" }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(errorJson)
                        .addHeader("Content-Type", "application/json")
                        .setResponseCode(404));

        String mockUrl = mockWebServer.url("").toString().replaceAll("/$", "");

        JsonBinOkHttp<UserList> jsonBin =
                new JsonBinOkHttp.Builder()
                        .withMasterKey("dummy-key")
                        .withBaseUrl(mockUrl)
                        .build(UserList.class);

        JsonBinException exception =
                assertThrows(JsonBinException.class, () -> jsonBin.readBin("invalid-id"));

        assertTrue(exception.getMessage().contains("Bin not found"));
    }
}
