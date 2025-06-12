package com.serpstat.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import java.util.concurrent.TimeUnit;

/**
 * SerpstatApiClient provides a convenient interface for interacting with the Serpstat API v4.
 * It supports making generic API method calls, handles authentication, request rate limiting,
 * response caching, and error handling. The client uses Java HttpClient for HTTP requests and
 * Jackson for JSON serialization/deserialization.
 * Default: 60 min cache, 1000 entries, 10 req/sec rate limit, 30 sec timeout.
 */
public class SerpstatApiClient {

    private static final String SERPSTAT_API_URL = "https://api.serpstat.com/v4";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiToken;
    private final Cache<String, Object> cache;
    private final RateLimiter rateLimiter;



    public SerpstatApiClient(String apiToken) {
        this.apiToken = apiToken;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .build();
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
        this.objectMapper = new ObjectMapper();

        this.rateLimiter = new RateLimiter(10, Duration.ofSeconds(1));
    }

    /**
     * Универсальный метод для вызова любого Serpstat API метода
     */
    public SerpstatApiResponse callMethod(String method, Map<String, Object> params)
            throws SerpstatApiException {

        // Проверяем кэш
        final String cacheKey = method + ":" + params.toString();
        if (cache.getIfPresent(cacheKey) != null) {
            return new SerpstatApiResponse((JsonNode) cache.getIfPresent(cacheKey), method, params);
        }
        try {
            // Rate limiting
            rateLimiter.waitIfNeeded();

            // Создаем запрос
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("id", 1);
            requestBody.put("method", method);
            requestBody.set("params", objectMapper.valueToTree(params));

            // HTTP запрос
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SERPSTAT_API_URL+"/?token="+apiToken))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiToken)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                    .timeout(REQUEST_TIMEOUT)
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new SerpstatApiException("HTTP Error: " + response.statusCode() + " - " + response.body());
            }

            // Парсим ответ
            JsonNode responseJson = objectMapper.readTree(response.body());

            if (responseJson.has("error")) {
                JsonNode error = responseJson.get("error");
                throw new SerpstatApiException("Serpstat API Error: " + error.get("message").asText());
            }

            final SerpstatApiResponse apiResponse = new SerpstatApiResponse(responseJson.get("result"), method, params);

            // Сохраняем результат в кеш
            cache.put(cacheKey, apiResponse);

            return apiResponse;


        } catch (IOException | InterruptedException e) {
            throw new SerpstatApiException("Request failed: " + e.getMessage(), e);
        }
    }
}
