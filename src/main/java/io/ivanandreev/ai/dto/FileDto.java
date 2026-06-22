package io.ivanandreev.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * A single entry returned by the Bitbucket {@code browse} endpoint.
 *
 * <p>The browse endpoint is not modelled in the OpenAPI spec (its 200 response
 * has an empty schema), so the client deserializes it into this hand-written DTO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FileDto(
        Path path,
        String type,
        Long size
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Path(String name, List<String> components) {
    }
}
