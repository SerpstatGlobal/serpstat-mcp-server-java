package com.serpstat.core;

import io.modelcontextprotocol.server.McpServerFeatures;
import java.util.List;

/**
 * Base interface for all tool providers
 */
public interface ToolProvider {
    String getDomainName();
    List<McpServerFeatures.SyncToolSpecification> getTools();
}