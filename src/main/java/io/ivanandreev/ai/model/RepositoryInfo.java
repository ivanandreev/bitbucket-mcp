package io.ivanandreev.ai.model;

public record RepositoryInfo(
        String slug,
        String name,
        String projectKey,
        String description
) {
}
