package io.github.odunlamizo.jsonbin;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class JsonBinException extends RuntimeException {

    private static final long serialVersionUID = 9002135571226598881L;

    public JsonBinException(String message) {
        super(message);
    }

    public JsonBinException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
