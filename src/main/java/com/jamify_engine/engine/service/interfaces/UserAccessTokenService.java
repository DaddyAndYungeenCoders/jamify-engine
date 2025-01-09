package com.jamify_engine.engine.service.interfaces;

import com.jamify_engine.engine.models.dto.UserAccessTokenDto;

public interface UserAccessTokenService {
    String getAccessToken(String email, String provider);
    void saveAccessToken(UserAccessTokenDto userAccessTokenDto);
}
