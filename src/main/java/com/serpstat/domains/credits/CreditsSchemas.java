package com.serpstat.domains.credits;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * JSON schemas for limits tools
 */
public class CreditsSchemas {

    /**
     * JSON schema for getStats method
     */
    public static final String API_STATS_SCHEMA = loadSchema("api_stats.json");

    private static String loadSchema(String filename) {
        try (InputStream is = CreditsSchemas.class.getResourceAsStream("/schemas/credits/" + filename)) {
            if (is == null) {
                throw new RuntimeException("Schema file not found: " + filename);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load schema: " + filename, e);
        }
    }
}