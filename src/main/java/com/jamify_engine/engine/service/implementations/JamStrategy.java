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
import com.jamify_engine.engine.models.mappers.UserMapper;
import com.jamify_engine.engine.models.vms.JamInstantLaunching;
import com.jamify_engine.engine.repository.JamRepository;
import com.jamify_engine.engine.service.interfaces.IJamStrategy;
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
    private final UserService userService;

    @Autowired
    private final JamRepository jamRepository;

    private final JamMapper mapper;

    @Override
    public void playMusic(Long musicId, Long jamId) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
    }

    @Override
    public List<MusicDTO> getAllInQueue(Long jamId) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
    }

    @Override
    public void joinJam(Long jamId) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
    }

    @Override
    public void leaveJam(Long jamId) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
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
    public JamDTO findById(Long entityId) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
    }

    @Override
    public List<JamDTO> findAll() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not yet");
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
        String currentUserEmail = getCurrentUserEmail();
        UserEntity currentUser = userService.findEntityByEmail(currentUserEmail);
        return jamRepository.findFirstByHost_IdAndStatus(currentUser.getId(), JamStatusEnum.RUNNING).orElseThrow(
                () -> new JamNotFoundException("The host %d has no jam running".formatted(currentUser.getId()))
        );
    }

    private UserDTO updateUserWithNewJam(UserEntity user, JamEntity jam) {
        Set<JamEntity> hostedJams = user.getHostedJams();

        hostedJams.add(jam);

        user.setHostedJams(hostedJams);

        return userService.update(user.getId(), user);
    }


    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.toString();
    }

    private UserEntity getUserAndCheckIfUserIsAllowedToLaunchAJam() {
        String email = getCurrentUserEmail();

        UserEntity user = userService.findEntityByEmail(email);

        if (Objects.equals(userService.findByEmail(email), null)) {
            throw new UserNotFoundException(email);
        }

        if (user.isHasJamRunning()) {
            throw new JamAlreadyRunning(user.getName());
        }

        return user;
    }

    protected abstract void specificPlay();
}
