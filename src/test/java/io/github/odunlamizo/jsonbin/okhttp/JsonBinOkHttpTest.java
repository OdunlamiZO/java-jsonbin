package io.github.odunlamizo.jsonbin.okhttp;

import static org.junit.jupiter.api.Assertions.*;

import io.github.odunlamizo.jsonbin.JsonBin;
import io.github.odunlamizo.jsonbin.JsonBinException;
import io.github.odunlamizo.jsonbin.model.Bin;
import io.github.odunlamizo.jsonbin.model.BinHandle;
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

        JsonBin jsonBin =
                new JsonBinOkHttp.Builder().withMasterKey("dummy-key").withBaseUrl(mockUrl).build();

        Bin<UserList> result = jsonBin.readBin("test-bin-id", UserList.class);

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

        JsonBin jsonBin =
                new JsonBinOkHttp.Builder().withMasterKey("dummy-key").withBaseUrl(mockUrl).build();

        JsonBinException exception =
                assertThrows(
                        JsonBinException.class,
                        () -> jsonBin.readBin("invalid-id", UserList.class));

        assertTrue(exception.getMessage().contains("Bin not found"));
    }

    @Test
    void shouldCreateBinSuccessfully() {
        String responseJson =
                """
                {
                  "record": {
                    "name": "Morounfoluwa Mary",
                    "age": 19
                  },
                  "metadata": {
                    "id": "new-bin-id",
                    "private": false,
                    "createdAt": "2024-01-01T10:00:00Z",
                    "name": "Users Bin"
                  }
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(responseJson)
                        .addHeader("Content-Type", "application/json"));

        String mockUrl = mockWebServer.url("").toString().replaceAll("/$", "");

        JsonBin jsonBin =
                new JsonBinOkHttp.Builder().withMasterKey("dummy-key").withBaseUrl(mockUrl).build();

        User user = new User();
        user.setName("Morounfoluwa Mary");
        user.setAge(19);

        Bin<User> result = jsonBin.createBin(user, "Users Bin", false, null);

        assertNotNull(result);
        assertEquals("new-bin-id", result.getMetadata().getId());
        assertEquals("Users Bin", result.getMetadata().getName());
        assertEquals("Morounfoluwa Mary", result.getRecord().getName());
    }

    @Test
    void shouldUpdateBinSuccessfully() {
        String responseJson =
                """
                {
                  "record": {
                    "name": "Updated Name",
                    "age": 20
                  },
                  "metadata": {
                    "id": "bin-id",
                    "private": true,
                    "createdAt": "2024-01-01T10:00:00Z"
                  }
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(responseJson)
                        .addHeader("Content-Type", "application/json"));

        String mockUrl = mockWebServer.url("").toString().replaceAll("/$", "");

        JsonBin jsonBin =
                new JsonBinOkHttp.Builder().withMasterKey("dummy-key").withBaseUrl(mockUrl).build();

        User user = new User();
        user.setName("Updated Name");
        user.setAge(20);

        Bin<User> result = jsonBin.updateBin(user, "bin-id");

        assertEquals("bin-id", result.getMetadata().getId());
        assertEquals("Updated Name", result.getRecord().getName());
    }

    @Test
    void shouldReadCollectionBins() {
        String json =
                """
                [
                  {
                    "private": true,
                    "snippetMeta": { "name": "dev" },
                    "record": "bin-1",
                    "createdAt": "2024-01-01T10:00:00Z"
                  },
                  {
                    "private": false,
                    "snippetMeta": { "name": "staging" },
                    "record": "bin-2",
                    "createdAt": "2024-01-02T10:00:00Z"
                  }
                ]
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(json)
                        .addHeader("Content-Type", "application/json"));

        String mockUrl = mockWebServer.url("").toString().replaceAll("/$", "");

        JsonBin jsonBin =
                new JsonBinOkHttp.Builder().withMasterKey("dummy-key").withBaseUrl(mockUrl).build();

        List<BinHandle> bins = jsonBin.readCollection("collection-id");

        assertEquals(2, bins.size());
        assertEquals("bin-1", bins.get(0).getId());
        assertEquals("dev", bins.get(0).getSnippetMeta().getName());
    }

    @Test
    void shouldCreateCollection() {
        String json =
                """
                {
                  "record": "collection-id",
                  "metadata": {
                    "createdAt": "2024-01-01T10:00:00Z"
                  }
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(json)
                        .addHeader("Content-Type", "application/json"));

        String mockUrl = mockWebServer.url("").toString().replaceAll("/$", "");

        JsonBin jsonBin =
                new JsonBinOkHttp.Builder().withMasterKey("dummy-key").withBaseUrl(mockUrl).build();

        Bin<String> result = jsonBin.createCollection("My Collection");

        assertEquals("collection-id", result.getRecord());
    }

    @Test
    void shouldSendBinHeadersOnCreate() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        String mockUrl = mockWebServer.url("").toString().replaceAll("/$", "");

        JsonBin jsonBin =
                new JsonBinOkHttp.Builder().withMasterKey("dummy-key").withBaseUrl(mockUrl).build();

        jsonBin.createBin(new User(), "Test Bin", true, "collection-id");

        var recordedRequest = mockWebServer.takeRequest();

        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("Test Bin", recordedRequest.getHeader(JsonBin.HEADER_BIN_NAME));
        assertEquals("true", recordedRequest.getHeader(JsonBin.HEADER_BIN_PRIVATE));
        assertEquals("collection-id", recordedRequest.getHeader(JsonBin.HEADER_COLLECTION_ID));
    }

    @Test
    void shouldUpdateCollectionName() throws InterruptedException {
        String json =
                """
                {
                  "record": "collection-id",
                  "metadata": {
                    "createdAt": "2024-01-01T10:00:00Z"
                  }
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(json)
                        .addHeader("Content-Type", "application/json"));

        String mockUrl = mockWebServer.url("").toString().replaceAll("/$", "");

        JsonBin jsonBin =
                new JsonBinOkHttp.Builder().withMasterKey("dummy-key").withBaseUrl(mockUrl).build();

        Bin<String> result = jsonBin.updateCollection("collection-id", "New Name");

        assertEquals("collection-id", result.getRecord());

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("PUT", recordedRequest.getMethod());
        assertEquals("/c/collection-id/meta/name", recordedRequest.getPath());
        assertEquals("New Name", recordedRequest.getHeader(JsonBin.HEADER_COLLECTION_NAME));
    }
}
