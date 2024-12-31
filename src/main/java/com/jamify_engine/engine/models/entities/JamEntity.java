package com.jamify_engine.engine.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private String status;

    @OneToMany(mappedBy = "jam")
    private Set<JamMessageEntity> messages;

    @ManyToMany
    @JoinTable(
            name = "jam_participant",
            joinColumns = @JoinColumn(name = "jam_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> participants;
}