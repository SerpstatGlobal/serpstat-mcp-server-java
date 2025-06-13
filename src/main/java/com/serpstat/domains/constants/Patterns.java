package com.serpstat.domains.constants;

import java.util.regex.Pattern;

public class Patterns {
    public static final Pattern DOMAIN_PATTERN = Pattern.compile(
            "^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$"

    );
}
