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
public class RegionalKeywordsCount {
    @JsonProperty("country_name_en")
    private String countryNameEn;

    @JsonProperty("db_name")
    private String dbName;

    @JsonProperty("domain")
    private String googleDomain;

    @JsonProperty("keywords_count")
    private Integer keywordsCount;
}
