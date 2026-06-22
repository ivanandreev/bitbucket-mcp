package io.ivanandreev.ai.model;

public record PullRequestSummary(
        long id,
        String title,
        String state,
        String fromBranch,
        String toBranch,
        String author
) {
}
