package com.serpstat.domains.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Domain keyword data model
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unused")
public class DomainKeyword {

    @JsonProperty("domain")
    private String domain;

    @JsonProperty("subdomain")
    private String subdomain;

    @JsonProperty("keyword")
    private String keyword;

    @JsonProperty("keyword_length")
    private Integer keywordLength;

    @JsonProperty("url")
    private String url;

    @JsonProperty("position")
    private Integer position;

    @JsonProperty("types")
    private List<String> types;

    @JsonProperty("found_results")
    private Long foundResults;

    @JsonProperty("cost")
    private Double cost;

    @JsonProperty("concurrency")
    private Integer concurrency;

    @JsonProperty("region_queries_count")
    private Long regionQueriesCount;

    @JsonProperty("region_queries_count_wide")
    private Long regionQueriesCountWide;

    @JsonProperty("geo_names")
    private List<String> geoNames;

    @JsonProperty("traff")
    private Long traff;

    @JsonProperty("difficulty")
    private Integer difficulty;

    @JsonProperty("dynamic")
    private Integer dynamic;

    @JsonProperty("intents")
    private List<String> intents;
}