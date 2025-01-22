package com.jamify_engine.engine.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jam_participant")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JamParticipantEntity {
    @EmbeddedId
    private JamParticipantId id;

    @Column(name = "is_host")
    private boolean isHost;

    @ManyToOne
    @MapsId("jamId")
    @JoinColumn(name = "jam_id")
    private JamEntity jam;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
