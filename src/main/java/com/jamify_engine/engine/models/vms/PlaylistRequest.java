package com.jamify_engine.engine.models.vms;

import lombok.Value;
import lombok.With;

import java.util.Map;

@Value
@With
public class PlaylistRequest {
    String uri;
    String providerAccessToken;
    Long jamifyUserId;
    String username;
    Map<String, Object> requestBody;
}