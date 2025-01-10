package com.jamify_engine.engine.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamify_engine.engine.config.JacksonConfig;
import com.jamify_engine.engine.models.dto.event.EventCreateDTO;
import com.jamify_engine.engine.models.dto.event.EventDTO;
import com.jamify_engine.engine.models.entities.EventEntity;
import com.jamify_engine.engine.models.entities.EventStatus;
import com.jamify_engine.engine.security.SecurityTestConfig;
import com.jamify_engine.engine.service.implementations.EventServiceImpl;
import com.jamify_engine.engine.service.interfaces.UserService;
import com.jamify_engine.engine.utils.TestsUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;

import static com.jamify_engine.engine.utils.Constants.TEST_USER_EMAIL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({SecurityTestConfig.class, JacksonConfig.class})
@Sql(value = {"classpath:sql/insert_data_before_tests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"classpath:sql/delete_data_and_init_seq.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class EventControllerIntTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EventServiceImpl eventService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        // mock authenticated with test-user@example.com
        SecurityContextHolder.getContext().setAuthentication(TestsUtils.mockAuthenticationTrue());
    }

    @Test
    void createEvent_withValidEvent_shouldReturnTheNewEventScheduledAndUserAsHost() throws Exception {
        EventCreateDTO eventCreateDto = TestsUtils.buildEventCreateDto();

        mockMvc.perform(post("/api/v1/events/createHostedEvent")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(eventCreateDto)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value(EventStatus.SCHEDULED.getStatus()))
                .andExpect(jsonPath("$.participants[0].email").value(TEST_USER_EMAIL))
                .andExpect(jsonPath("$.name").value("Test Event"));

        EventDTO newEvent = eventService.findById(3L);
        Set<EventEntity> hostedEvents = eventService.findAllByHostId(1L);

        Assertions.assertNotNull(hostedEvents);
        Assertions.assertEquals(1, hostedEvents.size());
        Assertions.assertEquals("Test Event", hostedEvents.iterator().next().getName());

        Assertions.assertNotNull(newEvent);
        Assertions.assertEquals("Test Event", newEvent.getName());
        Assertions.assertEquals(EventStatus.SCHEDULED, newEvent.getStatus());
        Assertions.assertEquals(1, newEvent.getParticipants().size());
        Assertions.assertEquals("Test User", newEvent.getParticipants().iterator().next().getUsername());
    }

    @Test
    void createEvent_withIncompleteAddress_shouldReturnBadRequest() throws Exception {
        EventCreateDTO eventCreateDto = TestsUtils.buildEventCreateDto();
        eventCreateDto.getAddress().setCity(null);  // Adresse incomplète

        mockMvc.perform(post("/api/v1/events/createHostedEvent")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(eventCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEvent_withPastDate_shouldReturnBadRequest() throws Exception {
        EventCreateDTO eventCreateDto = TestsUtils.buildEventCreateDto();
        eventCreateDto.setScheduledStart(LocalDateTime.now().minusDays(1));

        mockMvc.perform(post("/api/v1/events/createHostedEvent")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(eventCreateDto)))
                .andExpect(status().isBadRequest());
    }


}
