package com.serpstat.core;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;



public abstract class BaseToolHandler {

    protected final SerpstatApiClient apiClient;
    protected final ObjectMapper objectMapper;

    protected BaseToolHandler(SerpstatApiClient apiClient) {
        this.apiClient = apiClient;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Handles the execution of a tool call, processes the response, and logs the results.
     *
     * @param exchange   The `McpSyncServerExchange` instance used for logging notifications.
     * @param arguments  A map of arguments required for the tool execution.
     * @param methodName The name of the method being executed, used for logging purposes.
     * @param executor   A functional interface that defines the execution logic for the tool.
     * @return A `CallToolResult` containing the processed result or an error message, along with a flag
     *         indicating whether the execution encountered an error.
     *
     * <p>This method performs the following steps:
     * <ul>
     *   <li>Logs the start of the tool call execution.</li>
     *   <li>Executes the tool using the provided `ToolExecutor`.</li>
     *   <li>Formats the response using the `formatResponse` method.</li>
     *   <li>Logs the successful processing of the tool call.</li>
     *   <li>Handles and logs any exceptions that occur during execution, including validation errors,
     *       API errors, and unexpected errors.</li>
     * </ul>
     *
     * <p>Exceptions are caught and logged, and appropriate error messages are returned in the result:
     * <ul>
     *      <li>ValidationException If validation of the arguments fails;</li>
     *      <li>SerpstatApiException If the Serpstat API call fails;</li>
     *      <li>Exception For any other unexpected errors.</li>
     *  </ul>
     */

    protected CallToolResult handleToolCall(McpSyncServerExchange exchange,
                                            Map<String, Object> arguments,
                                            String methodName,
                                            ToolExecutor executor) {
        try {

            logInfo(exchange, "Starting " + methodName + " request");

            SerpstatApiResponse response = executor.execute(arguments);

            String result = formatResponse(response, arguments);

            logInfo(exchange, "Successfully processed " + methodName + " request");

            return new CallToolResult(List.of(new TextContent(result)), false);

        } catch (ValidationException e) {
            logError(exchange, "Validation error: " + e.getMessage());
            return new CallToolResult(List.of(new TextContent("Validation error: " + e.getMessage())), true);

        } catch (SerpstatApiException e) {
            logError(exchange, "Serpstat API error: " + e.getMessage());
            return new CallToolResult(List.of(new TextContent("API error: " + e.getMessage())), true);

        } catch (Exception e) {
            logError(exchange, "Unexpected error: " + e.getMessage());
            return new CallToolResult(List.of(new TextContent("Unexpected error: " + e.getMessage())), true);
        }
    }

    protected void logInfo(McpSyncServerExchange exchange, String message) {
        exchange.loggingNotification(
                LoggingMessageNotification.builder()
                        .level(LoggingLevel.INFO)
                        .logger(getClass().getSimpleName())
                        .data(message)
                        .build()
        );
    }

    protected void logError(McpSyncServerExchange exchange, String message) {
        exchange.loggingNotification(
                LoggingMessageNotification.builder()
                        .level(LoggingLevel.ERROR)
                        .logger(getClass().getSimpleName())
                        .data(message)
                        .build()
        );
    }

    protected abstract String formatResponse(SerpstatApiResponse response, Map<String, Object> arguments)
            throws Exception;

    @FunctionalInterface
    protected interface ToolExecutor {
        SerpstatApiResponse execute(Map<String, Object> arguments) throws Exception;
    }
}