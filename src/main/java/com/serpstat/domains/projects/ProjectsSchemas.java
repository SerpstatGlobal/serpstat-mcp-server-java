package com.serpstat.domains.projects;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ProjectsSchemas {

    /**
     * JSON schema for getProjects method
     */
    public static final String PROJECTS_LIST_SCHEMA = loadSchema("projects_list.json");

    private static String loadSchema(String filename) {
        try (InputStream is = ProjectsSchemas.class.getResourceAsStream("/schemas/projects/" + filename)) {
            if (is == null) {
                throw new RuntimeException("Schema file not found: " + filename);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load schema: " + filename, e);
        }
    }
}
