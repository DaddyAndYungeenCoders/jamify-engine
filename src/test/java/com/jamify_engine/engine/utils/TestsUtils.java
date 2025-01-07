package com.jamify_engine.engine.utils;

import com.jamify_engine.engine.models.dto.UserDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
}
