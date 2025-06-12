package com.serpstat.domains.domain;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * JSON schemas for domain tools
 */
public class DomainSchemas {

    /**
     * JSON schema for getDomainsInfo method
     */
    public static final String DOMAINS_INFO_SCHEMA = loadSchema("domains_info.json");
    public static final String REGIONS_COUNT_SCHEMA = loadSchema("regions_count.json");
    public static final String DOMAIN_KEYWORDS_SCHEMA = loadSchema("domain_keywords.json");

    private static String loadSchema(String filename) {
        try (InputStream is = DomainSchemas.class.getResourceAsStream("/schemas/domain/" + filename)) {
            if (is == null) {
                throw new RuntimeException("Schema file not found: " + filename);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load schema: " + filename, e);
        }
    }
}

