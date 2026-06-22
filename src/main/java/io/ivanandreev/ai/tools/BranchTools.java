package io.ivanandreev.ai.tools;

import java.math.BigDecimal;

import io.ivanandreev.ai.client.BitbucketClient;
import io.ivanandreev.ai.mapper.BitbucketMapper;
import io.ivanandreev.ai.model.BranchInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BranchTools {

    private static final BigDecimal BIG_DECIMAL_25 = BigDecimal.valueOf(25);
    private final BitbucketClient client;
    private final BitbucketMapper mapper;


    @Tool(description = "List branches in a Bitbucket repository")
    public String listBranches(
            @ToolParam(description = "Project key (e.g. MYPROJ)") String projectKey,
            @ToolParam(description = "Repository slug (e.g. my-repo)") String repoSlug,
            @ToolParam(description = "Max results to return (default 25)", required = false) BigDecimal limit,
            @ToolParam(description = "Pagination start index (default 0)", required = false) BigDecimal start
    ) {
        var page = client.getBranches(
                projectKey, repoSlug,
                limit != null ? limit : BIG_DECIMAL_25,
                start != null ? start : BigDecimal.ZERO
        );
        var branches = mapper.toBranchInfos(page.values());

        var sb = new StringBuilder();
        sb.append("Branches in %s/%s (isLastPage=%b):\n\n".formatted(projectKey, repoSlug, page.isLastPage()));

        for (BranchInfo b : branches) {
            String latest = b.latestCommit() != null ? b.latestCommit() : "";
            sb.append("- %s%s | latest: %s\n".formatted(
                    b.displayId(),
                    b.isDefault() ? " [DEFAULT]" : "",
                    latest.length() > 8 ? latest.substring(0, 8) : latest));
        }
        return sb.toString();
    }
}
