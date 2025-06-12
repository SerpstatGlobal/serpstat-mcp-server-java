package com.serpstat.domains.backlinks;


import com.serpstat.core.BaseToolHandler;
import com.serpstat.core.SerpstatApiClient;
import com.serpstat.core.SerpstatApiResponse;
import com.serpstat.core.ToolProvider;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;
import java.util.Map;

/**
 * The BacklinksTools class provides tools for analyzing backlinks summary using the Serpstat API.
 * It extends the BaseToolHandler and implements the ToolProvider interface to define and handle specific tools.
 */
public class BacklinksTools extends BaseToolHandler implements ToolProvider {

    /**
     * Constructs a BacklinksTools instance with the specified Serpstat API client.
     *
     * @param apiClient the Serpstat API client used for API calls
     */
    public BacklinksTools(SerpstatApiClient apiClient) {
        super(apiClient);
    }

    /**
     * Returns the domain name of the tool.
     *
     * @return the domain name as a string
     */
    @Override
    public String getDomainName() {
        return "Backlinks Summary Analysis";
    }

    /**
     * Provides a list of tools available in this class.
     * @return a list of SyncToolSpecification objects
     */
    @Override
    public List<McpServerFeatures.SyncToolSpecification> getTools() {
        return List.of(
                createGetBacklinksSummaryTool()
        );
    }

    /**
     * Creates the specification for the "get_backlinks_summary" tool.
     * @return a SyncToolSpecification object defining the tool
     */
    private McpServerFeatures.SyncToolSpecification createGetBacklinksSummaryTool() {
        return new McpServerFeatures.SyncToolSpecification(
                new McpSchema.Tool(
                        "get_backlinks_summary",
                        "Get comprehensive backlinks summary using Serpstat API. Returns referring domains, backlinks count, link types, quality metrics and recent changes for domain or subdomain.",
                        BacklinksSchemas.BACKLINKS_SUMMARY_SCHEMA
                ),
                this::handleBacklinksSummary
        );
    }

    /**
     * Handles the "get_backlinks_summary" tool request.
     *
     * @param exchange  the server exchange object for communication
     * @param arguments the arguments provided for the tool call
     * @return the result of the tool call
     */
    private McpSchema.CallToolResult handleBacklinksSummary(McpSyncServerExchange exchange, Map<String, Object> arguments) {
        return handleToolCall(exchange, arguments, "getSummaryV2", (args) -> {
            // Validate the request arguments
            BacklinksSummaryValidator.validateBacklinksSummaryRequest(args);

            // Log request details
            String query = (String) args.get("query");
            String searchType = (String) args.getOrDefault("searchType", "domain");

            exchange.loggingNotification(
                    McpSchema.LoggingMessageNotification.builder()
                            .level(McpSchema.LoggingLevel.DEBUG)
                            .logger("BacklinksTools")
                            .data(String.format("Analyzing backlinks profile for %s: %s", searchType, query))
                            .build()
            );

            // Call the Serpstat API method
            return apiClient.callMethod("SerpstatBacklinksProcedure.getSummaryV2", args);
        });
    }

    /**
     * Formats the response from the Serpstat API.
     *
     * @param response  the API response object
     * @param arguments the arguments provided for the tool call
     * @return the formatted response as a string
     * @throws Exception if an error occurs during formatting
     */
    @Override
    protected String formatResponse(SerpstatApiResponse response, Map<String, Object> arguments) throws Exception {
        return BacklinksSummaryResponseFormatter.format(response, arguments, objectMapper);
    }
}