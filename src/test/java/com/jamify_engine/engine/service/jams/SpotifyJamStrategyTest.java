package com.jamify_engine.engine.service.jams;

import com.jamify_engine.engine.config.webClient.SpotifyWebClient;
import com.jamify_engine.engine.exceptions.jam.JamNotFoundException;
import com.jamify_engine.engine.exceptions.security.JamAlreadyRunning;
import com.jamify_engine.engine.exceptions.security.UnauthorizedException;
import com.jamify_engine.engine.models.dto.JamDTO;
import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.models.entities.JamEntity;
import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.models.enums.JamStatusEnum;
import com.jamify_engine.engine.models.mappers.JamMapper;
import com.jamify_engine.engine.models.mappers.MusicMapper;
import com.jamify_engine.engine.models.vms.JamInstantLaunching;
import com.jamify_engine.engine.repository.JamRepository;
import com.jamify_engine.engine.service.implementations.SpotifyJamStrategy;
import com.jamify_engine.engine.service.interfaces.MusicService;
import com.jamify_engine.engine.service.interfaces.UserAccessTokenService;
import com.jamify_engine.engine.service.interfaces.UserService;
import com.jamify_engine.engine.utils.TestsUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.jamify_engine.engine.utils.Constants.ACCESS_TOKEN_TEST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    @Mock
    private WebClient spotifyWebClient;
    @Mock
    UserAccessTokenService userAccessTokenService;
    @Mock
    private MusicService musicService;
    @Mock
    private MusicMapper musicMapper;

    private SpotifyJamStrategy spotifyJamStrategy;
    private static final String TEST_EMAIL = "test@test.com";
    private static final Long USER_ID = 1L;
    private static final String TEST_NAME = "Test Jam";
    private static final JamEntity jamEntity = TestsUtils.buildJamEntity(JamStatusEnum.RUNNING);
    private static UserEntity userEntityShared = TestsUtils.buildUserEntity();

    @BeforeEach
    void setUp() {
        spotifyJamStrategy = new SpotifyJamStrategy(userService, jamRepository, jamMapper, musicService, musicMapper, spotifyWebClient, userAccessTokenService);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(TEST_EMAIL);
        // To stub the email, see the SecurityTestConfig
        lenient().when(userService.findEntityByEmail("test@test.com")).thenReturn(userEntityShared);
        lenient().when(jamRepository.findById(jamEntity.getId())).thenReturn(Optional.of(jamEntity));
        lenient().when(jamRepository.save(jamEntity)).thenReturn(jamEntity);
    }

    @AfterEach
    void teardown() {
        userEntityShared = TestsUtils.buildUserEntity();
    }

    @Test
    void launchAJam_ThrowsJamAlreadyRunning() {
        // Arrange
        UserEntity user = createTestUser();
        user.setHasJamRunning(true);

        when(userService.findEntityByEmail(TEST_EMAIL)).thenReturn(user);

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

    @Test
    @WithMockUser
    void shouldJoinAJam() {
        // Given
        Object principal = ACCESS_TOKEN_TEST;
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        JamEntity jam = TestsUtils.buildJamEntity(JamStatusEnum.RUNNING);
        // When
        spotifyJamStrategy.joinJam(jam.getId());
        // Then
        Assertions.assertNotNull(userEntityShared.getCurrentJam());
    }

    @Test
    @WithMockUser
    void shouldNotBeAbleToJoinAJamIfAlreadyInAnotherRunningOne() {
        // Given
        userEntityShared.setCurrentJam(TestsUtils.buildJamEntity(JamStatusEnum.RUNNING));
        Object principal = ACCESS_TOKEN_TEST;
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        JamEntity jam = TestsUtils.buildJamEntity(JamStatusEnum.RUNNING);

        // When & Then
        Assertions.assertThrows(UnauthorizedException.class, () -> spotifyJamStrategy.joinJam(jam.getId()));
    }

    @Test
    @WithMockUser
    void shouldBeAbleToJoinAJamIfTheUserIsAlreadyInAnotherOneButItIsNotRunning() {
        // Given
        userEntityShared.setCurrentJam(TestsUtils.buildJamEntity(JamStatusEnum.STOPPED));
        Object principal = ACCESS_TOKEN_TEST;
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        JamEntity jam = TestsUtils.buildJamEntity(JamStatusEnum.RUNNING);
        // When
        spotifyJamStrategy.joinJam(jam.getId());
        // Then
        Assertions.assertNotNull(userEntityShared.getCurrentJam());
    }

    @Test
    @WithMockUser
    void shouldBeAbleToLeaveAJam() {
        // Given
        userEntityShared.setCurrentJam(TestsUtils.buildJamEntity(JamStatusEnum.STOPPED));
        Object principal = ACCESS_TOKEN_TEST;
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        JamEntity jam = TestsUtils.buildJamEntity(JamStatusEnum.RUNNING);
        // When
        spotifyJamStrategy.leaveJam(jam.getId());
        // Then
        // asserting that the current jam participant field is empty
        Assertions.assertEquals(jamEntity.getParticipants(), new HashSet<>());
        Assertions.assertNull(userEntityShared.getCurrentJam());
    }

    @Test
    @WithMockUser
    void shouldNotBeAbleToLeaveAJam() {
        // Given
        Object principal = ACCESS_TOKEN_TEST;
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        JamEntity jam = TestsUtils.buildJamEntity(JamStatusEnum.RUNNING);
        // When & Then
        Assertions.assertThrows(UnauthorizedException.class, () -> spotifyJamStrategy.leaveJam(jam.getId()));
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