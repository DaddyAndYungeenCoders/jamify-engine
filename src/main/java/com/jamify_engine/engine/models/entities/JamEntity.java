package com.jamify_engine.engine.models.entities;

import com.jamify_engine.engine.models.enums.JamStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Table(name = "jam")
public class JamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jam_id")
    private Long id;

    @Column(name = "jam_name")
    private String name;

    @Column(name = "jam_scheduled_start")
    private LocalDateTime schedStart;

    @Column(name = "jam_status")
    @Enumerated(EnumType.STRING)
    private JamStatusEnum status;

    @ManyToMany
    @JoinTable(
            name = "jam_tags",
            joinColumns = @JoinColumn(name = "jam_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<TagEntity> tags;

    @OneToMany(mappedBy = "jam")
    private Set<JamMessageEntity> messages;

    @OneToMany(mappedBy = "jam")
    private Set<JamParticipantEntity> participants;
}