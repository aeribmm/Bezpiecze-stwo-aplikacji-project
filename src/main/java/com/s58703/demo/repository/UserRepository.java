package com.s58703.demo.repository;

import com.s58703.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository
 * Data access layer for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     * @param username Username
     * @return Optional<User>
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     * @param email Email address
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username or email
     * @param username Username
     * @param email Email
     * @return Optional<User>
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Check if username exists
     * @param username Username
     * @return true if exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     * @param email Email address
     * @return true if exists
     */
    boolean existsByEmail(String email);
}