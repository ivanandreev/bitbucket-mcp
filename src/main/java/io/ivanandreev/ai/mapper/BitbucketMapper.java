package io.ivanandreev.ai.mapper;

import io.ivanandreev.ai.bitbucketmcp.gen.model.RestBranch;
import io.ivanandreev.ai.bitbucketmcp.gen.model.RestChangesetToCommitAuthor;
import io.ivanandreev.ai.bitbucketmcp.gen.model.RestCommentAnchorPullRequestAuthor;
import io.ivanandreev.ai.bitbucketmcp.gen.model.RestCommentAnchorPullRequestAuthorUser;
import io.ivanandreev.ai.bitbucketmcp.gen.model.RestCommit;
import io.ivanandreev.ai.bitbucketmcp.gen.model.RestProject;
import io.ivanandreev.ai.bitbucketmcp.gen.model.RestPullRequest;
import io.ivanandreev.ai.bitbucketmcp.gen.model.RestPullRequestParticipant;
import io.ivanandreev.ai.bitbucketmcp.gen.model.RestRepository;
import io.ivanandreev.ai.dto.FileDto;
import io.ivanandreev.ai.model.BranchInfo;
import io.ivanandreev.ai.model.CommitInfo;
import io.ivanandreev.ai.model.FileEntry;
import io.ivanandreev.ai.model.ProjectInfo;
import io.ivanandreev.ai.model.PullRequestDetail;
import io.ivanandreev.ai.model.PullRequestSummary;
import io.ivanandreev.ai.model.RepositoryInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Maps the POJOs generated from the Bitbucket OpenAPI spec
 * ({@code com.example.bitbucketmcp.gen.model.Rest*}) into the flat model
 * records the tools format. MapStruct generates the implementation at compile
 * time; componentModel=spring makes it an injectable bean.
 */
@Mapper(componentModel = "spring")
public interface BitbucketMapper {

    // --- Projects ---
    ProjectInfo toProjectInfo(RestProject dto);

    List<ProjectInfo> toProjectInfos(List<RestProject> dtos);

    // --- Repositories ---
    @Mapping(target = "projectKey", source = "project.key")
    RepositoryInfo toRepositoryInfo(RestRepository dto);

    List<RepositoryInfo> toRepositoryInfos(List<RestRepository> dtos);

    // --- Branches (JSON "default" -> isDefault) ---
    @Mapping(target = "isDefault", source = "default")
    BranchInfo toBranchInfo(RestBranch dto);

    List<BranchInfo> toBranchInfos(List<RestBranch> dtos);

    // --- Commits (author RestChangesetToCommitAuthor -> String via commitAuthorName) ---
    CommitInfo toCommitInfo(RestCommit dto);

    List<CommitInfo> toCommitInfos(List<RestCommit> dtos);

    // --- Pull requests ---
    @Mapping(target = "fromBranch", source = "fromRef.displayId")
    @Mapping(target = "toBranch", source = "toRef.displayId")
    PullRequestSummary toPullRequestSummary(RestPullRequest dto);

    List<PullRequestSummary> toPullRequestSummaries(List<RestPullRequest> dtos);

    @Mapping(target = "fromBranch", source = "fromRef.displayId")
    @Mapping(target = "toBranch", source = "toRef.displayId")
    PullRequestDetail toPullRequestDetail(RestPullRequest dto);

    // --- Files (browse endpoint is not modelled in the OpenAPI spec) ---
    @Mapping(target = "name", source = "path.name")
    FileEntry toFileEntry(FileDto dto);

    List<FileEntry> toFileEntries(List<FileDto> dtos);

    // --- shared value mappings ---

    /** Commit author -> display string. */
    default String commitAuthorName(RestChangesetToCommitAuthor author) {
        if (author == null) {
            return "unknown";
        }
        return author.getName();
    }

    /** PR author (a participant wrapper) -> display string; drives the {@code author} field. */
    default String pullRequestAuthorName(RestCommentAnchorPullRequestAuthor author) {
        return author == null ? "unknown" : userName(author.getUser());
    }

    /** PR reviewer -> display string; drives List&lt;RestPullRequestParticipant&gt; -> List&lt;String&gt;. */
    default String reviewerName(RestPullRequestParticipant participant) {
        return participant == null ? "unknown" : userName(participant.getUser());
    }

    /** ApplicationUser -> display string, preferring displayName over name. */
    default String userName(RestCommentAnchorPullRequestAuthorUser user) {
        if (user == null) {
            return "unknown";
        }
        if (!user.getDisplayName().isBlank()) {
            return user.getDisplayName();
        }
        return user.getName();
    }
}
