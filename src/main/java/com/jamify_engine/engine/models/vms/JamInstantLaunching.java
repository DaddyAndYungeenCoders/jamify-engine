package com.jamify_engine.engine.models.vms;

import lombok.Builder;

import java.util.List;

@Builder
public record JamInstantLaunching(String name, List<String> themes) {
}
