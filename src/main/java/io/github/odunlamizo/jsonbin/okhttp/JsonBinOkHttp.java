package io.github.odunlamizo.jsonbin.okhttp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.odunlamizo.jsonbin.JsonBin;
import io.github.odunlamizo.jsonbin.JsonBinException;
import io.github.odunlamizo.jsonbin.model.Bin;
import io.github.odunlamizo.jsonbin.model.BinHandle;
import io.github.odunlamizo.jsonbin.model.Error;
import io.github.odunlamizo.jsonbin.util.JsonUtil;
import java.io.IOException;
import java.util.List;
import lombok.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/** JSONBIN.io Java SDK implementation powered by OkHttp */
public class JsonBinOkHttp implements JsonBin {
    private final String baseUrl;

    private final OkHttpClient client;

    private static final okhttp3.MediaType JSON = okhttp3.MediaType.parse("application/json");

    private JsonBinOkHttp(String masterKey, String accessKey, String baseUrl) {
        this.baseUrl = baseUrl;
        this.client =
                new OkHttpClient.Builder()
                        .addInterceptor(new AuthInterceptor(masterKey, accessKey))
                        .build();
    }

    public static class Builder {
        private String masterKey;

        private String accessKey;

        private String baseUrl = "https://api.jsonbin.io/v3";

        public Builder withMasterKey(String masterKey) {
            this.masterKey = masterKey;
            return this;
        }

        public Builder withAccessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public Builder withBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public JsonBinOkHttp build() {
            if ((masterKey == null || masterKey.isBlank())
                    && (accessKey == null || accessKey.isBlank())) {
                throw new IllegalArgumentException(
                        "Either masterKey or accessKey must be provided.");
            }

            return new JsonBinOkHttp(masterKey, accessKey, baseUrl);
        }
    }

    @Override
    public <T> Bin<T> readBin(@NonNull String binId, @NonNull Class<T> cls) {
        String url = String.format("%s/b/%s", baseUrl, binId);
        Request request = new Request.Builder().url(url).build();

        return newCall(request, getTypeRef(cls));
    }

    @Override
    public <T> Bin<T> createBin(T record, String binName, Boolean isPrivate, String collectionId) {
        String url = String.format("%s/b", baseUrl);

        String bodyJson;
        try {
            bodyJson = JsonUtil.toJson(record);
        } catch (Exception exception) {
            throw new JsonBinException("Failed to serialize record", exception);
        }

        okhttp3.RequestBody body = okhttp3.RequestBody.create(bodyJson, JSON);

        Request.Builder requestBuilder = new Request.Builder().url(url).post(body);

        if (binName != null && !binName.isBlank()) {
            requestBuilder.header(HEADER_BIN_NAME, binName);
        }

        if (isPrivate != null) {
            requestBuilder.header(HEADER_BIN_PRIVATE, isPrivate.toString());
        }

        if (collectionId != null && !collectionId.isBlank()) {
            requestBuilder.header(HEADER_COLLECTION_ID, collectionId);
        }

        Request request = requestBuilder.build();

        return newCall(request, getTypeRef(getClass(record)));
    }

    @Override
    public <T> Bin<T> updateBin(@NonNull T record, @NonNull String binId) {
        String url = String.format("%s/b/%s", baseUrl, binId);

        String bodyJson;
        try {
            bodyJson = JsonUtil.toJson(record);
        } catch (Exception exception) {
            throw new JsonBinException("Failed to serialize record", exception);
        }

        okhttp3.RequestBody body = okhttp3.RequestBody.create(bodyJson, JSON);

        Request request = new Request.Builder().url(url).put(body).build();

        return newCall(request, getTypeRef(getClass(record)));
    }

    @Override
    public List<BinHandle> readCollection(@NonNull String collectionId) {
        String url = String.format("%s/c/%s/bins", baseUrl, collectionId);
        Request request = new Request.Builder().url(url).build();

        return newCall(request, new TypeReference<>() {});
    }

    @Override
    public Bin<String> createCollection(@NonNull String collectionName) {
        String url = String.format("%s/c", baseUrl);

        Request request =
                new Request.Builder()
                        .url(url)
                        .post(okhttp3.internal.Util.EMPTY_REQUEST)
                        .header(HEADER_COLLECTION_NAME, collectionName)
                        .build();

        return newCall(request, new TypeReference<>() {});
    }

    @Override
    public Bin<String> updateCollection(
            @NonNull String collectionId, @NonNull String collectionName) {
        String url = String.format("%s/c/%s/meta/name", baseUrl, collectionId);

        Request request =
                new Request.Builder()
                        .url(url)
                        .put(okhttp3.internal.Util.EMPTY_REQUEST)
                        .header(HEADER_COLLECTION_NAME, collectionName)
                        .build();

        return newCall(request, new TypeReference<>() {});
    }

    private <T> T newCall(Request request, TypeReference<T> ref) {
        try (okhttp3.Response response = client.newCall(request).execute()) {

            if (response.body() == null) {
                throw new JsonBinException("Response body is null");
            }

            String json = response.body().string();

            if (!response.isSuccessful()) {
                Error error = JsonUtil.toValue(json, new TypeReference<>() {});
                throw new JsonBinException(error.getMessage());
            }

            return JsonUtil.toValue(json, ref);

        } catch (IOException exception) {
            throw new JsonBinException(exception.getMessage(), exception);
        }
    }

    private <T> TypeReference<Bin<T>> getTypeRef(Class<T> cls) {
        return new TypeReference<>() {
            @Override
            public java.lang.reflect.Type getType() {
                return TypeFactory.defaultInstance()
                        .constructParametricType(Bin.class, cls);
            }
        };
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> getClass(T object) {
        return (Class<T>) object.getClass();
    }
}
