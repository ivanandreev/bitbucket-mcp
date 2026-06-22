package io.ivanandreev.ai.model;

import java.util.List;

public record PullRequestDetail(
        long id,
        String title,
        String state,
        String description,
        String fromBranch,
        String toBranch,
        String author,
        List<String> reviewers
) {
}
