package io.github.odunlamizo.jsonbin;

import io.github.odunlamizo.jsonbin.model.Bin;
import io.github.odunlamizo.jsonbin.model.BinHandle;
import java.util.List;
import lombok.NonNull;

/** JSONBin.io Java SDK */
public interface JsonBin {

    /** HTTP header used to set a human‑readable name for a created bin. */
    String HEADER_BIN_NAME = "X-Bin-Name";

    /** HTTP header used to control the privacy of a bin (true = private, false = public). */
    String HEADER_BIN_PRIVATE = "X-Bin-Private";

    /** HTTP header specifying the identifier of an existing collection to attach the bin to. */
    String HEADER_COLLECTION_ID = "X-Collection-Id";

    /** HTTP header used when creating a collection to provide its display name. */
    String HEADER_COLLECTION_NAME = "X-Collection-Name";

    /**
     * Reads the contents of a bin from JSONBin.io using its unique identifier.
     *
     * <p>This method fetches the bin data from the remote JSONBin.io API and deserializes it into a
     * strongly-typed {@link Bin} object containing the expected data type {@code T}.
     *
     * @param binId the unique identifier of the bin to retrieve; must not be {@code null}
     * @param recordClass the class of the expected record type {@code T} used for deserialization;
     *     must not be {@code null}
     * @return a {@link Bin} object containing the deserialized data
     */
    <T> Bin<T> readBin(@NonNull String binId, @NonNull Class<T> recordClass);

    /**
     * Creates a new bin with the provided record.
     *
     * <p>Optional headers can be supplied to set a human-readable name, privacy setting, and/or the
     * collection to which the bin should belong.
     *
     * @param record the payload to store in the new bin; must be serializable to JSON
     * @param binName optional display name for the bin; ignored if {@code null} or blank
     * @param isPrivate optional privacy flag; when {@code true} the bin is private, when {@code
     *     false} public; when {@code null} the server default is used
     * @param collectionId optional collection identifier to attach the bin to; ignored if {@code
     *     null} or blank
     * @param <T> the type of the record stored in the bin
     * @return the created {@link Bin} including server-assigned metadata and the stored record
     */
    <T> Bin<T> createBin(T record, String binName, Boolean isPrivate, String collectionId);

    /**
     * Updates an existing bin with a new record value.
     *
     * <p>The entire record is replaced with the provided value.
     *
     * @param record the new payload that will replace the existing bin contents; must not be {@code
     *     null}
     * @param binId the identifier of the bin to update; must not be {@code null}
     * @param <T> the type of the record stored in the bin
     * @return the updated {@link Bin} as returned by the server
     */
    <T> Bin<T> updateBin(@NonNull T record, @NonNull String binId);

    /**
     * Retrieves the list of bin handles contained in a collection.
     *
     * <p>The returned list provides lightweight metadata (handles) for bins in the specified
     * collection. Use {@link #readBin(String, Class)} to fetch a bin's full contents.
     *
     * @param collectionId the identifier of the collection whose bins should be listed; must not be
     *     {@code null}
     * @return a list of {@link BinHandle} entries in the collection
     */
    List<BinHandle> readCollection(@NonNull String collectionId);

    /**
     * Creates a new collection with the specified name.
     *
     * @param collectionName the name for the collection; must not be {@code null}
     * @return a {@link Bin} containing the server response; the record type is a {@link String}
     *     representing the collection identifier
     */
    Bin<String> createCollection(@NonNull String collectionName);

    /**
     * Updates the display name of an existing collection.
     *
     * <p>This operation targets the collection's metadata and changes only its human-readable name.
     * It does not modify the bins contained within the collection.
     *
     * @param collectionId the unique identifier of the collection to update; must not be {@code
     *     null}
     * @param collectionName the new display name to assign to the collection; must not be {@code
     *     null}
     * @return a {@link Bin} wrapping the server response; the record type is a {@link String}
     *     representing the (unchanged) collection identifier
     */
    Bin<String> updateCollection(@NonNull String collectionId, @NonNull String collectionName);
}
