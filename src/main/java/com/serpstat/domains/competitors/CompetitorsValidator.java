package com.serpstat.domains.competitors;

import com.serpstat.core.ValidationException;
import com.serpstat.domains.constants.Patterns;
import com.serpstat.domains.constants.SearchEngines;
import com.serpstat.domains.utils.ValidationUtils;

import java.util.Map;



/**
 * Validator for domain-related requests
 */
public class CompetitorsValidator {

    /**
     * Validate getCompetitors request parameters
     */
    public static void validateDomainCompetitorsRequest(Map<String, Object> arguments)
            throws ValidationException {

        // Validate domain

        String domain = (String) arguments.get("domain");
        if (domain == null || domain.trim().isEmpty()) {
            throw new ValidationException("Parameter 'domain' is required");
        }
        domain = domain.trim().toLowerCase();
        if (!Patterns.DOMAIN_PATTERN.matcher(domain).matches()) {
            throw new ValidationException(String.format("Invalid domain format: %s ", domain));
        }

        // Validate search engine
        String searchEngine = (String) arguments.getOrDefault("se", "g_us");
        if (!SearchEngines.SUPPORTED_SEARCH_ENGINES.contains(searchEngine)) {
            throw new ValidationException(String.format(
                    "Unsupported search engine: '%s'. Supported: %s",
                    searchEngine,
                    SearchEngines.SUPPORTED_SEARCH_ENGINES
            ));
        }

        // Validate filters if present
        Object filtersObj = arguments.get("filters");
        if (filtersObj != null) {
            ValidationUtils.validateFilters(filtersObj);
        }
    }

}