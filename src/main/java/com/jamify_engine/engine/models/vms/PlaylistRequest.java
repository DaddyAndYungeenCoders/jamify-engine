package com.jamify_engine.engine.models.vms;

import lombok.Value;
import lombok.With;

import java.util.Map;

@Value
@With
public class PlaylistRequest {
    String uri;
    String providerAccessToken;
    String userProviderId;
    String username;
    Map<String, Object> requestBody;
}