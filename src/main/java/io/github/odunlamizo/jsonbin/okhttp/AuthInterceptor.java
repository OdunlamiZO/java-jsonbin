package io.github.odunlamizo.jsonbin.okhttp;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class AuthInterceptor implements Interceptor {

    private final String masterKey;

    private final String accessKey;

    public AuthInterceptor(String masterKey, String accessKey) {
        this.masterKey = masterKey;
        this.accessKey = accessKey;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        boolean hasAccessKey = accessKey != null && !accessKey.isBlank();

        Request requestWithAuth =
                original.newBuilder()
                        .header(
                                hasAccessKey ? "x-access-key" : "x-master-key",
                                hasAccessKey ? accessKey : masterKey)
                        .build();

        return chain.proceed(requestWithAuth);
    }
}
