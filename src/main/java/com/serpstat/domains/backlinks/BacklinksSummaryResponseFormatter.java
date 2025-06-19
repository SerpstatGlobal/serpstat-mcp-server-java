package com.serpstat.domains.backlinks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.serpstat.core.SerpstatApiResponse;

import java.util.Map;

public class BacklinksSummaryResponseFormatter {
    public static String format(SerpstatApiResponse response, Map<String, Object> arguments, ObjectMapper mapper)
            throws Exception {

        JsonNode resultNode = response.getResult();

        // Extract request parameters for context
        String query = (String) arguments.get("query");
        String searchType = (String) arguments.getOrDefault("searchType", "domain");

        // Create formatted response
        ObjectNode formattedResponse = mapper.createObjectNode();
        formattedResponse.put("status", "success");
        formattedResponse.put("method", "SerpstatBacklinksProcedure.getSummaryV2");
        formattedResponse.put("query", query);
        formattedResponse.put("search_type", searchType);

        // Process backlinks data
        JsonNode dataNode = resultNode.get("data");
        if (dataNode != null) {
            formattedResponse.set("backlinks_data", dataNode);

            // Add summary statistics
            ObjectNode summary = mapper.createObjectNode();

            // Main metrics
            summary.put("total_backlinks", dataNode.path("backlinks").asInt(0));
            summary.put("referring_domains", dataNode.path("referring_domains").asInt(0));
            summary.put("dofollow_backlinks", dataNode.path("dofollow_backlinks").asInt(0));
            summary.put("nofollow_backlinks", dataNode.path("nofollow_backlinks").asInt(0));
            // typo in sersptat?
            summary.put("serpstat_domain_rank", dataNode.path("sersptat_domain_rank").asInt(0));

            // Quality metrics
            summary.put("referring_ip_addresses", dataNode.path("referring_ip_addresses").asInt(0));
            summary.put("referring_subnets", dataNode.path("referring_subnets").asInt(0));
            summary.put("malicious_domains", dataNode.path("referring_malicious_domains").asInt(0));

            // Link types breakdown
            ObjectNode linkTypes = mapper.createObjectNode();
            linkTypes.put("text", dataNode.path("text_backlinks").asInt(0));
            linkTypes.put("image", dataNode.path("image_backlinks").asInt(0));
            linkTypes.put("redirect", dataNode.path("redirect_backlinks").asInt(0));
            linkTypes.put("canonical", dataNode.path("canonical_backlinks").asInt(0));
            linkTypes.put("from_mainpages", dataNode.path("backlinks_from_mainpages").asInt(0));
            summary.set("link_types", linkTypes);

            // Changes (dynamics)
            ObjectNode changes = mapper.createObjectNode();
            changes.put("backlinks_change", dataNode.path("backlinks_change").asInt(0));
            changes.put("referring_domains_change", dataNode.path("referring_domains_change").asInt(0));
            changes.put("dofollow_change", dataNode.path("dofollow_backlinks_change").asInt(0));
            changes.put("nofollow_change", dataNode.path("nofollow_backlinks_change").asInt(0));
            summary.set("recent_changes", changes);

            // Quality indicators
            ObjectNode quality = mapper.createObjectNode();

            // is it correct default? wouldn't it influence the percentage calculation? (I get that it avoids division 0)
            int totalBacklinks = dataNode.path("backlinks").asInt(1);
            int dofollowBacklinks = dataNode.path("dofollow_backlinks").asInt(0);
            // is it correct default? wouldn't it influence the percentage calculation? (I get that it avoids division 0)
            int referringDomains = dataNode.path("referring_domains").asInt(1);
            // typo in sersptat?
            int serpstatDomainRank = dataNode.path("sersptat_domain_rank").asInt(0);

            double dofollowRatio = (double) dofollowBacklinks / totalBacklinks * 100;
            double diversityRatio = (double) referringDomains / totalBacklinks * 100;

            quality.put("total_backlinks", totalBacklinks);
            quality.put("referring_domains", referringDomains);
            quality.put("serpstat_domain_rank", serpstatDomainRank);
            // a little suspicious, what format are we trying to achieve? I think we can lose some info here
            quality.put("dofollow_percentage", Math.round(dofollowRatio * 100.0) / 100.0);
            quality.put("domain_diversity_percentage", Math.round(diversityRatio * 100.0) / 100.0);
            quality.put("avg_backlinks_per_domain", Math.round((double) totalBacklinks / referringDomains * 100.0) / 100.0);
            summary.set("quality_metrics", quality);

            formattedResponse.set("summary", summary);
        } else {
            formattedResponse.set("backlinks_data", mapper.createObjectNode());
        }

        // Add API metadata
        JsonNode summaryInfo = resultNode.get("summary_info");
        if (summaryInfo != null) {
            ObjectNode apiInfo = mapper.createObjectNode();
            if (summaryInfo.has("left_lines")) {
                apiInfo.put("credits_remaining", summaryInfo.get("left_lines").asLong());
            }
            formattedResponse.set("api_info", apiInfo);
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(formattedResponse);
    }
}
