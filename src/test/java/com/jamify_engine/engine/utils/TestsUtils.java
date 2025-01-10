package com.jamify_engine.engine.utils;

import com.jamify_engine.engine.models.dto.event.EventCreateDTO;
import com.jamify_engine.engine.models.dto.event.EventDTO;
import com.jamify_engine.engine.models.dto.UserAccessTokenDto;
import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.models.entities.AddressType;
import com.jamify_engine.engine.models.entities.JamEntity;
import com.jamify_engine.engine.models.entities.PlaylistEntity;
import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.models.enums.JamStatusEnum;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.jamify_engine.engine.utils.Constants.*;

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
                .country(FRANCE_ALPHA_CODE2)
                .provider(TEST_PROVIDER)
                .imgUrl("http://example.com/img.jpg")
                .userProviderId("11111111")
                .jams(new ArrayList<>())
                .build();
    }

    public static UserEntity buildUserEntity() {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        Set<JamEntity> jams = new HashSet<>();
        Set<PlaylistEntity> playlists = new HashSet<>();
        return new UserEntity(
                1L,
                TEST_USER_EMAIL,
                TEST_USER_NAME,
                roles,
                FRANCE_ALPHA_CODE2,
                TEST_PROVIDER,
                "user provider id",
                "img.png",
                null,
                false,
                jams,
                playlists,
                null,
                new HashSet<>(),
                new HashSet<>()
        );
    }

    public static JamEntity buildJamEntity(JamStatusEnum jamStatusEnum) {
        return new JamEntity(
                1L,
                buildUserEntity(),
                1L,
                JAM_NAME_TEST,
                LocalDateTime.of(2024, 12, 31, 12, 0),
                jamStatusEnum,
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>()
        );
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

    public static EventCreateDTO buildEventCreateDto() {
        EventCreateDTO eventCreateDTO = new EventCreateDTO();
        eventCreateDTO.setName("Test Event");
        eventCreateDTO.setAddress(buildAddress());
        eventCreateDTO.setScheduledStart(LocalDateTime.of(2024, 12, 31, 12, 0));
        return eventCreateDTO;
    }

    public static AddressType buildAddress() {
        AddressType addressType = new AddressType();
        addressType.setCity("Paris");
        addressType.setCountry("France");
        addressType.setZipCode("75000");
        addressType.setStreet("10 Rue de la Paix");
        return addressType;
    }

}
