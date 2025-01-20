package com.jamify_engine.engine.models.enums;

import lombok.Getter;

@Getter
public enum ProvidersEnum {
    SPOTIFY("spotify"),
    AMAZON("amazon music");

    private final String provider;

    ProvidersEnum(String provider) {
        this.provider = provider;
    }
}
