package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.exceptions.user.UserNotFoundException;
import com.jamify_engine.engine.models.dto.event.EventCreateDTO;
import com.jamify_engine.engine.models.dto.event.EventDTO;
import com.jamify_engine.engine.models.entities.EventEntity;
import com.jamify_engine.engine.models.entities.EventStatus;
import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.models.mappers.EventMapper;
import com.jamify_engine.engine.repository.EventRepository;
import com.jamify_engine.engine.service.interfaces.EventService;
import com.jamify_engine.engine.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper, UserService userService) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.userService = userService;
    }

    @Transactional
    public EventDTO createHostedEvent(EventCreateDTO eventDTO) {
        // get current logged in user
        // TODO
        Object email = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity hostEntity = userService.findEntityByEmail(email.toString());

        if (hostEntity == null) {
            throw new UserNotFoundException("User with id " + email + " not found");
        }

        EventEntity eventToCreate = eventMapper.toEntityfromCreateDTO(eventDTO);
        eventToCreate.setParticipants(new HashSet<>());
        eventToCreate.setStatus(EventStatus.SCHEDULED);

        eventToCreate.setHost(hostEntity);
        eventToCreate.getParticipants().add(hostEntity);

        return eventMapper.toDTO(eventRepository.save(eventToCreate));
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
    public List<EventDTO> findAll() throws ExecutionControl.NotImplementedException {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }
}
