package io.github.odunlamizo.jsonbin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BinHandle {

    @JsonProperty("record")
    private String id;

    @JsonProperty("private")
    private boolean _private;

    private SnippetMeta snippetMeta;

    private String createdAt;
}
