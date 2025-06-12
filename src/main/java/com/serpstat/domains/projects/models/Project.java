package com.serpstat.domains.projects.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * Project data model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("domain")
    private String domain;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("group")
    private String group;

    @JsonProperty("type")
    private ProjectType type;

    /**
     * Parse creation date to LocalDateTime
     */
    public LocalDateTime getParsedCreatedAt() {
        try {
            return LocalDateTime.parse(createdAt);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Check if a user is an owner of this project
     */
    public boolean isOwner() {
        return ProjectType.OWNER.equals(type);
    }
}

