package io.ivanandreev.ai.tools;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;

import io.ivanandreev.ai.client.BitbucketClient;
import io.ivanandreev.ai.dto.BitbucketPage;
import io.ivanandreev.ai.mapper.BitbucketMapper;
import io.ivanandreev.ai.model.ProjectInfo;
import io.ivanandreev.ai.model.RepositoryInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectToolsService {

    private static final BigDecimal BIG_DECIMAL_25 = BigDecimal.valueOf(25);
    private final BitbucketClient client;
    private final BitbucketMapper mapper;

    @Tool(description = "List all Bitbucket projects the token has access to")
    public String listProjects(
            @ToolParam(description = "Max results to return (default 25, max 100)", required = false) BigDecimal limit,
            @ToolParam(description = "Pagination start index (default 0)", required = false) BigDecimal start
    ) {
        var page = client.getProjects(
                limit != null ? limit : BIG_DECIMAL_25,
                start != null ? start : ZERO
        );
        var projects = mapper.toProjectInfos(page.values());

        var sb = new StringBuilder(header("projects", projects.size(), page));
        for (ProjectInfo p : projects) {
            sb.append("- %s | %s".formatted(p.key(), p.name()));
            if (p.description() != null && !p.description().isBlank()) {
                sb.append(" | ").append(p.description());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Tool(description = "List repositories in a Bitbucket project")
    public String listRepositories(
            @ToolParam(description = "Project key (e.g. MYPROJ)") String projectKey,
            @ToolParam(description = "Max results to return (default 25)", required = false) BigDecimal limit,
            @ToolParam(description = "Pagination start index (default 0)", required = false) BigDecimal start
    ) {
        var page = client.getRepositories(
                projectKey,
                limit != null ? limit : BIG_DECIMAL_25,
                start != null ? start : ZERO
        );
        var repositories = mapper.toRepositoryInfos(page.values());

        var sb = new StringBuilder(header("repositories", repositories.size(), page));
        for (RepositoryInfo r : repositories) {
            sb.append("- %s | %s".formatted(r.slug(), r.name()));
            if (r.description() != null && !r.description().isBlank()) {
                sb.append(" | ").append(r.description());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String header(String type, int count, BitbucketPage<?> page) {
        return "Found %d %s (isLastPage=%b%s):\n\n".formatted(
                count, type, page.isLastPage(),
                page.isLastPage() ? "" : ", nextStart=" + page.nextStart());
    }
}
