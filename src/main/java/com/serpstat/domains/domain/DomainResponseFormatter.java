package com.serpstat.domains.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.serpstat.core.SerpstatApiResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Formatter for domain API responses
 */
public class DomainResponseFormatter {

    /**
     * Format getDomainsInfo response
     */
    public static String format(SerpstatApiResponse response, Map<String, Object> arguments, ObjectMapper mapper)
            throws Exception {

        JsonNode resultNode = response.getResult();

        // Extract request parameters for context
        @SuppressWarnings("unchecked")
        List<String> requestedDomains = (List<String>) arguments.get("domains");
        String searchEngine = (String) arguments.getOrDefault("se", "g_us");

        // Create formatted response
        ObjectNode formattedResponse = mapper.createObjectNode();
        formattedResponse.put("status", "success");
        formattedResponse.put("method", "SerpstatDomainProcedure.getDomainsInfo");
        formattedResponse.put("search_engine", searchEngine);
        formattedResponse.put("requested_domains_count", requestedDomains.size());

        // Process domains data
        JsonNode dataArray = resultNode.get("data");
        if (dataArray != null && dataArray.isArray()) {
            formattedResponse.put("found_domains_count", dataArray.size());
            formattedResponse.set("domains", dataArray);

            // Add summary statistics
            ObjectNode summary = mapper.createObjectNode();
            double totalVisibility = 0;
            long totalTraffic = 0;
            int totalKeywords = 0;

            for (JsonNode domainNode : dataArray) {
                if (domainNode.has("visible")) {
                    totalVisibility += domainNode.get("visible").asDouble();
                }
                if (domainNode.has("traff")) {
                    totalTraffic += domainNode.get("traff").asLong();
                }
                if (domainNode.has("keywords")) {
                    totalKeywords += domainNode.get("keywords").asInt();
                }
            }

            summary.put("total_visibility", Math.round(totalVisibility * 100.0) / 100.0);
            summary.put("total_estimated_traffic", totalTraffic);
            summary.put("total_keywords", totalKeywords);
            summary.put("average_visibility", !dataArray.isEmpty() ?
                    Math.round((totalVisibility / dataArray.size()) * 100.0) / 100.0 : 0);

            formattedResponse.set("summary", summary);
        } else {
            formattedResponse.put("found_domains_count", 0);
            formattedResponse.set("domains", mapper.createArrayNode());
        }

        // Add API metadata
        createSummaryInfo(mapper, resultNode, formattedResponse);

        // Calculate estimated cost (5 credits per domain according to documentation)
        int estimatedCost = requestedDomains.size() * 5;
        formattedResponse.put("estimated_credits_used", estimatedCost);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(formattedResponse);
    }

    public static void createSummaryInfo(ObjectMapper mapper, JsonNode resultNode, ObjectNode formattedResponse) {
        JsonNode summaryInfo = resultNode.get("summary_info");
        if (summaryInfo != null) {
            ObjectNode apiInfo = mapper.createObjectNode();
            if (summaryInfo.has("left_lines")) {
                apiInfo.put("credits_remaining", summaryInfo.get("left_lines").asLong());
            }
            if (summaryInfo.has("page")) {
                apiInfo.put("page", summaryInfo.get("page").asInt());
            }
            formattedResponse.set("api_info", apiInfo);
        }
    }

