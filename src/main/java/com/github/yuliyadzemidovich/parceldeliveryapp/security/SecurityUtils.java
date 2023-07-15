package com.github.yuliyadzemidovich.parceldeliveryapp.security;

import java.util.UUID;

public class SecurityUtils {

    /**
     * If input is valid UUID, does nothing.
     * Otherwise generates new UUID string.
     *
     * @param reqUuid input that claims to be UUID string
     * @return UUID string
     */
    public static String getOrGenerateUuid(String reqUuid) {
        if (isValidUuid(reqUuid)) {
            return reqUuid;
        } else {
            return UUID.randomUUID().toString();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static boolean isValidUuid(String input) {
        if (input == null) {
            return false;
        }
        try {
            UUID.fromString(input).toString();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
