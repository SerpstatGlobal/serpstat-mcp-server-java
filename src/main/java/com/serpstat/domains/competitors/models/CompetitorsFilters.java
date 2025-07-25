package com.serpstat.domains.competitors.models;

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
public class CompetitorsFilters {
    @JsonProperty("visible")
    private Double minimumVisibility;

    @JsonProperty("traff")
    private Long minimumTraffic;
}