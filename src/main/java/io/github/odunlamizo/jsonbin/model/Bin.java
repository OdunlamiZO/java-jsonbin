package io.github.odunlamizo.jsonbin.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a response wrapper from JSONBIN.io containing both the data record and its metadata.
 *
 * <p>This generic class is used to hold the actual data stored in the bin (as {@code record}) and
 * additional information about the bin such as creation time, visibility, and name (as {@link
 * Metadata}).
 *
 * @param <T> the type of the data record stored in the bin
 */
@Getter
@Setter
@ToString
public class Bin<T> {

    /** The actual content or data retrieved from the bin. */
    private T record;

    /** Metadata information about the bin, such as its ID, name, visibility, and timestamps. */
    private Metadata metadata;
}
