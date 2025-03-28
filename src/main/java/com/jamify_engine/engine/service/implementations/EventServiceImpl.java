package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.exceptions.common.BadRequestException;
import com.jamify_engine.engine.exceptions.common.NotFoundException;
import com.jamify_engine.engine.models.dto.event.EventCreateDTO;
import com.jamify_engine.engine.models.dto.event.EventDTO;
import com.jamify_engine.engine.models.entities.EventEntity;
import com.jamify_engine.engine.models.enums.EventStatus;
import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.models.mappers.EventMapper;
import com.jamify_engine.engine.repository.EventRepository;
import com.jamify_engine.engine.service.interfaces.EventService;
import com.jamify_engine.engine.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jamify_engine.engine.utils.SecurityUtils.getCurrentUser;

@Slf4j
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper, UserService userService) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Transactional
    public EventDTO createHostedEvent(EventCreateDTO eventDTO) {
        UserEntity hostEntity = getCurrentUser();

        EventEntity eventToCreate = eventMapper.toEntityfromCreateDTO(eventDTO);
        eventToCreate.setParticipants(new HashSet<>());
        eventToCreate.setStatus(EventStatus.SCHEDULED);

        eventToCreate.setHost(hostEntity);
        eventToCreate.getParticipants().add(hostEntity);

        return eventMapper.toDTO(eventRepository.save(eventToCreate));
    }

    @Override
    public List<EventDTO> findAllByHostId(long hostId) {
        return eventRepository.findAllByHostId(hostId)
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public EventDTO joinEvent(Long eventId) {
        UserEntity userEntity = getCurrentUser();
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new BadRequestException("Event with id " + eventId + " not found."));

        validateJoiningEvent(userEntity, eventEntity);

        eventEntity.getParticipants().add(userEntity);
        eventRepository.save(eventEntity);

        return eventMapper.toDTO(eventEntity);
    }

    @Override
    @Transactional
    public void cancelEvent(Long eventId) {
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found."));

        if (eventEntity.getStatus() != EventStatus.SCHEDULED) {
            throw new BadRequestException("Event with id '" + eventId + "' is not cancelable.");
        }

        eventEntity.setStatus(EventStatus.CANCELLED);

        // notify participants
        // TODO

        eventRepository.save(eventEntity);
    }

    @Override
    @Transactional
    public void leaveEvent(Long eventId) {
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found."));

        UserEntity userEntity = getCurrentUser();

        validateLeavingEvent(userEntity, eventEntity);

        eventEntity.getParticipants().remove(userEntity);
        eventRepository.save(eventEntity);
    }

    @Override
    public List<EventDTO> findByStatus(EventStatus status) {
        if (status == null || !Arrays.asList(EventStatus.values()).contains(status)) {
            throw new BadRequestException("Status '" + status + "' is not valid.");
        }
        return eventRepository.findAllByStatus(status)
                .orElseThrow(
                        () -> new NotFoundException("No events found with status '" + status + "'.")
                )
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }

    private void validateJoiningEvent(UserEntity userEntity, EventEntity eventEntity) {
        if (eventEntity.getStatus() == EventStatus.FINISHED) {
            throw new BadRequestException("Event has already finished.");
        }

        if (eventEntity.getStatus() == EventStatus.CANCELLED) {
            throw new BadRequestException("Event has been cancelled.");
        }

        if (eventEntity.getParticipants().contains(userEntity)) {
            throw new BadRequestException("User is already a participant of this event.");
        }
    }

    private void validateLeavingEvent(UserEntity userEntity, EventEntity eventEntity) {
        if (eventEntity.getHost().equals(userEntity)) {
            throw new BadRequestException("Host cannot leave the event.");
        }

        if (!eventEntity.getParticipants().contains(userEntity)) {
            throw new BadRequestException("User is not a participant of this event.");
        }
    }


    @Override
    public EventDTO create(EventDTO entityToCreate) {
        return null;
    }

    @Override
    public EventDTO update(Long id, EventDTO entityToUpdate) {
        eventRepository.findById(id)
                .ifPresent(eventEntity -> {
                    eventMapper.updateEntityFromDTO(entityToUpdate, eventEntity);
                    eventRepository.save(eventEntity);
                });

        return entityToUpdate;
    }

    @Override
    public void delete(Long entityToDeleteId) throws ExecutionControl.NotImplementedException {
        eventRepository.deleteById(entityToDeleteId);
    }

    @Override
    public EventDTO findById(Long entityId) {
        return eventRepository.findById(entityId)
                .map(eventMapper::toDTO)
                .orElse(null);
    }

    @Override
    public List<EventDTO> findAll() {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }
}
