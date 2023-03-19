package com.github.yuliyadzemidovich.parceldeliveryapp.entity;

import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represent roles for this application.
 * {@link #securityValue} is meant for use with Spring Security and to store it in the database;
 * {@link #shortValue} is meanth for use in the request for create or manage users - this is the value to be shown to the client.
 */
@Getter
public enum Role {

    // only one super admin - full access to this app
    ROLE_SUPER_ADMIN("ROLE_SUPER_ADMIN", "super admin"),

    // regular user which can order deliveries
    ROLE_USER("ROLE_USER", "user"),

    // admin manages couriers work
    ROLE_ADMIN("ROLE_ADMIN", "admin"),

    // courier handles the deliveries to users
    ROLE_COURIER("ROLE_COURIER", "courier");

    private final String securityValue;
    private final String shortValue;

    Role(String securityValue, String shortValue) {
        this.securityValue = securityValue;
        this.shortValue = shortValue;
    }

    public static Optional<Role> findRoleByShortValue(String shortValue) {
        return Stream.of(Role.values())
                .filter(s -> s.getShortValue().equals(shortValue))
                .findFirst();
    }
}
