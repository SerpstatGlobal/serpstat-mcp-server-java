package com.serpstat.domains.credits.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * API limits and usage statistics model
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unused")
public class ApiStats {

    @JsonProperty("max_lines")
    private Long maxLines;

    @JsonProperty("used_lines")
    private Long usedLines;

    @JsonProperty("left_lines")
    private Long leftLines;

    @JsonProperty("user_info")
    private UserInfo userInfo;

    /**
     * Calculate usage percentage
     */
    public double getUsagePercentage() {
        if (maxLines == null || maxLines == 0) return 0.0;
        return (double) usedLines / maxLines * 100.0;
    }

    /**
     * Check if usage is critical (>90%)
     */
    public boolean isCriticalUsage() {
        return getUsagePercentage() > 90.0;
    }

    /**
     * Check if usage is high (>75%)
     */
    public boolean isHighUsage() {
        return getUsagePercentage() > 75.0;
    }
}
