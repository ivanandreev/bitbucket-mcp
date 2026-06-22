# Bitbucket MCP Server

A Model Context Protocol (MCP) server for Bitbucket, built with **Java 25 + Spring Boot 4**.

## Tools exposed

| Tool | Description |
|---|---|
| `listProjects` | List all accessible Bitbucket projects |
| `listRepositories` | List repos in a project |
| `listBranches` | List branches in a repo |
| `listPullRequests` | List PRs (OPEN / MERGED / DECLINED / ALL) |
| `getPullRequest` | Full detail of a single PR |
| `listCommits` | Recent commits on a branch |
| `getFileContent` | Raw file content (up to 32k chars) |
| `browseDirectory` | List files/folders at a path |

## Setup

### 1. Credentials

Provide the Bitbucket URL and token either as environment variables:

```bash
export BITBUCKET_URL=https://bitbucket.example.com
export BITBUCKET_TOKEN=your-personal-access-token
```

…or in a `.env` file in the project root (loaded automatically via
`spring.config.import`):

```dotenv
BITBUCKET_URL=https://bitbucket.example.com
BITBUCKET_TOKEN=your-personal-access-token
```

> Add `.env` to `.gitignore` before committing so the token isn't checked in.

### Get a token
Bitbucket → Profile → Manage account → Personal access tokens → Create token  
Permissions: Projects (Read), Repositories (Read), Pull Requests (Read)

### 2. Build

```bash
mvn clean package -DskipTests
```

### 3. Run

The server supports two MCP transports:

| Mode | When to use | How the client connects |
|---|---|---|
| **SSE / HTTP** (default) | Server runs as a long-lived process; one server can serve remote/multiple clients | Client points at the `/sse` URL |
| **STDIO** | Client launches the server locally on demand | Client spawns the jar; JSON-RPC over stdin/stdout |

#### SSE / HTTP mode (default)

```bash
java -jar target/bitbucket-mcp-1.0.0.jar
# Listening on http://localhost:8080, MCP endpoint at /sse
```

#### STDIO mode

Activate the `stdio` profile — it disables the web server and silences the
banner/console logging so stdout carries only the protocol:

```bash
SPRING_PROFILES_ACTIVE=stdio java -jar target/bitbucket-mcp-1.0.0.jar
```

## Connect an MCP client

The config file location depends on the client (e.g. Claude Desktop:
`~/Library/Application Support/Claude/claude_desktop_config.json` on macOS).

### Option A — SSE transport (connect to an already-running server)

Start the server in SSE mode (above), then point the client at its `/sse` URL:

```json
{
  "mcpServers": {
    "bitbucket": {
      "url": "http://localhost:8080/sse"
    }
  }
}
```

### Option B — STDIO transport (client spawns the process)

The client launches the jar itself with the `stdio` profile active and the
credentials passed in `env`:

```json
{
  "mcpServers": {
    "bitbucket": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/bitbucket-mcp-1.0.0.jar"
      ],
      "env": {
        "SPRING_PROFILES_ACTIVE": "stdio",
        "BITBUCKET_URL": "https://bitbucket.example.com",
        "BITBUCKET_TOKEN": "your-token-here"
      }
    }
  }
}
```

## Project structure

```
src/main/java/com/example/bitbucketmcp/
├── BitbucketMcpApplication.java
├── client/
│   └── BitbucketClient.java        # Wraps the generated API services
├── config/
│   ├── BitbucketProperties.java    # Env/.env-based config record
│   └── BitbucketClientConfig.java  # ApiClient + API beans + tool registration
├── dto/
│   ├── BitbucketPage.java          # Flat view of the paged-response envelope
│   ├── BrowseDto.java              # browse endpoint (not in the OpenAPI spec)
│   └── FileDto.java
├── mapper/
│   └── BitbucketMapper.java        # MapStruct: generated POJOs -> flat models
├── model/                          # Flat records returned to the tools
└── tools/
    ├── ProjectToolsService.java    # listProjects, listRepositories
    ├── BranchTools.java            # listBranches
    ├── PullRequestTools.java       # listPullRequests, getPullRequest
    ├── CommitTools.java            # listCommits
    ├── FileTools.java              # getFileContent, browseDirectory
    └── HelloTools.java             # helloWorld (sanity check)
```

> POJOs and API services are generated from `src/main/resources/bitbucket-openapi.yaml`
> at build time (openapi-generator) into `com.example.bitbucketmcp.gen.*`.
