package com.jamify_engine.engine.service.implementations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SpotifyJamStrategy extends JamStrategy {
    @Override
    protected void specificPlay() {
      log.debug("Nothing currently...");
    }
}
