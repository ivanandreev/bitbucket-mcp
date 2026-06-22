package io.ivanandreev.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Response of the Bitbucket {@code browse} endpoint for a directory path.
 *
 * <p>Not modelled in the OpenAPI spec, so it is deserialized into this
 * hand-written DTO. For a file (rather than a directory) {@code children} is
 * absent/{@code null}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BrowseDto(Children children) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Children(
            List<FileDto> values,
            int size,
            int limit,
            boolean isLastPage,
            Integer nextPageStart
    ) {
        public Children {
            values = values != null ? values : List.of();
        }
    }
}
