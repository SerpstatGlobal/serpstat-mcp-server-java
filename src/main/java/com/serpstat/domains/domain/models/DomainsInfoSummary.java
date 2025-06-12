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
@SuppressWarnings("unused")
public class DomainsInfoSummary {

    @JsonProperty("page")
    private Integer page;

    @JsonProperty("left_lines")
    private Long leftCredits;
}
