package com.serpstat.domains.domain;

import com.serpstat.core.ValidationException;
import com.serpstat.domains.utils.ValidationUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.serpstat.domains.constants.Patterns.DOMAIN_PATTERN;
import static com.serpstat.domains.constants.SearchEngines.SUPPORTED_SEARCH_ENGINES;


/**
 * Validator for domain-related requests
 */
public class DomainValidator {

    /**
     * Validate getDomainsInfo request parameters
     */
    public static void validateDomainsInfoRequest(Map<String, Object> arguments)
            throws ValidationException {

        // Validate domains array
        Object domainsObj = arguments.get("domains");
        if (domainsObj == null) {
            throw new ValidationException("Parameter 'domains' is required");
        }

        if (!(domainsObj instanceof List)) {
            throw new ValidationException("Parameter 'domains' must be an array");
        }

        @SuppressWarnings("unchecked")
        List<String> domains = (List<String>) domainsObj;

        if (domains.isEmpty()) {
            throw new ValidationException("Parameter 'domains' cannot be empty");
        }

        if (domains.size() > 100) {
            throw new ValidationException("Maximum 100 domains allowed per request");
        }

        // Validate each domain
        for (int i = 0; i < domains.size(); i++) {
            String domain = domains.get(i);
            if (domain == null || domain.trim().isEmpty()) {
                throw new ValidationException(String.format("Domain at index %d is empty", i));
            }

            domain = domain.trim().toLowerCase();
            if (!DOMAIN_PATTERN.matcher(domain).matches()) {
                throw new ValidationException(String.format("Invalid domain format: %s ", domain));
            }

            // Update the normalized domain back to the list
            domains.set(i, domain);
        }

        // Check for duplicates
        Set<String> uniqueDomains = Set.copyOf(domains);
        if (uniqueDomains.size() != domains.size()) {
            throw new ValidationException("Duplicate domains are not allowed");
        }

        // Validate search engine
        String searchEngine = (String) arguments.getOrDefault("se", "g_us");
        if (!SUPPORTED_SEARCH_ENGINES.contains(searchEngine)) {
            throw new ValidationException(String.format(
                    "Unsupported search engine: '%s'. Supported: %s",
                    searchEngine,
                    SUPPORTED_SEARCH_ENGINES
            ));
        }

        // Validate filters if present
        Object filtersObj = arguments.get("filters");
        if (filtersObj != null) {
            ValidationUtils.validateFilters(filtersObj);
        }
    }

