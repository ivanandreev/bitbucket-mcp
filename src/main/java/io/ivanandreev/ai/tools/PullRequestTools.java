package io.ivanandreev.ai.tools;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;

import io.ivanandreev.ai.client.BitbucketClient;
import io.ivanandreev.ai.mapper.BitbucketMapper;
import io.ivanandreev.ai.model.PullRequestDetail;
import io.ivanandreev.ai.model.PullRequestSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PullRequestTools {

    private static final BigDecimal BIG_DECIMAL_25 = BigDecimal.valueOf(25);
    private final BitbucketClient client;
    private final BitbucketMapper mapper;

    @Tool(description = """
            List pull requests in a Bitbucket repository.
            State can be OPEN, MERGED, DECLINED, or ALL (default OPEN).
            """)
    public String listPullRequests(
            @ToolParam(description = "Project key") String projectKey,
            @ToolParam(description = "Repository slug") String repoSlug,
            @ToolParam(description = "PR state: OPEN | MERGED | DECLINED | ALL (default OPEN)", required = false) String state,
            @ToolParam(description = "Max results (default 25)", required = false) BigDecimal limit,
            @ToolParam(description = "Pagination start index (default 0)", required = false) BigDecimal start
    ) {
        var page = client.getPullRequests(
                projectKey, repoSlug,
                state != null ? state.toUpperCase() : "OPEN",
                limit != null ? limit : BIG_DECIMAL_25,
                start != null ? start : ZERO
        );
        var prs = mapper.toPullRequestSummaries(page.values());

        var sb = new StringBuilder();
        sb.append("Pull requests in %s/%s (isLastPage=%b):\n\n".formatted(projectKey, repoSlug, page.isLastPage()));

        for (PullRequestSummary pr : prs) {
            sb.append("PR #%d [%s] %s\n  %s → %s | by %s\n\n"
                    .formatted(pr.id(), pr.state(), pr.title(),
                            pr.fromBranch(), pr.toBranch(), pr.author()));
        }
        return sb.toString();
    }

    @Tool(description = "Get full details of a specific pull request including description and reviewers")
    public String getPullRequest(
            @ToolParam(description = "Project key") String projectKey,
            @ToolParam(description = "Repository slug") String repoSlug,
            @ToolParam(description = "Pull request ID") long pullRequestId
    ) {
        PullRequestDetail pr = mapper.toPullRequestDetail(client.getPullRequest(projectKey, repoSlug, pullRequestId));

        return """
                PR #%d [%s]: %s
                Author:    %s
                Branch:    %s → %s
                Reviewers: %s

                Description:
                %s
                """.formatted(pr.id(), pr.state(), pr.title(), pr.author(),
                pr.fromBranch(), pr.toBranch(),
                pr.reviewers().isEmpty() ? "none" : String.join(", ", pr.reviewers()),
                pr.description() == null || pr.description().isBlank() ? "(none)" : pr.description());
    }
}
