package com.serpstat.domains.competitors;

import com.serpstat.core.*;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import com.serpstat.domains.competitors.models.CompetitorsResponseFormatter;

import java.util.List;
import java.util.Map;

/**
 * Tools for competitor analysis.
 */
public class CompetitorsTools extends BaseToolHandler implements ToolProvider {

    public CompetitorsTools(SerpstatApiClient apiClient) {
        super(apiClient);
    }

    @Override
    public String getDomainName() {
        return "Competitor Analysis";
    }

    @Override
    public List<McpServerFeatures.SyncToolSpecification> getTools() {
        return List.of(
                createGetCompetitorsTool()
        );
    }

    @Override
    protected String formatResponse(SerpstatApiResponse response, Map<String, Object> arguments) throws Exception {
        return CompetitorsResponseFormatter.format(response, arguments, objectMapper);
    }

    private McpServerFeatures.SyncToolSpecification createGetCompetitorsTool() {
        return new McpServerFeatures.SyncToolSpecification(
                new McpSchema.Tool(
                        "get_domain_competitors",
                        "Get top 20 domain competitors from search results with SEO metrics, traffic, visibility, and relevance score.",
                        CompetitorsSchemas.COMPETITORS_SCHEMA
                ),
                this::handleGetDomainCompetitors
        );
    }

    private McpSchema.CallToolResult handleGetDomainCompetitors(McpSyncServerExchange exchange, Map<String, Object> arguments) {
        return handleToolCall(exchange, arguments, "getCompetitors", (args) -> {
            // Validation
            CompetitorsValidator.validateDomainCompetitorsRequest(args);
            // Log request details
            @SuppressWarnings("unchecked")
            String domain = (String) args.get("domain");
            String searchEngine = (String) args.getOrDefault("se", "g_us");

            exchange.loggingNotification(
                    McpSchema.LoggingMessageNotification.builder()
                            .level(McpSchema.LoggingLevel.DEBUG)
                            .logger("DomainTools")
                            .data(String.format("Processing competitors for %s domain for %s database",
                                    domain, searchEngine))
                            .build()
            );

            // Call Serpstat API
            return apiClient.callMethod("SerpstatDomainProcedure.getCompetitors", args);
        });
    }
}
