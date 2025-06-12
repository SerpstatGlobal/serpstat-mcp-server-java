package com.serpstat.domains.credits;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.serpstat.core.SerpstatApiResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Formatter for limits API responses
 */
public class CreditsResponseFormatter {

    /**
     * Format getStats response
     */
    public static String format(SerpstatApiResponse response, Map<String, Object> arguments, ObjectMapper mapper)
            throws Exception {

        JsonNode resultNode = response.getResult();

        // Create formatted response
        ObjectNode formattedResponse = mapper.createObjectNode();
        formattedResponse.put("status", "success");
        formattedResponse.put("method", "SerpstatLimitsProcedure.getStats");
        formattedResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Process API stats data
        JsonNode dataNode = resultNode.get("data");
        if (dataNode != null) {
            formattedResponse.set("api_stats", dataNode);

            // Add enhanced analytics
            ObjectNode analytics = mapper.createObjectNode();

            long maxLines = dataNode.path("max_lines").asLong(0);
            long usedLines = dataNode.path("used_lines").asLong(0);
            long leftLines = dataNode.path("left_lines").asLong(0);

            // Usage statistics
            ObjectNode usage = mapper.createObjectNode();
            usage.put("max_credits", maxLines);
            usage.put("used_credits", usedLines);
            usage.put("remaining_credits", leftLines);

            if (maxLines > 0) {
                double usagePercentage = (double) usedLines / maxLines * 100.0;
                usage.put("usage_percentage", Math.round(usagePercentage * 100.0) / 100.0);

                // Usage status
                String usageStatus;
                String statusIcon;
                if (usagePercentage > 95) {
                    usageStatus = "CRITICAL";
                    statusIcon = "🔴";
                } else if (usagePercentage > 90) {
                    usageStatus = "HIGH";
                    statusIcon = "🟠";
                } else if (usagePercentage > 75) {
                    usageStatus = "MODERATE";
                    statusIcon = "🟡";
                } else {
                    usageStatus = "NORMAL";
                    statusIcon = "🟢";
                }

                usage.put("status", usageStatus);
                usage.put("status_icon", statusIcon);
            } else {
                usage.put("usage_percentage", 0.0);
                usage.put("status", "UNKNOWN");
                usage.put("status_icon", "❓");
            }

            analytics.set("usage", usage);

            // Projections and recommendations
            ObjectNode recommendations = mapper.createObjectNode();

            if (leftLines > 0) {
                // Estimate how many domain analyses can be performed (5 credits each)
                long domainAnalyses = leftLines / 5;
                recommendations.put("estimated_domain_analyses", domainAnalyses);

                // Estimate how many keyword researches (1 credit each)
                recommendations.put("estimated_keyword_researches", leftLines);

                // Usage recommendations
                ObjectNode tips = mapper.createObjectNode();

                if (leftLines < 100) {
                    tips.put("priority", "HIGH");
                    tips.put("message", "Very low credits remaining. Consider upgrading plan or optimizing usage.");
                } else if (leftLines < 1000) {
                    tips.put("priority", "MEDIUM");
                    tips.put("message", "Credits running low. Monitor usage carefully.");
                } else {
                    tips.put("priority", "LOW");
                    tips.put("message", "Sufficient credits available for continued usage.");
                }

                recommendations.set("tips", tips);
            } else {
                recommendations.put("estimated_domain_analyses", 0);
                recommendations.put("estimated_keyword_researches", 0);

                ObjectNode tips = mapper.createObjectNode();
                tips.put("priority", "CRITICAL");
                tips.put("message", "No credits remaining. Plan upgrade required.");
                recommendations.set("tips", tips);
            }

            analytics.set("recommendations", recommendations);

            // User information
            JsonNode userInfo = dataNode.get("user_info");
            if (userInfo != null) {
                ObjectNode userAnalytics = mapper.createObjectNode();
                userAnalytics.set("user_details", userInfo);
                analytics.set("user", userAnalytics);
            }

            formattedResponse.set("analytics", analytics);
        } else {
            formattedResponse.set("api_stats", mapper.createObjectNode());
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(formattedResponse);
    }
}
