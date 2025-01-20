package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.exceptions.common.BadRequestException;
import com.jamify_engine.engine.exceptions.jam.JamNotFoundException;
import com.jamify_engine.engine.exceptions.security.JamAlreadyRunning;
import com.jamify_engine.engine.exceptions.security.UnauthorizedException;
import com.jamify_engine.engine.exceptions.security.UserNotFoundException;
import com.jamify_engine.engine.models.dto.JamDTO;
import com.jamify_engine.engine.models.dto.MusicDTO;
import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.models.entities.JamEntity;
import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.models.enums.JamStatusEnum;
import com.jamify_engine.engine.models.mappers.JamMapper;
import com.jamify_engine.engine.models.mappers.MusicMapper;
import com.jamify_engine.engine.models.vms.JamInstantLaunching;
import com.jamify_engine.engine.repository.JamRepository;
import com.jamify_engine.engine.service.interfaces.IJamStrategy;
import com.jamify_engine.engine.service.interfaces.MusicService;
import com.jamify_engine.engine.service.interfaces.UserService;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public abstract class JamStrategy implements IJamStrategy {
    @Autowired
    protected final UserService userService;

    @Autowired
    protected final JamRepository jamRepository;

    protected final JamMapper mapper;

    @Autowired
    protected final MusicService musicService;

    protected final MusicMapper musicMapper;

    @Override
    public void playMusic(Long musicId, Long jamId) throws ExecutionControl.NotImplementedException {
        JamEntity jam = jamRepository.findById(jamId).orElseThrow(() -> new JamNotFoundException("The jam with id %d could not be found".formatted(jamId)));
        Long currentUserId = getCurrentUser().getId();

        if (!Objects.equals(jam.getHost().getId(), currentUserId) || !JamStatusEnum.RUNNING.equals(jam.getStatus())) {
            throw new UnauthorizedException(currentUserId);
        }

        MusicDTO music = musicService.findById(musicId);
        JamDTO jamDTO = mapper.toDTO(jam);

        if (music == null || jamDTO == null) {
            throw new InternalError();
        }

        specificPlay(music, jamDTO);
    }


    @Override
    public List<MusicDTO> getAllInQueue(Long jamId) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
    }

    @Override
    public void joinJam(Long jamId) {
        UserEntity currentUser = getCurrentUser();

        if (currentUser == null) {
            throw new UnauthorizedException("The current user does not exist, unable to join a jam.");
        }

        checkIfUserIsAllowedToJoinAJam(currentUser);

        // join
        JamEntity jam = jamRepository.findById(jamId).orElseThrow(() -> new JamNotFoundException("The jam id %d is not corresponding to any known jam".formatted(jamId)));

        Set<UserEntity> jamParticipants = jam.getParticipants();

        jamParticipants.add(currentUser);

        jam.setParticipants(jamParticipants);

        JamEntity savedJam = jamRepository.save(jam);

        currentUser.setCurrentJam(savedJam);

        userService.update(currentUser.getId(), currentUser);

        notify(savedJam.getHostId(), "User joining", currentUser.getName());
    }

    @Override
    public void leaveJam(Long jamId) {
        UserEntity currentUser = getCurrentUser();

        if (currentUser == null) {
            throw new UnauthorizedException("The current user does not exist, unable to join a jam.");
        }

        checkIfUserIsAllowedToLeaveAJam(currentUser, jamId);

        JamEntity jam = jamRepository.findById(jamId).orElseThrow(() -> new JamNotFoundException("The jam id %d is not corresponding to any known jam".formatted(jamId)));

        Set<UserEntity> jamParticipants = jam.getParticipants();

        jamParticipants.removeIf(participant -> Objects.equals(participant.getId(), currentUser.getId()));

        jam.setParticipants(jamParticipants);

        JamEntity savedJam = jamRepository.save(jam);

        currentUser.setCurrentJam(null);

        userService.update(currentUser.getId(), currentUser);

        notify(savedJam.getHostId(), "User leaving", currentUser.getName());
    }

    @Override
    public JamDTO create(JamDTO entityToCreate) {
        String email = getCurrentUserEmail();

        if (Objects.equals(userService.findByEmail(email), null)) {
            throw new UserNotFoundException(email);
        }

        UserEntity user = userService.findEntityByEmail(email);

        if (user.isHasJamRunning()) {
            throw new JamAlreadyRunning(user.getName());
        }

        // FIXME use mapper
        JamEntity jamEntity = JamEntity.builder()
                .hostId(user.getId())
                .status(entityToCreate.status())
                .schedStart(entityToCreate.scheduledDate())
                .tags(new HashSet<>())
                .messages(new HashSet<>())
                .participants(new HashSet<>())
                .build();

        UserDTO updatedUser = updateUserWithNewJam(user, jamEntity);

        if (updatedUser == null) {
            throw new BadRequestException("Something went wrong while trying to update the user status");
        }

        JamEntity savedEntity = jamRepository.save(jamEntity);

        return mapper.toDTO(savedEntity);
    }

    @Override
    public JamDTO update(Long id, JamDTO entityToUpdate) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
    }

    @Override
    public void delete(Long entityToDeleteId) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
    }

    @Override
    public JamDTO findById(Long entityId) {
        JamEntity jam = jamRepository.findById(entityId).orElseThrow(() -> new JamNotFoundException("The jam with id %d was not found"));
        return mapper.toDTO(jam);
    }

    @Override
    public List<JamDTO> findAll() {
        return this.mapper.toDTO(this.jamRepository.findAll());
    }

    @Override
    public List<JamDTO> findAllRunningJams() {
        return this.mapper.toDTO(this.jamRepository.findAllByStatus(JamStatusEnum.RUNNING).orElseThrow(() -> new JamNotFoundException("No runnning jam found")));
    }

    @Override
    public JamDTO launchAJam(JamInstantLaunching jamVM) {
        UserEntity user = getUserAndCheckIfUserIsAllowedToLaunchAJam();

        List<UserEntity> participants = Collections.singletonList(user);

        JamEntity newJam = JamEntity.builder()
                .host(user)
                .hostId(user.getId())
                .status(JamStatusEnum.RUNNING)
                .schedStart(LocalDateTime.now())
                .tags(new HashSet<>())
                .messages(new HashSet<>())
                .participants(new HashSet<>(participants))
                .build();

        user.setHasJamRunning(true);

        UserDTO updatedUser = updateUserWithNewJam(user, newJam);

        if (updatedUser == null) {
            throw new BadRequestException("Something went wrong while trying to update the user status");
        }

        // Not returning directly newJam to be sure that the action has been done properly
        return updatedUser.jams()
                .stream()
                .filter(jamDTO ->
                        JamStatusEnum.RUNNING.equals(jamDTO.status())).findFirst().orElseThrow(
                        () -> new JamNotFoundException("Jam not found")
                );
    }

    @Override
    public void stopAJam(Long jamId) {
        JamEntity jam = jamRepository.findById(jamId).orElseThrow(() -> new JamNotFoundException("The jam with id %d was not found"));
        String currentUserEmail = getCurrentUserEmail();
        UserEntity currentUser = userService.findEntityByEmail(currentUserEmail);

        if (!Objects.equals(jam.getHost().getId(), currentUser.getId())) {
            throw new UnauthorizedException("The user with id %d is not authorized to stop the jam with id %d!");
        }

        jam.setStatus(JamStatusEnum.STOPPED);

        jamRepository.save(jam);

        currentUser.setHasJamRunning(false);

        userService.update(currentUser.getId(), currentUser);
    }

    @Override
    public JamEntity findRunningJamForUser() {
        UserEntity currentUser = getCurrentUser();
        return jamRepository.findFirstByHost_IdAndStatus(currentUser.getId(), JamStatusEnum.RUNNING).orElseThrow(
                () -> new JamNotFoundException("The host %d has no jam running".formatted(currentUser.getId()))
        );
    }

    protected UserEntity getCurrentUser() {
        String currentUserEmail = getCurrentUserEmail();
        return userService.findEntityByEmail(currentUserEmail);
    }

    protected UserDTO updateUserWithNewJam(UserEntity user, JamEntity jam) {
        Set<JamEntity> hostedJams = user.getHostedJams();

        hostedJams.add(jam);

        user.setHostedJams(hostedJams);

        return userService.update(user.getId(), user);
    }


    protected String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.toString();
    }

    protected UserEntity getUserAndCheckIfUserIsAllowedToLaunchAJam() {
        UserEntity user = getCurrentUser();

        if (Objects.equals(user, null)) {
            throw new UserNotFoundException(user.getEmail());
        }

        if (user.isHasJamRunning()) {
            throw new JamAlreadyRunning(user.getName());
        }

        return user;
    }

    protected void checkIfUserIsAllowedToJoinAJam(UserEntity user) {
        // Refusing if user is hosting a jam
        if (user.isHasJamRunning()) {
            throw new UnauthorizedException("Unable to join a jam: the user %s is already hosting a jam.".formatted(user.getEmail()));
        }
        // Refusing if user is already listening to a jam
        if (user.getCurrentJam() != null && JamStatusEnum.RUNNING.equals(user.getCurrentJam().getStatus())) {
            throw new UnauthorizedException("Unable to join a jam: the user %s is already listening to a jam.".formatted(user.getEmail()));
        }
    }

    protected void checkIfUserIsAllowedToLeaveAJam(UserEntity user, Long jamId) {
        // Refusing if user is in the jam
        if (!(user.getCurrentJam() != null && Objects.equals(user.getCurrentJam().getId(), jamId))) {
            throw new UnauthorizedException("Unable to leave the jam: the user %s is not listening to this jam.".formatted(user.getEmail()));
        }
    }

    //TODO handle with StreamListeners, set type to an enum, call a notification service
    protected void notify(Long userId, String type, String message) {
        log.debug("Notifying the user %d about %s, notification type: %s".formatted(userId, type, message));
    }

    protected Set<UserEntity> getAllUsersInAJam(JamDTO jam) {
        return userService.findAllEntitiesByIds(jam.participants());
    }

    protected abstract void specificPlay(MusicDTO musicDTO, JamDTO jamDTO);
}
