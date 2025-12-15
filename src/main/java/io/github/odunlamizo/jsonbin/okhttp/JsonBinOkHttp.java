package io.github.odunlamizo.jsonbin.okhttp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.github.odunlamizo.jsonbin.JsonBin;
import io.github.odunlamizo.jsonbin.JsonBinException;
import io.github.odunlamizo.jsonbin.model.Bin;
import io.github.odunlamizo.jsonbin.model.Error;
import io.github.odunlamizo.jsonbin.util.JsonUtil;
import java.io.IOException;
import lombok.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/** JSONBIN.io Java SDK implementation powered by OkHttp */
public class JsonBinOkHttp implements JsonBin {

    private final OkHttpClient client;
    private final String baseUrl;

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
    public <T> Bin<T> readBin(@NonNull String binId, Class<T> recordClass) throws JsonBinException {
        final String URL = String.format("%s/b/%s", baseUrl, binId);
        Request request = new Request.Builder().url(URL).build();

        return newCall(request, recordClass);
    }

    private <T> Bin<T> newCall(Request request, Class<T> recordClass) {
        try (okhttp3.Response response = client.newCall(request).execute()) {

            if (response.body() == null) {
                throw new JsonBinException("Response body is null");
            }

            String json = response.body().string();

            if (!response.isSuccessful()) {
                Error error = JsonUtil.toValue(json, new TypeReference<Error>() {});
                throw new JsonBinException(error.getMessage());
            }

            TypeReference<Bin<T>> ref =
                    new TypeReference<>() {
                        @Override
                        public java.lang.reflect.Type getType() {
                            return TypeFactory.defaultInstance()
                                    .constructParametricType(Bin.class, recordClass);
                        }
                    };

            return JsonUtil.toValue(json, ref);

        } catch (IOException e) {
            throw new JsonBinException(e.getMessage(), e);
        }
    }
}
