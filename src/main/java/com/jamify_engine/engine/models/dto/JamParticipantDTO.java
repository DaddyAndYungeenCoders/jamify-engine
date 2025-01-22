package com.jamify_engine.engine.models.dto;

import com.jamify_engine.engine.models.entities.JamParticipantId;
import com.jamify_engine.engine.models.entities.UserEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class JamParticipantDTO {
    private JamParticipantId id;
    private boolean isHost;
    private JamDTO jam;
    private UserEntity user;
}
