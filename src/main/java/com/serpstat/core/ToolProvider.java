package com.serpstat.core;

import io.modelcontextprotocol.server.McpServerFeatures;
import java.util.List;

/**
 * Базовый интерфейс для всех провайдеров инструментов
 */
public interface ToolProvider {
    String getDomainName();
    List<McpServerFeatures.SyncToolSpecification> getTools();
}