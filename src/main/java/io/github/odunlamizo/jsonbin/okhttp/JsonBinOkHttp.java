package io.github.odunlamizo.jsonbin.okhttp;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.odunlamizo.jsonbin.JsonBin;
import io.github.odunlamizo.jsonbin.JsonBinException;
import io.github.odunlamizo.jsonbin.model.Bin;
import io.github.odunlamizo.jsonbin.model.Error;
import io.github.odunlamizo.jsonbin.util.JsonUtil;
import java.io.IOException;
import lombok.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/** JSONBin.io Java SDK implementation powered by OkHttp */
public class JsonBinOkHttp<T> implements JsonBin<T> {

    private final OkHttpClient client;

    private final String baseUrl;

    private final TypeReference<Bin<T>> typeReference;

    public JsonBinOkHttp(@NonNull String masterKey) {
        this(masterKey, "https://api.jsonbin.io/v3");
    }

    public JsonBinOkHttp(@NonNull String masterKey, @NonNull String baseUrl) {
        this.baseUrl = baseUrl;
        this.typeReference = new TypeReference<Bin<T>>() {};
        this.client =
                new OkHttpClient.Builder()
                        .addInterceptor(new AuthInterceptor(masterKey, null))
                        .build();
    }

    @Override
    public Bin<T> readBin(@NonNull String binId) throws JsonBinException {
        final String URL = String.format("%s/b/%s", baseUrl, binId);
        Request request = new Request.Builder().url(URL).build();

        return newCall(request);
    }

    private Bin<T> newCall(Request request) {
        try (okhttp3.Response response = client.newCall(request).execute()) {

            String json = response.body().string();

            if (!response.isSuccessful()) {
                Error errorResponse = JsonUtil.toValue(json, new TypeReference<Error>() {});
                throw new JsonBinException(errorResponse.getMessage());
            }

            return JsonUtil.toValue(json, typeReference);
        } catch (IOException exception) {
            throw new JsonBinException(exception.getMessage(), exception.getCause());
        }
    }
}
