package com.serpstat.domains.keywords;


import com.serpstat.core.*;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Инструменты для работы с ключевыми словами
 */
public class KeywordTools extends BaseToolHandler implements ToolProvider {

    public KeywordTools(SerpstatApiClient apiClient) {
        super(apiClient);
    }

    @Override
    public String getDomainName() {
        return "Keyword Research";
    }

    @Override
    public List<McpServerFeatures.SyncToolSpecification> getTools() {
        return Arrays.asList(
                createKeywordResearchTool(),
                createKeywordDifficultyTool()
        );
    }

    private McpServerFeatures.SyncToolSpecification createKeywordResearchTool() {
        return new McpServerFeatures.SyncToolSpecification(
                new Tool(
                        "keyword_research",
                        "Research keywords and get search volume data",
                        KeywordSchemas.KEYWORD_RESEARCH_SCHEMA
                ),
                this::handleKeywordResearch
        );
    }

    private McpServerFeatures.SyncToolSpecification createKeywordDifficultyTool() {
        return new McpServerFeatures.SyncToolSpecification(
                new Tool(
                        "keyword_difficulty",
                        "Get keyword difficulty and competition analysis",
                        KeywordSchemas.KEYWORD_DIFFICULTY_SCHEMA
                ),
                this::handleKeywordDifficulty
        );
    }

    private CallToolResult handleKeywordResearch(McpSyncServerExchange exchange, Map<String, Object> arguments) {
        return handleToolCall(exchange, arguments, "keywordResearch", (args) -> {
            KeywordValidator.validateKeywordResearchRequest(args);
            return apiClient.callMethod("SerpstatKeywordProcedure.getKeywordInfo", args);
        });
    }

    private CallToolResult handleKeywordDifficulty(McpSyncServerExchange exchange, Map<String, Object> arguments) {
        return handleToolCall(exchange, arguments, "keywordDifficulty", (args) -> {
            KeywordValidator.validateKeywordDifficultyRequest(args);
            return apiClient.callMethod("SerpstatKeywordProcedure.getKeywordDifficulty", args);
        });
    }

    @Override
    protected String formatResponse(SerpstatApiResponse response, Map<String, Object> arguments) throws Exception {
        return KeywordResponseFormatter.format(response, arguments, objectMapper);
    }
}
