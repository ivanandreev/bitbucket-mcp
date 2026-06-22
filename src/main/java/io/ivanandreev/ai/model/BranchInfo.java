package io.ivanandreev.ai.model;

public record BranchInfo(
        String displayId,
        String id,
        boolean isDefault,
        String latestCommit
) {
}
