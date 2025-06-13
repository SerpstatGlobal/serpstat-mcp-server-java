package com.serpstat.core;

import com.serpstat.domains.backlinks.BacklinksTools;
import com.serpstat.domains.credits.CreditsTools;
import com.serpstat.domains.projects.ProjectsTools;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.McpServerFeatures;

import com.serpstat.domains.domain.DomainTools;
import com.serpstat.domains.competitors.CompetitorsTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry of all MCP tools with automatic registration
 */
public class ToolRegistry {

    private final SerpstatApiClient apiClient;
    private final List<ToolProvider> providers;

    public ToolRegistry(SerpstatApiClient apiClient) {
        this.apiClient = apiClient;
        this.providers = initializeProviders();
    }

    /**
     * Initialization of all tool providers
     */
    private List<ToolProvider> initializeProviders() {
        List<ToolProvider> providers = new ArrayList<>();

        // Регистрируем провайдеры по доменам
        providers.add(new DomainTools(apiClient));
        providers.add(new CompetitorsTools(apiClient));
        providers.add(new BacklinksTools(apiClient));
        providers.add(new CreditsTools(apiClient));
        providers.add(new ProjectsTools(apiClient));
        // providers.add(new KeywordTools(apiClient));
        // providers.add(new CompetitorsTools(apiClient));
        // TODO: добавить остальные домены

        return providers;
    }

    /**
     * Automatic registration of all tools in the MCP server
     */
    public void registerAllTools(McpSyncServer mcpServer) {
        for (ToolProvider provider : providers) {
            List<McpServerFeatures.SyncToolSpecification> tools = provider.getTools();
            for (McpServerFeatures.SyncToolSpecification tool : tools) {
                mcpServer.addTool(tool);
                System.err.printf("🔧 Registered tool: %s (%s)%n",
                        tool.tool().name(), provider.getDomainName());
            }
        }
    }

    public int getToolCount() {
        return providers.stream()
                .mapToInt(p -> p.getTools().size())
                .sum();
    }

    public int getDomainCount() {
        return providers.size();
    }
}