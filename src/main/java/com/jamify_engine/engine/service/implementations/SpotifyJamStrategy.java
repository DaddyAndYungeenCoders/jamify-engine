package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.models.mappers.JamMapper;
import com.jamify_engine.engine.models.mappers.UserMapper;
import com.jamify_engine.engine.repository.JamRepository;
import com.jamify_engine.engine.repository.UserRepository;
import com.jamify_engine.engine.service.interfaces.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SpotifyJamStrategy extends JamStrategy {
    @Autowired
    public SpotifyJamStrategy(UserService userService, JamRepository jamRepository, JamMapper jamMapper) {
        super(userService, jamRepository, jamMapper);
    }

    @Override
    protected void specificPlay() {
      log.debug("Nothing currently...");
    }
}
