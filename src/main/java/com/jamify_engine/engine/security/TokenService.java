package com.jamify_engine.engine.security;

import com.jamify_engine.engine.exceptions.security.AccessTokenNotFoundException;
import com.jamify_engine.engine.exceptions.security.AccessTokenProcessingException;
import com.jamify_engine.engine.models.entities.UserAccessTokenEntity;
import com.jamify_engine.engine.repository.UserAccessTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Service class for managing user access tokens.
 */
@Service
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private final UserAccessTokenRepository userTokenRepository;
    private final WebClient uaaWebClient;

    /**
     * Constructor for TokenService.
     *
     * @param userTokenRepository the repository for user access tokens
     * @param uaaWebClient        the WebClient for making HTTP requests
     */
    public TokenService(UserAccessTokenRepository userTokenRepository, @Qualifier("uaaServiceWebClient") WebClient uaaWebClient) {
        this.userTokenRepository = userTokenRepository;
        this.uaaWebClient = uaaWebClient;
    }

    /**
     * Retrieves the access token for a given user and provider. Used before making requests to providers APIs.
     *
     * @param email    the email of the user
     * @param provider the provider of the access token
     * @return the access token
     * @throws AccessTokenNotFoundException if the access token is not found
     */// TODO: get the access token with user id once schema is updated
    public String getAccessToken(String email, String provider) {
        UserAccessTokenEntity userAccessToken = userTokenRepository.findByEmailAndProvider(email, provider);
        if (userAccessToken == null) {
            throw new AccessTokenNotFoundException("Access token not found for user: " + email + " and provider: " + provider);
        }

        if (userAccessToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.debug("Access token expired for user {}. Refreshing...", email);
            String refreshedToken = refreshAccessToken(email, provider);
            userAccessToken.setAccessToken(refreshedToken);
            userAccessToken.setExpiresAt(LocalDateTime.now().plusHours(1));
            userTokenRepository.save(userAccessToken);
        }

        return userAccessToken.getAccessToken();
    }

    /**
     * Refreshes the access token for a given user and provider.
     *
     * @param email    the email of the user
     * @param provider the provider of the access token
     * @return the refreshed access token
     * @throws RuntimeException if the access token refresh fails
     */
    private String refreshAccessToken(String email, String provider) {
        String refreshAccessTokenParams = "?provider=%s&email=%s";
        String uri = String.format(refreshAccessTokenParams, provider, email);

        try {

            Map res = uaaWebClient
                    .post()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            assert res != null;
            return res.get("access_token").toString();

        } catch (WebClientResponseException e) {
            log.error("HTTP Error ({}) while refreshing access token: {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error while refreshing access token: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Saves the access token for a user.
     *
     * @param newUserAccessToken the user access token entity
     * @throws AccessTokenProcessingException if there is an error while saving the access token
     */
    public void saveAccessToken(UserAccessTokenEntity newUserAccessToken) {
        try {
            // check if user already has an access token for the provider
            UserAccessTokenEntity existingToken = userTokenRepository.findByEmailAndProvider(newUserAccessToken.getEmail(), newUserAccessToken.getProvider());
            if (existingToken != null) {
                existingToken.setAccessToken(newUserAccessToken.getAccessToken());
                existingToken.setExpiresAt(newUserAccessToken.getExpiresAt());
                userTokenRepository.save(existingToken);
                return;
            }
            userTokenRepository.save(newUserAccessToken);
        } catch (Exception e) {
            throw new AccessTokenProcessingException("Error while saving access token: " + e.getMessage());
        }
    }
}