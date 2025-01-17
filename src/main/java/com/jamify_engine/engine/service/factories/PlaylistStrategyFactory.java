package com.jamify_engine.engine.service.factories;

import com.jamify_engine.engine.service.implementations.SpotifyPlaylistImpl;
import com.jamify_engine.engine.service.interfaces.IPlaylistStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PlaylistStrategyFactory {
    private final Map<String, IPlaylistStrategy> strategies;

    @Autowired
    public PlaylistStrategyFactory(List<IPlaylistStrategy> strategyList) {
        strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        IPlaylistStrategy::getProviderName,
                        strategy -> strategy
                ));
    }

    public IPlaylistStrategy getStrategy(String provider) {
        IPlaylistStrategy strategy = strategies.get(provider.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for provider: " + provider);
        }
        return strategy;
    }
}
