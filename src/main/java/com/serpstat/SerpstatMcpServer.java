// ==================== MAIN SERVER CLASS ====================
package com.serpstat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serpstat.domains.utils.VersionUtils;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;

import com.serpstat.core.ToolRegistry;
import com.serpstat.core.SerpstatApiClient;

import java.util.Arrays;

/**
 * Main class for the MCP server for Serpstat API.
 * Architecture: modular with automatic tool registration.
 */
public class SerpstatMcpServer {

    private final String apiToken;
    private McpSyncServer mcpServer;

    public SerpstatMcpServer(String apiToken) {
        this.apiToken = apiToken;
    }

    public static void main(String[] args) {

        final String envName = "SERPSTAT_API_TOKEN";
        String apiToken = null;

        try {
            apiToken = System.getenv(envName);
            if (apiToken == null || apiToken.isEmpty()) {
                System.err.printf("Error: Environment variable %s is not set or empty. Check README.md for instructions.%n", envName);
                System.exit(1);
            }
        } catch (SecurityException e) {
            System.err.printf("Error: Unable to access environment variable %s due to security restrictions.%n", envName);
            System.exit(1);
        }


        SerpstatMcpServer server = new SerpstatMcpServer(apiToken);

        try {
            server.start();
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            System.err.println(Arrays.toString(e.getStackTrace()));
            System.exit(1);
        }
    }

    public void start() throws Exception {
            // Create API client
            SerpstatApiClient apiClient = new SerpstatApiClient(apiToken);

            // Create a tool registry and automatically register all tools
            ToolRegistry toolRegistry = new ToolRegistry(apiClient);

            // Create STDIO transport
            var transportProvider = new StdioServerTransportProvider(new ObjectMapper());

            // Create and configure an MCP server
            this.mcpServer = McpServer.sync(transportProvider)
                    .serverInfo("serpstat-mcp-server", VersionUtils.getVersion())
                    .capabilities(ServerCapabilities.builder()
                            .tools(true)
                            .logging()
                            .build())
                    .build();

            // Automatically register all tools
            toolRegistry.registerAllTools(mcpServer);

            System.err.println("🚀 Serpstat MCP Server started successfully!");
            System.err.printf("📊 Registered %d tools across %d domains%n",
                    toolRegistry.getToolCount(), toolRegistry.getDomainCount());
            System.err.println("⏳ Waiting for MCP client connections...");

            // Graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

            // Block the main thread
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