    /**
     * Validate getRegionsCount request parameters
     */
    public static void validateRegionsCountRequest(Map<String, Object> arguments)
            throws ValidationException {

        // Validate domain parameter
        Object domainObj = arguments.get("domain");
        if (domainObj == null) {
            throw new ValidationException("Parameter 'domain' is required");
        }

        if (!(domainObj instanceof String)) {
            throw new ValidationException("Parameter 'domain' must be a string");
        }

        String domain = ((String) domainObj).trim();
        if (domain.isEmpty()) {
            throw new ValidationException("Parameter 'domain' cannot be empty");
        }

        if (domain.length() < 4 || domain.length() > 253) {
            throw new ValidationException("Parameter 'domain' must be between 4 and 253 characters");
        }

        // Validate domain format
        if (!DOMAIN_PATTERN.matcher(domain.toLowerCase()).matches()) {
            throw new ValidationException(String.format(
                    "Invalid domain format: '%s'. Expected format: example.com", domain
            ));
        }

        // Normalize domain to lowercase
        arguments.put("domain", domain.toLowerCase());

        // Validate sort parameter
        String sort = (String) arguments.getOrDefault("sort", "keywords_count");
        Set<String> validSortFields = Set.of("keywords_count", "country_name_en", "db_name");
        if (!validSortFields.contains(sort)) {
            throw new ValidationException(String.format(
                    "Invalid sort field: '%s'. Valid options: %s", sort, validSortFields
            ));
        }

        // Validate order parameter
        String order = (String) arguments.getOrDefault("order", "desc");
        Set<String> validOrders = Set.of("asc", "desc");
        if (!validOrders.contains(order)) {
            throw new ValidationException(String.format(
                    "Invalid order: '%s'. Valid options: %s", order, validOrders
            ));
        }
    }
    public static void validateDomainKeywordsRequest(Map<String, Object> arguments)
            throws ValidationException {

        // Validate domain parameter
        Object domainObj = arguments.get("domain");
        if (domainObj == null) {
            throw new ValidationException("Parameter 'domain' is required");
        }

        if (!(domainObj instanceof String)) {
            throw new ValidationException("Parameter 'domain' must be a string");
        }

        String domain = ((String) domainObj).trim();
        if (domain.isEmpty()) {
            throw new ValidationException("Parameter 'domain' cannot be empty");
        }

        if (domain.length() < 4 || domain.length() > 253) {
            throw new ValidationException("Parameter 'domain' must be between 4 and 253 characters");
        }

        // Validate domain format
        if (!DOMAIN_PATTERN.matcher(domain.toLowerCase()).matches()) {
            throw new ValidationException(String.format(
                    "Invalid domain format: '%s'. Expected format: example.com", domain
            ));
        }

        // Normalize domain to lowercase
        arguments.put("domain", domain.toLowerCase());

        // Validate search engine
        String searchEngine = (String) arguments.get("se");
        if (searchEngine == null) {
            throw new ValidationException("Parameter 'se' is required");
        }

        if (!SUPPORTED_SEARCH_ENGINES.contains(searchEngine)) {
            throw new ValidationException(String.format(
                    "Unsupported search engine: '%s'. Supported: %s",
                    searchEngine,
                    SUPPORTED_SEARCH_ENGINES
            ));
        }

        // Validate pagination parameters
        Object pageObj = arguments.get("page");
        if (pageObj != null) {
            try {
                int page = ((Number) pageObj).intValue();
                if (page < 1) {
                    throw new ValidationException("Parameter 'page' must be >= 1");
                }
            } catch (ClassCastException e) {
                throw new ValidationException("Parameter 'page' must be an integer");
            }
        }

        Object sizeObj = arguments.get("size");
        if (sizeObj != null) {
            try {
                int size = ((Number) sizeObj).intValue();
                if (size < 1 || size > 1000) {
                    throw new ValidationException("Parameter 'size' must be between 1 and 1000");
                }
            } catch (ClassCastException e) {
                throw new ValidationException("Parameter 'size' must be an integer");
            }
        }

        // Validate URL parameter
        Object urlObj = arguments.get("url");
        if (urlObj != null) {
            if (!(urlObj instanceof String)) {
                throw new ValidationException("Parameter 'url' must be a string");
            }
            String url = (String) urlObj;
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                throw new ValidationException("Parameter 'url' must be a valid HTTP/HTTPS URL");
            }
        }

        // Validate keyword arrays
        validateKeywordArray(arguments, "keywords", 50);
        validateKeywordArray(arguments, "minusKeywords", 50);

        // Validate sort parameters
        validateSortParameters(arguments);

