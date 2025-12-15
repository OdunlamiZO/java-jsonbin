package io.github.odunlamizo.jsonbin;

import io.github.odunlamizo.jsonbin.model.Bin;
import lombok.NonNull;

/** JSONBin.io Java SDK */
public interface JsonBin {

    /**
     * Reads the contents of a bin from JSONBin.io using its unique identifier.
     *
     * <p>This method fetches the bin data from the remote JSONBin.io API and deserializes it into a
     * strongly-typed {@link Bin} object containing the expected data type {@code T}.
     *
     * @param binId the unique identifier of the bin to retrieve; must not be {@code null}
     * @param recordClass the class of the expected record type {@code T} used for deserialization; must not be {@code null}
     * @return a {@link Bin} object containing the deserialized data
     * @throws JsonBinException if the request fails, the bin is not found, or deserialization fails
     */
    <T> Bin<T> readBin(@NonNull String binId, Class<T> recordClass) throws JsonBinException;
}
