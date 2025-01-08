package com.jamify_engine.engine.utils;

import com.jamify_engine.engine.models.dto.UserAccessTokenDto;
import com.jamify_engine.engine.models.dto.UserDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import static com.jamify_engine.engine.utils.Constants.TEST_PROVIDER;
import static com.jamify_engine.engine.utils.Constants.TEST_USER_EMAIL;

@Slf4j
@Component
public class TestsUtils {

    @PostConstruct
    public void init() {
    }

    public static UserDTO buildUserDto() {
        return UserDTO.builder()
                .name("Test User")
                .email(TEST_USER_EMAIL)
                .country("FR")
                .provider(TEST_PROVIDER)
                .imgUrl("img.png")
                .userProviderId("11111111")
                .jams(null)
                .build();
    }

    public static UserAccessTokenDto buildValidUserAccessTokenDto() {
        return UserAccessTokenDto.builder()
                .email(TEST_USER_EMAIL)
                .provider(TEST_PROVIDER)
                .accessToken("test")
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();
    }

    public static UserAccessTokenDto buildExpiredUserAccessTokenDto() {
        return UserAccessTokenDto.builder()
                .email(TEST_USER_EMAIL)
                .provider(TEST_PROVIDER)
                .accessToken("test")
                .expiresAt(LocalDateTime.now().minusDays(1))
                .build();
    }

}
