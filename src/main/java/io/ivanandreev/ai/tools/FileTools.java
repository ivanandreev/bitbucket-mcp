package io.ivanandreev.ai.tools;

import io.ivanandreev.ai.client.BitbucketClient;
import io.ivanandreev.ai.dto.BrowseDto;
import io.ivanandreev.ai.mapper.BitbucketMapper;
import io.ivanandreev.ai.model.FileEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileTools {

    private static final int MAX_FILE_CHARS = 32_000;

    private final BitbucketClient client;
    private final BitbucketMapper mapper;

    @Tool(description = """
            Get the raw content of a file from a Bitbucket repository.
            Returns up to 32 000 characters. For large files use line-range parameters.
            """)
    public String getFileContent(
            @ToolParam(description = "Project key") String projectKey,
            @ToolParam(description = "Repository slug") String repoSlug,
            @ToolParam(description = "File path relative to repo root, e.g. src/main/App.java") String filePath,
            @ToolParam(description = "Branch or commit ref (default: main)", required = false) String branch
    ) {
        String content = client.getFileContent(
                projectKey, repoSlug, filePath,
                branch != null ? branch : "main"
        );

        if (content == null) return "File not found or empty.";

        if (content.length() > MAX_FILE_CHARS) {
            return content.substring(0, MAX_FILE_CHARS)
                   + "\n\n[TRUNCATED — file exceeds %d chars]".formatted(MAX_FILE_CHARS);
        }
        return content;
    }

    @Tool(description = "Browse files and directories in a Bitbucket repository path")
    public String browseDirectory(
            @ToolParam(description = "Project key") String projectKey,
            @ToolParam(description = "Repository slug") String repoSlug,
            @ToolParam(description = "Directory path (use empty string or / for root)", required = false) String path,
            @ToolParam(description = "Branch or commit ref (default: main)", required = false) String branch
    ) {
        String safePath = (path == null || path.isBlank() || path.equals("/")) ? "" : path;
        BrowseDto result = client.getFileMetadata(
                projectKey, repoSlug, safePath,
                branch != null ? branch : "main"
        );

        List<FileEntry> entries = result == null || result.children() == null
                ? List.of()
                : mapper.toFileEntries(result.children().values());

        var sb = new StringBuilder();
        String displayPath = safePath.isBlank() ? "/" : safePath;
        sb.append("Contents of %s/%s:%s\n\n".formatted(projectKey, repoSlug, displayPath));

        for (FileEntry entry : entries) {
            sb.append("%s %s\n".formatted(entry.isDirectory() ? "📁" : "📄", entry.name()));
        }
        return sb.toString();
    }
}
