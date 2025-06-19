package com.serpstat.domains.competitors.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.serpstat.core.SerpstatApiResponse;
import com.serpstat.domains.domain.DomainResponseFormatter;

import java.util.Map;

/**
 * Formatter for domain competitors API responses
 */
// why is this class in models? + kinda the same situation as in schema loading, many classes have it, maybe leverage
// some work to the typesystem?
public class CompetitorsResponseFormatter {

    /**
     * Format getDomainCompetitors response
     */
    public static String format(SerpstatApiResponse response, Map<String, Object> arguments, ObjectMapper mapper)
            throws Exception {

        JsonNode resultNode = response.getResult();

        // Extract request parameters for context
        @SuppressWarnings("unchecked")
        // don't understand why its here and what's the significance of it
        String requestedDomain = (String) arguments.get("domain");
        String searchEngine = (String) arguments.getOrDefault("se", "g_us");

        // Create a formatted response
        ObjectNode formattedResponse = mapper.createObjectNode();
        formattedResponse.put("status", "success");
        formattedResponse.put("method", "SerpstatDomainProcedure.getDomainCompetitors");
        formattedResponse.put("search_engine", searchEngine);
        // formattedResponse.put("requested_domains_count", requestedDomains.size());

        // Process competitors data
        JsonNode dataArray = resultNode.get("data");
        // Request spent minimum 1 credit per query if no data returned
        int estimatedCost = 1;
        if (dataArray != null && dataArray.isArray()) {
            formattedResponse.put("found_competitors_count", dataArray.size());
            formattedResponse.set("competitors", dataArray);

            // Add summary statistics
            ObjectNode summary = mapper.createObjectNode();
            double totalRelevance = 0;
            int totalIntersectedKeywords = 0;

            for (JsonNode competitorNode : dataArray) {
                if (competitorNode.has("relevance")) {
                    totalRelevance += competitorNode.get("relevance").asDouble();
                }
                if (competitorNode.has("intersected")) {
                    totalIntersectedKeywords += competitorNode.get("intersected").asInt();
                }
                // if we would have 5 items in array we would end up with 6 limits estimated? (default is already 1)
                // is this right? couldn't we just use size
                estimatedCost ++;
            }

            summary.put("total_relevance", Math.round(totalRelevance * 100.0) / 100.0);
            summary.put("total_intersected_keywords", totalIntersectedKeywords);
            summary.put("average_relevance", !dataArray.isEmpty() ?
                    // can't array be empty here? division by zero may occur unless I am misinterpreting something
                    Math.round((totalRelevance / dataArray.size()) * 100.0) / 100.0 : 0);

            formattedResponse.set("summary", summary);
        } else {
            formattedResponse.put("found_competitors_count", 0);
            formattedResponse.set("competitors", mapper.createArrayNode());
        }

        // Add API metadata
        DomainResponseFormatter.createSummaryInfo(mapper, resultNode, formattedResponse);

        // Calculate estimated cost (5 credits per competitor according to documentation)

        // but we have calculated 1 per competitor above
        formattedResponse.put("estimated_credits_used", estimatedCost);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(formattedResponse);
    }
}