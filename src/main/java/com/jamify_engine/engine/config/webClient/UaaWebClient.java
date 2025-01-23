package com.jamify_engine.engine.config.webClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for UAA WebClient.
 */
@Configuration
public class UaaWebClient {

    /**
     * API key for Jamify Engine.
     */
    @Value("${config.uaa.jamify-engine-api-key}")
    private String jamifyApiKey;

    /**
     * Base URL for UAA.
     */
    @Value("${config.uaa.url.refresh-access-token}")
    private String uaaUrl;

    /**
     * Creates a WebClient bean configured for UAA.
     *
     * @return a configured WebClient instance
     */
    @Bean(name = "uaaServiceWebClient")
    public WebClient uaaWebClient() {
        return WebClient.builder()
                .baseUrl(uaaUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-API-KEY", jamifyApiKey)
                .build();
    }
}