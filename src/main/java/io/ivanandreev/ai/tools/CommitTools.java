package io.ivanandreev.ai.tools;

import java.math.BigDecimal;

import io.ivanandreev.ai.client.BitbucketClient;
import io.ivanandreev.ai.mapper.BitbucketMapper;
import io.ivanandreev.ai.model.CommitInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommitTools {

    private final BitbucketClient client;
    private final BitbucketMapper mapper;
    private static final BigDecimal BIG_DECIMAL_20 = BigDecimal.valueOf(20);


    @Tool(description = "List recent commits in a repository branch")
    public String listCommits(
            @ToolParam(description = "Project key") String projectKey,
            @ToolParam(description = "Repository slug") String repoSlug,
            @ToolParam(description = "Branch name (default: main)", required = false) String branch,
            @ToolParam(description = "Max results (default 20)", required = false) BigDecimal limit,
            @ToolParam(description = "Pagination start index (default 0)", required = false) BigDecimal start
    ) {
        String ref = branch != null ? branch : "main";
        var page = client.getCommits(
                projectKey, repoSlug, ref,
                limit != null ? limit : BIG_DECIMAL_20,
                start != null ? start : BigDecimal.ZERO
        );
        var commits = mapper.toCommitInfos(page.values());

        var sb = new StringBuilder();
        sb.append("Commits in %s/%s@%s (isLastPage=%b):\n\n".formatted(
                projectKey, repoSlug, ref, page.isLastPage()));

        for (CommitInfo c : commits) {
            String id = c.id() != null ? c.id() : "";
            String shortId = id.length() > 8 ? id.substring(0, 8) : id;
            String message = c.message() != null ? c.message() : "";
            String firstLine = message.lines().findFirst().orElse("").trim();
            sb.append("%s | %s | %s\n".formatted(shortId, c.author(), firstLine));
        }
        return sb.toString();
    }
}
