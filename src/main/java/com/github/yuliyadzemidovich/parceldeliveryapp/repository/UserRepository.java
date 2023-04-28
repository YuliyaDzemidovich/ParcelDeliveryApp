package com.github.yuliyadzemidovich.parceldeliveryapp.repository;

import com.github.yuliyadzemidovich.parceldeliveryapp.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by his/her email
     * @param email user's email
     * @return found User or null if user not found
     */
    User findByEmail(@NotNull String email);

    /**
     * Checks if user with given email exists.
     * @param email user's email
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(@NotNull String email);
}
