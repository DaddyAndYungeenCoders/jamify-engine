package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.exceptions.common.BadRequestException;
import com.jamify_engine.engine.exceptions.jam.JamNotFoundException;
import com.jamify_engine.engine.exceptions.security.UnauthorizedException;
import com.jamify_engine.engine.models.dto.JamDTO;
import com.jamify_engine.engine.models.dto.MusicDTO;
import com.jamify_engine.engine.models.entities.*;
import com.jamify_engine.engine.models.enums.JamStatusEnum;
import com.jamify_engine.engine.models.mappers.JamMapper;
import com.jamify_engine.engine.models.mappers.MusicMapper;
import com.jamify_engine.engine.models.vms.JamInstantLaunching;
import com.jamify_engine.engine.repository.JamRepository;
import com.jamify_engine.engine.service.interfaces.*;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    protected final TagsService tagsService;

    protected final MusicMapper musicMapper;

    protected final ParticipantService participantService;

    @Override
    public void playMusic() {
        UserEntity currentUser = getCurrentUser();
        Long currentUserId = currentUser.getId();
        Set<JamParticipantEntity> jamParticipantEntity = participantService.getFromUserId(currentUserId);
        JamParticipantEntity jamParticipant = jamParticipantEntity.stream().filter(
                jamParticipantEntity1 -> jamParticipantEntity1.isHost() && JamStatusEnum.RUNNING.equals(jamParticipantEntity1.getJam().getStatus())
        ).findFirst().orElseThrow(() -> new UnauthorizedException("The current user is not allowed to launch a music in a jam since he is not hosting any jam."));

        specificPlay(currentUser, jamParticipant.getJam());
    }

    @Override
    public void playMusic(Long musicId, Long jamId) {
        JamEntity jam = jamRepository.findById(jamId).orElseThrow(() -> new JamNotFoundException("The jam with id %d could not be found".formatted(jamId)));
        Long currentUserId = getCurrentUser().getId();

        if (!Objects.equals(getHostIdFromJam(jam), currentUserId) || !JamStatusEnum.RUNNING.equals(jam.getStatus())) {
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

        //join
        JamEntity jam = jamRepository.findById(jamId).orElseThrow(() -> new JamNotFoundException("The jam id %d is not corresponding to any known jam".formatted(jamId)));

        updateJamParticipation(currentUser, jam, false);

        userService.update(currentUser.getId(), currentUser);

        notify(getHostIdFromJam(jam), "User joining", currentUser.getName());
    }

    @Override
    public void leaveJam(Long jamId) {
        UserEntity currentUser = getCurrentUser();

        if (currentUser == null) {
            throw new UnauthorizedException("The current user does not exist, unable to join a jam.");
        }

        checkIfUserIsAllowedToLeaveAJam(currentUser, jamId);

        JamEntity jam = jamRepository.findById(jamId).orElseThrow(() -> new JamNotFoundException("The jam id %d is not corresponding to any known jam".formatted(jamId)));

        JamParticipantEntity jamParticipantEntity = participantService.getFromUserId(
                        currentUser.getId())
                .stream()
                .filter(jamParticipation -> Objects.equals(jamParticipation.getJam().getId(), jamId))
                .findFirst().orElseThrow(() -> new JamNotFoundException("The jam was not found"));

        participantService.deleteJamParticipant(jamParticipantEntity);

        notify(getHostIdFromJam(jam), "User leaving", currentUser.getName());
    }

    // FIXME implement ??!!
    @Override
    public JamDTO create(JamDTO entityToCreate) {
        return entityToCreate;
        /*
        String email = getCurrentUserEmail();

        if (Objects.equals(userService.findByEmail(email), null)) {
            throw new UserNotFoundException(email);
        }

        UserEntity user = userService.findEntityByEmail(email);

        if (user.isHasJamRunning()) {
            throw new JamAlreadyRunning(user.getName());
        }

        JamEntity jamEntity = JamEntity.builder()
                .hostId(user.getId())
                .status(entityToCreate.status())
                .schedStart(entityToCreate.scheduledDate())
                .tags(new HashSet<>())
                .messages(new HashSet<>())
                .participants(new HashSet<>())
                .build();

        JamEntity updatedEntity = updateJamParticipation(user, jamEntity);

        if (updatedEntity == null) {
            throw new BadRequestException("Something went wrong while trying to update the user status");
        }

        return mapper.toDTO(updatedEntity);
        */
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

        Set<TagEntity> tags = new HashSet<>();

        /**
         * TODO discuss, we should think about giving the user a search input to set a tag for his jam.
         */
        for (String tagLabel: jamVM.themes()) {
            Optional<TagEntity> tagEntity = tagsService.findByLabel(tagLabel);
            if (tagEntity.isEmpty()) {
                TagEntity tagToCreate = new TagEntity();
                tagToCreate.setLabel(tagLabel);
                TagEntity tag = tagsService.createNewTag(tagToCreate);
                tags.add(tag);
            } else {
                tags.add(tagEntity.get());
            }
        }

        JamEntity jamEntity = new JamEntity(
                null,
                jamVM.name(),
                LocalDateTime.now(),
                JamStatusEnum.RUNNING,
                tags,
                new HashSet<>(),
                new HashSet<>()
        );

        JamParticipantEntity jamParticipantEntity = updateJamParticipation(user, jamEntity, true);

        if (jamParticipantEntity == null) {
            throw new BadRequestException("Something went wrong while trying to update the user status");
        }

        return mapper.toDTO(jamParticipantEntity.getJam());
    }

    @Override
    public void stopAJam(Long jamId) {
        JamEntity jam = jamRepository.findById(jamId).orElseThrow(() -> new JamNotFoundException("The jam with id %d was not found"));
        String currentUserEmail = getCurrentUserEmail();
        UserEntity currentUser = userService.findEntityByEmail(currentUserEmail);

        if (!Objects.equals(
                getHostIdFromJam(jam),
                currentUser.getId())) {
            throw new UnauthorizedException("The user with id %d is not authorized to stop the jam with id %d!");
        }

        jam.setStatus(JamStatusEnum.STOPPED);

        jamRepository.save(jam);

        userService.update(currentUser.getId(), currentUser);
    }

    @Override
    public JamEntity findRunningJamForUser() {
        UserEntity currentUser = getCurrentUser();
        return jamRepository.findFirstByStatusAndParticipantUserId(JamStatusEnum.RUNNING, currentUser.getId()).orElseThrow(
                () -> new JamNotFoundException("The host %d has no jam running".formatted(currentUser.getId()))
        );
    }

    protected Long getHostIdFromJam(JamEntity jam) {
        return jam.getParticipants().stream().filter(
                        JamParticipantEntity::isHost
                ).findFirst().orElseThrow(() -> new IllegalArgumentException("Jam seems to have no host :("))
                .getId()
                .getUserId();
    }

    protected UserEntity getCurrentUser() {
        String currentUserEmail = getCurrentUserEmail();
        return userService.findEntityByEmail(currentUserEmail);
    }

    protected JamParticipantEntity updateJamParticipation(UserEntity user, JamEntity jam, boolean isHost) {
        // Sauvegarde du jam
        JamEntity savedJam = jamRepository.save(jam);

        // Création du participant
        JamParticipantEntity participant = new JamParticipantEntity();
        participant.setJam(savedJam);
        participant.setUser(user);
        participant.setHost(isHost);

        JamParticipantId jamParticipantId = new JamParticipantId();
        jamParticipantId.setUserId(user.getId());
        jamParticipantId.setJamId(savedJam.getId());
        participant.setId(jamParticipantId);

        // Mise à jour du Set participants côté JamEntity
        // (important pour que la liste ne soit plus vide en mémoire)
        savedJam.getParticipants().add(participant);

        // Sauvegarde du participant
        return participantService.createJamParticipant(participant);
    }


    protected String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.toString();
    }

    protected UserEntity getUserAndCheckIfUserIsAllowedToLaunchAJam() {
        UserEntity user = getCurrentUser();
        Set<JamParticipantEntity> participantEntities = participantService.getFromUserId(user.getId());

        for (JamParticipantEntity participant: participantEntities) {
            if (JamStatusEnum.RUNNING.equals(participant.getJam().getStatus())) {
                throw new UnauthorizedException("User already has a running jam");
            }
        }

        return user;
    }

    protected void checkIfUserIsAllowedToJoinAJam(UserEntity user) {
        // Refusing if user is hosting a jam
        if (user.isHasJamRunning()) {
            throw new UnauthorizedException("Unable to join a jam: the user %s is already hosting a jam.".formatted(user.getEmail()));
        }
        // Refusing if user is already listening to a jam
        if (user.getJams() != null && user.getJams().stream().anyMatch(jam -> jam.getJam().getStatus() == JamStatusEnum.RUNNING)) {
            throw new UnauthorizedException("Unable to join a jam: the user %s is already listening to a jam.".formatted(user.getEmail()));
        }
    }

    protected void checkIfUserIsAllowedToLeaveAJam(UserEntity user, Long jamId) {
        // Refusing if user is in the jam
        if (!(user.getJams() != null && user.getJams().stream().anyMatch(participation -> Objects.equals(participation.getId().getJamId(), jamId)))) {
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

    /**
     * Get all non-host users in a jam
     * @param jam the jam we want the follower of
     * @return a set of the users in a jam
     */
    protected Set<UserEntity> getAllUsersInAJam(JamEntity jam) {
        return jam.getParticipants().stream().filter(jamParticipantEntity -> !jamParticipantEntity.isHost()).map(JamParticipantEntity::getUser).collect(Collectors.toSet());
    }

    protected abstract void specificPlay(MusicDTO musicDTO, JamDTO jamDTO);
    protected abstract void specificPlay(UserEntity host, JamEntity jam);
}
