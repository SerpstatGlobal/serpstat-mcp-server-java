package com.serpstat.domains.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unused")
public class DomainKeywordsSummary {
    @JsonProperty("page")
    private Integer page;

    @JsonProperty("total")
    private Integer total;

    @JsonProperty("left_lines")
    private Long leftLines;
}
