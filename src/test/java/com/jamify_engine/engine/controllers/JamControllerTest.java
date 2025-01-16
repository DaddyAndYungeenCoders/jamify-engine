package com.jamify_engine.engine.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamify_engine.engine.controllers.jam.JamController;
import com.jamify_engine.engine.models.dto.MusicDTO;
import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.models.vms.JamInstantLaunching;
import com.jamify_engine.engine.security.SecurityTestConfig;
import com.jamify_engine.engine.service.interfaces.IJamStrategy;
import com.jamify_engine.engine.utils.TestsUtils;
import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JamController.class)
@Import(SecurityTestConfig.class)
@ActiveProfiles("test")
public class JamControllerTest {
    private final Long TEST_MUSIC_ID = 2L;
    private final Long TEST_JAM_ID = 2L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IJamStrategy mockedJamStrategy;

    @BeforeEach
    public void setup() throws ExecutionControl.NotImplementedException {
        doNothing().when(mockedJamStrategy).joinJam(anyLong());
        doNothing().when(mockedJamStrategy).leaveJam(anyLong());
        doNothing().when(mockedJamStrategy).playMusic(anyLong(), anyLong());
        when(mockedJamStrategy.getAllInQueue(anyLong())).thenReturn(buildQueue());
    }

    private List<MusicDTO> buildQueue() {
        List<MusicDTO> musicDTOS = new ArrayList<>();
        MusicDTO mockMusicContent = new MusicDTO(TEST_MUSIC_ID, "", "", "", "", "", "", new HashSet<>());
        musicDTOS.add(mockMusicContent);
        return musicDTOS;
    }

    @Test
    @WithMockUser
    public void shouldTryToJoinAJam() throws Exception {
        // GIVEN
        // WHEN
        mockMvc.perform(put("/api/jams/join/{jamId}", TEST_JAM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());
        // THEN
        verify(mockedJamStrategy).joinJam(eq(TEST_JAM_ID));
    }

    @Test
    @WithMockUser
    public void shouldTryToLeaveAJam() throws Exception {
        // GIVEN
        // WHEN
        mockMvc.perform(put("/api/jams/leave/{jamId}", TEST_JAM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());
        // THEN
        verify(mockedJamStrategy).leaveJam(eq(TEST_JAM_ID));
    }

    @Test
    @WithMockUser
    public void shouldTryToGetAllQueuedMusicsInAJam() throws Exception {
        // GIVEN
        List<MusicDTO> expectedResponse = buildQueue();

        // WHEN
        MvcResult result = mockMvc.perform(get("/api/jams/queue/{jamId}", TEST_JAM_ID)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        // THEN
        verify(mockedJamStrategy).getAllInQueue(eq(TEST_JAM_ID));
        String jsonResponse = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        String expectedResponseAsString = objectMapper.writeValueAsString(expectedResponse);
        Assertions.assertEquals(expectedResponseAsString, jsonResponse);
    }

    @Test
    @WithMockUser
    public void shouldTryToPlayAMusicInAJam() throws Exception {
        // GIVEN
        // WHEN
        mockMvc.perform(post("/api/jams/play/{musicId}/{jamId}", TEST_MUSIC_ID, TEST_JAM_ID)
                        .with(csrf()))
                .andExpect(status().isOk());

        // THEN
        verify(mockedJamStrategy).playMusic(eq(TEST_MUSIC_ID), eq(TEST_JAM_ID));
    }

    @Test
    @WithMockUser
    public void shouldLaunchAJam() throws Exception {
        // GIVEN
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        UserEntity mockedUser = TestsUtils.buildUserEntity();
        List<String> themes = new ArrayList<>();
        themes.add("testTheme");
        JamInstantLaunching jamVM = JamInstantLaunching.builder().name("chill").themes(themes).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(jamVM);

        // WHEN
        mockMvc.perform(post("/api/jams/launch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk());

        // THEN
        verify(mockedJamStrategy).launchAJam(jamVM);
    }
}
