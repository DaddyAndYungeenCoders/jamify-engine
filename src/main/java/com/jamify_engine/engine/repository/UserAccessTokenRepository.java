package com.jamify_engine.engine.repository;

import com.jamify_engine.engine.models.entities.UserAccessTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserAccessTokenRepository extends JpaRepository<UserAccessTokenEntity, Long> {

    @Query("SELECT u FROM user_access_tokens u LEFT JOIN FETCH u.user WHERE u.provider = ?2 AND u.user.email = ?1")
    UserAccessTokenEntity findByEmailAndProvider(String email, String provider);

    UserAccessTokenEntity findByUserEmail(String email);
}
