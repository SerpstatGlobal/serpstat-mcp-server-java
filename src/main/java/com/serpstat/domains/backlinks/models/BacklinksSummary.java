package com.serpstat.domains.backlinks.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response model of API method {@code SerpstatBacklinksProcedure.getSummaryV2}.
 * Provides general information about the link profile: number of external links,
 * referring IP addresses and subnets, number of referring domains and subdomains,
 * domain authority indicator, and types of referring links.
 * Costs 5 API credits.
 * @see <a href="https://api-docs.serpstat.com/docs/serpstat-public-api/ylg7q8n96yjci-get-summary-v2">
 * Documentation for SerpstatBacklinksProcedure.getSummaryV2</a>
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unused")
public class BacklinksSummary {

        @JsonProperty("sersptat_domain_rank")
        private Integer serpstatDomainRank;

        @JsonProperty("referring_domains")
        private Integer referringDomains;

        @JsonProperty("referring_domains_change")
        private Integer referringDomainsChange;

        @JsonProperty("redirected_domains")
        private Integer redirectedDomains;

        @JsonProperty("referring_malicious_domains")
        private Integer referringMaliciousDomains;

        @JsonProperty("referring_malicious_domains_change")
        private Integer referringMaliciousDomainsChange;

        @JsonProperty("referring_ip_addresses")
        private Integer referringIpAddresses;

        @JsonProperty("referring_ip_addresses_change")
        private Integer referringIpAddressesChange;

        @JsonProperty("referring_subnets")
        private Integer referringSubnets;

        @JsonProperty("referring_subnets_change")
        private Integer referringSubnetsChange;

        @JsonProperty("backlinks")
        private Integer backlinks;

        @JsonProperty("backlinks_change")
        private Integer backlinksChange;

        @JsonProperty("backlinks_from_mainpages")
        private Integer backlinksFromMainpages;

        @JsonProperty("backlinks_from_mainpages_change")
        private Integer backlinksFromMainpagesChange;

        @JsonProperty("nofollow_backlinks")
        private Integer nofollowBacklinks;

        @JsonProperty("nofollow_backlinks_change")
        private Integer nofollowBacklinksChange;

        @JsonProperty("dofollow_backlinks")
        private Integer dofollowBacklinks;

        @JsonProperty("dofollow_backlinks_change")
        private Integer dofollowBacklinksChange;

        @JsonProperty("text_backlinks")
        private Integer textBacklinks;

        @JsonProperty("text_backlinks_change")
        private Integer textBacklinksChange;

        @JsonProperty("image_backlinks")
        private Integer imageBacklinks;

        @JsonProperty("image_backlinks_change")
        private Integer imageBacklinksChange;

        @JsonProperty("redirect_backlinks")
        private Integer redirectBacklinks;

        @JsonProperty("redirect_backlinks_change")
        private Integer redirectBacklinksChange;

        @JsonProperty("canonical_backlinks")
        private Integer canonicalBacklinks;

        @JsonProperty("canonical_backlinks_change")
        private Integer canonicalBacklinksChange;

        @JsonProperty("alternate_backlinks")
        private Integer alternateBacklinks;

        @JsonProperty("alternate_backlinks_change")
        private Integer alternateBacklinksChange;

        @JsonProperty("rss_backlinks")
        private Integer rssBacklinks;

        @JsonProperty("rss_backlinks_change")
        private Integer rssBacklinksChange;

        @JsonProperty("frame_backlinks")
        private Integer frameBacklinks;

        @JsonProperty("frame_backlinks_change")
        private Integer frameBacklinksChange;

        @JsonProperty("form_backlinks")
        private Integer formBacklinks;

        @JsonProperty("form_backlinks_change")
        private Integer formBacklinksChange;

        @JsonProperty("external_domains")
        private Integer externalDomains;

        @JsonProperty("external_domains_change")
        private Integer externalDomainsChange;

        @JsonProperty("external_malicious_domains")
        private Integer externalMaliciousDomains;

        @JsonProperty("external_malicious_domains_change")
        private Integer externalMaliciousDomainsChange;

        @JsonProperty("external_links")
        private Integer externalLinks;

        @JsonProperty("external_links_change")
        private Integer externalLinksChange;
}