        // Validate filters
        validateFiltersParameters(arguments);
    }


    /**
     * Validate keyword arrays
     */
    private static void validateKeywordArray(Map<String, Object> arguments, String paramName, int maxItems)
            throws ValidationException {
        Object keywordsObj = arguments.get(paramName);
        if (keywordsObj != null) {
            if (!(keywordsObj instanceof List)) {
                throw new ValidationException(String.format("Parameter '%s' must be an array", paramName));
            }

            @SuppressWarnings("unchecked")
            List<Object> keywords = (List<Object>) keywordsObj;

            if (keywords.size() > maxItems) {
                throw new ValidationException(String.format("Parameter '%s' cannot have more than %d items", paramName, maxItems));
            }

            for (int i = 0; i < keywords.size(); i++) {
                Object keyword = keywords.get(i);
                if (!(keyword instanceof String)) {
                    throw new ValidationException(String.format("All items in '%s' must be strings", paramName));
                }

                String keywordStr = (String) keyword;
                if (keywordStr.trim().isEmpty()) {
                    throw new ValidationException(String.format("Empty keyword found in '%s' at index %d", paramName, i));
                }

                if (keywordStr.length() > 100) {
                    throw new ValidationException(String.format("Keyword in '%s' at index %d exceeds 100 characters", paramName, i));
                }
            }
        }
    }


    /**
     * Validate sort parameters
     */
    private static void validateSortParameters(Map<String, Object> arguments) throws ValidationException {
        Object sortObj = arguments.get("sort");
        if (sortObj != null) {
            if (!(sortObj instanceof Map)) {
                throw new ValidationException("Parameter 'sort' must be an object");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> sort = (Map<String, Object>) sortObj;

            Set<String> validSortFields = Set.of(
                    "position", "region_queries_count", "cost", "traff", "difficulty",
                    "keyword_length", "concurrency", "types", "geo_names", "region_queries_count_wide",
                    "dynamic", "found_results"
            );

            Set<String> validSortOrders = Set.of("asc", "desc");

            for (Map.Entry<String, Object> entry : sort.entrySet()) {
                String field = entry.getKey();
                Object order = entry.getValue();

                if (!validSortFields.contains(field)) {
                    throw new ValidationException(String.format("Invalid sort field: '%s'. Valid fields: %s", field, validSortFields));
                }

                if (!(order instanceof String) || !validSortOrders.contains(order)) {
                    throw new ValidationException(String.format("Invalid sort order for field '%s': '%s'. Valid orders: %s", field, order, validSortOrders));
                }
            }
        }
    }


    /**
     * Validate filters parameters
     */
    private static void validateFiltersParameters(Map<String, Object> arguments) throws ValidationException {
        Object filtersObj = arguments.get("filters");
        if (filtersObj != null) {
            if (!(filtersObj instanceof Map)) {
                throw new ValidationException("Parameter 'filters' must be an object");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> filters = (Map<String, Object>) filtersObj;

            // Validate position filters
            validateIntegerFilter(filters, "position", 1, 100);
            validateIntegerFilter(filters, "position_from", 1, 100);
            validateIntegerFilter(filters, "position_to", 1, 100);

            // Validate cost filters
            validateNumberFilter(filters, "cost", 0.0, null);
            validateNumberFilter(filters, "cost_from", 0.0, null);
            validateNumberFilter(filters, "cost_to", 0.0, null);

            // Validate difficulty filters
            validateNumberFilter(filters, "difficulty", 0.0, 100.0);
            validateNumberFilter(filters, "difficulty_from", 0.0, 100.0);
            validateNumberFilter(filters, "difficulty_to", 0.0, 100.0);

            // Validate concurrency filters
            validateIntegerFilter(filters, "concurrency", 1, 100);
            validateIntegerFilter(filters, "concurrency_from", 1, 100);
            validateIntegerFilter(filters, "concurrency_to", 1, 100);

            // Validate volume filters
            validateIntegerFilter(filters, "region_queries_count", 0, null);
            validateIntegerFilter(filters, "region_queries_count_from", 0, null);
            validateIntegerFilter(filters, "region_queries_count_to", 0, null);

            // Validate traffic filter
            validateIntegerFilter(filters, "traff", 0, null);

            // Validate keyword length
            validateIntegerFilter(filters, "keyword_length", 1, null);

            // Validate intent filters
            validateIntentFilter(filters, "intents_contain");
            validateIntentFilter(filters, "intents_not_contain");
        }
    }


    /**
     * Validate integer filter values
     */
    private static void validateIntegerFilter(Map<String, Object> filters, String filterName, Integer minValue, Integer maxValue)
            throws ValidationException {
        Object filterValue = filters.get(filterName);
        if (filterValue != null) {
            try {
                int value = ((Number) filterValue).intValue();
                if (minValue != null && value < minValue) {
                    throw new ValidationException(String.format("Filter '%s' must be >= %d", filterName, minValue));
                }
                if (maxValue != null && value > maxValue) {
                    throw new ValidationException(String.format("Filter '%s' must be <= %d", filterName, maxValue));
                }
            } catch (ClassCastException e) {
                throw new ValidationException(String.format("Filter '%s' must be an integer", filterName));
            }
        }
    }

    /**
     * Validate number filter values
     */
    private static void validateNumberFilter(Map<String, Object> filters, String filterName, Double minValue, Double maxValue)
            throws ValidationException {
        Object filterValue = filters.get(filterName);
        if (filterValue != null) {
            try {
                double value = ((Number) filterValue).doubleValue();
                if (minValue != null && value < minValue) {
                    throw new ValidationException(String.format("Filter '%s' must be >= %s", filterName, minValue));
                }
                if (maxValue != null && value > maxValue) {
                    throw new ValidationException(String.format("Filter '%s' must be <= %s", filterName, maxValue));
                }
            } catch (ClassCastException e) {
                throw new ValidationException(String.format("Filter '%s' must be a number", filterName));
            }
        }
    }

    /**
     * Validate intent filter values
     */
    private static void validateIntentFilter(Map<String, Object> filters, String filterName) throws ValidationException {
        Object filterValue = filters.get(filterName);
        if (filterValue != null) {
            if (!(filterValue instanceof List)) {
                throw new ValidationException(String.format("Filter '%s' must be an array", filterName));
            }

            @SuppressWarnings("unchecked")
            List<Object> intents = (List<Object>) filterValue;

            Set<String> validIntents = Set.of("informational", "navigational", "commercial", "transactional");

            for (Object intent : intents) {
                if (!(intent instanceof String) || !validIntents.contains(intent)) {
                    throw new ValidationException(String.format("Invalid intent in '%s': '%s'. Valid intents: %s", filterName, intent, validIntents));
                }
            }
        }
    }

}