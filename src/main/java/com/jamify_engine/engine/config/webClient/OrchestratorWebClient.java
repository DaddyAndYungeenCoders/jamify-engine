package com.jamify_engine.engine.config.webClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class OrchestratorWebClient {
    /**
     * API key for Jamify Engine.
     */
    @Value("${config.uaa.jamify-engine-api-key}")
    private String jamifyApiKey;

    /**
     * Base URL for UAA.
     */
    @Value("${config.orchestrator.url.base}")
    private String orchestratorUrl;

    @Bean(name = "orchestrationWebClient")
    public WebClient orchestratorWebClient() {
        return WebClient.builder()
                .baseUrl(orchestratorUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-API-KEY", jamifyApiKey)
                .build();
    }
}
