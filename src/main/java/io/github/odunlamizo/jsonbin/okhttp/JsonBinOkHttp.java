package io.github.odunlamizo.jsonbin.okhttp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.odunlamizo.jsonbin.JsonBin;
import io.github.odunlamizo.jsonbin.JsonBinException;
import io.github.odunlamizo.jsonbin.model.Bin;
import io.github.odunlamizo.jsonbin.model.Error;
import io.github.odunlamizo.jsonbin.util.JsonUtil;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;
import lombok.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/** JSONBin.io Java SDK implementation powered by OkHttp */
public class JsonBinOkHttp<T> implements JsonBin<T> {

    private final OkHttpClient client;
    private final String baseUrl;
    private final Function<String, Bin<T>> deserializer;

    private JsonBinOkHttp(
            String masterKey,
            String accessKey,
            String baseUrl,
            Function<String, Bin<T>> deserializer) {
        this.baseUrl = baseUrl;
        this.deserializer = deserializer;
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

        public <T> JsonBinOkHttp<T> build(Class<T> recordClass) {
            if ((masterKey == null || masterKey.isBlank())
                    && (accessKey == null || accessKey.isBlank())) {
                throw new IllegalArgumentException(
                        "Either masterKey or accessKey must be provided.");
            }

            return new JsonBinOkHttp<>(
                    masterKey,
                    accessKey,
                    baseUrl,
                    json -> {
                        TypeReference<Bin<T>> ref =
                                new TypeReference<>() {
                                    @Override
                                    public java.lang.reflect.Type getType() {
                                        return TypeFactory.defaultInstance()
                                                .constructParametricType(Bin.class, recordClass);
                                    }
                                };
                        try {
                            return JsonUtil.toValue(json, ref);
                        } catch (JsonProcessingException exception) {
                            throw new JsonBinException(
                                    exception.getMessage(), exception.getCause());
                        }
                    });
        }
    }

    @Override
    public Bin<T> readBin(@NonNull String binId) throws JsonBinException {
        final String URL = String.format("%s/b/%s", baseUrl, binId);
        Request request = new Request.Builder().url(URL).build();

        return newCall(request);
    }

    private Bin<T> newCall(Request request) {
        try (okhttp3.Response response = client.newCall(request).execute()) {

            if (Objects.isNull(response.body())) {
                throw new JsonBinException("Response body is null");
            }
            String json = response.body().string();

            if (!response.isSuccessful()) {
                Error errorResponse = JsonUtil.toValue(json, new TypeReference<>() {});
                throw new JsonBinException(errorResponse.getMessage());
            }

            return deserializer.apply(json);
        } catch (IOException exception) {
            throw new JsonBinException(exception.getMessage(), exception.getCause());
        }
    }
}
