package com.jamify_engine.engine.repository;

import com.jamify_engine.engine.models.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    /**
     * Checks if a user exists by their email.
     *
     * @param email the email to check
     * @return true if a user with the given email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by their email.
     *
     * @param email the email to search for
     * @return the UserEntity with the given email, or null if no user is found
     */
    UserEntity findByEmail(String email);

    /**
     * Find a user from its provider id
     * @param userProviderId -
     * @return The corresponding user
     */
    Optional<UserEntity> findByUserProviderId(String userProviderId);
}