    /**
     * Format getRegionsCount response
     */
    public static String formatRegionsCount(SerpstatApiResponse response, Map<String, Object> arguments, ObjectMapper mapper)
            throws Exception {

        JsonNode resultNode = response.getResult();

        // Extract request parameters for context
        String domain = (String) arguments.get("domain");
        String sort = (String) arguments.getOrDefault("sort", "keywords_count");
        String order = (String) arguments.getOrDefault("order", "desc");

        // Create formatted response
        ObjectNode formattedResponse = mapper.createObjectNode();
        formattedResponse.put("status", "success");
        formattedResponse.put("method", "SerpstatDomainProcedure.getRegionsCount");
        formattedResponse.put("analyzed_domain", domain);
        formattedResponse.put("sort_field", sort);
        formattedResponse.put("sort_order", order);

        // Process regional data
        JsonNode dataArray = resultNode.get("data");
        if (dataArray != null && dataArray.isArray()) {
            formattedResponse.set("regional_data", dataArray);
            formattedResponse.put("regions_found", dataArray.size());

            // Calculate analytics
            ObjectNode analytics = mapper.createObjectNode();

            // Total keywords across all regions
            long totalKeywords = 0;
            int regionsWithKeywords = 0;
            int maxKeywords = 0;
            int minKeywords = Integer.MAX_VALUE;
            String topRegion = "";
            String topCountry = "";

            // Top regions analysis
            ArrayNode topRegions = mapper.createArrayNode();
            ArrayNode keywordDistribution = mapper.createArrayNode();

            for (JsonNode regionNode : dataArray) {
                int keywordsCount = regionNode.path("keywords_count").asInt(0);
                String countryName = regionNode.path("country_name_en").asText("");
                String dbName = regionNode.path("db_name").asText("");

                totalKeywords += keywordsCount;

                if (keywordsCount > 0) {
                    regionsWithKeywords++;

                    if (keywordsCount > maxKeywords) {
                        maxKeywords = keywordsCount;
                        topRegion = dbName;
                        topCountry = countryName;
                    }

                    if (keywordsCount < minKeywords) {
                        minKeywords = keywordsCount;
                    }

                    // Add to top regions (limit to top 5)
                    if (topRegions.size() < 5) {
                        ObjectNode regionInfo = mapper.createObjectNode();
                        regionInfo.put("country", countryName);
                        regionInfo.put("database", dbName);
                        regionInfo.put("keywords", keywordsCount);
                        regionInfo.put("percentage", Math.round((double) keywordsCount / totalKeywords * 10000.0) / 100.0);
                        topRegions.add(regionInfo);
                    }

                    // Keyword distribution for visualization
                    ObjectNode distribution = mapper.createObjectNode();
                    distribution.put("region", countryName);
                    distribution.put("keywords", keywordsCount);
                    keywordDistribution.add(distribution);
                }
            }

            // Summary statistics
            ObjectNode summary = mapper.createObjectNode();
            summary.put("total_keywords_across_regions", totalKeywords);
            summary.put("regions_with_keywords", regionsWithKeywords);
            summary.put("regions_without_keywords", dataArray.size() - regionsWithKeywords);
            summary.put("average_keywords_per_region", regionsWithKeywords > 0 ?
                    Math.round((double) totalKeywords / regionsWithKeywords * 100.0) / 100.0 : 0);

            if (regionsWithKeywords > 0) {
                summary.put("max_keywords_in_region", maxKeywords);
                summary.put("min_keywords_in_region", minKeywords == Integer.MAX_VALUE ? 0 : minKeywords);
                summary.put("top_performing_region", topCountry);
                summary.put("top_performing_database", topRegion);
            }

            analytics.set("summary", summary);
            analytics.set("top_regions", topRegions);
            analytics.set("keyword_distribution", keywordDistribution);

            // Regional insights
            ObjectNode insights = mapper.createObjectNode();

            if (regionsWithKeywords == 0) {
                insights.put("status", "NO_KEYWORDS");
                insights.put("message", "Domain has no keywords in any regional Google databases");
                insights.put("recommendation", "Check domain spelling or try analyzing a different domain");
            } else if (regionsWithKeywords == 1) {
                insights.put("status", "SINGLE_REGION");
                insights.put("message", String.format("Domain only has keywords in %s", topCountry));
                insights.put("recommendation", "Consider expanding to other regional markets");
            } else if (regionsWithKeywords <= 3) {
                insights.put("status", "LIMITED_REGIONS");
                insights.put("message", String.format("Domain has presence in %d regions", regionsWithKeywords));
                insights.put("recommendation", "Good regional focus, consider expanding to similar markets");
            } else {
                insights.put("status", "MULTI_REGIONAL");
                insights.put("message", String.format("Domain has global presence across %d regions", regionsWithKeywords));
                insights.put("recommendation", "Strong international presence - optimize for top performing regions");
            }

            analytics.set("insights", insights);
            formattedResponse.set("analytics", analytics);

        } else {
            formattedResponse.set("regional_data", mapper.createArrayNode());
            formattedResponse.put("regions_found", 0);
        }

        // Add summary info from API
        JsonNode summaryInfo = resultNode.get("summary_info");
        if (summaryInfo != null) {
            ObjectNode apiInfo = mapper.createObjectNode();

            if (summaryInfo.has("regions_db_count")) {
                apiInfo.put("total_databases_checked", summaryInfo.get("regions_db_count").asInt());
            }
            if (summaryInfo.has("total_keywords")) {
                apiInfo.put("api_total_keywords", summaryInfo.get("total_keywords").asLong());
            }
            if (summaryInfo.has("left_lines")) {
                apiInfo.put("credits_remaining", summaryInfo.get("left_lines").asLong());
            }

            formattedResponse.set("api_info", apiInfo);
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(formattedResponse);
    }

    public static String formatDomainKeywords(SerpstatApiResponse response, Map<String, Object> arguments, ObjectMapper mapper)
            throws Exception {

        JsonNode resultNode = response.getResult();

        // Extract request parameters for context
        String domain = (String) arguments.get("domain");
        String searchEngine = (String) arguments.get("se");
        Integer page = (Integer) arguments.getOrDefault("page", 1);
        Integer size = (Integer) arguments.getOrDefault("size", 100);
        Boolean withSubdomains = (Boolean) arguments.getOrDefault("withSubdomains", false);
        Boolean withIntents = (Boolean) arguments.getOrDefault("withIntents", false);

        // Create formatted response
        ObjectNode formattedResponse = mapper.createObjectNode();
        formattedResponse.put("status", "success");
        formattedResponse.put("method", "SerpstatDomainProcedure.getDomainKeywords");
        formattedResponse.put("analyzed_domain", domain);
        formattedResponse.put("search_engine", searchEngine);
        formattedResponse.put("page", page);
        formattedResponse.put("page_size", size);
        formattedResponse.put("with_subdomains", withSubdomains);
        formattedResponse.put("with_intents", withIntents);

        // Process keyword data
        JsonNode dataArray = resultNode.get("data");
        if (dataArray != null && dataArray.isArray()) {
            formattedResponse.set("keywords", dataArray);
            formattedResponse.put("keywords_on_page", dataArray.size());

            // Calculate analytics
            ObjectNode analytics = mapper.createObjectNode();

            // Basic statistics
            long totalTraffic = 0;
            double totalCost = 0.0;
            int topPositions = 0; // positions 1-3
            int firstPage = 0; // positions 1-10
            int secondPage = 0; // positions 11-20
            int beyondSecondPage = 0; // positions 21+

            // Difficulty and competition analysis
            double totalDifficulty = 0;
            double totalConcurrency = 0;
            int difficultyCount = 0;
            int concurrencyCount = 0;

            // Keyword length analysis
            Map<Integer, Integer> lengthDistribution = new HashMap<>();

            // Intent analysis (if available)
            Map<String, Integer> intentDistribution = new HashMap<>();

            // SERP features analysis
            Map<String, Integer> serpFeatures = new HashMap<>();

            // Position distribution analysis
            for (JsonNode keywordNode : dataArray) {
                // Traffic and cost
                totalTraffic += keywordNode.path("traff").asLong(0);
                totalCost += keywordNode.path("cost").asDouble(0.0);

                // Position analysis
                int position = keywordNode.path("position").asInt(0);
                if (position >= 1 && position <= 3) {
                    topPositions++;
                }
                if (position >= 1 && position <= 10) {
                    firstPage++;
                } else if (position >= 11 && position <= 20) {
                    secondPage++;
                } else if (position > 20) {
                    beyondSecondPage++;
                }

                // Difficulty analysis
                if (keywordNode.has("difficulty") && !keywordNode.get("difficulty").isNull()) {
                    totalDifficulty += keywordNode.path("difficulty").asDouble(0.0);
                    difficultyCount++;
                }

                // Concurrency analysis
                if (keywordNode.has("concurrency") && !keywordNode.get("concurrency").isNull()) {
                    totalConcurrency += keywordNode.path("concurrency").asDouble(0.0);
                    concurrencyCount++;
                }

                // Keyword length distribution
                int length = keywordNode.path("keyword_length").asInt(0);
                lengthDistribution.put(length, lengthDistribution.getOrDefault(length, 0) + 1);

                // Intent analysis
                JsonNode intentsNode = keywordNode.get("intents");
                if (intentsNode != null && intentsNode.isArray()) {
                    for (JsonNode intentNode : intentsNode) {
                        String intent = intentNode.asText();
                        intentDistribution.put(intent, intentDistribution.getOrDefault(intent, 0) + 1);
                    }
                }

                // SERP features analysis
                JsonNode typesNode = keywordNode.get("types");
                if (typesNode != null && typesNode.isArray()) {
                    for (JsonNode typeNode : typesNode) {
                        String type = typeNode.asText();
                        serpFeatures.put(type, serpFeatures.getOrDefault(type, 0) + 1);
                    }
                }
            }

            // Summary statistics
            ObjectNode summary = mapper.createObjectNode();
            summary.put("total_traffic_estimate", totalTraffic);
            summary.put("total_cost_estimate", Math.round(totalCost * 100.0) / 100.0);
            summary.put("average_difficulty", difficultyCount > 0 ? Math.round((totalDifficulty / difficultyCount) * 100.0) / 100.0 : 0);
            summary.put("average_concurrency", concurrencyCount > 0 ? Math.round((totalConcurrency / concurrencyCount) * 100.0) / 100.0 : 0);

            analytics.set("summary", summary);

            // Position distribution
            ObjectNode positions = mapper.createObjectNode();
            positions.put("top_3_positions", topPositions);
            positions.put("first_page_positions", firstPage);
            positions.put("second_page_positions", secondPage);
            positions.put("beyond_second_page", beyondSecondPage);

            if (!dataArray.isEmpty()) {
                positions.put("top_3_percentage", Math.round((double) topPositions / dataArray.size() * 10000.0) / 100.0);
                positions.put("first_page_percentage", Math.round((double) firstPage / dataArray.size() * 10000.0) / 100.0);
            }

            analytics.set("position_distribution", positions);

            // Keyword length distribution
            ObjectNode lengthDist = mapper.createObjectNode();
            for (Map.Entry<Integer, Integer> entry : lengthDistribution.entrySet()) {
                lengthDist.put(entry.getKey() + "_words", entry.getValue());
            }
            analytics.set("keyword_length_distribution", lengthDist);

            // Intent distribution (if available)
            if (!intentDistribution.isEmpty()) {
                ObjectNode intentDist = mapper.createObjectNode();
                for (Map.Entry<String, Integer> entry : intentDistribution.entrySet()) {
                    intentDist.put(entry.getKey(), entry.getValue());
                }
                analytics.set("intent_distribution", intentDist);
            }

            // Top SERP features
            ObjectNode topSerpFeatures = mapper.createObjectNode();
            serpFeatures.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> topSerpFeatures.put(entry.getKey(), entry.getValue()));
            analytics.set("serp_features", topSerpFeatures);

            // Performance insights
            ObjectNode insights = mapper.createObjectNode();

            if (dataArray.isEmpty()) {
                insights.put("status", "NO_KEYWORDS");
                insights.put("message", "No keywords found for this domain");
                insights.put("recommendation", "Check domain spelling or expand search criteria");
            } else {
                double firstPagePercentage = !dataArray.isEmpty() ? (double) firstPage / dataArray.size() * 100.0 : 0;

                if (firstPagePercentage >= 80) {
                    insights.put("status", "EXCELLENT");
                    insights.put("message", String.format("Excellent performance with %.1f%% of keywords on first page", firstPagePercentage));
                    insights.put("recommendation", "Focus on improving positions to top 3 for maximum traffic");
                } else if (firstPagePercentage >= 60) {
                    insights.put("status", "GOOD");
                    insights.put("message", String.format("Good performance with %.1f%% of keywords on first page", firstPagePercentage));
                    insights.put("recommendation", "Optimize underperforming keywords to reach first page");
                } else if (firstPagePercentage >= 40) {
                    insights.put("status", "MODERATE");
                    insights.put("message", String.format("Moderate performance with %.1f%% of keywords on first page", firstPagePercentage));
                    insights.put("recommendation", "Significant SEO improvements needed for better rankings");
                } else {
                    insights.put("status", "POOR");
                    insights.put("message", String.format("Poor performance with only %.1f%% of keywords on first page", firstPagePercentage));
                    insights.put("recommendation", "Comprehensive SEO strategy required to improve rankings");
                }
            }

            analytics.set("insights", insights);
            formattedResponse.set("analytics", analytics);

        } else {
            formattedResponse.set("keywords", mapper.createArrayNode());
            formattedResponse.put("keywords_on_page", 0);
        }

        // Add summary info from API
        JsonNode summaryInfo = resultNode.get("summary_info");
        if (summaryInfo != null) {
            ObjectNode apiInfo = mapper.createObjectNode();

            if (summaryInfo.has("total")) {
                apiInfo.put("total_keywords_found", summaryInfo.get("total").asInt());
            }
            if (summaryInfo.has("page")) {
                apiInfo.put("current_page", summaryInfo.get("page").asInt());
            }
            if (summaryInfo.has("left_lines")) {
                apiInfo.put("credits_remaining", summaryInfo.get("left_lines").asLong());
            }

            // Calculate pagination info
            if (summaryInfo.has("total")) {
                int totalKeywords = summaryInfo.get("total").asInt();
                int totalPages = (int) Math.ceil((double) totalKeywords / size);
                apiInfo.put("total_pages", totalPages);
                apiInfo.put("has_next_page", page < totalPages);
                apiInfo.put("credits_used_this_request", dataArray != null ? dataArray.size() : 0);
            }

            formattedResponse.set("api_info", apiInfo);
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(formattedResponse);
    }
}
