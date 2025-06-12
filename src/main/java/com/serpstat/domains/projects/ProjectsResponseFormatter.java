package com.serpstat.domains.projects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.serpstat.core.SerpstatApiResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Formatter for projects API responses
 */
public class ProjectsResponseFormatter {

    /**
     * Format getProjects response
     */
    public static String format(SerpstatApiResponse response, Map<String, Object> arguments, ObjectMapper mapper)
            throws Exception {

        JsonNode resultNode = response.getResult();

        // Extract request parameters for context
        Integer page = (Integer) arguments.getOrDefault("page", 1);
        Integer size = (Integer) arguments.getOrDefault("size", 100);

        // Create formatted response
        ObjectNode formattedResponse = mapper.createObjectNode();
        formattedResponse.put("status", "success");
        formattedResponse.put("method", "ProjectProcedure.getProjects");
        formattedResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        formattedResponse.put("requested_page", page);
        formattedResponse.put("requested_size", size);

        // Process projects data
        JsonNode dataArray = resultNode.get("data");
        if (dataArray != null && dataArray.isArray()) {
            formattedResponse.set("projects", dataArray);
            formattedResponse.put("projects_on_page", dataArray.size());

            // Calculate analytics
            ObjectNode analytics = mapper.createObjectNode();

            // Project statistics
            int ownerProjects = 0;
            int readerProjects = 0;
            Map<String, Integer> groupDistribution = new HashMap<>();
            Map<String, Integer> domainExtensions = new HashMap<>();

            // Recent projects (last 30 days)
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            int recentProjects = 0;

            // Process each project
            for (JsonNode projectNode : dataArray) {
                // Type distribution
                String type = projectNode.path("type").asText("");
                if ("owner".equals(type)) {
                    ownerProjects++;
                } else if ("reader".equals(type)) {
                    readerProjects++;
                }

                // Group distribution
                String group = projectNode.path("group").asText("Unknown");
                groupDistribution.put(group, groupDistribution.getOrDefault(group, 0) + 1);

                // Domain extension analysis
                String domain = projectNode.path("domain").asText("");
                if (!domain.isEmpty()) {
                    String extension = extractDomainExtension(domain);
                    domainExtensions.put(extension, domainExtensions.getOrDefault(extension, 0) + 1);
                }

                // Recent projects analysis
                String createdAt = projectNode.path("created_at").asText("");
                try {
                    LocalDateTime createdDate = LocalDateTime.parse(createdAt);
                    if (createdDate.isAfter(thirtyDaysAgo)) {
                        recentProjects++;
                    }
                } catch (Exception e) {
                    // Skip invalid dates
                }
            }

            // Summary statistics
            ObjectNode summary = mapper.createObjectNode();
            summary.put("total_projects_on_page", dataArray.size());
            summary.put("owner_projects", ownerProjects);
            summary.put("reader_projects", readerProjects);
            summary.put("recent_projects_30_days", recentProjects);
            summary.put("unique_groups", groupDistribution.size());
            summary.put("unique_domain_extensions", domainExtensions.size());

            analytics.set("summary", summary);

            // Group distribution
            ObjectNode groups = mapper.createObjectNode();
            groupDistribution.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> groups.put(entry.getKey(), entry.getValue()));
            analytics.set("group_distribution", groups);

            // Domain extensions
            ObjectNode extensions = mapper.createObjectNode();
            domainExtensions.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> extensions.put(entry.getKey(), entry.getValue()));
            analytics.set("domain_extensions", extensions);

            // Project management insights
            ObjectNode insights = mapper.createObjectNode();

            if (dataArray.size() == 0) {
                insights.put("status", "NO_PROJECTS");
                insights.put("message", "No projects found");
                insights.put("recommendation", "Create your first project to start tracking domains");
            } else if (ownerProjects == 0) {
                insights.put("status", "READER_ONLY");
                insights.put("message", "You only have reader access to projects");
                insights.put("recommendation", "Create your own projects for full control");
            } else if (groupDistribution.size() == 1) {
                insights.put("status", "SINGLE_GROUP");
                insights.put("message", "All projects are in one group");
                insights.put("recommendation", "Consider organizing projects into different groups");
            } else {
                insights.put("status", "WELL_ORGANIZED");
                insights.put("message", String.format("Projects well organized across %d groups", groupDistribution.size()));
                insights.put("recommendation", "Continue maintaining good project organization");
            }

            // Recent activity insight
            if (recentProjects > 0) {
                insights.put("recent_activity", String.format("%d projects created in last 30 days", recentProjects));
            } else {
                insights.put("recent_activity", "No new projects in last 30 days");
            }

            analytics.set("insights", insights);
            formattedResponse.set("analytics", analytics);

        } else {
            formattedResponse.set("projects", mapper.createArrayNode());
            formattedResponse.put("projects_on_page", 0);
        }

        // Add summary info from API
        JsonNode summaryInfo = resultNode.get("summary_info");
        if (summaryInfo != null) {
            ObjectNode apiInfo = mapper.createObjectNode();

            if (summaryInfo.has("page")) {
                apiInfo.put("current_page", summaryInfo.get("page").asInt());
            }
            if (summaryInfo.has("page_total")) {
                apiInfo.put("total_pages", summaryInfo.get("page_total").asInt());
            }
            if (summaryInfo.has("count")) {
                apiInfo.put("count_on_page", summaryInfo.get("count").asInt());
            }
            if (summaryInfo.has("total")) {
                apiInfo.put("total_projects", summaryInfo.get("total").asInt());
            }

            // Pagination info
            if (summaryInfo.has("page") && summaryInfo.has("page_total")) {
                int currentPage = summaryInfo.get("page").asInt();
                int totalPages = summaryInfo.get("page_total").asInt();
                apiInfo.put("has_next_page", currentPage < totalPages);
                apiInfo.put("has_previous_page", currentPage > 1);
            }

            formattedResponse.set("api_info", apiInfo);
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(formattedResponse);
    }

    /**
     * Extract domain extension from domain name
     */
    private static String extractDomainExtension(String domain) {
        if (domain == null || domain.isEmpty()) {
            return "unknown";
        }

        int lastDot = domain.lastIndexOf('.');
        if (lastDot == -1 || lastDot == domain.length() - 1) {
            return "unknown";
        }

        return domain.substring(lastDot + 1).toLowerCase();
    }
}
