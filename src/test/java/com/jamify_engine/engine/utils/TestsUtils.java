package com.jamify_engine.engine.utils;

import com.jamify_engine.engine.models.dto.UserDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestsUtils {

    private static final String ISSUER_URI = "https://test-issuer.com";
    private static final String KEY_ID = "test-key-id";
    private static final String TEST_USER_EMAIL = "test-user@example.com";
    private static final String TEST_EXPIRED_USER_EMAIL = "test-expired-user@example.com";
    private static final String TEST_PROVIDER = "spotify";


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


}
