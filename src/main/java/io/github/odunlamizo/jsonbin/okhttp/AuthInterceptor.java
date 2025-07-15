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

        Request.Builder requestBuilder = original.newBuilder();
        if (accessKey != null && !accessKey.isEmpty()) {
            requestBuilder.header("x-access-key", accessKey);
        } else {
            requestBuilder.header("x-master-key", masterKey);
        }

        return chain.proceed(requestBuilder.build());
    }
}
