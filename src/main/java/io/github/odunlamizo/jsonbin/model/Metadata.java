package io.github.odunlamizo.jsonbin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents metadata information returned from a JSONBin.io response.
 *
 * <p>This includes details such as the bin's unique identifier, visibility, creation timestamp, and
 * assigned name.
 */
@Getter
@Setter
@ToString
public class Metadata {

    /** The unique identifier of the bin. */
    private String id;

    /**
     * Indicates whether the bin is private.
     *
     * <p>Uses @JsonProperty to map from the JSON key {@code "private"} since it is a reserved
     * keyword in Java.
     */
    @JsonProperty("private")
    private boolean _private;

    /**
     * The timestamp when the bin was created.
     *
     * <p>Expected in ISO 8601 format with timezone information.
     */
    private ZonedDateTime createdAt;

    /** The human-readable name assigned to the bin. */
    private String name;
}
