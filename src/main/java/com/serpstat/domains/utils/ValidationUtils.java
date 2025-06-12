package com.serpstat.domains.utils;

import com.serpstat.core.ValidationException;

import java.util.Map;
import java.util.Set;

public class ValidationUtils {

    /**
     * Validate filter parameters
     */
    public static void validateFilters(Object filtersObj) throws ValidationException {
        if (!(filtersObj instanceof Map)) {
            throw new ValidationException("Parameter 'filters' must be an object");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> filters = (Map<String, Object>) filtersObj;

        // Validate visibility filter
        Object visibleObj = filters.get("visible");
        if (visibleObj != null) {
            try {
                double visible = ((Number) visibleObj).doubleValue();
                if (visible < 0) {
                    throw new ValidationException("Filter 'visible' must be non-negative");
                }
            } catch (ClassCastException | NullPointerException e) {
                throw new ValidationException("Filter 'visible' must be a number");
            }
        }

        // Validate traffic filter
        Object traffObj = filters.get("traff");
        if (traffObj != null) {
            try {
                long traff = ((Number) traffObj).longValue();
                if (traff < 0) {
                    throw new ValidationException("Filter 'traff' must be non-negative");
                }
            } catch (ClassCastException | NullPointerException e) {
                throw new ValidationException("Filter 'traff' must be a number");
            }
        }

        // Check for unknown filter parameters
        Set<String> allowedFilters = Set.of("visible", "traff");
        for (String key : filters.keySet()) {
            if (!allowedFilters.contains(key)) {
                throw new ValidationException(String.format("Unknown filter parameter: '%s'", key));
            }
        }
    }
}