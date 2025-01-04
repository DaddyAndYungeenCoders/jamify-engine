package com.jamify_engine.engine.repository;

import com.jamify_engine.engine.models.entities.UserAccessTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccessTokenRepository extends JpaRepository<UserAccessTokenEntity, Long> {
    UserAccessTokenEntity findByEmailAndProvider(String email, String provider);
}
