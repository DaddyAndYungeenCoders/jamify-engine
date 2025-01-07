package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.exceptions.common.BadRequestException;
import com.jamify_engine.engine.exceptions.user.UserNotFoundException;
import com.jamify_engine.engine.models.dto.UserDTO;
import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.models.mappers.UserMapper;
import com.jamify_engine.engine.repository.UserRepository;
import com.jamify_engine.engine.service.interfaces.UserService;
import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
    public UserDTO create(UserDTO entityToCreate) throws ExecutionControl.NotImplementedException {
        log.info("Creating or updating user: {}", entityToCreate);
        if (entityToCreate.email() == null || entityToCreate.email().isBlank()) {
            throw new BadRequestException("Email cannot be null or empty");
        }
        UserEntity existingUser = userRepository.findByEmail(entityToCreate.email());

        if (existingUser != null) {
            // if the provider is different, we update provider's linked fields, and will update access token in next UAA request
            if (!Objects.equals(entityToCreate.provider(), existingUser.getProvider())) {
                existingUser.setProvider(entityToCreate.provider());
                existingUser.setUserProviderId(entityToCreate.userProviderId());
                userRepository.save(existingUser);
            }
            return userMapper.toDTO(existingUser);
        }
        return userMapper.toDTO(userRepository.save(userMapper.toEntity(entityToCreate)));
    }

    @Override
    public UserDTO update(Long id, UserDTO entityToUpdate) throws ExecutionControl.NotImplementedException {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        if (!entityToUpdate.name().isBlank()) {
            userEntity.setEmail(entityToUpdate.email());
        }
        if (!entityToUpdate.name().isBlank()) {
            userEntity.setName(entityToUpdate.name());
        }

        // TODO
//        Ajouter dans le mapper :     void updateEntityFromDto(UserDTO userDTO, @MappingTarget UserEntity userEntity);
//        utiliser ici : userMapper.updateEntityFromDto(entityToUpdate, userEntity); -> met a jour les champs non nulls

        UserEntity updatedUserEntity = userRepository.save(userEntity);
        return userMapper.toDTO(updatedUserEntity);
    }

    @Override
    public void delete(Long entityToDeleteId) throws ExecutionControl.NotImplementedException {
        userRepository.deleteById(entityToDeleteId);
    }

    @Override
    public UserDTO findById(Long entityId) throws ExecutionControl.NotImplementedException {
        UserEntity userEntity = userRepository.findById(entityId).orElseThrow(() -> new UserNotFoundException("User with id " + entityId + " not found"));
        return userMapper.toDTO(userEntity);
    }

    @Override
    public List<UserDTO> findAll() throws ExecutionControl.NotImplementedException {
        List<UserEntity> userEntities = userRepository.findAll();
        return userMapper.toDTOs(userEntities);
    }

    @Override
    public UserDTO findByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UserNotFoundException("User with email " + email + " not found");
        }

        return userMapper.toDTO(userEntity);
    }
}
