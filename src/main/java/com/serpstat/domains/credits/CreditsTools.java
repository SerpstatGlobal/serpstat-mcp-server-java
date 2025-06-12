package com.serpstat.domains.credits;

import com.serpstat.core.*;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Tools for API limits and usage monitoring
 */
public class CreditsTools extends BaseToolHandler implements ToolProvider {

    public CreditsTools(SerpstatApiClient apiClient) {
        super(apiClient);
    }

    @Override
    public String getDomainName() {
        return "API Limits & Usage";
    }

    @Override
    public List<McpServerFeatures.SyncToolSpecification> getTools() {
        return List.of(
                createApiStatsTool()
        );
    }

    /**
     * Create API stats tool specification
     */
    private McpServerFeatures.SyncToolSpecification createApiStatsTool() {
        return new McpServerFeatures.SyncToolSpecification(
                new Tool(
                        "api_stats",
                        "Get current API usage statistics and credit limits. Shows remaining credits, usage percentage, and provides recommendations for optimal usage.",
                        CreditsSchemas.API_STATS_SCHEMA
                ),
                this::handleApiStats
        );
    }

    /**
     * Handle API stats request
     */
    private CallToolResult handleApiStats(McpSyncServerExchange exchange, Map<String, Object> arguments) {
        return handleToolCall(exchange, arguments, "getStats", (args) -> {
            // Validation (minimal for this method)
            CreditsValidator.validateApiStatsRequest(args);

            // Log request
            exchange.loggingNotification(
                    LoggingMessageNotification.builder()
                            .level(LoggingLevel.DEBUG)
                            .logger("CreditsTools")
                            .data("Retrieving API usage statistics")
                            .build()
            );

            // Call Serpstat API - getStats doesn't need parameters
            return apiClient.callMethod("SerpstatLimitsProcedure.getStats", Map.of());
        });
    }

    @Override
    protected String formatResponse(SerpstatApiResponse response, Map<String, Object> arguments) throws Exception {
        return CreditsResponseFormatter.format(response, arguments, objectMapper);
    }
}