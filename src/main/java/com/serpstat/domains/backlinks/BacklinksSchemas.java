package com.serpstat.domains.backlinks;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * JSON schemas for domain tools
 */
public class BacklinksSchemas {

    /**
     * JSON schema for DomainSchemas method
     */

    public static final String BACKLINKS_SUMMARY_SCHEMA = loadSchema("backlinks_summary.json");

    private static String loadSchema(String filename) {
        try (InputStream is = BacklinksSchemas.class.getResourceAsStream("/schemas/backlinks/" + filename)) {
            if (is == null) {
                throw new RuntimeException("Schema file not found: " + filename);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load schema: " + filename, e);
        }
    }
}

