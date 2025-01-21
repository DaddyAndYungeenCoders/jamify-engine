package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.exceptions.common.BadRequestException;
import com.jamify_engine.engine.exceptions.security.UnauthorizedException;
import com.jamify_engine.engine.exceptions.user.UserNotFoundException;
import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.models.mappers.UserMapper;
import com.jamify_engine.engine.repository.UserRepository;
import com.jamify_engine.engine.service.interfaces.UserService;
import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO create(UserDTO userDtoToCreate) throws ExecutionControl.NotImplementedException {
        log.info("Creating or updating user: {}", userDtoToCreate);
        if (userDtoToCreate.email() == null || userDtoToCreate.email().isBlank()) {
            throw new BadRequestException("Email cannot be null or empty");
        }
        UserEntity existingUser = userRepository.findByEmail(userDtoToCreate.email());

        if (existingUser != null) {
            // if the provider is different, we update provider's linked fields, and will update access token in next UAA request

            this.updateFromRegisteringOrLoggingIn(existingUser.getId(), userDtoToCreate);
            if (!Objects.equals(userDtoToCreate.provider(), existingUser.getProvider())) {
                if (userDtoToCreate.imgUrl() == null) {
                    existingUser.setImgUrl("https://cdn.pixabay.com/photo/2016/08/08/09/17/avatar-1577909_1280.png");
                } else {
                    existingUser.setImgUrl(userDtoToCreate.imgUrl());
                }
                existingUser.setProvider(userDtoToCreate.provider());
                existingUser.setUserProviderId(userDtoToCreate.userProviderId());
                userRepository.save(existingUser);
            }
            return userMapper.toDTO(existingUser);
        }
        return userMapper.toDTO(userRepository.save(userMapper.toEntity(userDtoToCreate)));
    }

    @Override
    public UserDTO updateFromRegisteringOrLoggingIn(Long id, UserDTO dto) {
        UserEntity userEntity = getUserFromEmail(id, dto.email());

        userMapper.updateEntityFromDto(dto, userEntity);

        UserEntity updatedUserEntity = userRepository.save(userEntity);

        return userMapper.toDTO(updatedUserEntity);
    }

    @Override
    public UserDTO update(Long id, UserEntity userToUpdate) {
        UserEntity user = getUserFromEmail(id, userToUpdate.getEmail());
        UserEntity allowedUserToUpdate = isCurrentUserAllowed(user);

        UserEntity updatedUserEntity = userRepository.save(allowedUserToUpdate);

        return userMapper.toDTO(updatedUserEntity);
    }

    @Override
    public Set<UserEntity> findAllEntitiesByIds(Set<Long> ids) {
        return new HashSet<>(userRepository.findAllById(ids));
    }

    @Override
    public UserDTO update(Long id, UserDTO dto) {
        UserEntity user = getUserFromEmail(id, dto.email());
        UserEntity allowedUserToUpdate = isCurrentUserAllowed(user);

        userMapper.updateEntityFromDto(dto, allowedUserToUpdate);

        UserEntity updatedUserEntity = userRepository.save(allowedUserToUpdate);

        return userMapper.toDTO(updatedUserEntity);
    }

    private UserEntity getUserFromEmail(Long id, String userToUpdateEmail) {
        if (id == null) {
            throw new BadRequestException("Unable to update a user if the entity has no id");
        }
        if (userToUpdateEmail == null || userToUpdateEmail.isBlank()) {
            throw new BadRequestException("Email cannot be null or empty");
        }

        UserEntity userCorrespondingToEmail = Optional.ofNullable(userRepository
                        .findByEmail(userToUpdateEmail))
                .orElseThrow(() ->
                        new UserNotFoundException("User %s does not exist in database".formatted(userToUpdateEmail))
                );

        if (!userCorrespondingToEmail.getId().equals(id)) {
            throw new UnauthorizedException("The id %d and the id of the user you passed are not equals (%d)".formatted(id, userCorrespondingToEmail.getId()));
        }

        return userCorrespondingToEmail;
    }

    private UserEntity isCurrentUserAllowed(UserEntity userCorrespondingToEmail) {

        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        UserEntity currentUser = Optional.ofNullable(userRepository.findByEmail(currentUserEmail))
                .orElseThrow(() ->
                        new UserNotFoundException("Current user %s does not exist in database".formatted(currentUserEmail))
                );

        if (!Objects.equals(currentUser.getId(), userCorrespondingToEmail.getId())) {
            throw new UnauthorizedException(currentUser.getId());
        }

        return userCorrespondingToEmail;
    }

    @Override
    public void delete(Long entityToDeleteId) {
        userRepository.deleteById(entityToDeleteId);
    }

    @Override
    public UserDTO findById(Long entityId) {
        UserEntity userEntity = userRepository.findById(entityId).orElseThrow(() -> new UserNotFoundException("User with id " + entityId + " not found"));
        return userMapper.toDTO(userEntity);
    }

    @Override
    public UserDTO findByUserProviderId(String providerId) {
        UserEntity userEntity = userRepository.findByUserProviderId(providerId).orElseThrow(() -> new UserNotFoundException("User with providerId " + providerId + " not found"));
        return userMapper.toDTO(userEntity);
    }

    @Override
    public List<UserDTO> findAll() {
        List<UserEntity> userEntities = userRepository.findAll();
        return userMapper.toDTOs(userEntities);
    }

    @Override
    public UserDTO findByEmail(String email) {
        return userMapper.toDTO(findEntityByEmail(email));
    }

    @Override
    public UserEntity findEntityByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UserNotFoundException("User with email " + email + " not found");
        }

        return userEntity;
    }
}
