package com.jamify_engine.engine.service.jams;

import com.jamify_engine.engine.exceptions.jam.JamNotFoundException;
import com.jamify_engine.engine.exceptions.security.JamAlreadyRunning;
import com.jamify_engine.engine.exceptions.security.UnauthorizedException;
import com.jamify_engine.engine.models.dto.JamDTO;
import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.models.entities.JamEntity;
import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.models.enums.JamStatusEnum;
import com.jamify_engine.engine.models.mappers.JamMapper;
import com.jamify_engine.engine.models.vms.JamInstantLaunching;
import com.jamify_engine.engine.repository.JamRepository;
import com.jamify_engine.engine.service.implementations.SpotifyJamStrategy;
import com.jamify_engine.engine.service.interfaces.UserService;
import com.jamify_engine.engine.utils.Constants;
import com.jamify_engine.engine.utils.TestsUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpotifyJamStrategyTest {
    @Mock
    private UserService userService;
    @Mock
    private JamRepository jamRepository;
    @Mock
    private JamMapper jamMapper;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private SpotifyJamStrategy spotifyJamStrategy;
    private static final String TEST_EMAIL = "test@test.com";
    private static final Long USER_ID = 1L;
    private static final String TEST_NAME = "Test Jam";

    @BeforeEach
    void setUp() {
        spotifyJamStrategy = new SpotifyJamStrategy(userService, jamRepository, jamMapper);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(TEST_EMAIL);
    }

    @Test
    void launchAJam_ThrowsJamAlreadyRunning() {
        // Arrange
        UserEntity user = createTestUser();
        user.setHasJamRunning(true);

        when(userService.findEntityByEmail(TEST_EMAIL)).thenReturn(user);
        when(userService.findByEmail(TEST_EMAIL)).thenReturn(createUserDTO(user, null));

        JamInstantLaunching jamLaunching = JamInstantLaunching.builder()
                .name(TEST_NAME)
                .themes(List.of("Rock"))
                .build();

        // Act & Assert
        assertThrows(JamAlreadyRunning.class, () ->
                spotifyJamStrategy.launchAJam(jamLaunching)
        );
    }

    @Test
    void stopAJam_Success() {
        // Arrange
        UserEntity user = createTestUser();
        JamEntity jam = createTestJam(user);

        when(jamRepository.findById(anyLong())).thenReturn(Optional.of(jam));
        when(userService.findEntityByEmail(TEST_EMAIL)).thenReturn(user);

        // Act
        spotifyJamStrategy.stopAJam(1L);

        // Assert
        verify(jamRepository).save(any(JamEntity.class));
        verify(userService).update(eq(USER_ID), any(UserEntity.class));
        assertEquals(JamStatusEnum.STOPPED, jam.getStatus());
        assertFalse(user.isHasJamRunning());
    }

    @Test
    void stopAJam_ThrowsUnauthorized() {
        // Arrange
        UserEntity user = createTestUser();
        UserEntity otherUser = createTestUser();
        otherUser.setId(2L);
        JamEntity jam = createTestJam(otherUser);

        when(jamRepository.findById(anyLong())).thenReturn(Optional.of(jam));
        when(userService.findEntityByEmail(TEST_EMAIL)).thenReturn(user);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () ->
                spotifyJamStrategy.stopAJam(1L)
        );
    }

    @Test
    void findRunningJamForUser_Success() {
        // Arrange
        UserEntity user = createTestUser();
        JamEntity expectedJam = createTestJam(user);

        when(userService.findEntityByEmail(TEST_EMAIL)).thenReturn(user);
        when(jamRepository.findFirstByHost_IdAndStatus(USER_ID, JamStatusEnum.RUNNING))
                .thenReturn(Optional.of(expectedJam));

        // Act
        JamEntity result = spotifyJamStrategy.findRunningJamForUser();

        // Assert
        assertNotNull(result);
        assertEquals(expectedJam.getId(), result.getId());
        assertEquals(JamStatusEnum.RUNNING, result.getStatus());
    }

    @Test
    void findRunningJamForUser_ThrowsJamNotFound() {
        // Arrange
        UserEntity user = createTestUser();

        when(userService.findEntityByEmail(TEST_EMAIL)).thenReturn(user);
        when(jamRepository.findFirstByHost_IdAndStatus(USER_ID, JamStatusEnum.RUNNING))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(JamNotFoundException.class, () ->
                spotifyJamStrategy.findRunningJamForUser()
        );
    }

    private UserEntity createTestUser() {
        UserEntity user = new UserEntity();
        user.setId(USER_ID);
        user.setEmail(TEST_EMAIL);
        user.setName("Test User");
        user.setCountry("FR");
        user.setProvider("spotify");
        user.setUserProviderId("spotify123");
        user.setImgUrl("https://example.com/img.jpg");
        user.setRoles(Set.of("USER"));
        user.setHostedJams(new HashSet<>());
        user.setPlaylists(new HashSet<>());
        return user;
    }

    private JamEntity createTestJam(UserEntity host) {
        return JamEntity.builder()
                .id(1L)
                .name(TEST_NAME)
                .host(host)
                .hostId(host.getId())
                .status(JamStatusEnum.RUNNING)
                .schedStart(LocalDateTime.now())
                .tags(new HashSet<>())
                .messages(new HashSet<>())
                .participants(new HashSet<>())
                .build();
    }

    private UserDTO createUserDTO(UserEntity user, JamDTO jamDTO) {
        List<JamDTO> jams = jamDTO != null ? List.of(jamDTO) : List.of();
        return UserDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .country(user.getCountry())
                .provider(user.getProvider())
                .imgUrl(user.getImgUrl())
                .userProviderId(user.getUserProviderId())
                .roles(user.getRoles())
                .jams(jams)
                .hasJamRunning(user.isHasJamRunning())
                .build();
    }
}