package io.ivanandreev.ai.config;

import io.ivanandreev.ai.tools.BranchTools;
import io.ivanandreev.ai.tools.CommitTools;
import io.ivanandreev.ai.tools.FileTools;
import io.ivanandreev.ai.tools.HelloTools;
import io.ivanandreev.ai.tools.ProjectToolsService;
import io.ivanandreev.ai.tools.PullRequestTools;
import com.example.bitbucketmcp.gen.ApiClient;
import com.example.bitbucketmcp.gen.api.ProjectApi;
import com.example.bitbucketmcp.gen.api.PullRequestsApi;
import com.example.bitbucketmcp.gen.api.RepositoryApi;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BitbucketProperties.class)
public class BitbucketClientConfig {

    @Bean
    public ApiClient bitbucketApi(BitbucketProperties props) {
        // Generated operation paths already include "/api/latest", and the spec's
        // server template is "http://{baseurl}/rest", so the base path stops at "/rest".
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(props.url() + "/rest");
        apiClient.addDefaultHeader("Authorization", "Bearer " + props.token());
        apiClient.addDefaultHeader("Accept", "application/json");
        return apiClient;
    }

    @Bean public ProjectApi projectApi(ApiClient c)             { return new ProjectApi(c); }
    @Bean public RepositoryApi repositoryApi(ApiClient c)       { return new RepositoryApi(c); }
    @Bean public PullRequestsApi pullRequestsApi(ApiClient c)   { return new PullRequestsApi(c); }

    @Bean
    public ToolCallbackProvider bitbucketTools(
            ProjectToolsService projectTools,
            FileTools fileTools,
            BranchTools branchTools,
            PullRequestTools pullRequestTools,
            CommitTools commitTools,
            HelloTools helloTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(projectTools, fileTools, branchTools,
                        pullRequestTools, commitTools, helloTools)
                .build();
    }
}
