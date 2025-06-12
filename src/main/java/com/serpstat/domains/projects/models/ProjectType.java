package com.serpstat.domains.projects.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProjectType {
    @JsonProperty("owner")
    OWNER("owner", "You are the owner of this project"),

    @JsonProperty("reader")
    READER("reader", "The project's owner is a member of your team");

    private final String value;
    private final String description;

    ProjectType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() { return value; }
    public String getDescription() { return description; }
}
