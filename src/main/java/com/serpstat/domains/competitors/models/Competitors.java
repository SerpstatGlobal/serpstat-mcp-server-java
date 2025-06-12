package com.serpstat.domains.competitors.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

///  Response model of API method `SerpstatDomainProcedure.getDomainCompetitors`
///  Returns SEO metrics of domain competitors.
///  Each competitor in response costs 5 api credits
///  @link <a href="https://api-docs.serpstat.com/docs/serpstat-public-api/34gt6x6a3v4wp-get-domain-competitors">
///  Documentation for SerpstatDomainProcedure.getDomainCompetitors</a>

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unused")
public class Competitors {

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

    @JsonProperty("intersected")
    private Integer intersectedKeywords;

    @JsonProperty("common")
    private Integer commonKeywords;

    @JsonProperty("not_intersected")
    private Integer notIntersectedKeywords;

    @JsonProperty("missing")
    private Integer missingKeywords;

    @JsonProperty("relevance")
    private Double relevance;

    @JsonProperty("new_relevance")
    private Double newRelevance;

    @JsonProperty("our_relevance")
    private Double ourRelevance;
}