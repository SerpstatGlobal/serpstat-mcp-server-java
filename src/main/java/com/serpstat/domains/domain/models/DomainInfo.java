package com.serpstat.domains.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

///  Response model of API method `SerpstatDomainProcedure.getDomainsInfo`
///  Returns SEO metrics of domains.
///  Each domain in response costs 5 api credits
///  @link <a href="https://api-docs.serpstat.com/docs/serpstat-public-api/34gt6x6a3v4wp-get-domains-info">
///  Documentation for SerpstatDomainProcedure.getDomainsInfo</a>

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unused")
public class DomainInfo {

    @JsonProperty("domain")
    private String domain;

    @JsonProperty("visible")
    private Double visibility;

    @JsonProperty("keywords")
    private Integer keywordsCount;

    @JsonProperty("traff")
    private Long estimatedTraffic;

    @JsonProperty("visible_dynamic")
    private Double visibilityDynamic;

    @JsonProperty("keywords_dynamic")
    private Integer keywordsDynamic;

    @JsonProperty("traff_dynamic")
    private Long trafficDynamic;

    @JsonProperty("ads_dynamic")
    private Integer adsDynamic;

    @JsonProperty("new_keywords")
    private Integer newKeywords;

    @JsonProperty("out_keywords")
    private Integer outKeywords;

    @JsonProperty("rised_keywords")
    private Integer risedKeywords;

    @JsonProperty("down_keywords")
    private Integer downKeywords;

    @JsonProperty("ad_keywords")
    private Integer adKeywords;

    @JsonProperty("ads")
    private Integer ads;

    @JsonProperty("prev_date")
    private String previousDate;
}
