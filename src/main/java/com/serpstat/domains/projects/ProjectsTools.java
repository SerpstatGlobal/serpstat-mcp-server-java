package com.serpstat.domains.projects;

import com.serpstat.core.*;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.*;

import java.util.List;
import java.util.Map;

/**
 * Tools for projects management
 */
// ==================== USAGE EXAMPLES ====================
/*
Usage examples in Claude:

1. List all projects:
"Show my Serpstat projects"

2. Get a specific page:
"Show my projects page 2 with 50 results per page"

3. Project management:
"List my projects and show which ones I own"

4. Quick audit preparation:
"Get my project IDs and domains for analysis"

Automatic tool calls:
{
  "tool": "projects_list",
  "arguments": {
    "page": 1,
    "size": 100
  }
}
*/

public class ProjectsTools extends BaseToolHandler implements ToolProvider {

    public ProjectsTools(SerpstatApiClient apiClient) {
        super(apiClient);
    }

    @Override
    public String getDomainName() {
        return "Projects Management";
    }

    @Override
    public List<McpServerFeatures.SyncToolSpecification> getTools() {
        return List.of(
                createProjectsListTool()
        );
    }

    /**
     * Create projects list tool specification
     */
    private McpServerFeatures.SyncToolSpecification createProjectsListTool() {
        return new McpServerFeatures.SyncToolSpecification(
                new Tool(
                        "projects_list",
                        "Get list of user projects with details like project ID, name, domain, creation date and user role. Useful for project management and getting project IDs for other API calls. Does not consume API credits.",
                        ProjectsSchemas.PROJECTS_LIST_SCHEMA
                ),
                this::handleProjectsList
        );
    }

    /**
     * Handle projects list request
     */
    private CallToolResult handleProjectsList(McpSyncServerExchange exchange, Map<String, Object> arguments) {
        return handleToolCall(exchange, arguments, "getProjects", (args) -> {
            // Validation
            ProjectsValidator.validateProjectsListRequest(args);

            // Log request details
            Integer page = (Integer) args.getOrDefault("page", 1);
            Integer size = (Integer) args.getOrDefault("size", 20);

            exchange.loggingNotification(
                    LoggingMessageNotification.builder()
                            .level(LoggingLevel.DEBUG)
                            .logger("ProjectsTools")
                            .data(String.format("Retrieving projects list (page %d, size %d)", page, size))
                            .build()
            );

            // Call Serpstat API - ensure we have an empty params object if no args
            Map<String, Object> params = args.isEmpty() ? Map.of() : args;
            return apiClient.callMethod("ProjectProcedure.getProjects", params);
        });
    }

    @Override
    protected String formatResponse(SerpstatApiResponse response, Map<String, Object> arguments) throws Exception {
        return ProjectsResponseFormatter.format(response, arguments, objectMapper);
    }
}
