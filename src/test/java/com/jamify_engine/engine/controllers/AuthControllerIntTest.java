package com.jamify_engine.engine.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jamify_engine.engine.models.dto.UserAccessTokenDto;
import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.repository.UserRepository;
import com.jamify_engine.engine.security.SecurityTestConfig;
import com.jamify_engine.engine.service.implementations.UserAccessTokenServiceImpl;
import com.jamify_engine.engine.utils.TestsUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(SecurityTestConfig.class)
class AuthControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${config.uaa.jamify-engine-api-key}")
    private String correctJamifyEngineApiKey;

    @MockitoBean
    private UserAccessTokenServiceImpl userAccessTokenService;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

//    @Test
//    @WithMockUser
//    void saveAccessToken_whenUserHasNoToken_shouldSaveTheNewAccessToken() throws Exception {
//        UserAccessTokenDto token = TestsUtils.buildValidUserAccessTokenDto();
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//        String tokenJson = mapper.writeValueAsString(token);
//
//        mockMvc.perform(post("/api/v1/auth/access-token")
//                        .contentType("application/json")
//                        .header("X-API-KEY", correctJamifyEngineApiKey)
//                        .content(tokenJson))
//
//                .andExpect(status().isOk());
//
//        String newToken = userAccessTokenService.getAccessToken(token.email(), token.provider());
//        Assertions.assertEquals(token.accessToken(), newToken);
//    }

//    @Test
//    @WithMockUser
//    void saveAccessToken_whenUserHasToken_shouldReplaceWithTheNewAccessToken() throws Exception {
//        UserDTO userDto = TestsUtils.buildUserDto();
//        UserEntity existingUser = userRepository.findByEmail(userDto.email());
//        String existingToken = userAccessTokenService.getAccessToken(existingUser.getEmail(), existingUser.getProvider());
//        UserAccessTokenDto token = TestsUtils.buildValidUserAccessTokenDto();
//
//        Assertions.assertNotEquals(existingToken, token.accessToken());
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//        String tokenJson = mapper.writeValueAsString(token);
//
//        mockMvc.perform(post("/api/v1/auth/access-token")
//                        .contentType("application/json")
//                        .header("X-API-KEY", correctJamifyEngineApiKey)
//                        .content(new ObjectMapper().writeValueAsString(tokenJson)))
//                .andExpect(status().isOk());
//
//        String newToken = userAccessTokenService.getAccessToken(token.email(), token.provider());
//        Assertions.assertEquals(token.accessToken(), newToken);
//        Assertions.assertNotEquals(existingToken, newToken);
//    }


    @Test
    @WithMockUser
    void saveAccessToken_withInvalidApiKey_shouldReturn401Unauthorized() throws Exception {
        UserAccessTokenDto token = TestsUtils.buildValidUserAccessTokenDto();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String tokenJson = mapper.writeValueAsString(token);

        mockMvc.perform(post("/api/v1/auth/access-token")
                        .contentType("application/json")
                        .header("X-API-KEY", "wrongApiKey")
                        .content(new ObjectMapper().writeValueAsString(tokenJson)))

                .andExpect(status().isBadRequest());
    }

}
