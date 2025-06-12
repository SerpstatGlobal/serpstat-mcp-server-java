package com.serpstat.domains.competitors.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class CompetitorsSummary {

    @JsonProperty("page")
    private Integer page;

    @JsonProperty("left_lines")
    private Long leftCredits;
}
