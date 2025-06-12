package com.serpstat.domains.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegionalAnalysisSummary {
    @JsonProperty("analysed_domain")
    private String analysedDomain;

    @JsonProperty("sort")
    private String sort;

    @JsonProperty("order")
    private String order;

    @JsonProperty("regions_db_count")
    private Integer regionsDbCount;

    @JsonProperty("total_keywords")
    private Long totalKeywords;

    @JsonProperty("left_lines")
    private Long leftLines;
}
