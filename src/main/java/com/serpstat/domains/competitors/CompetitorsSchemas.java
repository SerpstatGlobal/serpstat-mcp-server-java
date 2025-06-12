package com.serpstat.domains.competitors;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * JSON schemas for domain tools
 */
public class CompetitorsSchemas {

    /**
     * JSON schema for DomainSchemas method
     */

    public static final String COMPETITORS_SCHEMA = loadSchema("get_competitors.json");


    private static String loadSchema(String filename) {
        try (InputStream is = CompetitorsSchemas.class.getResourceAsStream("/schemas/competitors/" + filename)) {
            if (is == null) {
                throw new RuntimeException("Schema file not found: " + filename);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load schema: " + filename, e);
        }
    }
}

