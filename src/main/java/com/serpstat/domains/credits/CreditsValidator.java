package com.serpstat.domains.credits;

import com.serpstat.core.ValidationException;

import java.util.Map;

public class CreditsValidator {
    /**
     * Validate getStats request parameters
     * Note: getStats doesn't require any parameters, but we validate the structure
     */
    public static void validateApiStatsRequest(Map<String, Object> arguments)
            throws ValidationException {

        // getStats doesn't require parameters, but check for unexpected ones
        if (arguments != null && !arguments.isEmpty()) {
            // Log warning but don't fail - some clients might send extra params
            System.err.printf("Warning: getStats received unexpected parameters: %s%n", arguments.keySet());
        }

        // This method is very simple since getStats requires no parameters
        // We keep it for consistency with other validators and future extensions
    }
}
