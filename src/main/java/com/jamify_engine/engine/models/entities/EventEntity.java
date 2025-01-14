package com.jamify_engine.engine.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "event")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    private String name;

    private LocalDateTime scheduledStart;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Embedded
    private AddressType address;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity host;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "event_participants",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> participants;
}
