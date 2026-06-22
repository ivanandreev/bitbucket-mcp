package io.ivanandreev.ai.client;

import io.ivanandreev.ai.dto.BitbucketPage;
import io.ivanandreev.ai.dto.BrowseDto;
import com.example.bitbucketmcp.gen.api.ProjectApi;
import com.example.bitbucketmcp.gen.api.PullRequestsApi;
import com.example.bitbucketmcp.gen.api.RepositoryApi;
import com.example.bitbucketmcp.gen.model.RestBranch;
import com.example.bitbucketmcp.gen.model.RestCommit;
import com.example.bitbucketmcp.gen.model.RestProject;
import com.example.bitbucketmcp.gen.model.RestPullRequest;
import com.example.bitbucketmcp.gen.model.RestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Low-level Bitbucket REST client.
 *
 * <p>Delegates to the API services generated from the Bitbucket OpenAPI spec
 * ({@code com.example.bitbucketmcp.gen.api}) and adapts their responses into the
 * flat {@link BitbucketPage} envelope the tools consume. The {@code raw} and
 * {@code browse} endpoints are not modelled in the spec, so they are read off
 * the generated request's {@code ResponseSpec} directly.
 */
@Component
@RequiredArgsConstructor
public class BitbucketClient {

    private final ProjectApi projectApi;
    private final RepositoryApi repositoryApi;
    private final PullRequestsApi pullRequestsApi;

    public BitbucketPage<RestProject> getProjects(BigDecimal limit, BigDecimal start) {
        var resp = projectApi.getProjects(null, null, start, limit);
        return BitbucketPage.of(resp.getValues(), resp.getSize(), resp.getLimit(),
                resp.getIsLastPage(), resp.getNextPageStart());
    }

    public BitbucketPage<RestRepository> getRepositories(String projectKey, BigDecimal limit, BigDecimal start) {
        var resp = projectApi.getRepositories(projectKey, start, limit);
        return BitbucketPage.of(resp.getValues(), resp.getSize(), resp.getLimit(),
                resp.getIsLastPage(), resp.getNextPageStart());
    }

    public BitbucketPage<RestBranch> getBranches(String projectKey, String repoSlug, BigDecimal limit, BigDecimal start) {
        var resp = repositoryApi.getBranches(projectKey, repoSlug,
                null, null, null, null, null, null, start, limit);
        return BitbucketPage.of(resp.getValues(), resp.getSize(), resp.getLimit(),
                resp.getIsLastPage(), resp.getNextPageStart());
    }

    public BitbucketPage<RestPullRequest> getPullRequests(String projectKey, String repoSlug,
                                                          String state, BigDecimal limit, BigDecimal start) {
        var resp = pullRequestsApi.getPage(projectKey, repoSlug,
                null, null, null, null, null, state, null, null, start, limit);
        return BitbucketPage.of(resp.getValues(), resp.getSize(), resp.getLimit(),
                resp.getIsLastPage(), resp.getNextPageStart());
    }

    public RestPullRequest getPullRequest(String projectKey, String repoSlug, long prId) {
        return pullRequestsApi.get3(projectKey, String.valueOf(prId), repoSlug, null);
    }

    public BitbucketPage<RestCommit> getCommits(String projectKey, String repoSlug,
                                                String branch, BigDecimal limit, BigDecimal start) {
        var resp = repositoryApi.getCommits(projectKey, repoSlug,
                null, null, null, null, branch, null, null, null, null, start, limit);
        return BitbucketPage.of(resp.getValues(), resp.getSize(), resp.getLimit(),
                resp.getIsLastPage(), resp.getNextPageStart());
    }

    public String getFileContent(String projectKey, String repoSlug, String filePath, String branch) {
        return repositoryApi
                .streamRawWithResponseSpec(filePath, projectKey, repoSlug, branch, null, null, null, null)
                .body(String.class);
    }

    public BrowseDto getFileMetadata(String projectKey, String repoSlug, String filePath, String branch) {
        return repositoryApi
                .getContent1WithResponseSpec(filePath, projectKey, repoSlug, null, branch, null, null, null)
                .body(BrowseDto.class);
    }
}
