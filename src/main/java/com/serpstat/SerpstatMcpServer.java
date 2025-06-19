// ==================== MAIN SERVER CLASS ====================
package com.serpstat;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    // personally I would like to locate main in other class, so I would have clear entrypoint and logic for env variable and
    // server start with possible failure handling in other place, separating it from SerpstatMcpServer, it would be
    // responsible only for start and shutdown
    public static void main(String[] args) {
        // this is an unsave operation, getenv could fail, may try catch it to be safe, could introduce var for env name
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
                    .serverInfo("serpstat-mcp-server", "0.0.1") // version is hardcoded, but I believe we have it in config?
                    .capabilities(ServerCapabilities.builder()
                            .tools(true)
                            // can it be deleted if we don't plan to use it yet
                            //.resources(false,false)
                            //.prompts(false)
                            .logging()
                            .build())
                    .build();

            // Automatically register all tools
            toolRegistry.registerAllTools(mcpServer);

            System.err.println("🚀 Serpstat MCP Server started successfully!");
            System.err.printf("📊 Registered %d tools across %d domains%n",
                    toolRegistry.getToolCount(), toolRegistry.getDomainCount());
            System.err.println("⏳ Waiting for MCP client connections...");

            // below is basically a panic hook and an event loop to keep the app alive? just curious, interesting
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
