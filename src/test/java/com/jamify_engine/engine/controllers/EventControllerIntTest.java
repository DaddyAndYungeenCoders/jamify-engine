package com.jamify_engine.engine.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamify_engine.engine.config.JacksonConfig;
import com.jamify_engine.engine.models.dto.event.EventCreateDTO;
import com.jamify_engine.engine.models.dto.event.EventDTO;
import com.jamify_engine.engine.models.dto.event.EventParticipantDTO;
import com.jamify_engine.engine.models.entities.EventEntity;
import com.jamify_engine.engine.models.entities.EventStatus;
import com.jamify_engine.engine.models.mappers.EventMapper;
import com.jamify_engine.engine.security.SecurityTestConfig;
import com.jamify_engine.engine.service.implementations.EventServiceImpl;
import com.jamify_engine.engine.service.interfaces.UserService;
import com.jamify_engine.engine.utils.TestsUtils;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import javax.xml.transform.Result;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.jamify_engine.engine.utils.Constants.TEST_USER_EMAIL;
import static com.jamify_engine.engine.utils.Constants.TEST_USER_EMAIL_2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @Autowired
    private EventMapper eventMapper;

    @BeforeEach
    void setUp() {
        // mock authenticated with test-user@example.com
        SecurityContextHolder.getContext().setAuthentication(TestsUtils.mocktestUser1Authenticated());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createEvent_withValidEvent_shouldReturnTheNewEventScheduledAndUserAsHost() throws Exception {
        EventCreateDTO eventCreateDto = TestsUtils.buildEventCreateDto();
        EventDTO eventBefore = eventService.findById(3L);

        // 3L : scheduled event, test user is host, check if test user is participant
        ArrayList<String> expectedParticipants = new ArrayList<>();
        expectedParticipants.add(TEST_USER_EMAIL);
        Assertions.assertArrayEquals(expectedParticipants.toArray(), eventBefore.getParticipants().stream().map(EventParticipantDTO::getEmail).toArray());

        mockMvc.perform(post("/api/v1/events/createHostedEvent")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(eventCreateDto)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value(EventStatus.SCHEDULED.getStatus()))
                .andExpect(jsonPath("$.participants[0].email").value(TEST_USER_EMAIL))
                .andExpect(jsonPath("$.name").value("New Test Event"));

        EventDTO newEvent = eventService.findById(4L);

        List<EventEntity> hostedEvents = eventService.findAllByHostId(1L)
                .stream()
                .map(eventMapper::toEntity)
                .collect(Collectors.toList());

        // 1L : test user, hosted 2 events now
        Assertions.assertNotNull(hostedEvents);
        Assertions.assertEquals(2, hostedEvents.size());
        // check if the new event is in the hosted events
        Assertions.assertTrue(hostedEvents.stream().anyMatch(event -> event.getId().equals(newEvent.getId())));

        Assertions.assertNotNull(newEvent);
        Assertions.assertEquals("New Test Event", newEvent.getName());
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

    @Test
    void joinEvent_withAvailableEvent_shouldReturnTheEventWithUserAsParticipant() throws Exception {
        // 2L : past event
        // 3L : future event
        Long availableEventId = 3L;

        //mock another user to join the event
        SecurityContextHolder.getContext().setAuthentication(TestsUtils.mocktestUser2Authenticated());

        ResultActions resultActions = mockMvc.perform(post("/api/v1/events/join/" + availableEventId))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();

        EventDTO event = objectMapper.readValue(contentAsString, EventDTO.class);
        Assertions.assertNotNull(event);
        Assertions.assertEquals(availableEventId, event.getId());
        Assertions.assertEquals(2, event.getParticipants().size());
        assertThat(event.getParticipants().stream()
                .map(EventParticipantDTO::getEmail)
                .toList(), containsInAnyOrder(TEST_USER_EMAIL, TEST_USER_EMAIL_2));
    }

    @Test
    void joinPastEvent_shouldReturnBadRequest() throws Exception {
        // 2L : past event
        Long pastEventId = 2L;

        mockMvc.perform(post("/api/v1/events/join/" + pastEventId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Event has already finished."));

    }

    @Test
    void joinEvent_withAlreadyJoinedEvent_shouldReturnBadRequest() throws Exception {
        // 3L : future event, but logged in user is already a participant (and host)
        Long availableEventId = 3L;

        mockMvc.perform(post("/api/v1/events/join/" + availableEventId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User is already a participant of this event."));
    }

    @Test
    void getAllEvent_shouldReturnAllEvents() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        Set<EventDTO> events = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });

        Assertions.assertNotNull(events);
        Assertions.assertEquals(3, events.size());
        Assertions.assertEquals(1, events.stream().filter(event -> event.getParticipants().size() == 1).count());
        Assertions.assertNotNull(events.stream().filter(event -> Objects.equals(event.getName(), "test-event")).findFirst().get());
        Assertions.assertNotNull(events.stream().filter(event -> Objects.equals(event.getName(), "past-event")).findFirst().get());
        Assertions.assertNotNull(events.stream().filter(event -> Objects.equals(event.getName(), "scheduled-event")).findFirst().get());
    }

    @Test
    void getEventsByStatus_shouldReturnEventsWithStatus() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/v1/events/with-status/SCHEDULED"))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        List<EventDTO> events = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });

        Assertions.assertNotNull(events);
        Assertions.assertEquals(2, events.size());
        Assertions.assertNotNull(events.stream().filter(event -> Objects.equals(event.getName(), "scheduled-event")).findFirst().get());
    }

    @Test
    void getEventsByStatus_withUnexistingStatus_shouldReturn400() throws Exception {
        mockMvc.perform(get("/api/v1/events/with-status/NOTVALID"))
                .andExpect(status().isBadRequest());

    }

    @Test
    void getEventsByHostId_shouldReturnEventsWithHostId() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/v1/events/by-host/1"))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        List<EventDTO> events = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });

        Assertions.assertNotNull(events);
        Assertions.assertEquals(1, events.size());
        Assertions.assertNotNull(events.stream().filter(event -> Objects.equals(event.getName(), "scheduled-event")).findFirst().get());
    }

    // comme ca on peut pas faire un test de tous les id qui existent si qqn cherche a le faire
    @Test
    void getEventsByHostId_withUnexistingHostId_shouldReturnHttpOkStatusAndEmptyList() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/v1/events/by-host/999"))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        List<EventDTO> events = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });

        Assertions.assertNotNull(events);
        Assertions.assertEquals(0, events.size());
    }

    @Test
    void getEventsByHostId_withHostNotHostingEvent_shouldReturnEmptyList() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/v1/events/by-host/2"))
                .andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        List<EventDTO> events = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });

        Assertions.assertNotNull(events);
        Assertions.assertEquals(0, events.size());
    }

}
