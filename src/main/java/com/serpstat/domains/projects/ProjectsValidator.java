package com.serpstat.domains.projects;

import com.serpstat.core.ValidationException;
import java.util.Map;
import java.util.Set;

/**
 * Validator for projects-related requests
 */
public class ProjectsValidator {

    private static final Set<Integer> VALID_PAGE_SIZES = Set.of(20, 50, 100, 200, 500);

    /**
     * Validate getProjects request parameters
     */
    public static void validateProjectsListRequest(Map<String, Object> arguments)
            throws ValidationException {

        // Validate page parameter
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

        // Validate size parameter
        Object sizeObj = arguments.get("size");
        if (sizeObj != null) {
            try {
                int size = ((Number) sizeObj).intValue();
                if (!VALID_PAGE_SIZES.contains(size)) {
                    throw new ValidationException(String.format(
                            "Parameter 'size' must be one of: %s", VALID_PAGE_SIZES
                    ));
                }
            } catch (ClassCastException e) {
                throw new ValidationException("Parameter 'size' must be an integer");
            }
        }
    }
}

