package com.github.yuliyadzemidovich.parceldeliveryapp;

import com.github.yuliyadzemidovich.parceldeliveryapp.entity.Role;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.User;

public class TestUtil {

    public static final String TEST_USER_1_NAME = "Bob";
    public static final String TEST_USER_2_NAME = "Alice";
    public static final String TEST_USER_1_EMAIL = "bob@email.com";
    public static final String TEST_USER_2_EMAIL = "alice@email.com";
    public static final String TEST_SUPER_ADMIN_EMAIL = "sa@email.com";
    public static final String TEST_USER_1_PWD = "pw_hash";
    public static final String TEST_USER_2_PWD = "pw_hash2";
    public static final String TEST_SUPER_ADMIN_PWD = "pw_hash3";


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

    public static User getSuperAdmin() {
        User user = new User();
        user.setName("SuperAdmin");
        user.setRole(Role.ROLE_SUPER_ADMIN);
        user.setEmail(TEST_SUPER_ADMIN_EMAIL);
        user.setPassword(TEST_SUPER_ADMIN_PWD);
        return user;
    }
}
