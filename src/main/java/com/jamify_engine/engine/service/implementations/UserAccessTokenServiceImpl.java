package com.jamify_engine.engine.service.implementations;

import com.jamify_engine.engine.exceptions.security.AccessTokenNotFoundException;
import com.jamify_engine.engine.exceptions.security.AccessTokenProcessingException;
import com.jamify_engine.engine.exceptions.user.UserNotFoundException;
import com.jamify_engine.engine.models.dto.ProviderAccessTokenResponse;
import com.jamify_engine.engine.models.dto.UserAccessTokenDto;
import com.jamify_engine.engine.models.entities.UserAccessTokenEntity;
import com.jamify_engine.engine.models.entities.UserEntity;
import com.jamify_engine.engine.repository.UserAccessTokenRepository;
import com.jamify_engine.engine.repository.UserRepository;
import com.jamify_engine.engine.service.interfaces.UserAccessTokenService;
import com.jamify_engine.engine.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;

/**
 * Service class for managing user access tokens.
 */
@Service
@Transactional
public class UserAccessTokenServiceImpl implements UserAccessTokenService {

    private static final Logger log = LoggerFactory.getLogger(UserAccessTokenServiceImpl.class);
    private final UserAccessTokenRepository userAccessTokenRepository;
    private final WebClient uaaWebClient;
    private final UserRepository userRepository;

    /**
     * Constructor for TokenService.
     *
     * @param userAccessTokenRepository the repository for user access tokens
     * @param uaaWebClient              the WebClient for making HTTP requests
     */
    public UserAccessTokenServiceImpl(UserAccessTokenRepository userAccessTokenRepository, @Qualifier("uaaServiceWebClient") WebClient uaaWebClient, UserRepository userRepository) {
        this.userAccessTokenRepository = userAccessTokenRepository;
        this.uaaWebClient = uaaWebClient;
        this.userRepository = userRepository;
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
        UserAccessTokenEntity userAccessToken = userAccessTokenRepository.findByEmailAndProvider(email, provider);
        if (userAccessToken == null) {
            throw new AccessTokenNotFoundException("Access token not found for user: " + email + " and provider: " + provider);
        }

        if (userAccessToken.getExpiresAt().isBefore(ChronoLocalDateTime.from(ZonedDateTime.now(ZoneId.of("Europe/Paris"))))) {
            log.debug("Access token expired for user {}. Refreshing...", email);
            String refreshedToken = refreshAccessToken(email, provider);
            if (refreshedToken == null) {
                throw new AccessTokenProcessingException("Error while refreshing access token for user: " + email + " and provider: " + provider);
            }
            userAccessToken.setAccessToken(refreshedToken);
            userAccessToken.setExpiresAt(LocalDateTime.now().plusHours(1));
            userAccessTokenRepository.save(userAccessToken);
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
    @Override
    public String refreshAccessToken(String email, String provider) {
        String refreshAccessTokenParams = "/auth/refresh-access-token?provider=%s&email=%s";
        String uri = String.format(refreshAccessTokenParams, provider, email);
        String jwt = SecurityUtils.getCurrentUserJwt();

        try {
            ProviderAccessTokenResponse res = uaaWebClient
                    .post()
                    .uri(uri)
                    .header("Authorization", "Bearer " + jwt)
                    .retrieve()
                    .bodyToMono(ProviderAccessTokenResponse.class)
                    .block();

            assert res != null;
            return res.getAccessToken();

        } catch (WebClientResponseException e) {
            log.error("HTTP Error ({}) while refreshing access token: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Error while refreshing access token: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public String refreshAccessToken(Long jamifyUserId, String provider) {
        String email = this.userRepository.findById(jamifyUserId).orElseThrow(() -> new UserNotFoundException("User with id %d does not exist".formatted(jamifyUserId))).getEmail();
        return this.refreshAccessToken(email, provider);
    }

    /**
     * Saves the access token for a user.
     *
     * @param newUserAccessToken the user access token entity
     * @throws AccessTokenProcessingException if there is an error while saving the access token
     */
    @Transactional
    public void saveAccessToken(UserAccessTokenDto newUserAccessToken) {
        try {
            UserAccessTokenEntity existingToken = userAccessTokenRepository
                    .findByUserEmail(newUserAccessToken.email());

            if (existingToken != null) {
                // Remove the old token from the user
                UserEntity user = existingToken.getUser();
                user.setAccessToken(null);

                // Delete the old token
                userAccessTokenRepository.delete(existingToken);
                userAccessTokenRepository.flush(); // flush to avoid constraint violation
            }

            UserAccessTokenEntity newToken = buildUserAccessTokenEntity(newUserAccessToken);
            userAccessTokenRepository.save(newToken);

        } catch (Exception e) {
            throw new AccessTokenProcessingException("Error while saving access token: " + e.getMessage());
        }    }

    private UserAccessTokenEntity buildUserAccessTokenEntity(UserAccessTokenDto newUserAccessToken) {
        UserAccessTokenEntity newUserAccessTokenEntity = new UserAccessTokenEntity();
        newUserAccessTokenEntity.setUser(userRepository.findByEmail(newUserAccessToken.email()));
        newUserAccessTokenEntity.setProvider(newUserAccessToken.provider());
        newUserAccessTokenEntity.setAccessToken(newUserAccessToken.accessToken());
        newUserAccessTokenEntity.setExpiresAt(newUserAccessToken.expiresAt());
        return newUserAccessTokenEntity;
    }
}