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

    /*
    as I understand we are operating with the assumption that we would always provide this file, it should be fine,
    even so in theory it can fail to initialize and throw an exception, in practice I assume that it is fine and we
    should see the error in the dev process

    I just don't understand why we need to provide a filename in this case, it is always the same, why not embed it
    in the method itself, its already hardcoded, and it is the only use of the method

    It just gives false assumption of reuse, kind of smell

    On this note, I see that we basically created this island classes for all the tools, and they appear to do the same
    thing

    Maybe it would be better to create some kind of interface for loading the schema, or obtaining the filepath and
    load it in some method implemented in super class?

    Just tie it in type system so the program will force as to implement it when we add new tool

    As it stands now this class is kinda lives on its own, and we have a code duplication in every/most tools.
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

