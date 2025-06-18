package com.serpstat.core;

import io.modelcontextprotocol.server.McpServerFeatures;
import java.util.List;

/**
 * Base interface for all tool providers
 */
public interface ToolProvider {
    // maybe can use a different name, a little confusing when we register tools, we have collisions of meanings
    // domain as a website and domain as a meaning, information in a certain area, maybe it just me
    /*
        providers.add(new DomainTools(apiClient));
        providers.add(new CompetitorsTools(apiClient));
        providers.add(new BacklinksTools(apiClient));
        providers.add(new CreditsTools(apiClient));
        providers.add(new ProjectsTools(apiClient));
        providers.add(new KeywordTools(apiClient));
     */
    String getDomainName();
    List<McpServerFeatures.SyncToolSpecification> getTools();
}