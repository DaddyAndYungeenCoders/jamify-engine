package com.jamify_engine.engine.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jamify_engine.engine.controllers.jam.JamController;
import com.jamify_engine.engine.models.dto.JamDTO;
import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.models.enums.JamStatusEnum;
import com.jamify_engine.engine.security.SecurityTestConfig;
import com.jamify_engine.engine.service.implementations.JamStrategy;
import com.jamify_engine.engine.service.interfaces.IJamStrategy;
import com.jamify_engine.engine.utils.TestsUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(SecurityTestConfig.class)
@Sql(value = {"classpath:sql/jamControllerIntTestSqlScripts/populate_db.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(value = {"classpath:sql/jamControllerIntTestSqlScripts/flush_all.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class JamControllerIntTest {
    // TODO more real integration tests one day (¬_¬ )

    @Autowired
    private MockMvc mockMvc;

    @Value("${config.uaa.jamify-engine-api-key}")
    private String correctJamifyEngineApiKey;

    @Autowired
    private IJamStrategy jamStrategy;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(TestsUtils.mocktestUser1Authenticated());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldGetAllJams() throws Exception {
        // Given
        // See populate_db.sql

        // When
        MvcResult result = mockMvc.perform(get("/api/jams/running"))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String content = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<JamDTO> jams = mapper.readValue(
                content,
                mapper.getTypeFactory().constructCollectionType(List.class, JamDTO.class)
        );

        // Vérifications
        Assertions.assertNotNull(jams);
        Assertions.assertEquals(1, jams.size());

        JamDTO runningJam = jams.get(0);
        Assertions.assertEquals("Johns Rock Party", runningJam.name());
        Assertions.assertEquals(JamStatusEnum.RUNNING, runningJam.status());
        Assertions.assertEquals(1L, runningJam.hostId());

        // Vérification des participants
        Assertions.assertEquals(2, runningJam.participants().size());
        Assertions.assertTrue(runningJam.participants().stream()
                .anyMatch(participant -> participant == 1L)); // John
        Assertions.assertTrue(runningJam.participants().stream()
                .anyMatch(participant -> participant == 2L)); // Jane

        // Vérification des tags
        Set<String> expectedThemes = Set.of("Rock", "Pop");
        Assertions.assertEquals(expectedThemes, new HashSet<>(runningJam.themes()));

        // Vérification des messages
        Assertions.assertEquals(3, runningJam.messages().size());
    }
}
