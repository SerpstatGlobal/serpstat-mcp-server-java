// ==================== MAIN SERVER CLASS ====================
package com.serpstat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;

import com.serpstat.core.ToolRegistry;
import com.serpstat.core.SerpstatApiClient;

/**
 * Главный класс MCP сервера для Serpstat API
 * Архитектура: модульная с автоматической регистрацией инструментов
 */
public class SerpstatMcpServer {

    private final String apiToken;
    private McpSyncServer mcpServer;

    public SerpstatMcpServer(String apiToken) {
        this.apiToken = apiToken;
    }

    public static void main(String[] args) {

        String apiToken = System.getenv("SERPSTAT_API_TOKEN");
        if (apiToken == null || apiToken.isEmpty()) {
            System.err.println("Error: Environment variable SERPSTAT_API_TOKEN is not set. Check README.md for instructions.");
            System.exit(1);
        }


        SerpstatMcpServer server = new SerpstatMcpServer(apiToken);

        try {
            server.start();
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            System.err.println(e.getStackTrace());
            System.exit(1);
        }
    }

    public void start() throws Exception {
        // Создаем API клиент
        SerpstatApiClient apiClient = new SerpstatApiClient(apiToken);

        // Создаем реестр инструментов и автоматически регистрируем все
        ToolRegistry toolRegistry = new ToolRegistry(apiClient);

        // Создаем STDIO транспорт
        var transportProvider =new StdioServerTransportProvider(new ObjectMapper());

        // Создаем и конфигурируем MCP сервер
        this.mcpServer = McpServer.sync(transportProvider)
                .serverInfo("serpstat-mcp-server", "0.0.1")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        //.resources(false,false)
                        //.prompts(false)
                        .logging()
                        .build())
                .build();

        // Автоматическая регистрация всех инструментов
        toolRegistry.registerAllTools(mcpServer);

        System.err.println("🚀 Serpstat MCP Server started successfully!");
        System.err.printf("📊 Registered %d tools across %d domains%n",
                toolRegistry.getToolCount(), toolRegistry.getDomainCount());
        System.err.println("⏳ Waiting for MCP client connections...");

        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        // Блокируем основной поток
        Thread.currentThread().join();
    }

    private void shutdown() {
        System.err.println("🛑 Shutting down Serpstat MCP Server...");
        try {
            if (mcpServer != null) {
                mcpServer.closeGracefully();
            }
        } catch (Exception e) {
            System.err.println("❌ Error during shutdown: " + e.getMessage());
        }
    }
}
