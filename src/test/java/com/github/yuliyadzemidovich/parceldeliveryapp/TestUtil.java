package com.github.yuliyadzemidovich.parceldeliveryapp;

import com.github.yuliyadzemidovich.parceldeliveryapp.entity.Role;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.User;

import java.math.BigDecimal;


public class TestUtil {

    public static final String TEST_USER_1_NAME = "Bob";
    public static final String TEST_USER_2_NAME = "Alice";
    public static final String TEST_USER_1_EMAIL = "bob@email.com";
    public static final String TEST_USER_2_EMAIL = "alice@email.com";
    public static final String TEST_NOT_EXISTING_EMAIL = "fake@email.com";
    public static final String TEST_SUPER_ADMIN_EMAIL = "sa@email.com";
    public static final String TEST_USER_1_PWD = "pw_hash";
    public static final String TEST_USER_2_PWD = "pw_hash2";
    public static final String TEST_SUPER_ADMIN_PWD = "pw_hash3";
    public static final String MOCKED_JWT = "mocked.jwt.token";


    public static User getUser1() {
        User user = new User();
        user.setName(TEST_USER_1_NAME);
        user.setRole(Role.ROLE_USER);
        user.setEmail(TEST_USER_1_EMAIL);
        user.setPassword(TEST_USER_1_PWD);
        return user;
    }

    public static User getUser2() {
        User user = new User();
        user.setName(TEST_USER_2_NAME);
        user.setRole(Role.ROLE_USER);
        user.setEmail(TEST_USER_2_EMAIL);
        user.setPassword(TEST_USER_2_PWD);
        return user;
    }

    public static User getExistingUser() {
        User user = getUser1();
        user.setId(1L);
        return user;
    }

    public static User getSuperAdmin() {
        User user = new User();
        user.setName("SuperAdmin");
        user.setRole(Role.ROLE_SUPER_ADMIN);
        user.setEmail(TEST_SUPER_ADMIN_EMAIL);
        user.setPassword(TEST_SUPER_ADMIN_PWD);
        return user;
    }

    public static boolean areBigDecimalsEqual(String num1, String num2, double precision) {
        return areBigDecimalsEqual(new BigDecimal(num1), new BigDecimal(num2), precision);
    }

    public static boolean areBigDecimalsEqual(BigDecimal num1, BigDecimal num2, double precision) {
        return Math.abs(num1
                .subtract(num2)
                .longValue()) < precision;
    }
}
