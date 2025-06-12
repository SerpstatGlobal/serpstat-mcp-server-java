package com.serpstat.core;

import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;


/**
 * Serpstat API response wrapper
 */
@Getter
public class SerpstatApiResponse {

    private final JsonNode result;
    private final String method;
    private final Map<String, Object> requestParams;
    private final long timestamp;

    public SerpstatApiResponse(JsonNode result, String method, Map<String, Object> requestParams) {
        this.result = result;
        this.method = method;
        this.requestParams = requestParams;
        this.timestamp = System.currentTimeMillis();
    }

}
