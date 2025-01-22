package com.jamify_engine.engine.models.entities;


import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class JamParticipantId implements Serializable {
    private Long jamId;
    private Long userId;
}
