package com.jamify_engine.engine.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.security.SecurityTestConfig;
import com.jamify_engine.engine.utils.TestsUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(SecurityTestConfig.class)
//@Sql(value = {"classpath:sql/insert_data_before_tests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = {"classpath:sql/delete_data_and_init_seq.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AuthControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

//    @Mock
//    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
//
//    @Mock
//    private WebClient.RequestHeadersSpec requestHeadersSpec;
//
//    @Mock
//    private WebClient.ResponseSpec responseSpec;

    @Value("${config.uaa.jamify-engine-api-key}")
    private String correctJamifyEngineApiKey;


    @BeforeEach
    void setUp() {
    }

    @Test
    @WithMockUser
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
    @WithMockUser
    void createUser_withInvalidUser_shouldReturn403BadRequest() throws Exception {
        UserDTO userDto = TestsUtils.buildUserDto();

        UserDTO wrongUser = userDto.builder()
                .name(null)
                .build();

        mockMvc.perform(post("/api/v1/users/uaa/create")
                        .contentType("application/json")
                        .header("X-API-KEY", correctJamifyEngineApiKey)
                        .content(new ObjectMapper().writeValueAsString(wrongUser)))

                .andExpect(status().isBadRequest());
    }
}
