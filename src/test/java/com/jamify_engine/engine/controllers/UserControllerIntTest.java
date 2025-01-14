package com.jamify_engine.engine.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.security.SecurityTestConfig;
import com.jamify_engine.engine.service.interfaces.UserService;
import com.jamify_engine.engine.utils.TestsUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(SecurityTestConfig.class)
@Sql(value = {"classpath:sql/insert_data_before_tests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"classpath:sql/delete_data_and_init_seq.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${config.uaa.jamify-engine-api-key}")
    private String correctJamifyEngineApiKey;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(TestsUtils.mocktestUser1Authenticated());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
//    @WithMockUser
    void createUser_withValidUser_shouldReturnTheNewUser() throws Exception {
        UserDTO userDto = TestsUtils.buildUserDto();
        mockMvc.perform(post("/api/v1/users/uaa/create")
                        .contentType("application/json")
                        .header("X-API-KEY", correctJamifyEngineApiKey)
                        .content(new ObjectMapper().writeValueAsString(userDto)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(userDto.name()))
                .andExpect(jsonPath("$.email").value(userDto.email()))
                .andExpect(jsonPath("$.country").value(userDto.country()))
                .andExpect(jsonPath("$.provider").value(userDto.provider()))
                .andExpect(jsonPath("$.imgUrl").value(userDto.imgUrl()))
                .andExpect(jsonPath("$.userProviderId").value(userDto.userProviderId()))
                .andExpect(jsonPath("$.jams").value(userDto.jams()));
    }

    @Test
    void createUser_withValidUserAndNewProvider_shouldReturnTheUpdatedNewUser() throws Exception {
        UserDTO userDto = TestsUtils.buildUserDto();

        UserDTO updatedUserDto = UserDTO.builder()
                .name(userDto.name())
                .email(userDto.email())
                .country(userDto.country())
                .provider("newProvider")
                .imgUrl("http://example")
                .userProviderId("11111111")
                .jams(userDto.jams())
                .build();

        mockMvc.perform(post("/api/v1/users/uaa/create")
                        .contentType("application/json")
                        .header("X-API-KEY", correctJamifyEngineApiKey)
                        .content(new ObjectMapper().writeValueAsString(updatedUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedUserDto.name()))
                .andExpect(jsonPath("$.email").value(updatedUserDto.email()))
                .andExpect(jsonPath("$.country").value(updatedUserDto.country()))
                .andExpect(jsonPath("$.provider").value("newProvider"))
                .andExpect(jsonPath("$.imgUrl").value("http://example"))
                .andExpect(jsonPath("$.userProviderId").value("11111111"))
                .andExpect(jsonPath("$.jams").value(updatedUserDto.jams()));

        UserEntity updatedUser = userService.findEntityByEmail(updatedUserDto.email());

        assert updatedUser != null;
        Assertions.assertEquals(updatedUser.getProvider(), "newProvider");
        Assertions.assertEquals(updatedUser.getImgUrl(), "http://example");
        Assertions.assertEquals(updatedUser.getUserProviderId(), "11111111");
    }
/*
    @Test
    @WithMockUser
    void createUser_withInvalidUser_shouldReturn403BadRequest() throws Exception {
        UserDTO wrongUser = UserDTO.builder()
                .name(null)
                .build();

        mockMvc.perform(post("/api/v1/users/uaa/create")
                        .contentType("application/json")
                        .header("X-API-KEY", correctJamifyEngineApiKey)
                        .content(new ObjectMapper().writeValueAsString(wrongUser)))

                .andExpect(status().isBadRequest());
    }
*/
}
