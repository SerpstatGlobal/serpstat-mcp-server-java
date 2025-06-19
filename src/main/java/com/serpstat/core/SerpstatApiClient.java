package com.serpstat.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.serpstat.domains.utils.VersionUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * SerpstatApiClient provides a convenient interface for interacting with the Serpstat API v4.
 * It supports making generic API method calls, handles authentication, request rate limiting,
 * response caching, and error handling. The client uses Java HttpClient for HTTP requests and
 * Jackson for JSON serialization/deserialization.
 * Default: 60 min cache, 1000 entries, 10 req/sec rate limit, 30 sec timeout.
 * Fixed UTF-8 encoding for Cyrillic characters support.
 */
public class SerpstatApiClient {

    // good candidates for some property file or just a class with global constants for ease of dev, test
    // we already have some util to work with config (version I believe)
    private static final String SERPSTAT_API_URL = "https://api.serpstat.com/v4";
    private static final Duration DEFAULT_REQUEST_TIMEOUT = Duration.ofSeconds(30);

    private final String apiToken;
    private String apiUrl;
    private final HttpClient httpClient;
    private final Cache<String, SerpstatApiResponse> cache;
    private final String version;
    private final ObjectMapper objectMapper;
    private final RateLimiter rateLimiter;
    private final Duration requestTimeout;

    /**
     * Default constructor uses production Serpstat API URL
     */
    public SerpstatApiClient(String apiToken) {
        this(apiToken, SERPSTAT_API_URL, DEFAULT_REQUEST_TIMEOUT);
    }

    /**
     * Constructor for custom API URL (for testing/mocking)
     */
    public SerpstatApiClient(String apiToken, String apiUrl) {
        this(apiToken, apiUrl, DEFAULT_REQUEST_TIMEOUT);
    }

    public SerpstatApiClient(String apiToken, String apiUrl, Duration requestTimeout) {
        this.apiToken = apiToken;
        this.apiUrl = apiUrl;
        this.requestTimeout = requestTimeout;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(requestTimeout)
                .build();
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
        this.version = VersionUtils.getVersion();
        this.objectMapper = new ObjectMapper();
        // already discussed on call, there are plenty of main plans that have 1 rps, maybe can be moved to some config too
        // can cause problems so will need some adjustments
        this.rateLimiter = new RateLimiter(10, Duration.ofSeconds(1));
    }

    /**
     * Protected method to get API URL - allows overriding in tests
     */
    protected String getApiUrl() {
        return apiUrl;
    }

    /**
     * Protected method to get request timeout - allows overriding in tests
     */
    protected Duration getRequestTimeout() {
        return requestTimeout;
    }

    /**
     * Universal method for calling any Serpstat API method with proper UTF-8 encoding
     */
    public SerpstatApiResponse callMethod(String method, Map<String, Object> params)
            throws SerpstatApiException {

        // Handle null parameters - prevents NullPointerException in cache key generation
        if (params == null) {
            params = Map.of(); // Use empty map instead of null
        }

        // Check cache
        final String cacheKey = method + ":" + params.toString();
        SerpstatApiResponse cachedResponse = cache.getIfPresent(cacheKey);
        if (cachedResponse != null) {
            return cachedResponse;
        }

        try {
            // Rate limiting
            rateLimiter.waitIfNeeded();

            // Create a request body
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("id", 1);
            requestBody.put("method", method);
            requestBody.set("params", objectMapper.valueToTree(params));

            // Convert to JSON string with proper UTF-8 encoding
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // Create an HTTP request with explicit UTF-8 charset
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/?token=" + apiToken))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .header("Accept", "application/json; charset=UTF-8")
                    .header("Accept-Charset", "UTF-8")
                    .header("User-Agent", "Serpstat MCP Server Java/" + this.version)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .timeout(getRequestTimeout())
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() != 200) {
                throw new SerpstatApiException("HTTP Error: " + response.statusCode() + " - " + response.body());
            }

            // Parse response with UTF-8
            JsonNode responseJson = objectMapper.readTree(response.body());

            if (responseJson.has("error")) {
                JsonNode error = responseJson.get("error");
                throw new SerpstatApiException("Serpstat API Error: " + error.get("message").asText());
            }

            final SerpstatApiResponse apiResponse = new SerpstatApiResponse(responseJson.get("result"), method, params);

            // Save result to cache
            cache.put(cacheKey, apiResponse);

            return apiResponse;

        } catch (IOException | InterruptedException e) {
            throw new SerpstatApiException("Request failed: " + e.getMessage(), e);
        }
    }
}